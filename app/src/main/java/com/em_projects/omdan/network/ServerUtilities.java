package com.em_projects.omdan.network;

import android.util.Log;

import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.utils.StringUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Thread.sleep;

/**
 * Created by eyal muchtar on 29/07/2017.
 */

public final class ServerUtilities implements Runnable {
    private static String TAG = "ServerUtilities";

    private static ServerUtilities instance = null;
    private ArrayList<CommRequest> queue = new ArrayList(10);
    private Thread runner = null;
    private boolean isRunning = false;
    private Object monitor = new Object();


    private ServerUtilities() {
        isRunning = true;
        runner = new Thread(this);
        runner.start();
    }

    public static ServerUtilities getInstance() {
        if (null == instance) {
            instance = new ServerUtilities();
        }
        return instance;
    }

    public void login(String user, String password, CommListener listener) {
        String serverUrl = Dynamics.serverURL +  Constants.login;
        HashMap params = new HashMap();
        params.put(Constants.userName, user);
        params.put(Constants.password, password);

        post(serverUrl, params, listener);
    }

//    public void requestSMSVerification(String deviceId, String phoneNumber, CommListener listener) {
//        String serverUrl = Constants.serverURL + "/" + Constants.smsVerification;
//        HashMap params = new HashMap();
//        params.put(Constants.ourSecret, Constants.secret);
//        params.put(Constants.deviceId, deviceId);
//        params.put(Constants.phoneNumber, phoneNumber);
//        params.put(Constants.timeStamp, String.valueOf(TimeUtils.getTime()));
//
//        post(serverUrl, params, listener);
//    }
//
//    public void verifyOtpCode(String otp, String phoneNumber, String deviceId, CommListener listener) {
//        String serverUrl = Constants.serverURL + "/" + Constants.otpVerification;
//        HashMap params = new HashMap();
//        params.put(Constants.otp, otp);
//        params.put(Constants.ourSecret, Constants.secret);
//        params.put(Constants.deviceId, deviceId);
//        params.put(Constants.phoneNumber, phoneNumber);
//        params.put(Constants.timeStamp, String.valueOf(TimeUtils.getTime()));
//
//        post(serverUrl, params, listener);
//    }

    // Puts the request into queue for requests and add some additional data
    private void post(final String serverURL, final Map<String, String> params, CommListener listener) {

        //Amend device information for the server
        /*params.put("PHONE_MODEL", Build.MODEL);
        params.put("PHONE_MANUFACTURER", Build.MANUFACTURER);
        params.put("VERSION", Build.VERSION.RELEASE);
        params.put("PHONE_TYPE", "Android");*/

        queue.add(new CommRequest(serverURL, params, CommRequest.MethodType.POST, listener));
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    // The main looper of the server requests
    @Override
    public void run() {
        while (isRunning) {
            if (queue.isEmpty()) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            CommRequest requestHolder = queue.remove(0);
            handleRequest(requestHolder);
            // Stop running between the threads' creation.
            try {
                sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    // Handle a single request till returns data to listener
    private void handleRequest(final CommRequest requestHolder) {
        try {
            if (requestHolder != null) {
                String response = transmitData(requestHolder);
                if (requestHolder.getListener() != null) {
                    if (StringUtils.isNullOrEmpty(response)) {
                        requestHolder.getListener().exceptionThrown(new Exception());
                    } else {
                        int firstIndex = response.indexOf("{");
                        int lastIndex = response.lastIndexOf("}");
                        response = response.substring(firstIndex, lastIndex + 1);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            char c = response.charAt(i);
                            if ('\\' == c) continue;
                            sb.append(c);
                        }
                        response = sb.toString();
                        requestHolder.getListener().newDataArrived(response);
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "handleRequest", ex);
            if (requestHolder != null && requestHolder.getListener() != null) {
                requestHolder.getListener().exceptionThrown(ex);
            }
        }
    }

    // Sends the request to the server. Supporting GET and POST only
    private synchronized String transmitData(CommRequest commRequest) throws IOException, JSONException {
        Map<String, String> params = commRequest.getParams();
        CommRequest.MethodType method = commRequest.getMethodType();
        String serverUrl = commRequest.getServerURL();
        HttpResponse httpResponse = null;
        HttpClient client = new DefaultHttpClient();

        if (method == CommRequest.MethodType.GET) {
            String body = encodeParams(params);
            String urlString = serverUrl + "?" + body;
            Log.d(TAG, "transmitData GET urlString: " + urlString);
            HttpGet request = new HttpGet(urlString);
            httpResponse = client.execute(request);
        } else if (method == CommRequest.MethodType.POST) {
            HttpPost httpPost = new HttpPost(serverUrl);
            ArrayList<NameValuePair> nameValuePairs = convertMapToNameValuePairs(params);
            Log.d(TAG, "transmitData POST urlString: " + serverUrl);
            //UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);//, "UTF-8");
            //httpPost.setEntity(entity);
            httpPost = postAsJson(httpPost, nameValuePairs);
            httpResponse = client.execute(httpPost);
        }

        // Check if server response is valid
        StatusLine status = httpResponse.getStatusLine();
        if (status.getStatusCode() != 200) {
            throw new IOException("Invalid response from server: " + status.toString());
        }
        // Return result from buffered stream
        String answer = handleHttpResponse(httpResponse);
        return answer;
    }

    // constructs the GET body using the parameters
    private String encodeParams(Map<String, String> params) {
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            if (param.getValue() != null) {
                try {
                    bodyBuilder.append(param.getKey()).append('=').append(URLEncoder.encode(param.getValue(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "encodeParams", e);
                }
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
        }
        return bodyBuilder.toString();
    }

    // constructs the POST body using the parameters
    private ArrayList<NameValuePair> convertMapToNameValuePairs(Map<String, String> params) {
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(params.size());
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            if (param.getValue() != null) {
                nameValuePairs.add(new MyNameValuePair(param.getKey(), param.getValue()));
            }
        }
        return nameValuePairs;
    }

    private HttpPost postAsJson(HttpPost httpost, ArrayList<NameValuePair> params) throws JSONException, UnsupportedEncodingException {
        JSONObject holder = new JSONObject();
        for (NameValuePair pairs : params) {
            holder.put((String) pairs.getName(), (String) pairs.getValue());
        }
        StringEntity se = new StringEntity(holder.toString());
        httpost.setEntity(se);
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");
        return httpost;
    }

    // Reads the returned data and convert it to String
    private String handleHttpResponse(HttpResponse httpResponse) throws IllegalStateException, IOException {
        InputStream is = httpResponse.getEntity().getContent();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuffer stringBuffer = new StringBuffer("");
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        bufferedReader.close();
        return stringBuffer.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        isRunning = false;
        if (null != runner) {
            runner.join();
            runner = null;
        }
        if (null != queue) {
            queue.clear();
            queue = null;
        }
        super.finalize();
    }
}

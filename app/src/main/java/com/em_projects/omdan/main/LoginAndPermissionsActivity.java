package com.em_projects.omdan.main;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.dialogs.LoginDialog;
import com.em_projects.omdan.dialogs.LoginFailedDialog;
import com.em_projects.omdan.dialogs.ServerConnectionDialog;
import com.em_projects.omdan.network.CommListener;
import com.em_projects.omdan.network.ServerUtilities;
import com.em_projects.omdan.utils.AppUtils;
import com.em_projects.omdan.utils.ErrorsUtils;
import com.em_projects.omdan.utils.PreferencesUtils;
import com.em_projects.omdan.utils.StringUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginAndPermissionsActivity extends AppCompatActivity implements
        ServerConnectionDialog.OnSetServerConnectionListener,
        LoginDialog.OnSetLoginDataListener, LoginFailedDialog.OnLoginFailedListener {
    private static final String TAG = "LoginAndPermissionsAct";
    // ******************************************  Permissions Section  ******************************************
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERM_REQUEST_CODE_DRAW_OVERLAYS = 250;
    private View view;
    private Context context;
    private ProgressDialog progressDialog;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        context = this;

        TextView versionNumber = (TextView) (view = findViewById(R.id.version_number));
        versionNumber.setText(getString(R.string.app_version_format,
                AppUtils.getAppVersion(this), AppUtils.getAppVerionCode(this)));


        // Check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                requestPermission();
            } else {
                continueLoading();
            }
        } else {
            continueLoading();
        }

        // Make sure the view adjust while showing keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void continueLoading() {
        Dynamics.serverURL = PreferencesUtils.getInstance(context).getServerConncetionString();
        if (null == Dynamics.serverURL) {
            showServerConnectionDialog();
            return;
        }
        if (true == StringUtils.isNullOrEmpty(Dynamics.uUID)) {
            showLoginDialog();
            return;
        }

        moveToNextScreen();
    }

    private void moveToNextScreen() {
        Intent intent = new Intent(context, MainScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
        finish();
    }

    public void showLoginDialog() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getFragmentManager();
                LoginDialog dialog = new LoginDialog();
                try {
                    dialog.show(fm, "LoginDialog");
                } catch (Throwable e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "showLoginDialog");
                    FirebaseCrash.report(e);
                }
            }
        }, 500);
    }

    private void showServerConnectionDialog() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getFragmentManager();
                ServerConnectionDialog dialog = new ServerConnectionDialog();
                if (false == StringUtils.isNullOrEmpty(Dynamics.serverURL)) {
                    Bundle args = new Bundle();
                    args.putString("serverIp", Dynamics.getServerIp());
                    args.putInt("serverPort", Dynamics.getServerPort());
                    dialog.setArguments(args);
                }
                try {
                    dialog.show(fm, "ServerConnectionDialog");
                } catch (Throwable e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "ServerConnectionDialog");
                    FirebaseCrash.report(e);
                }
            }
        }, 500);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        int cameraRes = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int locationRes = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int locationCRes = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int internetRes = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int readContactRes = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int wakeLockRes = ContextCompat.checkSelfPermission(getApplicationContext(), WAKE_LOCK);
        int writeFileRes = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int readFilekRes = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int alertWindow = ContextCompat.checkSelfPermission(getApplicationContext(), SYSTEM_ALERT_WINDOW);
        boolean overLay = Settings.canDrawOverlays(this);

        return cameraRes == PackageManager.PERMISSION_GRANTED &&
                locationRes == PackageManager.PERMISSION_GRANTED &&
                locationCRes == PackageManager.PERMISSION_GRANTED &&
                internetRes == PackageManager.PERMISSION_GRANTED &&
                readContactRes == PackageManager.PERMISSION_GRANTED &&
                wakeLockRes == PackageManager.PERMISSION_GRANTED &&
                writeFileRes == PackageManager.PERMISSION_GRANTED &&
                readFilekRes == PackageManager.PERMISSION_GRANTED &&
                alertWindow == PackageManager.PERMISSION_GRANTED &&
                overLay;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION, INTERNET, READ_CONTACTS, WAKE_LOCK, WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE, SYSTEM_ALERT_WINDOW}, PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraRes = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationRes = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean locationCRes = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean internetRes = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean contectRes = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean wakeLockRes = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean writeFileRes = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean readFileRes = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean alertWindow = grantResults[8] == PackageManager.PERMISSION_GRANTED;

                    if (cameraRes && locationRes && locationCRes && internetRes && contectRes &&
                            wakeLockRes && writeFileRes && readFileRes && alertWindow) {
                        Snackbar.make(view, "Permission Granted, Now you can use the application.", Snackbar.LENGTH_LONG).show();
                        if (false == Settings.canDrawOverlays(context)) {
                            permissionToDrawOverlays();
                        }
                        continueLoading();
                    } else {
                        Snackbar.make(view, "Permission Denied, You cannot access the application.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (hasPermissions(context, CAMERA, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET,
                                    READ_CONTACTS, WAKE_LOCK, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION,
                                                                    ACCESS_COARSE_LOCATION, INTERNET, READ_CONTACTS, WAKE_LOCK,
                                                                    WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, SYSTEM_ALERT_WINDOW},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
        }
    }

    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            FirebaseCrash.log("permissionToDrawOverlays");
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!checkPermissions()) {
                    requestPermission();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // ******************************************  Permissions Section  ******************************************

    // LoginDialog.OnSetLoginDataListener
    @Override
    public void onSetLoginDataListener(String usr, String pwd) {
        showProgressDialog();
        ServerUtilities.getInstance().login(usr, pwd, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                try {
                    if (response.contains(Constants.error)) {
                        int errorNo = ErrorsUtils.getError(response);
                        //showToast(context.getString(R.string.login_failed));
                        showLoginFailedDialog();
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has(Constants.uuid)) {
                            Dynamics.uUID = (String) jsonObject.get(Constants.uuid);
                            hideProgressDialog();
                            continueLoading();
                        }
                    }
                } catch (JSONException ex) {
                    showLoginFailedDialog();
                    Log.e(TAG, "onSetLoginDataListener -> newDataArrived response: " + response);
                    FirebaseCrash.logcat(Log.ERROR, TAG, "onSetLoginDataListener -> newDataArrived");
                    FirebaseCrash.report(ex);
                    FirebaseCrash.log("response: " + response);
                } finally {
                    hideProgressDialog();
                    //continueLoading();
                }
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                showLoginFailedDialog();
                Log.e(TAG, "onSetLoginDataListener -> exceptionThrown");
                //showToast(context.getString(R.string.login_failed));
                FirebaseCrash.logcat(Log.ERROR, TAG, "onSetLoginDataListener -> newDataArrived");
                FirebaseCrash.report(throwable);
                hideProgressDialog();
                //continueLoading();
            }
        });
    }

    private void showToast(final String message) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoginFailedDialog() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getFragmentManager();
                LoginFailedDialog dialog = new LoginFailedDialog();
                try {
                    dialog.show(fm, "LoginFailedDialog");
                } catch (Throwable e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "showLoginFailedDialog");
                    FirebaseCrash.report(e);
                }
            }
        }, 100);
    }

    // LoginFailedDialog.OnLoginFailedListener
    @Override
    public void okButtonPressed() {
        continueLoading();
    }

    // LoginDialog.OnSetLoginDataListener
    @Override
    public void settingButtonPressed() {
        showServerConnectionDialog();
    }

    private void hideProgressDialog() {
        if (null != progressDialog) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void showProgressDialog() {
        if (null == progressDialog || false == progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(context, "", "Loading...", true);
        }
    }

    // ServerConnectionDialog.OnSetServerConnectionListener
    @Override
    public void onSetServerConnection(String serverIp, int serverPort) {
        if (false == StringUtils.isNullOrEmpty(Dynamics.getServerIp())) {
            if ((false == Dynamics.getServerIp().equalsIgnoreCase(serverIp)) &&
                    (Dynamics.getServerPort() != serverPort)) {
                Dynamics.uUID = "";
            }
        } else {
            Dynamics.uUID = "";
        }
        PreferencesUtils.getInstance(context).setServerConncetionString(serverIp, serverPort);
        continueLoading();
    }
}

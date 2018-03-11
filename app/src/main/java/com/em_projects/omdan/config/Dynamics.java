package com.em_projects.omdan.config;

import android.content.Context;

import com.em_projects.omdan.utils.PreferencesUtils;

/**
 * Created by eyalmuchtar on 10/30/17.
 */

public class Dynamics {

    public static String serverURL = null;
    public static String uUID = null;
    private static Dynamics instance = null;
    private static String serverIp = null;
    private static int serverPort;
    private Context context;

    private Dynamics(Context context) {
        this.context = context;
    }

    public static Dynamics getInstance(Context context) {
        if (null == instance) {
            instance = new Dynamics(context);
        }
        return instance;
    }

    public static String getServerIp() {
        return serverIp;
    }

    public static void setServerIp(String serverIp) {
        Dynamics.serverIp = serverIp;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        Dynamics.serverPort = serverPort;
    }

    public String getCurrentRecordId() {
        return PreferencesUtils.getInstance(context).getRecordId();
    }

    public void setCurrentRecordId(String currentRecordId) throws Exception {
        PreferencesUtils.getInstance(context).setRecordId(currentRecordId);
    }
}

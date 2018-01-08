package com.em_projects.omdan.config;

import android.content.Context;

import com.em_projects.omdan.utils.PreferencesUtils;

/**
 * Created by eyalmuchtar on 10/30/17.
 */

public class Dynamics {

    private static Dynamics instance = null;
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

    public String getCurrentRecordId() {
        return PreferencesUtils.getInstance(context).getRecordId();
    }

    public void setCurrentRecordId(String currentRecordId) throws Exception {
        PreferencesUtils.getInstance(context).setRecordId(currentRecordId);
    }
}

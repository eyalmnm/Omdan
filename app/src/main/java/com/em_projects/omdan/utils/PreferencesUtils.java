package com.em_projects.omdan.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by eyalmuchtar on 30/10/2017.
 */

// @Ref: http://stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev
// @Ref: http://stackoverflow.com/a/40639277

public class PreferencesUtils {
    private static final String TAG = "PreferencesUtils";
    // Shared preferences file name
    private static final String PREF_NAME = "omdan";
    private static PreferencesUtils instance = null;
    // Shared preferences access components
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    // Application context
    private Context context;
    // Shared preferences working mode
    private int PRIVATE_MODE = 0;

    private PreferencesUtils(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public static PreferencesUtils getInstance(Context context) {
        if (null == instance) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    public String getRecordId() {
        return preferences.getString("recordId", null);
    }

    public void setRecordId(String recordId) throws Exception {
        if (false == StringUtils.isNullOrEmpty(recordId)) {
            editor.putString("recordId", recordId);
            editor.commit();
        } else {
            throw new Exception("Invalid Record Id");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        editor = null;
        preferences = null;
    }
}

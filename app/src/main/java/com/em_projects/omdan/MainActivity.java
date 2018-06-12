package com.em_projects.omdan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.em_projects.omdan.main.LoginAndPermissionsActivity;
import com.em_projects.omdan.utils.AppUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        TextView versionNumber = findViewById(R.id.version_number);
        versionNumber.setText(getString(R.string.app_version_format,
                AppUtils.getAppVersion(this), AppUtils.getAppVerionCode(this)));

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(context, IntroActivity.class);
                Intent intent = new Intent(context, LoginAndPermissionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
                finish();
            }
        }, 1500);
    }
}

package com.dolphpire.social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.dolphpire.social.preferences.AppDataPrefs.AppData;
import static com.dolphpire.social.preferences.AppDataPrefs.AppData_LoggedIn;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences splash_act;
    boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_act = getSharedPreferences(AppData, MODE_PRIVATE);
        loggedIn = splash_act.getBoolean(AppData_LoggedIn, false);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (loggedIn) {
                    Intent intent_home_activity = new  Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent_home_activity);
                } else {
                    Intent intent_home_activity = new  Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent_home_activity);
                }

            }

        }, 2000L);
    }

    @Override
    public void onBackPressed() {

    }
}

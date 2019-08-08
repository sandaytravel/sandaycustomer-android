package com.san.app.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.san.app.R;
import com.san.app.util.Constants;
import com.san.app.util.Pref;

import io.fabric.sdk.android.Fabric;

/**
 * Created by dharmesh on 3/8/17.
 */
public class SplashActivity extends BaseActivity {


    boolean isFinish = false;
    int _splashTime = 3000;

    LinearLayout splash_layout;
    Handler mSplashHandler;
    String decry;
    private Runnable mSplashRunnable = new Runnable() {
        @Override
        public void run() {
            isFinish = true;

            //  if (!TextUtils.isEmpty(Pref.getValue(SplashActivity.this, "isFirstTime", ""))) {
            startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            // } else {
            // startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            // }
            finish();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        splash_layout = (LinearLayout) findViewById(R.id.splash_layout);
        StartAnimations();
        //setLocale(this,"ja");
        //Configuration config = new Configuration();
        //config.locale = Locale.JAPANESE;
        //getResources().updateConfiguration(config, null);
        // Initialize Facebook sdk
        //  FacebookSdk.sdkInitialize(getApplicationContext());
        // AppEventsLogger.activateApp(this);
        Pref.setValue(SplashActivity.this, "from", "");
        createNotificationChannel();
    }


    @Override
    protected void onResume() {
        super.onResume();

        mSplashHandler = new Handler();
        mSplashHandler.postDelayed(mSplashRunnable, _splashTime);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinish) {
            if (mSplashRunnable != null) {
                if (mSplashHandler != null) {
                    mSplashHandler.removeCallbacks(mSplashRunnable);
                }
            }
        }

    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();

        splash_layout.clearAnimation();
        splash_layout.startAnimation(anim);


    }

    private void createNotificationChannel() {



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }

    }


}

package com.san.app.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.san.app.R;
import com.san.app.databinding.ActivityMainBinding;
import com.san.app.util.Constants;
import com.san.app.util.Pref;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    ActivityMainBinding mBinding;
    Context mContext;
    private String TAG = MainActivity.class.getSimpleName();
    public static MainActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = MainActivity.this;
        instance = this;
        prepareView();
        setOnClickListner();
    }

    private void prepareView() {
       // mBinding.tvSkip.setVisibility(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")) ? View.VISIBLE : View.GONE);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.e("DeviceToken","000   " + token);
                        Pref.setValue(MainActivity.this, Constants.PREF_DEVICE_TOKEN, token);
                    }
                });
    }

    private void setOnClickListner() {
        mBinding.tvLogin.setOnClickListener(this);
        mBinding.tvSignup.setOnClickListener(this);
        mBinding.tvSkip.setOnClickListener(this);
        mBinding.imgClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.tvLogin) {
            startActivity(new Intent(mContext, LoginActivity.class));
            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
        }
        if (view == mBinding.tvSignup) {
            startActivity(new Intent(mContext, SignupActivity.class));
            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
        }
        if (view == mBinding.tvSkip) {
            Pref.setValue(mContext, "isFirstTime", "1");
            startActivity(new Intent(mContext, DashboardActivity.class));
        }
        if(view == mBinding.imgClose){
            finish();
            overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }
}

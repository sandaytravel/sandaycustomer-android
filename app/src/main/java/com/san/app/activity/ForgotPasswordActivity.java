package com.san.app.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.san.app.R;
import com.san.app.databinding.ActivityForgotPasswordBinding;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPasswordActivity extends BaseActivity {

    ActivityForgotPasswordBinding mBinding;
    Context mContext;
    public static ForgotPasswordActivity instance = null;
    //variable declaration
    boolean isValid = true;
    private String TAG = ForgotPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        mContext = ForgotPasswordActivity.this;
        instance=this;
        prepareView();
        setOnClickListner();
    }

    private void prepareView() {

    }

    private void setOnClickListner() {


        mBinding.txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                }

                if (isValid) {
                    hideSoftKeyboard();
                    Utils.showProgressNormal(mContext);
                    callForgotPasswordAPI();
                }
            }
        });

        mBinding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
            }
        });


    }

    private void callForgotPasswordAPI() {
        ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.forgotpassword(mBinding.edtEmail.getText().toString().trim(),""+ Pref.getValue(mContext, Constants.APP_LANGUAGE,0));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        finish();
                    } else {
                        errorBody(response.errorBody());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }
}

package com.san.app.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.databinding.ActivitySignupBinding;
import com.san.app.model.UserDataModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.OnOneOffClickListener;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.DEVICE_TYPE;


public class SignupActivity extends BaseActivity {

    ActivitySignupBinding mBinding;
    Context mContext;
    //Facebook Variables Declaration
    CallbackManager callbackManager;
    public static SignupActivity instance = null;
    //variable declaration
    boolean isValid = true;
    private String TAG = SignupActivity.class.getSimpleName();
    private int year, month, day;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        mContext = SignupActivity.this;
        instance = this;
        prepareView();
        setOnClickListner();
    }

    private void prepareView() {
        // mBinding.tvSkip.setVisibility(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")) ? View.VISIBLE : View.GONE);
        // if(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")))
        // Utils.getKeyboardOpenorNot(mContext, mBinding.tvSkip, mBinding.tvSkip);

    }

    private void setOnClickListner() {

        mBinding.edtBirthday.setOnClickListener(new OnOneOffClickListener() {
            @Override
            public void onSingleClick(View v) {
                hideSoftKeyboard();
                datePicker();
            }
        });

        mBinding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginActivity.class));
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                finish();
            }
        });

        mBinding.fmFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null)
                    LoginManager.getInstance().logOut();
                mBinding.loginButton.performClick();
                LoginWithFacebook(); // Initialize Facebook Button and register callback url.
            }
        });

        mBinding.txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtName.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_name), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtBirthday.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_birthday), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_password), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtRepeatPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_repeat_password), R.mipmap.red_cross_er);
                } else if (!mBinding.edtPassword.getText().toString().equals(mBinding.edtRepeatPassword.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.both_password_not_match), R.mipmap.red_cross_er);
                }/* else if(!mBinding.switchTerms.isChecked()){
                    isValid=false;
                    customToastError(getString(R.string.error),getString(R.string.please_accept_terms_condition),R.mipmap.red_cross_er);
                }*/


                if (isValid) {
                    hideSoftKeyboard();
                    Utils.showProgress(mContext);
                    callSignupAPI(mBinding.edtName.getText().toString(), mBinding.edtEmail.getText().toString(), mBinding.edtPassword.getText().toString());
                    // startActivity(new Intent(mContext,DashboardActivity.class));
                }
            }
        });

        mBinding.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(mContext, "isFirstTime", "1");
                startActivity(new Intent(mContext, DashboardActivity.class));
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

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;

                // Show selected date
                int month1 = month + 1;
                mBinding.edtBirthday.setText(new StringBuilder().append(day < 10 ? "0" + day : day).append(" ").append(Utils.monthName(month1)).append(" ").append(year));
                selectedDate = "" + new StringBuilder().append(year).append("-").append(month1 < 10 ? "0" + month1 : month1).append("-").append(day < 10 ? "0" + day : day).append(" ");
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void callSignupAPI(String name, String email, String pwd) {
        ApiInterface apiService = ApiClient.getClient(SignupActivity.this).create(ApiInterface.class);

        Call<ResponseBody> call = apiService.customer_registration(name, email, pwd, Pref.getValue(mContext, Constants.PREF_DEVICE_TOKEN, ""), DEVICE_TYPE,selectedDate,""+ Pref.getValue(mContext, Constants.APP_LANGUAGE,0));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e("TestData","000   " + jsonObject.toString());
                        Gson gson = new Gson();
                        UserDataModel userData = gson.fromJson(jsonObject.optJSONObject("payload").toString(), UserDataModel.class);
                        new Pref(mContext).setUserInfo(userData);
                        Pref.setValue(mContext, Constants.PREF_USER_PROFILE_PIC, userData.profile_pic);
                        Pref.setValue(mContext, Constants.PREF_APP_TOKEN, jsonObject.optString("_token"));
                        Pref.setValue(mContext, "isFreshLogin", "yes");
                        if (MainActivity.instance != null) MainActivity.instance.finish();
                        if (ForgotPasswordActivity.instance != null)
                            ForgotPasswordActivity.instance.finish();
                        if (LoginActivity.instance != null) LoginActivity.instance.finish();
                        finish();
                        Log.e(TAG, "response " + jsonObject.toString());

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


    private void LoginWithFacebook() {

        callbackManager = CallbackManager.Factory.create();
        //Add permission what all information required.
        mBinding.loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        // If you are using in a fragment, call loginButton.setFragment(this);
        // Callback registration
        mBinding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("TestData", "000  " + object.toString());
                        String email = response.getJSONObject().optString("email");
                        String fb_id = response.getJSONObject().optString("id");
                        String name = response.getJSONObject().optString("name");
                        String profileImageUrl = ImageRequest.getProfilePictureUri(object.optString("id"), 200, 200).toString();

                        if (!email.equals("")) {
                            Utils.showProgress(mContext);
                            callLoginAPI(1, name, email, "", profileImageUrl, fb_id);
                        } else {
                            Toast.makeText(mContext, "We can't find your email address.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,first_name,last_name,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Utils.dismissProgress();
            }

            @Override
            public void onError(FacebookException exception) {
                Utils.dismissProgress();
            }
        });

    }


    private void callLoginAPI(int type, String name, String email, String pwd, final String profile_pic, final String fb_id) {

        ApiInterface apiService = ApiClient.getClient(SignupActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.facebooklogin(email, name, "", profile_pic, fb_id, Pref.getValue(mContext, Constants.PREF_DEVICE_TOKEN, ""), DEVICE_TYPE,""+ Pref.getValue(mContext, Constants.APP_LANGUAGE,0));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);

                        Gson gson = new Gson();
                        UserDataModel userData = gson.fromJson(jsonObject.optJSONObject("payload").toString(), UserDataModel.class);
                        new Pref(mContext).setUserInfo(userData);
                        Pref.setValue(mContext, Constants.PREF_USER_PROFILE_PIC, userData.profile_pic);
                        Pref.setValue(mContext, Constants.PREF_APP_TOKEN, jsonObject.optString("_token"));
                        Pref.setValue(mContext, "isFreshLogin", "yes");
                        if (MainActivity.instance != null) MainActivity.instance.finish();
                        if (ForgotPasswordActivity.instance != null)
                            ForgotPasswordActivity.instance.finish();
                        if (LoginActivity.instance != null) LoginActivity.instance.finish();
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
                Log.e(TAG, "fail " + t.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Utils.showProgress(mContext);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }
}

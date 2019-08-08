package com.san.app.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.san.app.databinding.ActivityLoginBinding;
import com.san.app.model.UserDataModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.DEVICE_TYPE;
import static com.san.app.util.Utils.setLocale;


public class LoginActivity extends BaseActivity {

    ActivityLoginBinding mBinding;
    Context mContext;
    public static LoginActivity instance = null;
    //Facebook Variables Declaration
    CallbackManager callbackManager;

    //google
    private static final int RC_SIGN_IN = 007;
    // private GoogleApiClient mGoogleApiClient;

    //variable declaration
    boolean isValid = true;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext = LoginActivity.this;
        instance = this;
        prepareView();
        setOnClickListner();
    }

    private void prepareView() {



        //if(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")))
        // Utils.getKeyboardOpenorNot(mContext, mBinding.tvSkip, mBinding.tvSkip);
        //mBinding.tvSkip.setVisibility(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")) ? View.VISIBLE : View.GONE);
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mBinding.btnGoogleLogin.setScopes(gso.getScopeArray());*/
    }

    private void setOnClickListner() {
        mBinding.fmFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();
                mBinding.loginButton.performClick();
                LoginWithFacebook(); // Initialize Facebook Button and register callback url.
            }
        });

        /*mBinding.lnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.btnGoogleLogin.performClick();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    Log.e("TestLogin", "000  " + status.toString());
                                }
                            });
                }

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });*/

        mBinding.txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SignupActivity.class));
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                finish();
            }
        });

        mBinding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ForgotPasswordActivity.class));
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
            }
        });

        mBinding.txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_password), R.mipmap.red_cross_er);
                }

                if (isValid) {
                    hideSoftKeyboard();
                    Utils.showProgress(mContext);
                    callLoginAPI(0, "", mBinding.edtEmail.getText().toString(), mBinding.edtPassword.getText().toString(), "", "");
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

    private void LoginWithFacebook() {

        callbackManager = CallbackManager.Factory.create();
        //Add permission what all information required.
        mBinding.loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        // If you are using in a fragment, call loginButton.setFragment(this);
        // Callback registration
        mBinding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

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

        ApiInterface apiService = ApiClient.getClient(LoginActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call;
        if (type == 0) {
            call = apiService.login(email, pwd, Pref.getValue(mContext, Constants.PREF_DEVICE_TOKEN, ""), DEVICE_TYPE,""+Pref.getValue(mContext,Constants.APP_LANGUAGE,0));
        } else {
            //Log.e("Profile", "000  " + profile_pic);
            call = apiService.facebooklogin(email, name, "", profile_pic, fb_id, Pref.getValue(mContext, Constants.PREF_DEVICE_TOKEN, ""), DEVICE_TYPE,""+Pref.getValue(mContext,Constants.APP_LANGUAGE,0));
        }
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
                        setLocale(LoginActivity.this, userData.language_id.equals("1") ? "en" : userData.language_id.equals("2") ? "ja" : userData.language_id.equals("3") ? "ko" : "en",1);
                        if (MainActivity.instance != null) MainActivity.instance.finish();
                        if (ForgotPasswordActivity.instance != null)
                            ForgotPasswordActivity.instance.finish();
                        if (SignupActivity.instance != null) SignupActivity.instance.finish();
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

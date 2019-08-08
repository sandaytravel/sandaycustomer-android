package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.NearyByDestinationAdapter;
import com.san.app.databinding.FragmentChangePasswordBinding;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordFragment extends BaseFragment {


    //class object declaration..
    FragmentChangePasswordBinding mBinding;
    View rootView;
    Context mContext;
    NearyByDestinationAdapter nearyByDestinationAdapter;

    //variable declaration.
    private String TAG = ChangePasswordFragment.class.getSimpleName();
    private boolean isValid = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_change_password, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            prepareView();
            setUp();
        }
        return rootView;
    }

    private void prepareView() {


    }


    private void setUp() {
        mBinding.tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtOldPwd.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_old_pwd), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtnewPwd.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_new_pwd), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtConfirmPwd.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_confirm_new_pwd), R.mipmap.red_cross_er);
                } else if (!mBinding.edtnewPwd.getText().toString().equals(mBinding.edtConfirmPwd.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.both_pwd_doest_not_match), R.mipmap.red_cross_er);
                }

                if (isValid) {
                    Utils.showProgress(mContext);
                    callChangePasswordAPI();
                }
            }
        });

        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity(), rootView);
    }

    private void callChangePasswordAPI() {
        HashMap<String, String> data = new HashMap<>();
        data.put("old_password", mBinding.edtOldPwd.getText().toString().trim());
        data.put("new_password", mBinding.edtConfirmPwd.getText().toString().trim());
        //data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.changepassword(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        getActivity().getSupportFragmentManager().popBackStack();

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

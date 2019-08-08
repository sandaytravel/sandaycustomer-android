package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.databinding.FragmentAboutUsBinding;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutUsFragment extends BaseFragment {


    //class object declaration..
    FragmentAboutUsBinding mBinding;
    View rootView;
    Context mContext;

    //variable declaration.
    private String TAG = AboutUsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_about_us, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
        }
        return rootView;
    }

    private void setUp() {
        WebSettings webSettings = mBinding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Utils.showProgressNormal(mContext);
        callAboutUsAPI();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callAboutUsAPI();
            }
        }, 400);*/



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


    /**
     * function for get about us detail
     */
    private void callAboutUsAPI() {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.aboutUs();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e(TAG, "response " + response.toString());
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.optInt("code") == 200) {
                            JSONObject dataJsonObj = jsonObject.optJSONObject("payload");
                            setWebViewSetting(mBinding.webview, dataJsonObj.optString("content"));
                        } else {
                            String message = jsonObject.optString("message");
                            new FieldsValidator(mContext).customToast(message, R.mipmap.cancel_toast_new);
                        }
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
                Utils.dismissProgress();
            }
        });

    }

    private void setWebViewSetting(WebView webView, String webData) {
        //mBinding.webview.loadData(webData, "text/html", "UTF-8");
        /*webView.getSettings().setDefaultFontSize(42);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_white));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);*/

        webView.loadDataWithBaseURL("http://localhost",webData, "text/html; video/mpeg", "UTF-8","");
        webView.getSettings().setDefaultFontSize(38);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);

        /*mBinding.webview.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        mBinding.webview.clearCache(true);
        mBinding.webview.clearHistory();
        mBinding.webview.getSettings().setJavaScriptEnabled(true);
        mBinding.webview.setHorizontalScrollBarEnabled(false);
        mBinding.webview.loadData(webData, "text/html", "UTF-8");*/
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

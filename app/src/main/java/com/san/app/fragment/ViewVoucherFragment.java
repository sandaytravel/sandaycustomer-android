package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.databinding.FragmentAboutUsBinding;


public class ViewVoucherFragment extends BaseFragment {


    //class object declaration..
    FragmentAboutUsBinding mBinding;
    View rootView;
    Context mContext;
    String voucher_url;

    //variable declaration.
    private String TAG = ViewVoucherFragment.class.getSimpleName();

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
        if (getArguments() != null) voucher_url = getArguments().getString("voucher_url");
        mBinding.tvTitle.setText("Voucher");
        WebSettings webSettings = mBinding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mBinding.webview.loadUrl(voucher_url);
        //setWebViewSetting(mBinding.webview, voucher_url);

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


    private void setWebViewSetting(WebView webView, String webData) {
        webView.loadUrl(webData);
        webView.getSettings().setDefaultFontSize(42);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_white));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
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

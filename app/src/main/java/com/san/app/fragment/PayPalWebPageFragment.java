package com.san.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.databinding.FragmentAboutUsBinding;
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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;
import static com.san.app.util.Utils.setCartBadgeCount;


public class PayPalWebPageFragment extends BaseFragment {


    //class object declaration..
    FragmentAboutUsBinding mBinding;
    View rootView;
    Context mContext;
    Timer timer;
    //variable declaration.
    private String TAG = PayPalWebPageFragment.class.getSimpleName();
    private String transaction_id = "";
    private String webviewurl = "";
    private String payment_status = "";
    private String fromWhere = "";

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
        if (getArguments() != null) {
            transaction_id = getArguments().getString("transaction_id");
            webviewurl = getArguments().getString("webviewurl");
            fromWhere = getArguments().getString("fromWhere");
        }
        mBinding.tvTitle.setText("Payment");
        mBinding.webview.loadUrl(webviewurl);
        mBinding.webview.setWebChromeClient(new MyWebChromeClient(mContext));
        mBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Utils.showProgressNormal(mContext);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Utils.dismissProgress();
                mBinding.webview.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Utils.dismissProgress();
            }
        });
        mBinding.webview.clearCache(true);
        mBinding.webview.clearHistory();
        mBinding.webview.getSettings().setJavaScriptEnabled(true);
        mBinding.webview.setHorizontalScrollBarEnabled(false);


        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payment_status.equals("Pending")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setMessage(R.string.are_you_sure_you_want_cancel_process)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setChecked(true);
                                    changeFragment_back(new MyBookingFragment());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setChecked(true);
                    changeFragment_back(new MyBookingFragment());
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                callPaymentStatusCheckAPI();
            }
        }, 0, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    private void callPaymentStatusCheckAPI() {
        HashMap<String, String> data = new HashMap<>();
        data.put("transaction_id", transaction_id);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        if(fromWhere.equals("cart"))data.put("is_cart", "1");
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.checkpaymentstatus(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        payment_status = jsonObject.optString("payment_status");

                        if (payment_status.equals("Completed")) {
                            if (fromWhere.equals("cart")) {
                                Pref.setValue(mContext, Constants.TAG_CART_BADGE_COUNT, 0);
                            }
                            setCartBadgeCount(mContext, ((DashboardActivity) mContext).tvCartBadge);
                            new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                            if(getActivity() != null) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();

                                }
                            }
                            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setChecked(true);
                            changeFragment_back(new MyBookingFragment());
                        } else if (payment_status.equals("Failed")) {
                            Toast.makeText(mContext, "Payment Failed!", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        //Log.e("CheckPayment", "000   " + jsonObject.toString());
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


    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
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
                        if (payment_status.equals("Pending")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setMessage(R.string.are_you_sure_you_want_cancel_process)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setChecked(true);
                                            changeFragment_back(new MyBookingFragment());
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            dialog.dismiss();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } else {
                            //getActivity().getSupportFragmentManager().popBackStack();
                            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setChecked(true);
                            changeFragment_back(new MyBookingFragment());
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

package com.san.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.MyBookingMainListAdapter;
import com.san.app.databinding.FragmentMybookingBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.MyOrderListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.BottomNavigationBehavior;
import com.san.app.util.Constants;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;


public class MyBookingFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentMybookingBinding mBinding;
    MyBookingMainListAdapter myBookingListAdapter;
    View rootView;
    Context mContext;
    ArrayList<MyOrderListModel.Payload> myOrderListModelsList = new ArrayList<>();
    MyOrderListModel myOrderListModel;
    LinearLayoutManager mLayoutManager;
    //variable declaration.
    private String TAG = MyBookingFragment.class.getSimpleName();
    private int pageNumber = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mybooking, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
        } else {
            BottomNavigationBehavior bottomNavigationBehavior = new BottomNavigationBehavior();
            bottomNavigationBehavior.showBottomNavigationView(((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
        }
        //setLocale(mContext, Pref.getValue(mContext,Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en");
        return rootView;
    }


    private void setUp() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);


        if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
            //mBinding.lnMain.setVisibility(View.VISIBLE);
            mBinding.lnGuestView.setVisibility(View.GONE);

            Utils.showProgress(mContext);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    callOrderList(); //get order list
                }
            }, 100);
        } else {
            mBinding.lnMain.setVisibility(View.GONE);
            mBinding.lnGuestView.setVisibility(View.VISIBLE);
        }


        mBinding.txtSignin.setOnClickListener(this);

        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                myOrderListModelsList.clear();
                pageNumber = 1;
                callOrderList(); //get cart list data
            }
        });

        mBinding.nestedMain.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                View view = (View)mBinding.nestedMain.getChildAt(mBinding.nestedMain.getChildCount() - 1);

                int diff = (view.getBottom() - (mBinding.nestedMain.getHeight() + mBinding.nestedMain
                        .getScrollY()));

                if (diff == 0) {
                   // Log.e("TestValide","999   " + pageNumber);
                    // your pagination code
                    if (pageNumber != 1) {
                        mBinding.progressBar.setVisibility(View.VISIBLE);
                        mBinding.swipeContainer.setPadding(0, 0, 0, mBinding.progressBar.getHeight() + 40);
                        //((DashboardActivity) mContext).hideShowBottomNav(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callOrderList(); //get noti list
                            }
                        }, 100);

                    }
                }
            }
        });

        /*mBinding.rvBookingList.addOnScrollListener(new com.san.app.util.EndlessRecyclerOnScrollListener(mLayoutManager) {

            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1) {
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    mBinding.swipeContainer.setPadding(0, 0, 0, mBinding.progressBar.getHeight() + 40);
                    //((DashboardActivity) mContext).hideShowBottomNav(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callOrderList(); //get noti list
                        }
                    }, 500);

                }
            }
        });*/

    }


    @Override
    public void onClick(View view) {
        if (view == mBinding.txtSignin) {
            openLoginView(mContext);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.hideKeyboard(getActivity());
        ((DashboardActivity) mContext).hideShowBottomNav(true);
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setChecked(true);
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "isFreshLogin", ""))) {
            Pref.setValue(mContext, "isFreshLogin", "");
            changeFragment(new MyBookingFragment());
        }

    }


    @Override
    public void onPause() {
        super.onPause();

    }


    private void callOrderList() {
        final HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + pageNumber);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<MyOrderListModel> call = apiService.vieworder(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<MyOrderListModel>() {
            @Override
            public void onResponse(Call<MyOrderListModel> call, Response<MyOrderListModel> response) {
                Utils.dismissProgress();
                mBinding.lnMain.setVisibility(View.VISIBLE);
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.swipeContainer.setPadding(0, 0, 0, 0);
                ((DashboardActivity) mContext).hideShowBottomNav(true);
                if (response.body() != null) {
                    myOrderListModel = response.body();

                    if (myOrderListModel.getPayload().size() > 0) {
                        pageNumber = myOrderListModel.getPage();
                        myOrderListModelsList.addAll(myOrderListModel.getPayload());
                        // myBookingListAdapter.notifyDataSetChanged();
                        //my booking list
                        myBookingListAdapter = new MyBookingMainListAdapter(mContext, myOrderListModelsList);
                        mBinding.rvBookingList.setLayoutManager(mLayoutManager);
                        mBinding.rvBookingList.setItemAnimator(new DefaultItemAnimator());
                        myBookingListAdapter.onClickPosition(onClickPosition);
                        mBinding.rvBookingList.setAdapter(myBookingListAdapter);
                    } else {
                        pageNumber = 1;
                    }
                    mBinding.lnMain.setVisibility(myOrderListModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoData.setVisibility(myOrderListModelsList.size() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<MyOrderListModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
                Utils.dismissProgress();
            }
        });
    }

    OnClickPosition onClickPosition=new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            callCheckPaymentStatusAPI(position,myOrderListModelsList.get(position).getTransactionNumber());
        }
    };

    private void callCheckPaymentStatusAPI(final Integer position, final String transactionNumber) {

        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        data.put("order_number", "" + transactionNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.checkproceedtopay(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.optInt("invalid") == 0){
                            PayPalWebPageFragment fragment = new PayPalWebPageFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("transaction_id", "" + myOrderListModelsList.get(position).getTransactionId());
                            bundle.putString("webviewurl", "" + myOrderListModelsList.get(position).getWebviewurl());
                            bundle.putString("fromWhere", "booking");
                            fragment.setArguments(bundle);

                            FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            transaction.replace(R.id.frame, fragment, "fragment");
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }else{
                            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setMessage(R.string.dialog_msg_remove_order_confirmation).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    callProceedtoValidRemoveOrderAPI(position,transactionNumber);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert).show();
                        }
                        Log.e(TAG, "response " + jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.dismissProgress();
            }
        });
    }

    private void callProceedtoValidRemoveOrderAPI(final Integer position, String transactionNumber) {

        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        data.put("order_number", "" + transactionNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.proceedtovalid(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        myOrderListModelsList.clear();
                        pageNumber = 1;
                        callOrderList(); //get cart list data
                        //Log.e(TAG, "response111 " + jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.dismissProgress();
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
                        getActivity().finishAffinity();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

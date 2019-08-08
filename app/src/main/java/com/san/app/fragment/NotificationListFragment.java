package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
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
import com.san.app.adapter.NotificationListAdapter;
import com.san.app.databinding.FragmentNotificationListBinding;
import com.san.app.model.NotificationListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
import com.san.app.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;


public class NotificationListFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentNotificationListBinding mBinding;
    private NotificationListAdapter notificationListAdapter;
    View rootView;
    Context mContext;
    private ArrayList<NotificationListModel.Payload> notiListModelsList = new ArrayList<>();
    NotificationListModel notificationModel;
    LinearLayoutManager mLayoutManager;
    //variable declaration.
    private String TAG = NotificationListFragment.class.getSimpleName();
    private int pageNumber = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_list, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
        }
        return rootView;
    }


    private void setUp() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        //notifcation list
        notificationListAdapter = new NotificationListAdapter(mContext, notiListModelsList);
        mBinding.rvNotificationList.setLayoutManager(mLayoutManager);
        mBinding.rvNotificationList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvNotificationList.setAdapter(notificationListAdapter);

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callNotiList(); //get noti list
            }
        }, 100);

        mBinding.rvNotificationList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!TextUtils.isEmpty(notiListModelsList.get(position).getOrderId())) {
                    BookingDetailPageFragment fragment = new BookingDetailPageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", notiListModelsList.get(position).getOrderId());
                    bundle.putString("is_from", "notification");
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                }
            }
        }));

        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                notiListModelsList.clear();
                pageNumber = 1;
                callNotiList(); //get noti list data
            }
        });

        mBinding.nestedMain.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) mBinding.nestedMain.getChildAt(mBinding.nestedMain.getChildCount() - 1);

                int diff = (view.getBottom() - (mBinding.nestedMain.getHeight() + mBinding.nestedMain.getScrollY()));

                if (diff == 0) {
                    //Log.e("TestValide","999   " + pageNumber);
                    // your pagination code
                    if (pageNumber != 1) {
                        mBinding.swipeContainer.setPadding(0, 0, 0, mBinding.progressBar.getHeight());
                        mBinding.progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callNotiList(); //get noti list
                            }
                        }, 100);

                    }
                }
            }
        });


       /* mBinding.rvNotificationList.addOnScrollListener(new com.san.app.util.EndlessRecyclerOnScrollListener(mLayoutManager) {

            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1) {
                    mBinding.swipeContainer.setPadding(0,0,0,mBinding.progressBar.getHeight());
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callNotiList(); //get noti list
                        }
                    }, 500);

                }
            }
        });*/

        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }


    @Override
    public void onClick(View view) {

    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.hideKeyboard(getActivity());
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }


    @Override
    public void onPause() {
        super.onPause();

    }


    private void callNotiList() {
        final HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + pageNumber);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        Log.e("TestData", "000   " + pageNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<NotificationListModel> call = apiService.customernotification(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<NotificationListModel>() {
            @Override
            public void onResponse(Call<NotificationListModel> call, Response<NotificationListModel> response) {
                Utils.dismissProgress();
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.lnMain.setVisibility(View.VISIBLE);
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.swipeContainer.setPadding(0, 0, 0, 0);
                //  notiListModelsList.clear();
                if (response.body() != null) {
                    notificationModel = response.body();

                    if (notificationModel.getPayload().size() > 0) {
                        pageNumber = notificationModel.getPage();
                        notiListModelsList.addAll(notificationModel.getPayload());
                        notificationListAdapter.notifyDataSetChanged();
                    } else {
                        pageNumber = 1;
                    }
                    mBinding.rvNotificationList.setVisibility(notiListModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoFilterData.setVisibility(notiListModelsList.size() > 0 ? View.GONE : View.VISIBLE);
                    Log.e("TestData", "1111   " + notiListModelsList.size());
                } else {
                    errorBody(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<NotificationListModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
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

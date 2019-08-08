package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.WishListActivitiesAdapter;
import com.san.app.databinding.FragmentWishlistBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.WishListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;


public class WishlistFragment extends BaseFragment {


    //class object declaration..
    public FragmentWishlistBinding mBinding;
    View rootView;
    Context mContext;
    WishListActivitiesAdapter wishListActivitiesAdapter;
    List<WishListModel.Payload> wishListModelList = new ArrayList<>();
    WishListModel wishListModel;
    //variable declaration.
    private String TAG = WishlistFragment.class.getSimpleName();
    private boolean isValid = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_wishlist, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
       // }
        return rootView;
    }


    private void setUp() {

        //popular activities list
        wishListActivitiesAdapter = new WishListActivitiesAdapter(mContext, wishListModelList);
        mBinding.rvPopularActList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPopularActList.setItemAnimator(new DefaultItemAnimator());
        wishListActivitiesAdapter.onClickPosition(onClickPosition);
        mBinding.rvPopularActList.setAdapter(wishListActivitiesAdapter);

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callWishList(); //get hot destination
            }
        }, 100);

    }

    private void setOnClickListener() {

        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                wishListModelList.clear();
                callWishList(); //get hot destination
            }
        });

       /* mBinding.rvPopularActList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        }));*/

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

    private void callWishList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        //data.put("activity_id", "" + activityId);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<WishListModel> call = apiService.whishlist(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""),data);
        call.enqueue(new Callback<WishListModel>() {
            @Override
            public void onResponse(Call<WishListModel> call, Response<WishListModel> response) {
                Utils.dismissProgress();
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.lnMain.setVisibility(View.VISIBLE);
                wishListModelList.clear();
                if (response.body() != null) {
                    wishListModel = response.body();
                    wishListModelList.addAll(wishListModel.getPayload());
                    wishListActivitiesAdapter.notifyDataSetChanged();
                    mBinding.swipeContainer.setVisibility(wishListModelList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoFilterData.setVisibility(wishListModelList.size() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<WishListModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
                Utils.dismissProgress();
            }
        });
    }

    OnClickPosition onClickPosition = new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            Utils.showProgressNormal(mContext);
            callActivityAddRemoveWishListAPI(position);
        }
    };

    private void callActivityAddRemoveWishListAPI(final Integer position) {
            HashMap<String, String> data = new HashMap<>();
            data.put("activity_id", "" + wishListModelList.get(position).getActivitymainId());
        data.put("activity_lang_id", "" + wishListModelList.get(position).getActivityWhishlistId());
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            Call<ResponseBody> call = apiService.add_remove_whishlist(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Utils.dismissProgress();
                        if (response.isSuccessful()) {
                            String res = response.body().string();
                            JSONObject jsonObject = new JSONObject(res);
                            wishListActivitiesAdapter.removeAt(position);
                            mBinding.swipeContainer.setVisibility(wishListModelList.size() > 0 ? View.VISIBLE : View.GONE);
                            mBinding.lnNoFilterData.setVisibility(wishListModelList.size() > 0 ? View.GONE : View.VISIBLE);
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

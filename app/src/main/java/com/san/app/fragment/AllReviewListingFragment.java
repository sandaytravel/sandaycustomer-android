package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.adapter.AllReviewListAdapter;
import com.san.app.databinding.FragmentAllReviewListingBinding;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
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


public class AllReviewListingFragment extends BaseFragment {

    //class object declaration..
    private List<String> mStringList;
    FragmentAllReviewListingBinding mBinding;
    View rootView;
    Context mContext;
    AllReviewListAdapter allReviewListAdapter;

    ArrayList<ViewActivityDetailModel.Review> reviewsArrayList = new ArrayList<>();
    ViewActivityDetailModel.Review reviewModel;
    LinearLayoutManager mLayoutManager;
    //variable declaration.
    private String TAG = AllReviewListingFragment.class.getSimpleName();
    private boolean isValid = true;
    private int pageNumber = 1;
    private int activity_id = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_all_review_listing, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            mLayoutManager=new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            setUp();
        }
        return rootView;
    }


    private void setUp() {
        if (getArguments() != null) {
            activity_id = getArguments().getInt("activity_id");
        }

        //review  list
        allReviewListAdapter = new AllReviewListAdapter(mContext, reviewsArrayList,1);
        mBinding.rvAllReviewList.setLayoutManager(mLayoutManager);
        mBinding.rvAllReviewList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvAllReviewList.setAdapter(allReviewListAdapter);


        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callAllReviewList(); //get ctyDetail
            }
        }, 100);


        mBinding.rvAllReviewList.addOnScrollListener(new com.san.app.util.EndlessRecyclerOnScrollListener(mLayoutManager) {

            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1) {
                    mBinding.rvAllReviewList.setPadding(0,0,0,50);
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callAllReviewList(); //get noti list
                        }
                    }, 100);

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

    }


    private void callAllReviewList() {
        final HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + activity_id);
        data.put("page", "" + pageNumber);
        data.put("language_id", ""+ Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.allActivityReview(data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Utils.dismissProgress();
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.rlMain.setVisibility(View.VISIBLE);
                    mBinding.rvAllReviewList.setPadding(0,0,0,0);
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e(TAG, "response " + jsonObject.toString());

                        JSONObject payLoadObj = jsonObject.optJSONObject("payload");
                        Gson gson = new Gson();
                        if(payLoadObj.optJSONArray("reviews").length() >0) {
                            pageNumber = jsonObject.optInt("page");
                            for (int i = 0; i < payLoadObj.optJSONArray("reviews").length(); i++) {
                                JSONObject dataObj = payLoadObj.optJSONArray("reviews").getJSONObject(i);
                                reviewModel = gson.fromJson(dataObj.toString(), ViewActivityDetailModel.Review.class);
                                reviewsArrayList.add(reviewModel);
                            }
                            allReviewListAdapter.notifyDataSetChanged();
                        }else{
                            pageNumber =1;
                        }

                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
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
                Log.e("ErroData", "000  " + t.getMessage());
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
                        getActivity().getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

package com.san.app.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.PopularActivitiesHomeAdapter;
import com.san.app.adapter.PopularDestinationHomeAdapter;
import com.san.app.databinding.FragmentExploreHomeNewBinding;
import com.san.app.model.CitiesListModel;
import com.san.app.model.ViewCityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.BottomNavigationBehavior;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
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
import static com.san.app.util.Utils.setCartBadgeCount;


public class ExploreHomeFragment extends BaseFragment {


    //class object declaration..
    FragmentExploreHomeNewBinding mBinding;
    View rootView;
    Context mContext;
    PopularDestinationHomeAdapter popularDestinationAdapter;
    PopularActivitiesHomeAdapter popularActivitiesHomeAdapter;
    CitiesListModel citiesListModel;
    ViewCityDetailModel.PopularActivity popularActivityModel;
    List<CitiesListModel> popularDestinationList = new ArrayList<>();
    List<ViewCityDetailModel.PopularActivity> popularActivityArrayList = new ArrayList<>();
    ArrayList<String> imgHomeList = new ArrayList<>();
    //variable declaration.
    private String TAG = ExploreHomeFragment.class.getSimpleName();
    private boolean isValid = true;
    private int countOfImage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_explore_home_new, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        }else{
            BottomNavigationBehavior bottomNavigationBehavior=new BottomNavigationBehavior();
            bottomNavigationBehavior.showBottomNavigationView(((DashboardActivity)mContext).mBinding.moreMenuBottomNavigation);
        }
        //setLocale(mContext, Pref.getValue(mContext, Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en");
        return rootView;
    }

    private void setOnClickListener() {
        mBinding.tvShowAllDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(mContext, "from", "showAll");
                changeFragment_back(new DestinationHomeNewFragment());

            }
        });

        mBinding.rvPopularDestList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("city_id", popularDestinationList.get(position).city_id);
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));

        mBinding.rvPopularActList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", popularActivityArrayList.get(position).getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));

        mBinding.lnSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchCityAttractionFragment fragment = new SearchCityAttractionFragment();
                Pref.setValue(mContext,"popular_dest_list",new Gson().toJson(popularDestinationList));
                Pref.setValue(mContext,"popular_activity_list",new Gson().toJson(popularActivityArrayList));
                changeFragment_up_bottom(fragment);
            }
        });

    }


    @SuppressLint("NewApi")
    private void setUp() {
        Pref.setValue(mContext, "from_edit", ""); //for edit cart
        //popular destination list
        popularDestinationAdapter = new PopularDestinationHomeAdapter(mContext, popularDestinationList);
        //mBinding.rvPopularDestList.setLayoutManager(new GridLayoutManager(mContext, 2));
        mBinding.rvPopularDestList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvPopularDestList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPopularDestList.setNestedScrollingEnabled(false);
        mBinding.rvPopularDestList.setAdapter(popularDestinationAdapter);

        //popular activity list
        popularActivitiesHomeAdapter = new PopularActivitiesHomeAdapter(mContext, popularActivityArrayList);
        mBinding.rvPopularActList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPopularActList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPopularActList.setAdapter(popularActivitiesHomeAdapter);


        Utils.showProgress(mContext);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callExploreDataAPI(); //get hot destination
            }
        }, 400);*/
        callExploreDataAPI(); //get hot destination


    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(true);
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_explore).setChecked(true);
        if (imgHomeList.size() > 0) startAnimDashImage();
        hideSoftKeyboard();
    }


    /**
     * function for get home data
     */
    private void callExploreDataAPI() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.exploreHome(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""),data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e(TAG, "response " + response.toString());
                    Utils.dismissProgress();
                    mBinding.lnMain.setVisibility(View.VISIBLE);
                    popularDestinationList.clear();
                    popularActivityArrayList.clear();
                    imgHomeList.clear();
                    if (response.body() != null) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.optInt("code") == 200) {
                            JSONObject dataJsonObj = jsonObject.optJSONObject("payload");
                            if(!TextUtils.isEmpty(dataJsonObj.optString("title")))mBinding.tvTitle.setText(dataJsonObj.optString("title"));
                            if(!TextUtils.isEmpty(dataJsonObj.optString("description")))mBinding.tvDesc.setText(dataJsonObj.optString("description"));
                            Pref.setValue(mContext, Constants.TAG_CART_BADGE_COUNT, Integer.parseInt(dataJsonObj.optString("cart_total")));
                            for (int i = 0; i < dataJsonObj.optJSONArray("images").length(); i++) {
                                JSONObject imageObj = dataJsonObj.optJSONArray("images").getJSONObject(i);
                                imgHomeList.add(imageObj.optString("fullsized_image"));
                            }
                            Gson gson = new Gson();
                            for (int i = 0; i < dataJsonObj.optJSONArray("popular_destination").length(); i++) {
                                citiesListModel = gson.fromJson(dataJsonObj.optJSONArray("popular_destination").optJSONObject(i).toString(), CitiesListModel.class);
                                if (citiesListModel.activity_count > 0) popularDestinationList.add(citiesListModel);
                            }

                            for (int i = 0; i < dataJsonObj.optJSONArray("popular_activity").length(); i++) {
                                popularActivityModel = gson.fromJson(dataJsonObj.optJSONArray("popular_activity").optJSONObject(i).toString(), ViewCityDetailModel.PopularActivity.class);
                                popularActivityArrayList.add(popularActivityModel);
                            }
                            popularDestinationAdapter.notifyDataSetChanged();
                            popularActivitiesHomeAdapter.notifyDataSetChanged();

                            mBinding.lnPopularDest.setVisibility(popularDestinationList.size() > 0 ? View.VISIBLE : View.GONE);
                            mBinding.lnPopularAct.setVisibility(popularActivityArrayList.size() > 0 ? View.VISIBLE : View.GONE);
                            setCartBadgeCount(mContext, ((DashboardActivity) mContext).tvCartBadge);

                            startAnimDashImage();
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

    private void startAnimDashImage() {
        if(imgHomeList.size()>0) {
            Glide.with(mContext).load(countOfImage > 0 ? imgHomeList.get(countOfImage) : imgHomeList.get(0)).apply(new RequestOptions().placeholder(R.color.light_gray).error(R.color.light_gray)).into(mBinding.imgHome);
        }

        Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
        mBinding.imgHome.startAnimation(fadeIn);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (countOfImage < imgHomeList.size() - 1) {
                    countOfImage += 1;
                } else {
                    countOfImage = 0;
                }
                startAnimDashImage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
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

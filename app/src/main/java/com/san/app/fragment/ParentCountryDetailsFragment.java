package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.CategoryActivityAdapter;
import com.san.app.adapter.NearyByDestinationAdapter;
import com.san.app.adapter.PopularActivitiesAdapter;
import com.san.app.adapter.RecentlyAddedActivitiesAdapter;
import com.san.app.databinding.FragmentParentCountryDetailsBinding;
import com.san.app.model.ViewCityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
import com.san.app.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;
import static com.san.app.util.Utils.setCartBadgeCount;


public class ParentCountryDetailsFragment extends BaseFragment {


    //class object declaration..
    FragmentParentCountryDetailsBinding mBinding;
    View rootView;
    Context mContext;
    NearyByDestinationAdapter nearyByDestinationAdapter;
    CategoryActivityAdapter categoryActivityAdapter;
    PopularActivitiesAdapter popularActivitiesAdapter;
    RecentlyAddedActivitiesAdapter recentlyAddedActivitiesAdapter;
    List<ViewCityDetailModel.Category> categoaryActivityModelList = new ArrayList<>();
    List<ViewCityDetailModel.PopularActivity> popularActivityArrayList = new ArrayList<>();
    List<ViewCityDetailModel.Recentlyadded> recentlyaddedArrayList = new ArrayList<>();
    ViewCityDetailModel viewCityDetailModel;
    //variable declaration.
    private String TAG = ParentCountryDetailsFragment.class.getSimpleName();
    private boolean isValid = true;
    private String city_Id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_parent_country_details, container, false);
            rootView = mBinding.getRoot();
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getActivity().getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }*/
            mContext = getActivity();
            prepareView();
            setUp();
        }else {
            setCartBadgeCount(mContext, mBinding.cartBadge);

        }
        return rootView;
    }

    private void prepareView() {
        if (getArguments() != null) city_Id = getArguments().getString("city_id");

         changeToolbarView();
        setCartBadgeCount(mContext,mBinding.cartBadge);

        //neayby destination
        nearyByDestinationAdapter = new NearyByDestinationAdapter(mContext);
        mBinding.rvNearbyDestList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvNearbyDestList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvNearbyDestList.setAdapter(nearyByDestinationAdapter);


    }


    private void setUp() {
        Utils.showProgress(mContext);
        callCityDetailList(); //get ctyDetail
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callCityDetailList(); //get ctyDetail
            }
        }, 1500);*/


        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mBinding.imgright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartListingFragment fragment = new CartListingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("badge_click",1);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right );
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mBinding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewCityDetailModel.getPayload().getPopularActivity().size()>0 || viewCityDetailModel.getPayload().getRecentlyadded().size()>0) {
                    SearchActivityInCityFragment searchCityAttractionFragment = new SearchActivityInCityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("acitivityData", new Gson().toJson(viewCityDetailModel));
                    searchCityAttractionFragment.setArguments(bundle);
                    changeFragment_up_bottom(searchCityAttractionFragment);
                }else{
                    Toast.makeText(mContext, "No activity found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.rvCategoryList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryFilterInActivityFragment fragment = new CategoryFilterInActivityFragment();
                Bundle bundle = new Bundle();
                bundle.putString("acitivityData",  new Gson().toJson(viewCityDetailModel));
                bundle.putInt("category_id",viewCityDetailModel.getPayload().getCategories().get(position).getCategoryId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));

        mBinding.rvPopularActList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pref.setValue(mContext, "from_edit", ""); //for edit cart
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id",popularActivityArrayList.get(position).getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));

        mBinding.rvRecentlyAddedActList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pref.setValue(mContext, "from_edit", ""); //for edit cart
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id",recentlyaddedArrayList.get(position).getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));
    }

    private void callCityDetailList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("city_id", city_Id);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ViewCityDetailModel> call = apiService.viewcityDetail(data);
        call.enqueue(new Callback<ViewCityDetailModel>() {
            @Override
            public void onResponse(Call<ViewCityDetailModel> call, Response<ViewCityDetailModel> response) {
                Log.e(TAG, "response " + response.toString());
                Utils.dismissProgress();
                mBinding.activityMain.setVisibility(View.VISIBLE);
                categoaryActivityModelList.clear();
                if (response.body() != null) {
                    viewCityDetailModel = response.body();
                    categoaryActivityModelList = viewCityDetailModel.getPayload().getCategories();
                    popularActivityArrayList = viewCityDetailModel.getPayload().getPopularActivity();
                    recentlyaddedArrayList = viewCityDetailModel.getPayload().getRecentlyadded();
                    setCityDetailDataRes(viewCityDetailModel.getPayload().getCity());

                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<ViewCityDetailModel> call, Throwable t) {

            }
        });
    }

    private void setCityDetailDataRes(ViewCityDetailModel.City viewCityDetailModel) {
        mBinding.tvCityName.setText("About " + viewCityDetailModel.getCity());
        mBinding.tvTitle.setText("About " + viewCityDetailModel.getCity());
        mBinding.tvDesc.setText(viewCityDetailModel.getDescription());
        Glide.with(mContext)
                .load(viewCityDetailModel.getImageFullsize())
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                .into(mBinding.imgMain);
        mBinding.lnCateView.setVisibility(categoaryActivityModelList.size() == 0 ? View.GONE : View.VISIBLE);
        mBinding.lnPopularActView.setVisibility(popularActivityArrayList.size() == 0 ? View.GONE : View.VISIBLE);
        mBinding.lnRecentlyAddedActView.setVisibility(recentlyaddedArrayList.size() == 0 ? View.GONE : View.VISIBLE);
        //category list
        categoryActivityAdapter = new CategoryActivityAdapter(mContext, categoaryActivityModelList);
        mBinding.rvCategoryList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvCategoryList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvCategoryList.setAdapter(categoryActivityAdapter);

        //popular activity list
        popularActivitiesAdapter = new PopularActivitiesAdapter(mContext, popularActivityArrayList);
        mBinding.rvPopularActList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvPopularActList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPopularActList.setAdapter(popularActivitiesAdapter);

        //recently added activity list
        recentlyAddedActivitiesAdapter = new RecentlyAddedActivitiesAdapter(mContext, recentlyaddedArrayList);
        mBinding.rvRecentlyAddedActList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvRecentlyAddedActList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvRecentlyAddedActList.setAdapter(recentlyAddedActivitiesAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
        //((DashboardActivity) mContext).hideBottomNav();
        //Utils.hideBottomBar(mContext, rootView, ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void changeToolbarView() {
        mBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 150) {
                    mBinding.tvTitle.setVisibility(View.VISIBLE);
                   mBinding.imgBackView.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                   mBinding.imgSearch.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                    mBinding.imgright.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                    isShow = true;
                } else if (isShow) {
                    mBinding.tvTitle.setVisibility(View.GONE);
                    mBinding.imgBackView.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    mBinding.imgSearch.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    mBinding.imgright.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    isShow = false;
                }
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

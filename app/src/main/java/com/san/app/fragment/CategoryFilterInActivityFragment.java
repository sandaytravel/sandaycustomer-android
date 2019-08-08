package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.adapter.ActivityListAdapter;
import com.san.app.adapter.CategoryFilterAdapter;
import com.san.app.adapter.SubCategoryFilterAdapter;
import com.san.app.databinding.FragmentCategoryFilterBinding;
import com.san.app.model.ActivityListModel;
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


public class CategoryFilterInActivityFragment extends BaseFragment {


    //class object declaration..
    FragmentCategoryFilterBinding mBinding;
    View rootView;
    Context mContext;
    CategoryFilterAdapter categoryFilterAdapter;
    ActivityListAdapter activityListAdapter;
    SubCategoryFilterAdapter subCategoryFilterAdapter;
    ViewCityDetailModel viewCityDetailModel;
    List<ActivityListModel.Payload> activityListModelList = new ArrayList<>();
    List<ViewCityDetailModel.Category> categoryArrayList = new ArrayList<>();
    List<ViewCityDetailModel.Category.Subcategory> subCategoryArrayList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    View view;
    //variable declaration.
    private String TAG = CategoryFilterInActivityFragment.class.getSimpleName();
    private boolean isValid = true;
    private int category_id = 0;
    private int sub_category_id = 0;
    private int pageNumber = 1;
    private int subCategoryItemPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_category_filter, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        } else {
            setCartBadgeCount(mContext, mBinding.cartBadge);

        }
        return rootView;
    }

    private void setOnClickListener() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        mBinding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivityInCityFragment searchCityAttractionFragment = new SearchActivityInCityFragment();
                Bundle bundle = new Bundle();
                bundle.putString("acitivityData", new Gson().toJson(viewCityDetailModel));
                searchCityAttractionFragment.setArguments(bundle);
                changeFragment_up_bottom(searchCityAttractionFragment);
            }
        });

        mBinding.imgright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartListingFragment fragment = new CartListingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("badge_click", 1);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mBinding.tvCategoryHdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.imgUpDown.performClick();
            }
        });

        mBinding.imgUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.imgUpDown.getTag().equals("down")) {
                    mBinding.imgUpDown.setTag("up");
                    mBinding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_up_grey));
                    mBinding.lnCatePopup.setVisibility(View.VISIBLE);
                    Animation bottomDown = AnimationUtils.loadAnimation(getContext(),
                            R.anim.bottom_down_view);
                    mBinding.lnCatePopup.startAnimation(bottomDown);
                    setCategoryFilterData();
                } else {
                    mBinding.imgUpDown.setTag("down");
                    mBinding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_down_grey));
                    Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                            R.anim.bottom_up_view);
                    mBinding.lnCatePopup.startAnimation(bottomUp);
                    mBinding.lnCatePopup.setVisibility(View.GONE);
                }
            }
        });

        mBinding.rvCategoryPartList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                activityListModelList.clear();
                pageNumber = 1;
                sub_category_id = 0;
                subCategoryItemPosition = 0;
                category_id = categoryArrayList.get(position).getCategoryId();
                setCategoryFilterData();
                Utils.showProgress(mContext);
                callCategoryFilterList(category_id);
                mBinding.imgUpDown.setTag("down");
                mBinding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_down_grey));
                Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                        R.anim.bottom_up_view);
                mBinding.lnCatePopup.startAnimation(bottomUp);
                mBinding.lnCatePopup.setVisibility(View.GONE);
            }
        }));

        mBinding.rvSubCateogryList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                activityListModelList.clear();
                pageNumber = 1;
                sub_category_id = subCategoryArrayList.get(position).getSubcategoryId();
                subCategoryItemPosition = position;
                Utils.showProgress(mContext);
                callCategoryFilterList(category_id);

            }
        }));
    }


    private void setUp() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        if (getArguments() != null) {
            Gson gson = new Gson();
            viewCityDetailModel = gson.fromJson(getArguments().getString("acitivityData").toString(), ViewCityDetailModel.class);
            categoryArrayList = viewCityDetailModel.getPayload().getCategories();
            category_id = getArguments().getInt("category_id");
        }
        mBinding.tvTitle.setText("Search activities in " + viewCityDetailModel.getPayload().getCity().getCity());


        setCartBadgeCount(mContext, mBinding.cartBadge);

        setCategoryFilterData();
        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callCategoryFilterList(category_id); //get ctyDetail
            }
        }, 100);



        mBinding.rvPopularActList.addOnScrollListener(new com.san.app.util.EndlessRecyclerOnScrollListener(mLayoutManager) {

            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1) {
                    mBinding.rvPopularActList.setPadding(0,0,0,50);
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callCategoryFilterList(category_id); //get ctyDetail
                        }
                    }, 100);

                }
            }
        });

        mBinding.rvPopularActList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pref.setValue(mContext, "from_edit", "");
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", activityListModelList.get(position).getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void setCategoryFilterData() {
        subCategoryArrayList.clear();
        for (int i = 0; i < viewCityDetailModel.getPayload().getCategories().size(); i++) {
            if (viewCityDetailModel.getPayload().getCategories().get(i).getCategoryId() == category_id) {
                subCategoryArrayList.addAll(viewCityDetailModel.getPayload().getCategories().get(i).getSubcategories());
                mBinding.tvCategoryHdr.setText(viewCityDetailModel.getPayload().getCategories().get(i).getCategoryName());
                break;
            }
        }
        if (subCategoryArrayList.size() == 0) mBinding.lnSubPart.setVisibility(View.VISIBLE);
//categoryFilterAdapter list
        categoryFilterAdapter = new CategoryFilterAdapter(mContext, categoryArrayList, category_id);
        mBinding.rvCategoryPartList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvCategoryPartList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvCategoryPartList.setAdapter(categoryFilterAdapter);


    }

    private void callCategoryFilterList(int category_id) {
        final HashMap<String, String> data = new HashMap<>();
        data.put("city_id", "" + viewCityDetailModel.getPayload().getCity().getCityId());
        data.put("page", "" + pageNumber);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        if (category_id != 0) data.put("category_id", "" + category_id);
        if (sub_category_id != 0) data.put("subcategory_id", "" + sub_category_id);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ActivityListModel> call = apiService.activitylist(data);
        call.enqueue(new Callback<ActivityListModel>() {
            @Override
            public void onResponse(Call<ActivityListModel> call, Response<ActivityListModel> response) {
                Log.e(TAG, "response " + response.toString());
                Utils.dismissProgress();
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.lnMain.setVisibility(View.VISIBLE);
                mBinding.rvPopularActList.setPadding(0,0,0,0);
                if (response.body() != null) {
                    ActivityListModel activityListModel = response.body();
                    if (pageNumber == 1) {
                        // subCategoryFilterAdapter.setSubCategoryId(sub_category_id);
                        //Sub category FilterAdapter list
                        subCategoryFilterAdapter = new SubCategoryFilterAdapter(mContext, subCategoryArrayList, sub_category_id);
                        mBinding.rvSubCateogryList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        mBinding.rvSubCateogryList.setItemAnimator(new DefaultItemAnimator());
                        mBinding.rvSubCateogryList.setAdapter(subCategoryFilterAdapter);
                        if (subCategoryArrayList.size() > 0)
                            mBinding.rvSubCateogryList.scrollToPosition(subCategoryItemPosition);


                    }

                    if (activityListModel.getPayload().size() > 0) {
                        pageNumber = activityListModel.getPage();
                        activityListModelList.addAll(activityListModel.getPayload());
                        //activityListAdapter list
                        activityListAdapter = new ActivityListAdapter(mContext, activityListModelList);
                        mBinding.rvPopularActList.setLayoutManager(mLayoutManager);
                        mBinding.rvPopularActList.setItemAnimator(new DefaultItemAnimator());
                        mBinding.rvPopularActList.setAdapter(activityListAdapter);

                    }else {
                        pageNumber = 1;
                    }


                    mBinding.lnNoFilterData.setVisibility(activityListModelList.size() == 0 ? View.VISIBLE : View.GONE);
                    mBinding.rvPopularActList.setVisibility(activityListModelList.size() == 0 ? View.GONE : View.VISIBLE);



                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<ActivityListModel> call, Throwable t) {
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

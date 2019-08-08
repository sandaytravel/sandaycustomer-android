package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.tommykw.tagview.DataTransform;
import com.github.tommykw.tagview.TagView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.SearchAttractionHomeAdapter;
import com.san.app.adapter.SearchCityHomeAdapter;
import com.san.app.databinding.FragmentSearchCityAttractionBinding;
import com.san.app.model.CitiesListModel;
import com.san.app.model.SearchCityArractionListModel;
import com.san.app.model.ViewCityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
import com.san.app.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;


public class SearchCityAttractionFragment extends BaseFragment {


    //class object declaration..
    FragmentSearchCityAttractionBinding mBinding;
    View rootView;
    Context mContext;
    List<CitiesListModel> popularDestinationList = new ArrayList<>();
    List<ViewCityDetailModel.PopularActivity> popularActivityArrayList = new ArrayList<>();
    ArrayList<SearchCityArractionListModel.Activity> activityArrayList = new ArrayList<>();
    ArrayList<SearchCityArractionListModel.City> cityArrayList = new ArrayList<>();
    SearchCityHomeAdapter searchCityHomeAdapter;
    SearchAttractionHomeAdapter searchAttractionHomeAdapter;
    //variable declaration.
    private String TAG = SearchCityAttractionFragment.class.getSimpleName();
    private boolean isValid = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_search_city_attraction, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        }
        return rootView;
    }


    private void setUp() {
        Glide.with(mContext)
                .load(ContextCompat.getDrawable(mContext, R.mipmap.search_page_bg))
                .apply(new RequestOptions().placeholder(R.color.bg_white)
                        .error(R.color.bg_white))
                .into(mBinding.imgBg);

        Type type = new TypeToken<ArrayList<CitiesListModel>>() {
        }.getType();
        Type type1 = new TypeToken<ArrayList<ViewCityDetailModel.PopularActivity>>() {
        }.getType();
        popularDestinationList = new Gson().fromJson(Pref.getValue(mContext, "popular_dest_list", ""), type);
        popularActivityArrayList = new Gson().fromJson(Pref.getValue(mContext, "popular_activity_list", ""), type1);

        mBinding.lnDestination.setVisibility(popularDestinationList.size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.lnActivity.setVisibility(popularActivityArrayList.size() > 0 ? View.VISIBLE : View.GONE);

        setPopularDestActivityAdapter();


        mBinding.tagSearchDestList.setTags(popularDestinationList, new DataTransform<CitiesListModel>() {
            @NotNull
            @Override
            public String transfer(CitiesListModel item) {
                return item.city;
            }
        });

        mBinding.tagSearchActivityList.setTags(popularActivityArrayList, new DataTransform<ViewCityDetailModel.PopularActivity>() {
            @NotNull
            @Override
            public String transfer(ViewCityDetailModel.PopularActivity item) {
                return item.getTitle();
            }
        });

        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    //Utils.showProgressNormal(mContext);
                    callSearchCityAttractionAPI(s.toString());
                }
                //searchActivity(1);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBinding.imgCloseSearch.setVisibility(View.VISIBLE);
                } else {
                    setPopularDestActivityAdapter();
                }
            }
        });

        mBinding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        mBinding.imgCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.edtSearch.setText("");
                setPopularDestActivityAdapter();
            }
        });
    }


    private void setOnClickListener() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                getFragmentManager().popBackStack();
            }
        });

        mBinding.tagSearchDestList.setClickListener(new TagView.TagClickListener<CitiesListModel>() {
            @Override
            public void onTagClick(CitiesListModel item) {
                ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("city_id", item.city_id);
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        });

        mBinding.tagSearchActivityList.setClickListener(new TagView.TagClickListener<ViewCityDetailModel.PopularActivity>() {
            @Override
            public void onTagClick(ViewCityDetailModel.PopularActivity item) {
                Pref.setValue(mContext, "from_edit", ""); //for edit cart
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", item.getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        });


        mBinding.rvSearchAllList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (cityArrayList.size() > 0) {
                    ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("city_id", "" + cityArrayList.get(position).getCityId());
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                } else {
                    if (activityArrayList.size() > 0) {
                        DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("activity_id", activityArrayList.get(position).getActivityId());
                        fragment.setArguments(bundle);
                        changeFragment_back(fragment);
                    }
                }
            }
        }));
    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard();
    }

    private void callSearchCityAttractionAPI(String search) {
        final HashMap<String, String> data = new HashMap<>();
        data.put("searchterm", "" + search);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<SearchCityArractionListModel> call = apiService.searchCityOrAttraction(data);
        call.enqueue(new Callback<SearchCityArractionListModel>() {
            @Override
            public void onResponse(Call<SearchCityArractionListModel> call, Response<SearchCityArractionListModel> response) {
                activityArrayList.clear();
                cityArrayList.clear();
                Utils.dismissProgress();
                mBinding.lnMain.setVisibility(View.VISIBLE);
                if (response.body() != null) {
                    SearchCityArractionListModel activityListModel = response.body();
                    activityArrayList.addAll(activityListModel.getPayload().getActivity());
                    cityArrayList.addAll(activityListModel.getPayload().getCity());

                    mBinding.lnTopDes.setVisibility(View.GONE);

                    mBinding.rvSearchAllList.setVisibility(cityArrayList.size() > 0 || activityArrayList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.tvSuggestions.setVisibility(cityArrayList.size() > 0 || activityArrayList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoFilterData.setVisibility(cityArrayList.size() == 0 && activityArrayList.size() == 0 ? View.VISIBLE : View.GONE);
                    if (cityArrayList.size() > 0) {
                        //for search city
                        searchCityHomeAdapter = new SearchCityHomeAdapter(mContext, cityArrayList);
                        mBinding.rvSearchAllList.setLayoutManager(new LinearLayoutManager(mContext));
                        mBinding.rvSearchAllList.setItemAnimator(new DefaultItemAnimator());
                        mBinding.rvSearchAllList.setAdapter(searchCityHomeAdapter);
                    } else {
                        //for search attraction
                        searchAttractionHomeAdapter = new SearchAttractionHomeAdapter(mContext, activityArrayList);
                        mBinding.rvSearchAllList.setLayoutManager(new LinearLayoutManager(mContext));
                        mBinding.rvSearchAllList.setItemAnimator(new DefaultItemAnimator());
                        mBinding.rvSearchAllList.setAdapter(searchAttractionHomeAdapter);
                    }

                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<SearchCityArractionListModel> call, Throwable t) {
                Log.e("ErroData", "000  " + t.getMessage());
            }
        });

    }

    private void setPopularDestActivityAdapter() {
        mBinding.lnNoFilterData.setVisibility(View.GONE);
        mBinding.rvSearchAllList.setVisibility(View.GONE);
        mBinding.tvSuggestions.setVisibility(View.GONE);
        mBinding.lnTopDes.setVisibility(View.VISIBLE);
        mBinding.imgCloseSearch.setVisibility(View.GONE);
        hideSoftKeyboard();

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

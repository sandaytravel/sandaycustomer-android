package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.AllDestinationChildAdapter;
import com.san.app.adapter.PopularDestinationAdapter;
import com.san.app.adapter.SearchDestinationChildAdapter;
import com.san.app.databinding.FragmentDestinationHomeNewBinding;
import com.san.app.model.CitiesListModel;
import com.san.app.model.CitiesSearchListModel;
import com.san.app.model.LocationsOfCityListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.BottomNavigationBehavior;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
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


public class DestinationHomeNewFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentDestinationHomeNewBinding mBinding;
    View rootView;
    Context mContext;
    PopularDestinationAdapter popularDestinationAdapter;
    AllDestinationChildAdapter allDestinationChildAdapter;
    SearchDestinationChildAdapter searchDestinationChildAdapter;
    //variable declaration.
    private String TAG = DestinationHomeNewFragment.class.getSimpleName();
    ArrayList<CitiesListModel> citiesListModelArrayList = new ArrayList<>();
    CitiesListModel citiesListModel = new CitiesListModel();
    ArrayList<LocationsOfCityListModel> locationsOfCityListModelsList = new ArrayList<>();
    ArrayList<CitiesSearchListModel> citiesListModelArrayList_Search = new ArrayList<>();
    LocationsOfCityListModel locationsOfCityListModel = new LocationsOfCityListModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_destination_home_new, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();

            setUp();
        } else {
            BottomNavigationBehavior bottomNavigationBehavior = new BottomNavigationBehavior();
            bottomNavigationBehavior.showBottomNavigationView(((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
        }
        //setLocale(mContext, Pref.getValue(mContext, Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en");
        return rootView;
    }

    private void setUp() {

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callHotLocationCityList(); //get hot destination
            }
        }, 100);
        setHotDestinationList(0); //0 for more and 1 for less


        mBinding.rvHotDestList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("city_id", citiesListModelArrayList.get(position).city_id);
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));


        mBinding.rvAllDestList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (citiesListModelArrayList_Search.size() > 0) {
                    ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("city_id", citiesListModelArrayList_Search.get(position).city_id);
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                }
            }
        }));


        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) searchDestination();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBinding.imgCancelSearch.setVisibility(View.VISIBLE);
                } else {
                    setAllCityData(1);
                    mBinding.imgCancelSearch.setVisibility(View.GONE);
                }
            }
        });

        mBinding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchDestination();
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        mBinding.imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.edtSearch.setText("");
                setAllCityData(1);
                hideSoftKeyboard();
            }
        });

        mBinding.tvViewMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.tvViewMoreLess.getTag().equals("more")) {
                    mBinding.tvViewMoreLess.setTag("less");
                    mBinding.tvViewMoreLess.setText(R.string.view_less);
                    setHotDestinationList(1);
                } else {
                    mBinding.tvViewMoreLess.setTag("more");
                    mBinding.tvViewMoreLess.setText(R.string.view_more);
                    setHotDestinationList(0);
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

    private void setHotDestinationList(int i) {
        //whats new list
        popularDestinationAdapter = new PopularDestinationAdapter(mContext, citiesListModelArrayList, i);
        mBinding.rvHotDestList.setLayoutManager(new GridLayoutManager(mContext, 2));
        mBinding.rvHotDestList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvHotDestList.setNestedScrollingEnabled(false);
        mBinding.rvHotDestList.setAdapter(popularDestinationAdapter);
    }


    private void searchDestination() {
        if (mBinding.edtSearch.getText().toString().trim().length() > 0) {
            citiesListModelArrayList_Search.clear();
            for (int i = 0; i < locationsOfCityListModelsList.size(); i++) {
                if (locationsOfCityListModelsList.get(i).country.toLowerCase().contains(mBinding.edtSearch.getText().toString().toLowerCase())) {
                    for (int j = 0; j < locationsOfCityListModelsList.get(i).cities.size(); j++) {
                        CitiesSearchListModel citiesSearchListModel = new CitiesSearchListModel();
                        citiesSearchListModel.city = locationsOfCityListModelsList.get(i).cities.get(j).city;
                        citiesSearchListModel.city_id = locationsOfCityListModelsList.get(i).cities.get(j).city_id;
                        citiesSearchListModel.countryName = locationsOfCityListModelsList.get(i).country;
                        citiesListModelArrayList_Search.add(citiesSearchListModel);
                    }
                } else {
                    for (int j = 0; j < locationsOfCityListModelsList.get(i).cities.size(); j++) {
                        if (locationsOfCityListModelsList.get(i).cities.get(j).city.toLowerCase().contains(mBinding.edtSearch.getText().toString().toLowerCase())) {
                            CitiesSearchListModel citiesSearchListModel = new CitiesSearchListModel();
                            citiesSearchListModel.city = locationsOfCityListModelsList.get(i).cities.get(j).city;
                            citiesSearchListModel.city_id = locationsOfCityListModelsList.get(i).cities.get(j).city_id;
                            citiesSearchListModel.countryName = locationsOfCityListModelsList.get(i).country;
                            citiesListModelArrayList_Search.add(citiesSearchListModel);
                        }
                    }
                }
            }
            mBinding.lnNoFilterData.setVisibility(citiesListModelArrayList_Search.size() == 0 ? View.VISIBLE : View.GONE);
            // mBinding.lnSearchView.setVisibility(citiesListModelArrayList_Search.size() == 0 ? View.GONE : View.VISIBLE);
            mBinding.svData.setVisibility(citiesListModelArrayList_Search.size() == 0 ? View.GONE : View.VISIBLE);
            setAllCityData(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "from", ""))) {
            mBinding.toolbar.setVisibility(View.VISIBLE);
            ((DashboardActivity) mContext).hideShowBottomNav(false);
        } else {
            mBinding.toolbar.setVisibility(View.GONE);
            Utils.getKeyboardOpenorNot(mContext, rootView, ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
        }
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_destination).setChecked(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * function for get hot location with city
     */
    private void callHotLocationCityList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", ""+ Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.destinations("All",data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e(TAG, "response " + response.toString());
                    Utils.dismissProgress();
                    mBinding.lnMain.setVisibility(View.VISIBLE);
                    citiesListModelArrayList.clear();
                    if (response.body() != null) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.optInt("code") == 200) {
                            JSONObject dataJsonObj = jsonObject.optJSONObject("payload");
                            Gson gson = new Gson();
                            for (int i = 0; i < dataJsonObj.optJSONArray("hot_destination").length(); i++) {
                                citiesListModel = gson.fromJson(dataJsonObj.optJSONArray("hot_destination").optJSONObject(i).toString(), CitiesListModel.class);
                                if (citiesListModel.activity_count > 0)
                                    citiesListModelArrayList.add(citiesListModel);
                            }

                            for (int i = 0; i < dataJsonObj.optJSONArray("locations").length(); i++) {
                                locationsOfCityListModel = gson.fromJson(dataJsonObj.optJSONArray("locations").optJSONObject(i).toString(), LocationsOfCityListModel.class);
                                locationsOfCityListModelsList.add(locationsOfCityListModel);
                            }
                            setAllCityData(1);
                            popularDestinationAdapter.notifyDataSetChanged();
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

            }
        });

    }

    private void setAllCityData(int type) {  // 1 for normal and 0 for filter
        if (type == 1) {
            allDestinationChildAdapter = new AllDestinationChildAdapter(mContext, locationsOfCityListModelsList);
            mBinding.rvAllDestList.setAdapter(allDestinationChildAdapter);
            mBinding.rvAllDestList.setVisibility(View.VISIBLE);
            mBinding.lnHot.setVisibility(View.VISIBLE);
            mBinding.rvHotDestList.setVisibility(View.VISIBLE);
            mBinding.svData.setVisibility(View.VISIBLE);
            mBinding.lnNoFilterData.setVisibility(View.GONE);
            citiesListModelArrayList_Search.clear();
        } else {
            searchDestinationChildAdapter = new SearchDestinationChildAdapter(mContext, citiesListModelArrayList_Search);
            mBinding.rvAllDestList.setAdapter(searchDestinationChildAdapter);
            mBinding.lnHot.setVisibility(View.GONE);
            mBinding.rvHotDestList.setVisibility(View.GONE);
        }
        mBinding.rvAllDestList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvAllDestList.setNestedScrollingEnabled(false);
        mBinding.rvAllDestList.setItemAnimator(new DefaultItemAnimator());

        popularDestinationAdapter.notifyDataSetChanged();  //refresh hot destination
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
                        if (!TextUtils.isEmpty(Pref.getValue(mContext, "from", ""))) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            getActivity().finishAffinity();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

}

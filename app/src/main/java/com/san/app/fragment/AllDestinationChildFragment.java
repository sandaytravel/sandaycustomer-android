package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.AllDestinationChildAdapter;
import com.san.app.adapter.SearchDestinationChildAdapter;
import com.san.app.databinding.FragmentAllDestinationBinding;
import com.san.app.model.CitiesSearchListModel;
import com.san.app.model.LocationsOfCityListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.FieldsValidator;
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


public class AllDestinationChildFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentAllDestinationBinding mBinding;
    View rootView;
    Context mContext;
    AllDestinationChildAdapter allDestinationChildAdapter;
    SearchDestinationChildAdapter searchDestinationChildAdapter;
    //variable declaration.
    private String TAG = AllDestinationChildFragment.class.getSimpleName();
    ArrayList<LocationsOfCityListModel> locationsOfCityListModelsList = new ArrayList<>();
    ArrayList<CitiesSearchListModel> citiesListModelArrayList_Search = new ArrayList<>();
    LocationsOfCityListModel locationsOfCityListModel = new LocationsOfCityListModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_all_destination, container, false);
        rootView = mBinding.getRoot();
        mContext = getActivity();

        setUp();
        return rootView;
    }


    private void setUp() {
        callAllLocationCityList();

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
            mBinding.rvPopularDestList.setVisibility(citiesListModelArrayList_Search.size() == 0 ? View.GONE : View.VISIBLE);
            setAllCityData(0);
        }
    }

    @Override
    public void onClick(View view) {


    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.getKeyboardOpenorNot(mContext, rootView, ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    /**
     * function for get hot location with city
     */
    private void callAllLocationCityList() {
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
                    locationsOfCityListModelsList.clear();
                    if (response.body() != null) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e(TAG, "555 " + jsonObject.toString());
                        if (jsonObject.optInt("code") == 200) {
                            JSONObject dataJsonObj = jsonObject.optJSONObject("payload");
                            Gson gson = new Gson();
                            for (int i = 0; i < dataJsonObj.optJSONArray("locations").length(); i++) {
                                locationsOfCityListModel = gson.fromJson(dataJsonObj.optJSONArray("locations").optJSONObject(i).toString(), LocationsOfCityListModel.class);
                                locationsOfCityListModelsList.add(locationsOfCityListModel);
                            }
                            setAllCityData(1);
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
            mBinding.rvPopularDestList.setAdapter(allDestinationChildAdapter);
            mBinding.rvPopularDestList.setVisibility(View.VISIBLE);
            //mBinding.lnSearchView.setVisibility(View.VISIBLE);
            mBinding.lnNoFilterData.setVisibility(View.GONE);
        } else {
            searchDestinationChildAdapter = new SearchDestinationChildAdapter(mContext, citiesListModelArrayList_Search);
            mBinding.rvPopularDestList.setAdapter(searchDestinationChildAdapter);
        }
        mBinding.rvPopularDestList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPopularDestList.setItemAnimator(new DefaultItemAnimator());
    }
}

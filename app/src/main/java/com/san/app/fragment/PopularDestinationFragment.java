package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.PopularDestinationAdapter;
import com.san.app.databinding.FragmentPopularDestinationBinding;
import com.san.app.model.CitiesListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
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


public class PopularDestinationFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentPopularDestinationBinding mBinding;
    View rootView;
    Context mContext;
    PopularDestinationAdapter popularDestinationAdapter;
    //variable declaration.
    private String TAG = PopularDestinationFragment.class.getSimpleName();
    ArrayList<CitiesListModel> citiesListModelArrayList = new ArrayList<>();
    CitiesListModel citiesListModel = new CitiesListModel();
    String fromPage = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_popular_destination, container, false);
        rootView = mBinding.getRoot();
        mContext = getActivity();

        setUp();
        return rootView;
    }


    private void setUp() {
        final DestinationHomeFragment parentFrag = ((DestinationHomeFragment)PopularDestinationFragment.this.getParentFragment());
        if (getArguments() != null) fromPage = getArguments().getString("from");


        Utils.showProgress(mContext);
        callHotLocationCityList(); //get hot destination


        //whats new list
        popularDestinationAdapter = new PopularDestinationAdapter(mContext, citiesListModelArrayList);
        mBinding.rvPopularDestList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPopularDestList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPopularDestList.setAdapter(popularDestinationAdapter);


        mBinding.rvPopularDestList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               // getActivity().getSupportFragmentManager().beginTransaction()
                 //       .setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                   //     .replace(R.id.frame, new ParentCountryDetailsFragment()).addToBackStack(null).commit();
                //changeFragment_back(new  ParentCountryDetailsFragment());
                ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.setVisibility(View.GONE);
                parentFrag.changeFragment_back(new ParentCountryDetailsFragment());
            }
        }));

    }

    @Override
    public void onClick(View view) {


    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
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
                    citiesListModelArrayList.clear();
                    if (response.body() != null) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.optInt("code") == 200) {
                            JSONObject dataJsonObj = jsonObject.optJSONObject("payload");
                            Gson gson = new Gson();
                            for (int i = 0; i < dataJsonObj.optJSONArray("hot_destination").length(); i++) {
                                citiesListModel = gson.fromJson(dataJsonObj.optJSONArray("hot_destination").optJSONObject(i).toString(), CitiesListModel.class);
                                citiesListModelArrayList.add(citiesListModel);
                            }

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

}

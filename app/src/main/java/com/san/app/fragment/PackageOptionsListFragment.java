package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.PackageOptionFromActivitiesAdapter;
import com.san.app.databinding.FragmentPackageOptionsBinding;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class PackageOptionsListFragment extends BaseFragment {


    //class object declaration..
    FragmentPackageOptionsBinding mBinding;
    View rootView;
    Context mContext;
    PackageOptionFromActivitiesAdapter packageOptionFromActivitiesAdapter;
    ViewActivityDetailModel.Basicdetails basicdetails;
    ArrayList<ViewActivityDetailModel.Packageoption> packageoptionArrayList = new ArrayList<>();
    //variable declaration.
    private String TAG = PackageOptionsListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_package_options, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            prepareView();
            setUp();
        }
        return rootView;
    }

    private void prepareView() {
        Pref.setValue(mContext, "Quantity_Package_Edit", "");
        Pref.setValue(mContext, "booking_date_Edit", "");
        Type type = new TypeToken<ArrayList<ViewActivityDetailModel.Packageoption>>() {}.getType();
        packageoptionArrayList = new Gson().fromJson(Pref.getValue(mContext, "packageOptionList", ""), type);
        if (getArguments() != null) {
            basicdetails = new Gson().fromJson(getArguments().getString("basicDetails").toString(), ViewActivityDetailModel.Basicdetails.class);
        }

        packageOptionFromActivitiesAdapter = new PackageOptionFromActivitiesAdapter(mContext, packageoptionArrayList, basicdetails, 0);
        mBinding.rvPackageOptionsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPackageOptionsList.setNestedScrollingEnabled(true);
        mBinding.rvPackageOptionsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPackageOptionsList.setAdapter(packageOptionFromActivitiesAdapter);

    }


    private void setUp() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    ;

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.setVisibility(View.GONE);
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity(), rootView);
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

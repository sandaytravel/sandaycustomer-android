package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.san.app.R;
import com.san.app.adapter.NearyByDestinationAdapter;
import com.san.app.databinding.FragmentAboutParentCountryBinding;


public class AboutOfParentPopularCountryFragment extends BaseFragment {


    //class object declaration..
    FragmentAboutParentCountryBinding mBinding;
    View rootView;
    Context mContext;
    NearyByDestinationAdapter nearyByDestinationAdapter;

    //variable declaration.
    private String TAG = AboutOfParentPopularCountryFragment.class.getSimpleName();
    private boolean isValid = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_about_parent_country, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            prepareView();
            setUp();
        }
        return rootView;
    }

    private void prepareView() {
        //whats new list
        nearyByDestinationAdapter = new NearyByDestinationAdapter(mContext);
        mBinding.rvNearbyDestList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvNearbyDestList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvNearbyDestList.setAdapter(nearyByDestinationAdapter);


        Glide.with(this).load("http://www.destination-asia.com/media/upload/Landing_SIngapore_1600x565.jpg.1600x565_q85.jpg").apply(new RequestOptions().override(50, 50)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBinding.lnMain.setBackground(resource);
                }
            }
        });


    }


    private void setUp() {

    }


    @Override
    public void onResume() {
        super.onResume();

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

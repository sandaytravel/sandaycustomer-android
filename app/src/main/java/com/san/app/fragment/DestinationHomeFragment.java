package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.DestinationViewPagerAdapter;
import com.san.app.databinding.FragmentDestinationHomeBinding;


public class DestinationHomeFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentDestinationHomeBinding mBinding;
    View rootView;
    Context mContext;
    //variable declaration.
    private String TAG = DestinationHomeFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_destination_home, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();

            setUp();
        }
        return rootView;
    }

    private void setUp() {
        if (getArguments() != null) {
            if (getArguments().getString("from").equals("showAll")) {
                mBinding.toolbar.setVisibility(View.VISIBLE);
                ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.setVisibility(View.GONE);
            } else {
                mBinding.toolbar.setVisibility(View.GONE);
                ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.setVisibility(View.VISIBLE);
            }
        }

        mBinding.viewpager.setOffscreenPageLimit(2);
        setupViewPager(mBinding.viewpager);

        mBinding.tablayout.setupWithViewPager(mBinding.viewpager);

        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.viewpager.setCurrentItem(position, false);
                hideSoftKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {

    }

    private void setupViewPager(ViewPager viewPager) {
        DestinationViewPagerAdapter adapter = new DestinationViewPagerAdapter(getChildFragmentManager());
        PopularDestinationFragment popularDestinationFragment = new PopularDestinationFragment();
        AllDestinationChildFragment allDestinationChildFragment = new AllDestinationChildFragment();
        if (getArguments() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("from", getArguments().getString("from"));
            popularDestinationFragment.setArguments(bundle);
            allDestinationChildFragment.setArguments(bundle);
        }
        adapter.addFragment(popularDestinationFragment, "Popular");
        adapter.addFragment(allDestinationChildFragment, "All");
        viewPager.setAdapter(adapter);
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

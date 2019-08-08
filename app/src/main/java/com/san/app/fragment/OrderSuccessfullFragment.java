package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.WishListActivitiesAdapter;
import com.san.app.databinding.FragmentOrderSuccessfullBinding;
import com.san.app.model.WishListModel;

import java.util.ArrayList;
import java.util.List;

import static com.san.app.util.Utils.setCartBadgeCount;


public class OrderSuccessfullFragment extends BaseFragment {


    //class object declaration..
    public FragmentOrderSuccessfullBinding mBinding;
    View rootView;
    Context mContext;
    WishListActivitiesAdapter wishListActivitiesAdapter;
    List<WishListModel.Payload> wishListModelList = new ArrayList<>();
    WishListModel wishListModel;
    //variable declaration.
    private String TAG = OrderSuccessfullFragment.class.getSimpleName();
    private boolean isValid = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_order_successfull, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        }
        return rootView;
    }


    private void setUp() {
        setCartBadgeCount(mContext, ((DashboardActivity) mContext).tvCartBadge);

    }

    private void setOnClickListener() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();

                }
                ((DashboardActivity)mContext).mBinding.moreMenuBottomNavigation.setSelectedItemId(R.id.action_explore);
                ExploreHomeFragment fragment = new ExploreHomeFragment();
                getFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
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
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();

                        }
                        ((DashboardActivity)mContext).mBinding.moreMenuBottomNavigation.setSelectedItemId(R.id.action_explore);
                        ExploreHomeFragment fragment = new ExploreHomeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();

                        return true;
                    }
                }
                return false;
            }
        });
    }
}

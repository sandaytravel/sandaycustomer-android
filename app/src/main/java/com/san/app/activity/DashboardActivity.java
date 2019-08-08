package com.san.app.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.san.app.R;
import com.san.app.databinding.ActivityDashboardBinding;
import com.san.app.fragment.BookingDetailPageFragment;
import com.san.app.fragment.CartListingFragment;
import com.san.app.fragment.DestinationHomeNewFragment;
import com.san.app.fragment.ExploreHomeFragment;
import com.san.app.fragment.MyAccountFragment;
import com.san.app.fragment.MyBookingFragment;
import com.san.app.fragment.NotificationListFragment;
import com.san.app.util.BottomNavigationBehavior;
import com.san.app.util.Constants;
import com.san.app.util.Pref;

import java.util.List;

public class DashboardActivity extends BaseActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    public ActivityDashboardBinding mBinding;
    Context mContext;
    public BottomNavigationItemView itemView;
    public TextView tvCartBadge;
    public View cart_badge;
    // UserDataModel userDataModel;
    //variable declaration
    boolean isValid = true;
    private String TAG = DashboardActivity.class.getSimpleName();
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        mContext = DashboardActivity.this;
        changeFragment(new ExploreHomeFragment());
        if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")))
            requestStoragePermission();
        setOnClickListener();
        setUp();
        //getMyContext(this,"fr");
       // setLocale(mContext, Pref.getValue(mContext,Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en",0);

    }

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase,Pref.getValue(mContext,Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en"));

    }*/

    private void setOnClickListener() {

        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().popBackStack();
            }
        });


        mBinding.moreMenuBottomNavigation.setOnNavigationItemSelectedListener(this);
    }


    private void setUp() {

        if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")))
            Pref.setValue(mContext, "isFirstTime", "1");
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBinding.moreMenuBottomNavigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        BottomNavigationMenuView mbottomNavigationMenuView =
                (BottomNavigationMenuView) mBinding.moreMenuBottomNavigation.getChildAt(0);
        View view = mbottomNavigationMenuView.getChildAt(2);

        itemView = (BottomNavigationItemView) view;

        cart_badge = LayoutInflater.from(this)
                .inflate(R.layout.custom_cart_item_layout,
                        mbottomNavigationMenuView, false);
        tvCartBadge = (TextView) cart_badge.findViewById(R.id.cart_badge);


        /*if (getIntent() != null) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("is_from")) && getIntent().getStringExtra("is_from").equals("notification"))
                changeFragment_back(new NotificationListFragment());
        }*/

        if (getIntent() != null) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("is_from")) && getIntent().getStringExtra("is_from").equals("notification")) {
                if(!TextUtils.isEmpty(getIntent().getStringExtra("order_id"))) {
                    BookingDetailPageFragment fragment = new BookingDetailPageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", getIntent().getStringExtra("order_id"));
                    bundle.putString("is_from", "notification");
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                }else{
                    changeFragment_back(new NotificationListFragment());
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked())
            item.setChecked(false);
        tvCartBadge.setVisibility(View.VISIBLE);
        switch (item.getItemId()) {
            case R.id.action_explore:
                //Check the Item
                item.setChecked(true);
                changeFragment(new ExploreHomeFragment());
                break;
            case R.id.action_destination:
                //Check the Item
                item.setChecked(true);
                Pref.setValue(mContext, "from", "");
                //changeFragment(new DestinationHomeFragment());
                changeFragment(new DestinationHomeNewFragment());
                break;
            case R.id.action_cart:
                //Check the Item
                item.setChecked(true);
                tvCartBadge.setVisibility(View.GONE);
                changeFragment(new CartListingFragment());
                break;
            case R.id.action_booking:
                //Check the Item
                item.setChecked(true);
                changeFragment(new MyBookingFragment());
                break;
            case R.id.action_account:
                //Check the Item
                item.setChecked(true);
                changeFragment(new MyAccountFragment());
                break;

            default:
                item.setChecked(true);
                changeFragment(new ExploreHomeFragment());
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {


    }

    public void hideShowBottomNav(final boolean isShown) {
        mBinding.getRoot().getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mBinding.moreMenuBottomNavigation.setVisibility(isShown ? View.VISIBLE : View.GONE);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TestResume", "999  ");
    }

    public void visibleMenuBack(String frag) {

        if (frag.equals("main")) {
            mBinding.imgBackView.setVisibility(View.GONE);
        } else {
            mBinding.imgBackView.setVisibility(View.VISIBLE);
        }

    }

    public void visibleRighttitle(String title) {
        mBinding.tvRightTitle.setText(title);
        mBinding.tvRightTitle.setVisibility(View.VISIBLE);
    }


    public void gonRightTitle() {
        mBinding.tvRightTitle.setVisibility(View.GONE);
    }

    public void visibleRightImage(int image) {
        mBinding.imgRightView.setImageResource(image);
        mBinding.imgRightView.setVisibility(View.VISIBLE);
    }


    public void goneRightImage() {
        mBinding.imgRightView.setVisibility(View.GONE);
    }

    public void setHeaderTitle(String title) {
        mBinding.tvTitle.setText(title);
    }

    public Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        return currentFragment;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 101) { //for runtim permission
            requestStoragePermission();
        }
    }

    /**
     * This uses multiple permission model from dexter
     */
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MainFragment()).commit();
                        }


                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied() || report.getDeniedPermissionResponses().size() > 0) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                       /* else{
                            Log.e("Dashboard","222");
                            requestStoragePermission();
                        }*/
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finishAffinity();
            }
        });

        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}

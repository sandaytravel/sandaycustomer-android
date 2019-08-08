package com.san.app.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.CartListingAdapter;
import com.san.app.databinding.FragmentCartListingBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.CartViewListModel;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
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
import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;
import static com.san.app.util.Utils.setCartBadgeCount;
import static com.san.app.util.Utils.setLocale;


public class CartListingFragment extends BaseFragment {


    //class object declaration..
    FragmentCartListingBinding mBinding;
    View rootView;
    Context mContext;
    CartListingAdapter cartListingAdapter;
    CartViewListModel cartViewListModel;
    ArrayList<CartViewListModel.Payload> currentCartViewList = new ArrayList<>();
    ViewActivityDetailModel viewActivityDetailModel;
    // ArrayList<CartViewListModel.Payload> expireCartViewList = new ArrayList<>();
    //variable declaration.
    private String TAG = CartListingFragment.class.getSimpleName();
    private boolean isValid = true;
    private double subTotalCart = 0;
    private int badge_click = 0;
    private boolean isValidItem = false;
    private boolean isValidToPassPayNow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart_listing, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        }/*else {
            BottomNavigationBehavior bottomNavigationBehavior = new BottomNavigationBehavior();
            bottomNavigationBehavior.showBottomNavigationView(((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
        }*/
        setLocale(mContext, Pref.getValue(mContext, Constants.APP_LANGUAGE, 0) == 0 || Pref.getValue(mContext, Constants.APP_LANGUAGE, 1) == 1 ? "en" : Pref.getValue(mContext, Constants.APP_LANGUAGE, 2) == 2 ? "ja" : Pref.getValue(mContext, Constants.APP_LANGUAGE, 3) == 3 ? "ko" : "en", 0);
        return rootView;
    }


    private void setUp() {
        mBinding.tvNoItemFound.setText(getString(R.string.your_shopping_cart_is_empty));
        if (getArguments() != null) badge_click = getArguments().getInt("badge_click");
        //mBinding.toolbar.setVisibility(badge_click == 1 ? View.VISIBLE : View.GONE);
        mBinding.imgBackView.setVisibility(badge_click == 1 && Pref.getValue(mContext, "from_edit", "").equals("") ? View.VISIBLE : View.GONE);
        if (mBinding.imgBackView.getVisibility() == View.VISIBLE) {
            mBinding.tvTitle.setVisibility(View.VISIBLE);
            mBinding.tvTitleTemp.setVisibility(View.GONE);
        } else {
            mBinding.tvTitle.setVisibility(View.GONE);
            mBinding.tvTitleTemp.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "from_edit", "")) && Pref.getValue(mContext, "from_edit", "").equals("1"))
            mBinding.imgBackView.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (badge_click == 1 && Pref.getValue(mContext, "from_edit", "").equals("")) {
            params.setMargins(0, 0, 0, 0);
        } else {
            params.setMargins(0, 0, 0, ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getHeight());
        }
        mBinding.lnMain.setLayoutParams(params);

        if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
            //mBinding.lnMain.setVisibility(View.VISIBLE);
            mBinding.lnGuestView.setVisibility(View.GONE);
            mBinding.lnTopMain.setVisibility(View.GONE);
            Utils.showProgress(mContext);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    callViewCartList(); //get cart view detail
                }
            }, 100);

        } else {
            mBinding.lnMain.setVisibility(View.GONE);
            mBinding.lnGuestView.setVisibility(View.VISIBLE);
            mBinding.toolbar.setVisibility(badge_click == 1 && Pref.getValue(mContext, "from_edit", "").equals("") ? View.VISIBLE : View.GONE);

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBinding.nestedMain.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    if (i1 > i3) {
                        params.setMargins(0, 0, 0, 0);
                    } else {
                        params.setMargins(0, 0, 0, ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getHeight());
                    }
                    if (badge_click != 1) mBinding.lnMain.setLayoutParams(params);
                }
            });

        }

    }


    private void setOnClickListener() {
        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                isValidToPassPayNow = false;
                callViewCartList(); //get cart list data
            }
        });

        mBinding.txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginView(mContext);
            }
        });

        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        mBinding.tvPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidItem) {
                    isValidToPassPayNow = true;
                    Utils.showProgressNormal(mContext);
                    callViewCartList();
                    // changeFragment_back(new PayPalWebPageFragment());
                }
            }
        });
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        Log.e("TestToken", "000   " + Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""));
        ((DashboardActivity) mContext).hideShowBottomNav(badge_click == 1 && Pref.getValue(mContext, "from_edit", "").equals("") ? false : true);
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_cart).setChecked(true);
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "isFreshLogin", ""))) {
            Pref.setValue(mContext, "isFreshLogin", "");
            setUp();
        }
    }

    private void callViewCartList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        // data.put("activity_id", "" + activityId);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<CartViewListModel> call = apiService.viewcartList(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<CartViewListModel>() {
            @Override
            public void onResponse(Call<CartViewListModel> call, Response<CartViewListModel> response) {
                Log.e(TAG, "response " + response.toString());
                Utils.dismissProgress();
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.lnMain.setVisibility(View.VISIBLE);
                mBinding.lnTopMain.setVisibility(View.VISIBLE);
                subTotalCart = 0;
                int currentCountTotal = 0;
                boolean isErrorFree = true;
                currentCartViewList.clear();
                if (response.body() != null) {
                    cartViewListModel = response.body();
                    for (int i = 0; i < cartViewListModel.getPayload().size(); i++) {
                        currentCartViewList.add(cartViewListModel.getPayload().get(i));
                        if (cartViewListModel.getPayload().get(i).getErrors().size() == 0) {
                            subTotalCart = subTotalCart + cartViewListModel.getPayload().get(i).getTotalPrice();
                            currentCountTotal = currentCountTotal + 1;
                            isValidItem = true;
                        }
                        if (cartViewListModel.getPayload().get(i).getErrors().size() > 0)
                            isErrorFree = false;
                    }
                    // cartListingAdapter.notifyDataSetChanged();
                    //whats new list
                    cartListingAdapter = new CartListingAdapter(mContext, currentCartViewList);
                    mBinding.rvCartList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    mBinding.rvCartList.setItemAnimator(new DefaultItemAnimator());
                    cartListingAdapter.onClickPosition(onClickPosition);
                    mBinding.rvCartList.setAdapter(cartListingAdapter);


                    mBinding.tvTotal.setText(getRMConverter(0.6f, getThousandsNotation(String.format("%.2f", subTotalCart))));
                    Pref.setValue(mContext, Constants.TAG_CART_BADGE_COUNT, currentCountTotal);
                    mBinding.cardPayNow.setCardBackgroundColor(isValidItem ? ContextCompat.getColor(mContext, R.color.app_theme_dark) : ContextCompat.getColor(mContext, R.color.gray));
                    mBinding.toolbar.setVisibility(cartViewListModel.getPayload().size() > 0 || badge_click == 1 ? View.VISIBLE : View.GONE);
                    mBinding.lnMain.setVisibility(cartViewListModel.getPayload().size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoData.setVisibility(cartViewListModel.getPayload().size() > 0 ? View.GONE : View.VISIBLE);
                    if (isErrorFree && cartViewListModel.getPayload().size() > 0 && isValidToPassPayNow) {
                        Pref.setValue(mContext, "currentCartViewList", new Gson().toJson(currentCartViewList));
                        BookNowPayFragment fragment = new BookNowPayFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("is_from", "cart");
                        fragment.setArguments(bundle);
                        changeFragment_back(fragment);
                    } else if (isValidToPassPayNow) {
                        isValidToPassPayNow = false;
                        Toast.makeText(mContext, getString(R.string.cart_validation_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<CartViewListModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
                Utils.dismissProgress();
            }
        });
    }

    private void callDeleteCartItemAPI(final Integer position) {

        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + currentCartViewList.get(position).getActivityMainId());
        data.put("package_id", "" + currentCartViewList.get(position).getPackageMainId());
        data.put("booking_date", currentCartViewList.get(position).getBookingDate());
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.deletecartItem(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        currentCartViewList.remove(position);
                        cartListingAdapter.removeAt(position);
                        cartListingAdapter = new CartListingAdapter(mContext, currentCartViewList);
                        mBinding.rvCartList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                        mBinding.rvCartList.setItemAnimator(new DefaultItemAnimator());
                        cartListingAdapter.onClickPosition(onClickPosition);
                        mBinding.rvCartList.setAdapter(cartListingAdapter);
                        subTotalCart = 0;
                        int currentCountTotal = 0;
                        for (int i = 0; i < currentCartViewList.size(); i++) {
                            if (currentCartViewList.get(i).getActivityStatus().equalsIgnoreCase("Current")) {
                                subTotalCart = subTotalCart + currentCartViewList.get(i).getTotalPrice();
                                currentCountTotal = currentCountTotal + 1;
                            }
                        }
                        mBinding.tvTotal.setText(getRMConverter(0.6f, getThousandsNotation(String.format("%.2f", subTotalCart))));
                        Pref.setValue(mContext, Constants.TAG_CART_BADGE_COUNT, currentCartViewList.size() == 0 ? 0 : currentCountTotal);
                        setCartBadgeCount(mContext, ((DashboardActivity) mContext).tvCartBadge);
                        mBinding.toolbar.setVisibility(currentCartViewList.size() > 0 ? View.VISIBLE : View.GONE);
                        mBinding.lnMain.setVisibility(currentCartViewList.size() > 0 ? View.VISIBLE : View.GONE);
                        mBinding.lnNoData.setVisibility(currentCartViewList.size() > 0 ? View.GONE : View.VISIBLE);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
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
                Utils.dismissProgress();
            }
        });
    }


    OnClickPosition onClickPosition = new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            if (msg.equalsIgnoreCase("Edit")) {
                Utils.showProgressNormal(mContext);
                callActivityDetailList(position, currentCartViewList.get(position).getActivityId()); //get activity detail
            } else {
                Utils.showProgressNormal(mContext);
                callDeleteCartItemAPI(position);
            }
        }
    };

    private void callActivityDetailList(final Integer position, Integer activityId) {
        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + activityId);
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ViewActivityDetailModel> call = apiService.viewactivityDetail(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ViewActivityDetailModel>() {
            @Override
            public void onResponse(Call<ViewActivityDetailModel> call, Response<ViewActivityDetailModel> response) {
                Log.e(TAG, "response " + response.toString());
                Utils.dismissProgress();
                //categoaryActivityModelList.clear();
                if (response.body() != null) {
                    viewActivityDetailModel = response.body();
                    Pref.setValue(mContext, "packageOptionList", new Gson().toJson(viewActivityDetailModel.getPayload().getPackageoptions()));
                    Pref.setValue(mContext, "booking_date_Edit", currentCartViewList.get(position).getBookingDate());
                    Pref.setValue(mContext, "Quantity_Package_Edit", new Gson().toJson(currentCartViewList.get(position).getQuantity()));

                    BookingOptionPackagesFragment fragment = new BookingOptionPackagesFragment();
                    Bundle bundle = new Bundle();
                    for (int i = 0; i < viewActivityDetailModel.getPayload().getPackageoptions().size(); i++) {
                        if (viewActivityDetailModel.getPayload().getPackageoptions().get(i).getId().toString().equals(currentCartViewList.get(position).getPackageId().toString())) {
                            bundle.putString("package_details", new Gson().toJson(viewActivityDetailModel.getPayload().getPackageoptions().get(i)));
                        }
                    }
                    bundle.putString("basicDetails", new Gson().toJson(viewActivityDetailModel.getPayload().getBasicdetails()));
                    bundle.putInt("isErrorAvailable", currentCartViewList.get(position).getErrors().size());
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    transaction.replace(R.id.frame, fragment, "fragment");
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.optInt("code") == 401) {
                            new FieldsValidator(getActivity()).customToast(jsonObject.getString("message"), R.mipmap.cancel_toast_new);
                        } else {
                            errorBody(response.errorBody());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }

            @Override
            public void onFailure(Call<ViewActivityDetailModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
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
                        if (badge_click == 1) {
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

package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptrstovka.calendarview2.CalendarDay;
import com.ptrstovka.calendarview2.CalendarView2;
import com.ptrstovka.calendarview2.OnDateSelectedListener;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.PackageOptionFromActivitiesAdapter;
import com.san.app.adapter.PackageQuantityOptionsAdapter;
import com.san.app.databinding.FragmentBookingOptionsPackagesBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.CartViewListModel;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;
import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;
import static com.san.app.util.Utils.setCartBadgeCount;
import static com.san.app.util.Utils.setLocale;


public class BookingOptionPackagesFragment extends BaseFragment {


    //class object declaration..
    FragmentBookingOptionsPackagesBinding mBinding;
    View rootView;
    Context mContext;
    PackageOptionFromActivitiesAdapter packageOptionFromActivitiesAdapter;
    ArrayList<ViewActivityDetailModel.Packageoption> packageoptionArrayList = new ArrayList<>();
    ArrayList<ViewActivityDetailModel.Packageoption> FullpackageoptionArrayList = new ArrayList<>();
    ArrayList<CartViewListModel.Quantity> QuantityPackageEditList = new ArrayList<>();
    ViewActivityDetailModel.Packageoption packageoptionModel;
    PackageQuantityOptionsAdapter packageQuantityOptionsAdapter;
    ArrayList<ViewActivityDetailModel.PackageQuantity> packageQuantitiesList = new ArrayList<>();
    ArrayList<ViewActivityDetailModel.PackageQuantity> packageQuantitiesList_temp = new ArrayList<>();
    ArrayList<CartViewListModel.Payload> currentCartViewList = new ArrayList<>();
    ViewActivityDetailModel.PackageQuantity packageQuantityModel;
    ViewActivityDetailModel.Basicdetails basicdetails;
    BottomSheetDialog mBottomSheetDialog;
    //variable declaration.
    private String TAG = BookingOptionPackagesFragment.class.getSimpleName();
    private boolean isValid = true;
    private double totalQuantity = 0;
    private int totalCountSelectQuantity = 0;
    private boolean isMorethanOneQuantity = false;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private String selectedDate = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking_options_packages, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            prepareView(0);
            setUp();
        }
        setLocale(mContext, Pref.getValue(mContext, Constants.APP_LANGUAGE, 0) == 0 || Pref.getValue(mContext, Constants.APP_LANGUAGE, 1) == 1 ? "en" : Pref.getValue(mContext, Constants.APP_LANGUAGE, 2) == 2 ? "ja" : Pref.getValue(mContext, Constants.APP_LANGUAGE, 3) == 3 ? "ko" : "en", 0);
        return rootView;
    }

    private void prepareView(int dataType) {
        Type type = new TypeToken<ArrayList<ViewActivityDetailModel.Packageoption>>() {
        }.getType();
        FullpackageoptionArrayList = new Gson().fromJson(Pref.getValue(mContext, "packageOptionList", ""), type);
        if (getArguments() != null) {
            basicdetails = new Gson().fromJson(getArguments().getString("basicDetails").toString(), ViewActivityDetailModel.Basicdetails.class);
            if (dataType == 0)
                packageoptionModel = new Gson().fromJson(getArguments().getString("package_details").toString(), ViewActivityDetailModel.Packageoption.class);
            packageQuantitiesList_temp.clear();
            packageoptionArrayList.clear();
            packageoptionArrayList.add(packageoptionModel);
            if (packageoptionArrayList.size() > 0) {
                if (packageoptionModel.getPackageQuantity().size() > 0) {
                    Log.e("TestValiddata", "111 " + packageoptionModel.getPackageQuantity().size());
                    packageQuantitiesList_temp.addAll(packageoptionModel.getPackageQuantity());
                    packageQuantitiesList.clear();
                    QuantityPackageEditList.clear();
                    Type type1 = new TypeToken<ArrayList<CartViewListModel.Quantity>>() {
                    }.getType();
                    if (!TextUtils.isEmpty(Pref.getValue(mContext, "Quantity_Package_Edit", "")))
                        QuantityPackageEditList = new Gson().fromJson(Pref.getValue(mContext, "Quantity_Package_Edit", ""), type1);
                    Pref.setValue(mContext, "Quantity_Package_Edit", "");
                    for (int i = 0; i < packageQuantitiesList_temp.size(); i++) {
                        if (QuantityPackageEditList.size() > 0) {
                            for (int j = 0; j < QuantityPackageEditList.size(); j++) {
                                if (packageQuantitiesList_temp.get(i).getQtmainId().toString().contains(QuantityPackageEditList.get(j).getQuantityMain_id().toString())) {
                                    packageQuantityModel = packageQuantitiesList_temp.get(i);
                                    packageQuantityModel.setItem_count(QuantityPackageEditList.get(j).getQuantity());
                                    totalQuantity = totalQuantity + QuantityPackageEditList.get(j).getTotalPrice();
                                    if (QuantityPackageEditList.get(j).getQuantity() > 0)
                                        totalCountSelectQuantity = totalCountSelectQuantity + QuantityPackageEditList.get(j).getQuantity();
                                    packageQuantitiesList.add(packageQuantityModel);
                                    break;
                                }
                            }
                        } else {
                            packageQuantityModel = packageQuantitiesList_temp.get(i);
                            packageQuantityModel.setItem_count(0);
                            packageQuantitiesList.add(packageQuantityModel);
                        }

                    }
                    Log.e("TestValiddata", "000   " + packageQuantitiesList.size() + " test " + dataType + "  ****  " + totalQuantity);

                    if (dataType == 1) {
                        totalCountSelectQuantity = 0;
                        totalQuantity = 0;
                        isMorethanOneQuantity = false;
                    }
                    if (totalCountSelectQuantity > 0) isMorethanOneQuantity = true;
                    mBinding.tvTotalQuantity.setText("" + totalCountSelectQuantity);
                    mBinding.tvTotal.setText(totalQuantity > 0 ? getRMConverter(0.5f, getThousandsNotation("" + totalQuantity)) : getRMConverter(0.5f, "0"));
                    mBinding.cardAddCart.setCardBackgroundColor(isMorethanOneQuantity ? ContextCompat.getColor(mContext, R.color.yellow) : ContextCompat.getColor(mContext, R.color.gray));
                    mBinding.cardBookNow.setCardBackgroundColor(isMorethanOneQuantity ? ContextCompat.getColor(mContext, R.color.orange) : ContextCompat.getColor(mContext, R.color.gray));
                }
            }
        }
        mBinding.cardAddCart.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "from_edit", "")) && Pref.getValue(mContext, "from_edit", "").equals("1")) {
            mBinding.tvBookNow.setText(R.string.ok);
            mBinding.tvNotValidPackage.setVisibility(packageQuantitiesList.size() == 0 ? View.VISIBLE : View.GONE);
            mBinding.rvOptoinsList.setVisibility(packageQuantitiesList.size() == 0 ? View.GONE : View.VISIBLE);
        } else {
            mBinding.tvBookNow.setText(Pref.getValue(mContext, "isAddCartOrBookNow", "").equals("addCart") ? " Add to Cart" : "Next");
            mBinding.tvTotal.setText(getRMConverter(0.5f, "0"));
            // mBinding.lnFreePurchase.setVisibility(packageoptionModel.getFreeVoucher() == 1 ? View.VISIBLE : View.GONE);
            // mBinding.lnNormalView.setVisibility(packageoptionModel.getFreeVoucher() == 1 ? View.GONE : View.VISIBLE);
        }

        mBinding.calendarView.addDecorator(new Utils.BeforeAllDaysDisabledDecorator());
        if (!TextUtils.isEmpty(packageoptionModel.getValidity()))
            mBinding.calendarView.addDecorator(new Utils.AfterAllDaysDisabledDecorator(Integer.parseInt(packageoptionModel.getValidity().toString().split("-")[0]), Integer.parseInt(packageoptionModel.getValidity().toString().split("-")[1]) - 1, Integer.parseInt(packageoptionModel.getValidity().toString().split("-")[2])));

        mBinding.tvPkgTitle.setText(packageoptionModel.getPackageTitle());
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(packageoptionModel.getDisplayPrice()) ? getRMConverter(0.6f, getThousandsNotation(packageoptionModel.getDisplayPrice())) : getRMConverter(0.6f, getThousandsNotation(packageoptionModel.getActualPrice())));
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(packageoptionModel.getDisplayPrice()) ? getThousandsNotation(packageoptionModel.getActualPrice()) : "");
        if (!TextUtils.isEmpty(packageoptionModel.getDisplayPrice()))
            mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //package Quantity
        packageQuantityOptionsAdapter = new PackageQuantityOptionsAdapter(mContext, packageQuantitiesList);
        mBinding.rvOptoinsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvOptoinsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvOptoinsList.setAdapter(packageQuantityOptionsAdapter);
        packageQuantityOptionsAdapter.onclickPosition(onClickPosition);

        //for edit cart
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "booking_date_Edit", ""))) {
            selectedDate = Pref.getValue(mContext, "booking_date_Edit", "");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar thatDay = Calendar.getInstance();
            try {
                thatDay.setTime(formatter.parse(selectedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //if (getArguments() != null) {
            mBinding.calendarView.setDateSelected(thatDay.getTime(), getArguments().getInt("isErrorAvailable") > 0 ? false : true);
            mBinding.tvOpenCalender.setText(getArguments().getInt("isErrorAvailable") > 0 ? "" + getString(R.string.please_select_a_participation_date) : getSelectedDatesString());
            // } else {
            //     mBinding.tvOpenCalender.setText(getSelectedDatesString());
            // }
        }

        List<CalendarDay> calendarDays = new ArrayList<CalendarDay>();
        //for date not available view disable in calender
        for (int i = 0; i < basicdetails.getDateNotavailable().size(); i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(basicdetails.getDateNotavailable().get(i)) * 1000);
            calendarDays.add(CalendarDay.from(cal));
        }
        //for days confirm on date
        for (int j = 0; j <= basicdetails.getConfirmDays(); j++) {
            if (basicdetails.getConfirmDays() == 1) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, j);
                calendarDays.add(CalendarDay.from(gc));
            } else if (basicdetails.getConfirmDays() == 2) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, j);
                calendarDays.add(CalendarDay.from(gc));
            } else {
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, j);
                calendarDays.add(CalendarDay.from(gc));
            }

        }

        // for day off
        ArrayList<String> daysCalenderList = new ArrayList<>();
        for (int i = 0; i < basicdetails.getDaysOff().size(); i++) {
            daysCalenderList.add(basicdetails.getDaysOff().get(i));
        }
        mBinding.calendarView.addDecorator(new Utils.DaysOffDecorator(daysCalenderList));
        mBinding.calendarView.addDecorator(new Utils.DateNotAvailableDecorator(calendarDays));
    }


    private void setUp() {

        mBinding.tvOpenCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.lnCalenderView.getVisibility() == View.GONE) {
                    mBinding.lnCalenderView.setVisibility(View.VISIBLE);
                    Animation bottomDown = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down_view);
                    mBinding.lnCalenderView.startAnimation(bottomDown);
                } else {
                    Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up_view);
                    mBinding.lnCalenderView.startAnimation(bottomUp);
                    mBinding.lnCalenderView.setVisibility(View.GONE);
                }
            }
        });

        mBinding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull CalendarView2 widget, @NonNull CalendarDay date, boolean selected) {
                int month = date.getMonth() + 1;
                selectedDate = date.getYear() + "-" + month + "-" + date.getDay();
                mBinding.tvOpenCalender.setText(getSelectedDatesString());
                Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up_view);
                mBinding.lnCalenderView.startAnimation(bottomUp);
                mBinding.lnCalenderView.setVisibility(View.GONE);
            }
        });


        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mBinding.cardEditPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditPackageOptionDialog(mContext);

            }
        });

        mBinding.cardFreePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBinding.tvOpenCalender.getText().equals(getString(R.string.please_select_a_participation_date))) {
                    Pref.setValue(mContext, "currentCartViewList", new Gson().toJson(getCurrentItemList()));
                    BookNowPayFragment fragment = new BookNowPayFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("is_from", "book_now");
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                } else {
                    Toast.makeText(mContext, "" + getString(R.string.please_select_a_participation_date), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.tvBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isMinQauntityValid = true;
                String packageName="";
                String minQuantity="";
                for (int i = 0; i < packageQuantitiesList.size(); i++) {
                    if (packageQuantitiesList.get(i).getItem_count() > 0 && packageQuantitiesList.get(i).getItem_count() < packageQuantitiesList.get(i).getMinimumQuantity()) {
                        isMinQauntityValid = false;
                        packageName=packageQuantitiesList.get(i).getName();
                        minQuantity= String.valueOf(packageQuantitiesList.get(i).getMinimumQuantity());
                        break;
                    }
                }
                if (isMinQauntityValid) {
                    if (!TextUtils.isEmpty(Pref.getValue(mContext, "from_edit", "")) && Pref.getValue(mContext, "from_edit", "").equals("1")) {
                        if (isMorethanOneQuantity) {
                            if (!mBinding.tvOpenCalender.getText().equals(getString(R.string.please_select_a_participation_date))) {
                                Utils.showProgressNormal(mContext);
                                callEditToCartAPI();
                            } else {
                                Toast.makeText(mContext, "" + getString(R.string.please_select_a_participation_date), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (isMorethanOneQuantity) {
                            if (Pref.getValue(mContext, "isAddCartOrBookNow", "").equals("bookNow")) {
                                if (!mBinding.tvOpenCalender.getText().equals(getString(R.string.please_select_a_participation_date))) {
                                    Pref.setValue(mContext, "currentCartViewList", new Gson().toJson(getCurrentItemList()));
                                    BookNowPayFragment fragment = new BookNowPayFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("is_from", "book_now");
                                    fragment.setArguments(bundle);
                                    changeFragment_back(fragment);
                                } else {
                                    Toast.makeText(mContext, "" + getString(R.string.please_select_a_participation_date), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mBinding.tvAddCart.performClick();
                            }
                        }
                    }
                }else {
                    Toast.makeText(mContext, ""+mContext.getString(R.string.you_can_add_min_item)+" "+minQuantity+" "+mContext.getString(R.string.qunatity_for_this_pkg)+" "+packageName+".", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isMinQauntityValid = true;
                String packageName="";
                String minQuantity="";
                for (int i = 0; i < packageQuantitiesList.size(); i++) {
                    if (packageQuantitiesList.get(i).getItem_count() > 0 &&  packageQuantitiesList.get(i).getItem_count() < packageQuantitiesList.get(i).getMinimumQuantity()) {
                        isMinQauntityValid = false;
                        packageName=packageQuantitiesList.get(i).getName();
                        minQuantity= String.valueOf(packageQuantitiesList.get(i).getMinimumQuantity());
                        break;
                    }
                }
                if (isMinQauntityValid) {
                    if (isMorethanOneQuantity) {
                        if (!mBinding.tvOpenCalender.getText().equals(getString(R.string.please_select_a_participation_date))) {
                            Utils.showProgressNormal(mContext);
                            callAddToCartAPI();
                        } else {
                            Toast.makeText(mContext, "" + getString(R.string.please_select_a_participation_date), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(mContext, ""+mContext.getString(R.string.you_can_add_min_item)+" "+minQuantity+" "+mContext.getString(R.string.qunatity_for_this_pkg)+" "+packageName+".", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void openEditPackageOptionDialog(final Context mContext) {
        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.package_options_edit_dialog, null);
        mBottomSheetDialog.setContentView(sheetView);

        TextView tv_close = (TextView) mBottomSheetDialog.findViewById(R.id.tv_close);
        RecyclerView rvPackageOptionsList = (RecyclerView) mBottomSheetDialog.findViewById(R.id.rvPackageOptionsList);


        packageOptionFromActivitiesAdapter = new PackageOptionFromActivitiesAdapter(mContext, FullpackageoptionArrayList, basicdetails, 2);
        rvPackageOptionsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvPackageOptionsList.setItemAnimator(new DefaultItemAnimator());
        rvPackageOptionsList.setAdapter(packageOptionFromActivitiesAdapter);
        packageOptionFromActivitiesAdapter.onClickPosition(onClickPosition1);

       /* rvPackageOptionsList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BookingOptionPackagesFragment fragment = new BookingOptionPackagesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("package_details", new Gson().toJson(packageoptionModel));
                bundle.putString("basicDetails", new Gson().toJson(basicdetails));
                fragment.setArguments(bundle);
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.commit();
                mBottomSheetDialog.dismiss();
            }
        }));*/

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });


        mBottomSheetDialog.show();
    }

    OnClickPosition onClickPosition1 = new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            packageoptionModel = FullpackageoptionArrayList.get(position);
            prepareView(1);
            if (mBottomSheetDialog != null) mBottomSheetDialog.dismiss();
        }
    };


    OnClickPosition onClickPosition = new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            isMorethanOneQuantity = false;
            for (int i = 0; i < packageQuantitiesList.size(); i++) {
                if (i == position) {
                    packageQuantityModel = packageQuantitiesList.get(i);
                    packageQuantityModel.setItem_count(msg.equals("plus") ? packageQuantityModel.getItem_count() + 1 : packageQuantityModel.getItem_count() - 1);
                    totalQuantity = msg.equals("plus") ? totalQuantity + Double.parseDouble(packageQuantityModel.getDisplayPrice()) : totalQuantity - Double.parseDouble(packageQuantityModel.getDisplayPrice());
                    totalCountSelectQuantity = msg.equals("plus") ? totalCountSelectQuantity + 1 : totalCountSelectQuantity - 1;
                    packageQuantitiesList.set(position, packageQuantityModel);
                }
                if (packageQuantitiesList.get(i).getItem_count() > 0) {
                    isMorethanOneQuantity = true;

                }
            }

            //package Quantity
            packageQuantityOptionsAdapter = new PackageQuantityOptionsAdapter(mContext, packageQuantitiesList);
            mBinding.rvOptoinsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mBinding.rvOptoinsList.setItemAnimator(new DefaultItemAnimator());
            mBinding.rvOptoinsList.setAdapter(packageQuantityOptionsAdapter);
            packageQuantityOptionsAdapter.onclickPosition(onClickPosition);

            mBinding.tvTotalQuantity.setText("" + totalCountSelectQuantity);
            mBinding.tvTotal.setText(totalQuantity > 0 ? getRMConverter(0.5f, getThousandsNotation("" + totalQuantity)) : getRMConverter(0.5f, "0"));
            mBinding.cardAddCart.setCardBackgroundColor(isMorethanOneQuantity ? ContextCompat.getColor(mContext, R.color.yellow) : ContextCompat.getColor(mContext, R.color.gray));
            mBinding.cardBookNow.setCardBackgroundColor(isMorethanOneQuantity ? ContextCompat.getColor(mContext, R.color.orange) : ContextCompat.getColor(mContext, R.color.gray));
        }
    };


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


    /**
     * this api for item add into cart
     */
    private void callAddToCartAPI() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < packageQuantitiesList.size(); i++) {
            //if (packageQuantitiesList.get(i).getItem_count() > 0) {
            JSONObject dataObj = new JSONObject();
            try {
                dataObj.put("id", "" + packageQuantitiesList.get(i).getQtmainId());
                dataObj.put("name", packageQuantitiesList.get(i).getName());
                dataObj.put("quantity", "" + packageQuantitiesList.get(i).getItem_count());
                jsonArray.put(dataObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // }
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + packageoptionModel.getActivityMainId());
        data.put("package_id", "" + packageoptionModel.getPackageMainId());
        data.put("package_quantity", jsonArray.toString());
        data.put("booking_date", selectedDate);
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        Log.e("TEstData", "898989   " + data.toString());
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.addtocart(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Pref.setValue(mContext, Constants.TAG_CART_BADGE_COUNT, Pref.getValue(mContext, Constants.TAG_CART_BADGE_COUNT, 0) + 1);
                        setCartBadgeCount(mContext, ((DashboardActivity) mContext).tvCartBadge);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
                                if (i < 2) getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }


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
                Log.e("TestError", "999  " + t.getMessage());
            }
        });
    }

    /**
     * this api for item edit cart item
     */

    private void callEditToCartAPI() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < packageQuantitiesList.size(); i++) {
            //if (packageQuantitiesList.get(i).getItem_count() > 0) {
            JSONObject dataObj = new JSONObject();
            try {
                dataObj.put("id", "" + packageQuantitiesList.get(i).getQtmainId());
                dataObj.put("name", packageQuantitiesList.get(i).getName());
                dataObj.put("quantity", "" + packageQuantitiesList.get(i).getItem_count());
                jsonArray.put(dataObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // }
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + packageoptionModel.getActivityMainId());
        data.put("package_id", "" + packageoptionModel.getPackageMainId());
        data.put("package_quantity", jsonArray.toString());
        data.put("booking_date", selectedDate);
        data.put("oldpackage_id", Pref.getValue(mContext, "oldPackageId", ""));
        data.put("oldbooking_date", Pref.getValue(mContext, "oldBookingDate", ""));
        data.put("language_id", "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        Log.e("TEstData", "edit    " + data.toString());
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.editcart(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        //changeFragment(new CartListingFragment());
                        CartListingFragment fragment = new CartListingFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("badge_click", 1);
                        fragment.setArguments(bundle);

                        FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        transaction.replace(R.id.frame, fragment, "fragment");
                        transaction.addToBackStack(null);
                        transaction.commit();
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

    private String getSelectedDatesString() {
        CalendarDay date = mBinding.calendarView.getSelectedDate();
        if (date == null) {
            return getString(R.string.please_select_a_participation_date);// return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }


    private ArrayList<CartViewListModel.Payload> getCurrentItemList() {
        currentCartViewList.clear();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("activity_id", basicdetails.getActivityId());
            jsonObject.put("activitymain_id", basicdetails.getActivityMain());
            jsonObject.put("activity_title", "" + basicdetails.getTitle());
            jsonObject.put("activity_image", "" + basicdetails.getImage());
            jsonObject.put("package_id", packageoptionModel.getId());
            jsonObject.put("packagemain_id", packageoptionModel.getPackageMainId());
            jsonObject.put("package_title", "" + packageoptionModel.getPackageTitle());
            jsonObject.put("activity_status", "" + basicdetails.getStatus());
            jsonObject.put("booking_date", "" + selectedDate);
            jsonObject.put("free_voucher", "" + packageoptionModel.getFreeVoucher());
            jsonObject.put("total_price", totalQuantity);
            JSONArray qunatJsonList = new JSONArray();
            for (int i = 0; i < packageQuantitiesList.size(); i++) {
                if (packageQuantitiesList.get(i).getItem_count() > 0) {
                    JSONObject packageQuantitiesObject = new JSONObject();
                    packageQuantitiesObject.put("quantitymain_id", packageQuantitiesList.get(i).getQtmainId());
                    packageQuantitiesObject.put("quantity_id", packageQuantitiesList.get(i).getId());
                    packageQuantitiesObject.put("quantity_name", "" + packageQuantitiesList.get(i).getName());
                    packageQuantitiesObject.put("actual_price", "" + packageQuantitiesList.get(i).getActualPrice());
                    packageQuantitiesObject.put("display_price", "" + packageQuantitiesList.get(i).getDisplayPrice());
                    packageQuantitiesObject.put("quantity", packageQuantitiesList.get(i).getItem_count());
                    String newPrice = !TextUtils.isEmpty(packageQuantitiesList.get(i).getDisplayPrice()) ? packageQuantitiesList.get(i).getDisplayPrice() : packageQuantitiesList.get(i).getActualPrice();
                    double optionTotlaPrice = Double.parseDouble(newPrice) * Double.parseDouble("" + packageQuantitiesList.get(i).getItem_count());
                    packageQuantitiesObject.put("total_price", optionTotlaPrice);
                    qunatJsonList.put(packageQuantitiesObject);
                }
            }
            jsonObject.put("Quantity", qunatJsonList);
            Log.e("TestData", "00000   " + jsonObject.toString());
            currentCartViewList.add(new Gson().fromJson(jsonObject.toString(), CartViewListModel.Payload.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentCartViewList;
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

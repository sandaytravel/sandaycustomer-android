package com.san.app.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.CountryListAdapter;
import com.san.app.databinding.FragmentMyDetailEditBinding;
import com.san.app.model.CountryListModel;
import com.san.app.model.UserDataModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.OnOneOffClickListener;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;


public class MyDetailEditFragment extends BaseFragment {


    //class object declaration..
    FragmentMyDetailEditBinding mBinding;
    View rootView;
    Context mContext;
    private UserDataModel userDataModel;
    ArrayList<CountryListModel.Payload> countryListModelArrayList = new ArrayList<>();
    ArrayList<CountryListModel.Payload> countryListModelArrayList_Search = new ArrayList<>();
    Dialog listDialog;
    //variable declaration.
    private String TAG = MyDetailEditFragment.class.getSimpleName();
    private boolean isValid = true;
    final CharSequence[] items = {"Mr", "Mrs", "Miss"};
    int selectedIndex = -1;
    private int year, month, day;
    String selectedDate = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_detail_edit, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            prepareView();
            setUp();
        }
        return rootView;
    }

    private void prepareView() {
        userDataModel = new Pref(mContext).getUserInfo();
        mBinding.edtFamilyName.setText(userDataModel.family_name);
        mBinding.edtFirstName.setText(userDataModel.name);
        mBinding.edtPhone.setText(userDataModel.phone);
        mBinding.edtEmail.setText(userDataModel.voucher_email);
        mBinding.edtTitle.setText(userDataModel.title);
        mBinding.edtCountryName.setText(!TextUtils.isEmpty(userDataModel.country_name) ? userDataModel.country_name : "Malaysia");
        mBinding.edtCountryCode.setText(!TextUtils.isEmpty(userDataModel.country_code) ? userDataModel.country_code : "+60");
        if (userDataModel.title.toLowerCase().equalsIgnoreCase("Mr")) {
            selectedIndex = 0;
        } else if (userDataModel.title.toLowerCase().equalsIgnoreCase("Mrs")) {
            selectedIndex = 1;
        } else if (userDataModel.title.toLowerCase().equalsIgnoreCase("Miss")) {
            selectedIndex = 2;
        } else {
            selectedIndex = -1;
        }
        mBinding.edtbirthday.setText(!TextUtils.isEmpty(userDataModel.birth_date) ? "" + userDataModel.birth_date.toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(userDataModel.birth_date.toString().split("-")[1])) + " " + userDataModel.birth_date.toString().split("-")[0] : "");
        selectedDate = userDataModel.birth_date;
    }


    private void setUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showProgressNormal(mContext);
                callCountryList();
            }
        }, 100);


        mBinding.edtbirthday.setOnClickListener(new OnOneOffClickListener() {
            @Override
            public void onSingleClick(View v) {
                datePicker();
            }
        });


        mBinding.edtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleSelectionDialog();
            }
        });

        mBinding.edtCountryName.setOnClickListener(new OnOneOffClickListener() {
            @Override
            public void onSingleClick(View v) {
                openCountryListDialog(mContext, "country");
            }
        });

        mBinding.edtCountryCode.setOnClickListener(new OnOneOffClickListener() {
            @Override
            public void onSingleClick(View v) {
                openCountryListDialog(mContext, "code");
            }
        });

        mBinding.tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtTitle.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_title), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtFamilyName.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_family_name), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtFirstName.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_first_name), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtbirthday.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_birthday), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtCountryName.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_country_name), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtCountryCode.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_country_code), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtPhone.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_phone_number), R.mipmap.red_cross_er);
                } else if (mBinding.edtPhone.getText().toString().trim().length() < 10 || mBinding.edtPhone.getText().toString().trim().length() > 10) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_phone_number), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                }


                if (isValid) {
                    Utils.showProgress(mContext);
                    callMyDetailsUpdateAPI();
                }
            }
        });


        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
                hideSoftKeyboard();
            }
        });


    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;

                // Show selected date
                int month1 = month + 1;
                mBinding.edtbirthday.setText(new StringBuilder().append(day < 10 ? "0" + day : day).append(" ").append(Utils.monthName(month1)).append(" ").append(year));
                selectedDate = "" + new StringBuilder().append(year).append("-").append(month1 < 10 ? "0" + month1 : month1).append("-").append(day < 10 ? "0" + day : day).append(" ");

            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void openCountryListDialog(Context mContext, final String type) {
        listDialog = new Dialog(mContext);
        final LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        listDialog.setContentView(R.layout.dialog_search_country);
        listDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = listDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(window.getAttributes());


        listDialog.getWindow().setGravity(Gravity.BOTTOM);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        final RecyclerView rvCountryList = (RecyclerView) listDialog.findViewById(R.id.rvCountryList);
        final EditText edtSearch = (EditText) listDialog.findViewById(R.id.edtSearch);
        final ImageView imgCancelSearch = (ImageView) listDialog.findViewById(R.id.imgCancelSearch);
        TextView tvCancel = (TextView) listDialog.findViewById(R.id.tvCancel);
        final LinearLayout lnNoFilterData = (LinearLayout) listDialog.findViewById(R.id.lnNoFilterData);
        edtSearch.setHint(type.equals("country") ? "Search Country" : "Search Country/Code");
        countryListModelArrayList_Search.clear();
        setCountryList(1, rvCountryList, type);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) searchCountry(edtSearch, lnNoFilterData, rvCountryList, type);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imgCancelSearch.setVisibility(View.VISIBLE);
                } else {
                    setCountryList(1, rvCountryList, type);
                    imgCancelSearch.setVisibility(View.GONE);
                }
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCountry(edtSearch, lnNoFilterData, rvCountryList, type);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.setText("");
                setCountryList(1, rvCountryList, type);
                hideSoftKeyboard();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog.dismiss();
            }
        });

        rvCountryList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (type.equals("country")) {
                    mBinding.edtCountryName.setText(countryListModelArrayList_Search.size() > 0 ? "" + countryListModelArrayList_Search.get(position).getCountryName() : "" + countryListModelArrayList.get(position).getCountryName());
                } else {
                    mBinding.edtCountryCode.setText(countryListModelArrayList_Search.size() > 0 ? "+" + countryListModelArrayList_Search.get(position).getCountryCode() : "+" + countryListModelArrayList.get(position).getCountryCode());
                }
                listDialog.dismiss();
            }
        }));

        listDialog.show();
    }


    private void searchCountry(EditText edtSearch, LinearLayout lnNoFilterData, RecyclerView rvCountryList, String type) {
        if (edtSearch.getText().toString().trim().length() > 0) {
            countryListModelArrayList_Search.clear();
            for (int i = 0; i < countryListModelArrayList.size(); i++) {
                if (type.equalsIgnoreCase("country")) {
                    if (countryListModelArrayList.get(i).getCountryName().toLowerCase().contains(edtSearch.getText().toString().toLowerCase())) {
                        countryListModelArrayList_Search.add(countryListModelArrayList.get(i));
                    }
                } else {
                    if (countryListModelArrayList.get(i).getCountryName().toLowerCase().contains(edtSearch.getText().toString().toLowerCase()) || countryListModelArrayList.get(i).getCountryCode().toString().contains(edtSearch.getText().toString().toLowerCase())) {
                        countryListModelArrayList_Search.add(countryListModelArrayList.get(i));
                    }
                }
            }
            lnNoFilterData.setVisibility(countryListModelArrayList_Search.size() == 0 ? View.VISIBLE : View.GONE);
            setCountryList(0, rvCountryList, type);
        }
    }

    private void setCountryList(int i, RecyclerView rvCountryList, String type) {
//country list
        final CountryListAdapter countryListAdapter;
        String typeValue = type.equals("country") ? "country" : "code";
        if (i == 1) {
            countryListAdapter = new CountryListAdapter(mContext, countryListModelArrayList, typeValue);
        } else {
            countryListAdapter = new CountryListAdapter(mContext, countryListModelArrayList_Search, typeValue);
        }
        rvCountryList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvCountryList.setItemAnimator(new DefaultItemAnimator());
        rvCountryList.setAdapter(countryListAdapter);
    }

    private void titleSelectionDialog() {

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
        alt_bld.setTitle(R.string.select_title);
        alt_bld.setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mBinding.edtTitle.setText(items[item]);
                selectedIndex = item;
                dialog.dismiss();// dismiss the alertbox after chose option

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    private void callMyDetailsUpdateAPI() {
        HashMap<String, String> data = new HashMap<>();
        data.put("title", "" + mBinding.edtTitle.getText().toString());
        data.put("first_name", mBinding.edtFirstName.getText().toString());
        data.put("family_name", mBinding.edtFamilyName.getText().toString());
        data.put("country_name", "" + mBinding.edtCountryName.getText().toString());
        data.put("country_code", "" + mBinding.edtCountryCode.getText().toString());
        data.put("mobile_number", mBinding.edtPhone.getText().toString());
        data.put("birth_date", selectedDate);
        data.put("voucher_email", mBinding.edtEmail.getText().toString());
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.updateprofile(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Gson gson = new Gson();
                        UserDataModel userData = gson.fromJson(jsonObject.optJSONObject("payload").toString(), UserDataModel.class);
                        new Pref(mContext).setUserInfo(userData);
                        Toast.makeText(mContext, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
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

    private void callCountryList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<CountryListModel> call = apiService.getcountrylist(data);
        call.enqueue(new Callback<CountryListModel>() {
            @Override
            public void onResponse(Call<CountryListModel> call, Response<CountryListModel> response) {
                Utils.dismissProgress();
                if (response.body() != null) {
                    CountryListModel countryListModel = response.body();
                    countryListModelArrayList.addAll(countryListModel.getPayload());
                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<CountryListModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
                Utils.dismissProgress();
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
                        getActivity().getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
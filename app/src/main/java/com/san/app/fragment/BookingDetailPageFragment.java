package com.san.app.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.MerchantNoteListAdapter;
import com.san.app.adapter.MultiplePickImageListAdapter;
import com.san.app.adapter.OrderSummaryPackageQuantityAdapter;
import com.san.app.databinding.FragmentBookingDetailBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.MyOrderListModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.RecyclerItemClickListener;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;
import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;


public class BookingDetailPageFragment extends BaseFragment {


    //class object declaration..
    public FragmentBookingDetailBinding mBinding;
    View rootView;
    Context mContext;
    MyOrderListModel.Order myOrderListModel;
    Dialog listDialog;
    ArrayList<String> imageSelectedList = new ArrayList<>();
    ArrayList<MultipartBody.Part> partsList = new ArrayList<>();
    ArrayList<MyOrderListModel.Packagequantity> packagequantityArrayList = new ArrayList<>();
    ArrayList<MyOrderListModel.Note> noteArrayList = new ArrayList<>();
    MultiplePickImageListAdapter multiplePickImageListAdapter;
    OrderSummaryPackageQuantityAdapter orderSummaryPackageQuantityAdapter;
    MerchantNoteListAdapter merchantNoteListAdapter;
    Bitmap bitmap;
    RecyclerView rvImageList;
    //variable declaration.
    private String TAG = BookingDetailPageFragment.class.getSimpleName();
    private boolean isValid = true;
    private final int REQUEST_CAMERA = 200, SELECT_FILE = 201;
    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking_detail, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        }
        return rootView;
    }


    private void setUp() {
        if (getArguments() != null)
            if (!TextUtils.isEmpty(getArguments().getString("is_from")) && getArguments().getString("is_from").equals("notification")) {
                Utils.showProgressNormal(mContext);
                getBookingDetails(getArguments().getString("order_id"));
            } else {
                mBinding.rlMain.setVisibility(View.VISIBLE);
                myOrderListModel = new Gson().fromJson(getArguments().getString("myOrderListModel"), MyOrderListModel.Order.class);
                fillArea();
            }


    }

    private void fillArea() {
        mBinding.tvTitle.setText("" + myOrderListModel.getOrderNumber()); //Booking Ref Id
        mBinding.tvActivityName.setText(myOrderListModel.getActivityName());
        mBinding.tvParticipateDate.setText("" + myOrderListModel.getParticipationDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(myOrderListModel.getParticipationDate().toString().split("-")[1])) + " " + myOrderListModel.getParticipationDate().toString().split("-")[0]);
        mBinding.tvOrderPalcedDate.setText("" + myOrderListModel.getBookingDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(myOrderListModel.getBookingDate().toString().split("-")[1])) + " " + myOrderListModel.getBookingDate().toString().split("-")[0]);
        mBinding.tvPaymentSuccessfullDate.setText("" + myOrderListModel.getBookingDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(myOrderListModel.getBookingDate().toString().split("-")[1])) + " " + myOrderListModel.getBookingDate().toString().split("-")[0]);
        mBinding.tvPaymentSuccessfull.setText(myOrderListModel.getOrderPaymentStatus().equals("Pending") || myOrderListModel.getOrderPaymentStatus().equals("Failed") ? getString(R.string.pending_payment) : getString(R.string.payment_successful));
        if (!TextUtils.isEmpty(myOrderListModel.getStatusDate()))
            mBinding.tvLastStageDate.setText("" + myOrderListModel.getStatusDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(myOrderListModel.getStatusDate().toString().split("-")[1])) + " " + myOrderListModel.getStatusDate().toString().split("-")[0]);

        mBinding.cardRateActivity.setVisibility(View.GONE);
        mBinding.cardViewVoucher.setVisibility(View.GONE);
        if (myOrderListModel.getOrderStatus() == 0) {//0 = Pending ,1 = Canceled, 2 = Confirmed, 3 = Expired
            mBinding.tvLastStage.setText(R.string.pending);
            mBinding.imgLastStage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.order_pending));
        } else if (myOrderListModel.getOrderStatus() == 1) {
            mBinding.tvLastStage.setText(R.string.canceled);
            mBinding.imgLastStage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.order_cancel));
        } else if (myOrderListModel.getOrderStatus() == 2) {
            mBinding.tvLastStage.setText(R.string.confirmed);
            mBinding.imgLastStage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.order_confirm));
            //mBinding.cardRateActivity.setVisibility(myOrderListModel.getIsReviewGiven().equals("1") ? View.GONE : View.VISIBLE);
            mBinding.cardRateActivity.setVisibility(myOrderListModel.getIsReviewGiven().equals("1") ? View.GONE : myOrderListModel.getIsRedeem() == 1 ? View.VISIBLE : View.GONE);
            mBinding.cardViewVoucher.setVisibility(myOrderListModel.getIsRedeem() == 1 ? View.GONE : View.VISIBLE);
        } else {
            mBinding.tvLastStage.setText(R.string.expired);
            mBinding.imgLastStage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.order_expired));
        }


        packagequantityArrayList.addAll(myOrderListModel.getPackagequantity());
        double optionTotlaPrice = 0;
        for (int i = 0; i < packagequantityArrayList.size(); i++) {
            optionTotlaPrice = optionTotlaPrice + Double.parseDouble(packagequantityArrayList.get(i).getQuantityPrice()) * Double.parseDouble("" + packagequantityArrayList.get(i).getQuantity());
        }
        //order summary list
        orderSummaryPackageQuantityAdapter = new OrderSummaryPackageQuantityAdapter(mContext, packagequantityArrayList);
        mBinding.rvOptoinsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvOptoinsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvOptoinsList.setAdapter(orderSummaryPackageQuantityAdapter);


        mBinding.tvTotal.setText(getRMConverter(0.5f, getThousandsNotation("" + optionTotlaPrice)));


        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(mBinding.bottomSheet);

// change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// set the peek height
        bottomSheetBehavior.setPeekHeight(80);

// set hideable or not
        bottomSheetBehavior.setHideable(false);


// set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        //merchant note list
        mBinding.tvMerchantNote.setVisibility(myOrderListModel.getNotes().size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.rvMerchantNoteList.setVisibility(myOrderListModel.getNotes().size() > 0 ? View.VISIBLE : View.GONE);
        noteArrayList.addAll(myOrderListModel.getNotes());
        merchantNoteListAdapter = new MerchantNoteListAdapter(mContext, noteArrayList);
        mBinding.rvMerchantNoteList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvMerchantNoteList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvMerchantNoteList.setAdapter(merchantNoteListAdapter);
    }

    private void setOnClickListener() {


        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mBinding.tvRateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giverateOfActivity(mContext);
            }
        });

        mBinding.tvViewVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewVoucherFragment fragment = new ViewVoucherFragment();
                Bundle bundle = new Bundle();
                bundle.putString("voucher_url", "" + myOrderListModel.getVoucherUrl());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);

            }
        });
    }


    private void getBookingDetails(String orderID) {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Log.e(TAG, "orderID : REQ : " + orderID);
        Call<ResponseBody> call = apiService.customerbookingdetails(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), orderID, "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    mBinding.rlMain.setVisibility(View.VISIBLE);
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Gson gson = new Gson();
                        myOrderListModel = gson.fromJson(jsonObject.optJSONObject("payload").toString(), MyOrderListModel.Order.class);
                        fillArea();
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
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Utils.dismissProgress();
            }
        });
    }

    private void giverateOfActivity(final Context mContext) {
        final Dialog listDialog = new Dialog(mContext);
        final LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        listDialog.setContentView(R.layout.dialog_write_review);
        listDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = listDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(window.getAttributes());
        listDialog.getWindow().setGravity(Gravity.CENTER);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        final RatingBar ratingBar = (RatingBar) listDialog.findViewById(R.id.ratingBar);
        final EditText edtReview = (EditText) listDialog.findViewById(R.id.edtReview);
        CardView cardSubmit = (CardView) listDialog.findViewById(R.id.cardSubmit);
        rvImageList = (RecyclerView) listDialog.findViewById(R.id.rvImageList);
        ImageView imgCancel = (ImageView) listDialog.findViewById(R.id.imgCancel);
        imageSelectedList.clear();
        rvImageList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    if (imageSelectedList.size() < 6) {
                        imageSelectionDialog(mContext);
                    } else {
                        Toast.makeText(mContext, R.string.you_can_select_max_5_images, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }));
        imageSelectedList.add(0, Utils.getURLForResource(R.mipmap.plus_first_img));

        //multiple image selected list
        multiplePickImageListAdapter = new MultiplePickImageListAdapter(mContext, imageSelectedList);
        rvImageList.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvImageList.setItemAnimator(new DefaultItemAnimator());
        multiplePickImageListAdapter.onclickPosition(onClickPosition);
        rvImageList.setAdapter(multiplePickImageListAdapter);


        cardSubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                isValid = true;
                if (ratingBar.getRating() == 0) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_rating), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(edtReview.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_review), R.mipmap.red_cross_er);
                }
                if (isValid) {
                    RequestBody activity_id = RequestBody.create(MediaType.parse("text/plain"), "" + myOrderListModel.getActivityId());
                    RequestBody order_id = RequestBody.create(MediaType.parse("text/plain"), "" + myOrderListModel.getOrderId());
                    RequestBody rating = RequestBody.create(MediaType.parse("text/plain"), "" + ratingBar.getRating());
                    RequestBody lang_id = RequestBody.create(MediaType.parse("text/plain"), "" + Pref.getValue(mContext, APP_LANGUAGE, 0));
                    RequestBody description = RequestBody.create(MediaType.parse("text/plain"), edtReview.getText().toString());  //URLEncoder.encode(edtReview.getText().toString(), "utf-8")

                    if (imageSelectedList.size() > 0) {
                        for (int i = 1; i < imageSelectedList.size(); i++) {
                            partsList.add(prepareFilePart("review_images[]", imageSelectedList.get(i)));
                        }
                    }
                    Log.e("TestValid","000   " + edtReview.getText().toString().trim());
                    callSubmitReviewList(activity_id, order_id, rating, description, lang_id, partsList, listDialog);
                    Utils.showProgressNormal(mContext);
                }
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog.dismiss();
            }
        });

        listDialog.show();

    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    private void imageSelectionDialog(Context context) {
        listDialog = new Dialog(context);
        final LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        listDialog.setContentView(R.layout.photo_choose_dialog_layout);
        listDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = listDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(window.getAttributes());


        listDialog.getWindow().setGravity(Gravity.BOTTOM);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        TextView mCameraTv = (TextView) listDialog.findViewById(R.id.txtcamera);
        TextView mGelleryTv = (TextView) listDialog.findViewById(R.id.txtgallery);
        TextView mCancelTv = (TextView) listDialog.findViewById(R.id.cancel);
        if (imageSelectedList.size() == 0) imageSelectedList.clear();
        mCameraTv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

                    return;
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent, REQUEST_CAMERA);
                }

                listDialog.dismiss();
            }
        });

        mGelleryTv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);

                    return;
                } else {
                    Intent intent = new Intent(mContext, AlbumSelectActivity.class);
                    intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 6 - imageSelectedList.size());
                    startActivityForResult(intent, SELECT_FILE);
                }
                listDialog.dismiss();
            }
        });

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog.dismiss();
            }
        });


        listDialog.show();
    }

    private void callSubmitReviewList(RequestBody activity_id, RequestBody order_id, RequestBody rating, RequestBody description, RequestBody lang_id, ArrayList<MultipartBody.Part> partsList, final Dialog listDialog) {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call;
        call = apiService.reviewSubmitPOST(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), activity_id, order_id, rating, description, lang_id, partsList);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();

                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        mBinding.cardRateActivity.setVisibility(View.GONE);
                        listDialog.dismiss();

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
            for (int i = 0; i < imageSelectedList.size(); i++) {
                if (i == position) {
                    imageSelectedList.remove(i);
                }
            }
            //multiple image selected list
            multiplePickImageListAdapter = new MultiplePickImageListAdapter(mContext, imageSelectedList);
            rvImageList.setLayoutManager(new GridLayoutManager(mContext, 3));
            rvImageList.setItemAnimator(new DefaultItemAnimator());
            multiplePickImageListAdapter.onclickPosition(onClickPosition);
            rvImageList.setAdapter(multiplePickImageListAdapter);
            // multiplePickImageListAdapter.removeAt(position);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent, REQUEST_CAMERA);
                    listDialog.dismiss();
                } else {
                }
                return;
            }
            case SELECT_FILE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mContext, AlbumSelectActivity.class);
                    intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 6 - imageSelectedList.size());
                    startActivityForResult(intent, SELECT_FILE);
                    listDialog.dismiss();
                } else {
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult(data);
                } else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult(data);
                }
            }
        } catch (Exception e) {
            Log.e("Error", "");
            // TODO: handle exception
        }
    }


    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), "" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageSelectedList.add(destination.getAbsolutePath());
        multiplePickImageListAdapter.notifyDataSetChanged();
    }

    private void onSelectFromGalleryResult(Intent data) {

        try {
            ArrayList<com.darsh.multipleimageselect.models.Image> images = data.getParcelableArrayListExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            //imageSelectedList.clear();
            for (int i = 0; i < images.size(); i++) {
                imageSelectedList.add(images.get(i).path);
            }
            multiplePickImageListAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public MultipartBody.Part prepareFilePart(String partName, String sfile) {
        File file = new File(sfile);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), reqFile);

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
                        FragmentTransaction ft = fm.beginTransaction();
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

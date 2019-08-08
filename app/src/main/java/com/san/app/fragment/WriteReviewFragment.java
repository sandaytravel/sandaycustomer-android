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
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.MultiplePickImageListAdapter;
import com.san.app.databinding.FragmentWriteReviewBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.MyOrderListModel;
import com.san.app.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class WriteReviewFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentWriteReviewBinding mBinding;
    View rootView;
    Context mContext;
    Dialog listDialog;
    Bitmap bitmap;
    MyOrderListModel.Order myOrderListModel;
    MultiplePickImageListAdapter multiplePickImageListAdapter;
    ArrayList<String> imageSelectedList = new ArrayList<>();
    ArrayList<MultipartBody.Part> partsList = new ArrayList<>();
    //variable declaration.
    private String TAG = WriteReviewFragment.class.getSimpleName();
    private String imgPath = null;
    private final int REQUEST_CAMERA = 200, SELECT_FILE = 201;
    private boolean isValid = false;
    private int position=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_write_review, container, false);
        rootView = mBinding.getRoot();
        mContext = getActivity();

        setUp();
        setOnClickListener();
        return rootView;
    }


    private void setUp() {
        mBinding.tvRightTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
        mBinding.tvRightTitle.setEnabled(false);
        if (getArguments() != null) {
            position=getArguments().getInt("position");
            myOrderListModel = new Gson().fromJson(getArguments().getString("detail"), MyOrderListModel.Order.class);
            Glide.with(mContext)
                    .load(myOrderListModel.getActivityImage())
                    .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                            .error(R.color.login_btn_bg))
                    .into(mBinding.imgActivity);

            mBinding.tvActivityName.setText(myOrderListModel.getActivityName());
            mBinding.tvParticipateDate.setText(myOrderListModel.getParticipationDate());
        }


        //multiple image selected list
        multiplePickImageListAdapter = new MultiplePickImageListAdapter(mContext, imageSelectedList);
        mBinding.rvImageList.setLayoutManager(new GridLayoutManager(mContext, 3));
        mBinding.rvImageList.setItemAnimator(new DefaultItemAnimator());
        multiplePickImageListAdapter.onclickPosition(onClickPosition);
        mBinding.rvImageList.setAdapter(multiplePickImageListAdapter);

    }

    private void setOnClickListener() {
        mBinding.lnCamera.setOnClickListener(this);
        mBinding.imgBackView.setOnClickListener(this);
        mBinding.imgCancel.setOnClickListener(this);

        mBinding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    isValid = true;
                    mBinding.tvRightTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black_color));
                    mBinding.tvRightTitle.setEnabled(true);
                }
            }
        });
        mBinding.tvRightTitle.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                isValid = true;
                if (TextUtils.isEmpty(mBinding.edtReview.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), "Please provide description", R.mipmap.red_cross_er);
                }
                if (isValid) {
                    RequestBody activity_id = RequestBody.create(MediaType.parse("text/plain"), "" + myOrderListModel.getActivityId());
                    RequestBody order_id = RequestBody.create(MediaType.parse("text/plain"), "" + myOrderListModel.getOrderId());
                    RequestBody rating = RequestBody.create(MediaType.parse("text/plain"), "" + mBinding.ratingBar.getRating());
                    RequestBody description = RequestBody.create(MediaType.parse("text/plain"), mBinding.edtReview.getText().toString().trim());

                    if (imageSelectedList.size() > 0) {
                        for (int i = 0; i < imageSelectedList.size(); i++) {
                            partsList.add(prepareFilePart("review_images[]", imageSelectedList.get(i)));
                        }
                    }

                  //  callSubmitReviewList(activity_id, order_id, rating, description, partsList);
                    Utils.showProgressNormal(mContext);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.lnCamera) {
            if (imageSelectedList.size() < 6) {
                imageSelectionDialog(mContext);
            } else {
                Toast.makeText(mContext, "You can pic max upto 6 images.", Toast.LENGTH_SHORT).show();
            }

        }

        if (view == mBinding.imgBackView) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
        if (view == mBinding.imgCancel) {
            mBinding.imgSelected.setImageBitmap(null);
            mBinding.rlImage.setVisibility(View.VISIBLE);
            mBinding.imgCancel.setVisibility(View.GONE);
        }
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

        mCameraTv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA);

                    return;
                }else{
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
                if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            SELECT_FILE);

                    return;
                }else{
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

    /*private void callSubmitReviewList(RequestBody activity_id, RequestBody order_id, RequestBody rating, RequestBody description, ArrayList<MultipartBody.Part> partsList) {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call;
        call = apiService.reviewSubmitPOST(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), activity_id, order_id, rating, description,""+Pref.getValue(mContext,APP_LANGUAGE,0), partsList);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
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
*/
    OnClickPosition onClickPosition = new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            imageSelectedList.remove(position);
            multiplePickImageListAdapter.removeAt(position);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
        File destination = new File(Environment.getExternalStorageDirectory(),
                "" + System.currentTimeMillis() + ".jpg");
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
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

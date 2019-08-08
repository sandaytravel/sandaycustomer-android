package com.san.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.FacebookSdk;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.adapter.PopularDestinationAdapter;
import com.san.app.databinding.FragmentMyAccountBinding;
import com.san.app.model.UserDataModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Utils.setLocale;


public class MyAccountFragment extends BaseFragment implements View.OnClickListener {


    //class object declaration..
    FragmentMyAccountBinding mBinding;
    View rootView;
    Context mContext;
    PopularDestinationAdapter popularDestinationAdapter;
    private Dialog listDialog;
    private Dialog dialog1;
    Bitmap bitmap;
    private UserDataModel userDataModel;
    BottomSheetDialog mBottomSheetDialog;
    //variable declaration.
    private String TAG = MyAccountFragment.class.getSimpleName();
    private final int REQUEST_CAMERA = 200, SELECT_FILE = 201;
    final CharSequence[] items = {"English", "日本語", "한국어"};
    int selectedIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if (rootView == null) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false);
        rootView = mBinding.getRoot();
        mContext = getActivity();
        prepareView();
        setUp();
        /*} else {
            BottomNavigationBehavior bottomNavigationBehavior = new BottomNavigationBehavior();
            bottomNavigationBehavior.showBottomNavigationView(((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation);
        }*/
        //setLocale(mContext, Pref.getValue(mContext,Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en");
        return rootView;
    }

    private void prepareView() {
        if (Pref.getValue(mContext,Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1) {
            selectedIndex = 0;

        } else if (Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2) {
            selectedIndex = 1;
        } else if (Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3) {
            selectedIndex = 2;
        } else {
            selectedIndex = -1;
        }
        mBinding.tvSelectedLang.setText(items[selectedIndex]);
        //setLocale(getActivity(),"ja");
        userDataModel = new Pref(mContext).getUserInfo();
        /*Glide.with(mContext)
                .load(ContextCompat.getDrawable(mContext,R.mipmap.account_bg))
                .apply(new RequestOptions().placeholder(R.color.bg_white)
                        .error(R.color.bg_white))
                .into(mBinding.imgBg);*/
        if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_USER_PROFILE_PIC, ""))) {
            Glide.with(mContext).load(Pref.getValue(mContext, Constants.PREF_USER_PROFILE_PIC, "")).apply(new RequestOptions().placeholder(R.mipmap.user_img).error(R.mipmap.user_img)).into(mBinding.imgUserImg);
        }
        if (userDataModel != null) {
            mBinding.lnChangePwd.setVisibility(userDataModel.registration_type.equals("1") ? View.VISIBLE : View.GONE);
            mBinding.vChangePwd.setVisibility(userDataModel.registration_type.equals("1") ? View.VISIBLE : View.GONE);
        }
        mBinding.tvGuest.setVisibility(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")) ? View.GONE : View.VISIBLE);
        mBinding.tvUserName.setVisibility(!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, "")) ? View.VISIBLE : View.GONE);
        if (TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
            mBinding.lnMyDetails.setVisibility(View.GONE);
            mBinding.vMyDetail.setVisibility(View.GONE);
            mBinding.lnChangePwd.setVisibility(View.GONE);
            mBinding.vChangePwd.setVisibility(View.GONE);
            mBinding.lnLogout.setVisibility(View.GONE);
            //mBinding.lnAbout.setVisibility(View.GONE);
            //mBinding.vAbout.setVisibility(View.GONE);
            //mBinding.vLogout.setVisibility(View.GONE);
        }
    }


    private void setUp() {
        mBinding.imgUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    if (userDataModel != null) {
                        if (userDataModel.registration_type.equals("1"))
                            imageSelectionDialog(mContext);
                    }
                } else {
                    openLoginView(mContext);
                }
            }
        });

        mBinding.lnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    changeFragment_back(new NotificationListFragment());
                } else {
                    openLoginView(mContext);
                }
            }
        });

        mBinding.lnMyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment_back(new MyDetailEditFragment());
            }
        });

        mBinding.lnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    changeFragment_back(new WishlistFragment());
                } else {
                    openLoginView(mContext);
                }

            }
        });

        mBinding.lnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageDialog(mContext);
            }
        });

        mBinding.lnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment_left_right(new EditProfileFragment());
            }
        });

        mBinding.lnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment_back(new ChangePasswordFragment());
            }
        });

        mBinding.lnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment_back(new AboutUsFragment());
            }
        });

        mBinding.tvGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginView(mContext);
            }
        });

        mBinding.lnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog(mContext);
            }
        });

    }


    @Override
    public void onClick(View view) {


    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.setVisibility(View.VISIBLE);
        ((DashboardActivity) mContext).hideShowBottomNav(true);
        ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_account).setChecked(true);
        if (!TextUtils.isEmpty(Pref.getValue(mContext, "isFreshLogin", ""))) {
            Pref.setValue(mContext, "isFreshLogin", "");
            refreshFragment();
        }
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    private void changeLanguageDialog(final Context mContext) {
        /*mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.package_options_edit_dialog, null);
        mBottomSheetDialog.setContentView(sheetView);

        mBottomSheetDialog.show();*/

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
        alt_bld.setTitle(R.string.select_language);
        alt_bld.setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mBinding.tvSelectedLang.setText(items[item]);
                selectedIndex = item;
                setLocale(mContext, selectedIndex == 0 ? "en" : selectedIndex == 1 ? "ja" : selectedIndex == 2 ? "ko" : "en",0);
                //getMyContext(mContext,selectedIndex == 0 ? "en" : selectedIndex == 1 ? "ja" : selectedIndex == 2 ? "ko" : "en");
                if(!TextUtils.isEmpty(Pref.getValue(mContext,Constants.PREF_APP_TOKEN,""))){
                    Utils.showProgressNormal(mContext);
                    callChangeLanguage();
                }else{
                    refreshFragment();
                }
                dialog.dismiss();// dismiss the alertbox after chose option

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void callChangeLanguage() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", "" + Pref.getValue(mContext,Constants.APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.languageselect(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""),data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        refreshFragment();
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
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, REQUEST_CAMERA);
                listDialog.dismiss();
            }
        });

        mGelleryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
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
        mBinding.imgUserImg.setImageBitmap(bitmap);
        Utils.showProgressNormal(mContext);
        callUpdateImageProfileAPI();
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(FacebookSdk.getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBinding.imgUserImg.setImageBitmap(bitmap);
        Utils.showProgressNormal(mContext);
        callUpdateImageProfileAPI();
    }


    private void logoutDialog(final Context mContext) {
        dialog1 = new Dialog(mContext, R.style.PauseDialog);
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog1.setContentView(R.layout.cust_logout_dialog);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog1.show();
        dialog1.getWindow().setAttributes(lp);

        dialog1.getWindow().setGravity(Gravity.CENTER);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));


        TextView mYesLogoutTv = (TextView) dialog1.findViewById(R.id.tv_yes);
        TextView mCancelTv = (TextView) dialog1.findViewById(R.id.tv_cancle);


        mYesLogoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLogoutAPI();
                dialog1.dismiss();

            }
        });


        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        dialog1.show();
    }

    private void callLogoutAPI() {
        HashMap<String, String> data = new HashMap<>();
        data.put("language_id", "" + Pref.getValue(mContext,Constants.APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.logout(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""),data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Pref.deleteAll(mContext);
                        getActivity().finishAffinity();
                        startActivity(new Intent(getActivity(), DashboardActivity.class));

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

    private void callUpdateImageProfileAPI() {
        File file = Utils.saveBitmap(bitmap);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = null;
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("profile_img", file.getName(), requestFile);
            call = apiService.updateprofilepic(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), body);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Pref.setValue(mContext, Constants.PREF_USER_PROFILE_PIC, jsonObject.optString("profile_img"));
                        Glide.with(mContext).load(Pref.getValue(mContext, Constants.PREF_USER_PROFILE_PIC, "")).apply(new RequestOptions().placeholder(R.mipmap.user_img).error(R.mipmap.user_img)).into(mBinding.imgUserImg);
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

    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
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

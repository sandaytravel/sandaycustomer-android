package com.san.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.san.app.R;
import com.san.app.adapter.CartListingAdapter;
import com.san.app.databinding.FragmentEditProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class EditProfileFragment extends BaseFragment {


    //class object declaration..
    FragmentEditProfileBinding mBinding;
    View rootView;
    Context mContext;
    CartListingAdapter cartListingAdapter;
    private Dialog listDialog;
    private Dialog dialog1;
    Bitmap bitmap;

    //variable declaration.
    private String TAG = EditProfileFragment.class.getSimpleName();
    private boolean isValid = true;
    private final int REQUEST_CAMERA = 200, SELECT_FILE = 201;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_edit_profile, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
        }
        return rootView;
    }


    private void setUp() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mBinding.llChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelectionDialog(mContext);
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
    public void onResume() {
        super.onResume();

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
        mBinding.imgAvtar.setImageBitmap(bitmap);

    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(FacebookSdk.getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBinding.imgAvtar.setImageBitmap(bitmap);

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

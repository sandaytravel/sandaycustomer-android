package com.san.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.activity.MainActivity;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

import static com.san.app.util.Utils.setLocale;


public class BaseFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(getActivity(), Pref.getValue(getActivity(), Constants.APP_LANGUAGE,0)==0 || Pref.getValue(getActivity(),Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(getActivity(),Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(getActivity(),Constants.APP_LANGUAGE,3)==3 ? "ko" : "en",0);
        //getMyContext(getActivity(),Pref.getValue(getActivity(), Constants.APP_LANGUAGE,0)==0 || Pref.getValue(getActivity(),Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(getActivity(),Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(getActivity(),Constants.APP_LANGUAGE,3)==3 ? "ko" : "en");
    }

    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    /*public Context getMyContext(){
        return MyContextWrapper.wrap(getContext(),"fr");
    }*/
    public void openLoginView(Context mContext) {
        startActivity(new Intent(mContext, MainActivity.class));
        getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        }
    }

    public void changeFragment_back(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frame, targetFragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void changeFragment_up_bottom(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.bottom_up, 0, 0, R.anim.bottom_down);
        transaction.replace(R.id.frame, targetFragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void changeFragment_left_right(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_slide_in_left, R.anim.nothing, R.anim.nothing, R.anim.activity_slide_out_right);
        transaction.replace(R.id.frame, targetFragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public void changeFragment(Fragment targetFragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }

    public void replaceFragment(Fragment frag) {
        FragmentManager manager = getFragmentManager();
        if (manager != null){
            FragmentTransaction t = manager.beginTransaction();
            Fragment currentFrag = manager.findFragmentById(R.id.frame);
            t.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //Check if the new Fragment is the same
            //If it is, don't add to the back stack
            if (currentFrag != null && currentFrag.getClass().equals(frag.getClass())) {
                t.replace(R.id.frame, frag).commit();
            } else {
                t.replace(R.id.frame, frag).addToBackStack(null).commit();
            }
        }
    }


    public void customToast(String msg, int imgResource) {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast, null, false);
        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);//setting the view of custom toast layout
        toast.show();
    }

    public void customToastError(String status, String msg, int imgResource) {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast_error, null, false);
        CardView cardView = v.findViewById(R.id.card_view);

        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        TextView txtstatus = (TextView) v.findViewById(R.id.txtstatus);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        txtstatus.setText(status);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(getActivity());
        toast.setView(v);//setting the view of custom toast layout
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 100);

        toast.show();
    }

    public void errorBody(ResponseBody responseBody) {

        try {
            String res = responseBody.string();
            JSONObject jsonObject = new JSONObject(res);
            new FieldsValidator(getActivity()).customToast(jsonObject.getString("message"), R.mipmap.cancel_toast_new);
            if (jsonObject.optString("code").equals("500")) {
                Pref.deleteAll(getContext());
                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), DashboardActivity.class));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}

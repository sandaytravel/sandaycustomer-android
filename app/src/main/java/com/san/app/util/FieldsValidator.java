package com.san.app.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.san.app.R;


public class FieldsValidator {

    private static Context context;

    public FieldsValidator(Context context) {
        this.context = context;
    }


    public void customToast(String msg, int imgResource) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast1, null, false);
        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);//setting the view of custom toast layout
        toast.show();
    }

    public void customToast(String msg, int imgResource, boolean isAdded) {
        if (!isAdded) {
            return;
        }
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast1, null, false);
        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);//setting the view of custom toast layout
        toast.show();
    }


}

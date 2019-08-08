package com.san.app.network;

import android.app.Activity;
import android.widget.Toast;

import com.san.app.R;
import com.san.app.util.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by user on 11/5/2017.
 */


public class ConnectivityInterceptor implements Interceptor {

    private Activity mContext;

    public ConnectivityInterceptor(Activity context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(mContext)) {

          mContext.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                 // new FieldsValidator(mContext).showtoast(mContext.getString(R.string.NO_INTERNET_CONNECTION));
                  Toast.makeText(mContext, ""+mContext.getString(R.string.NO_INTERNET_CONNECTION), Toast.LENGTH_SHORT).show();
                  Utils.dismissProgress();
              }
          });


            throw new NetworkUtil.NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

}


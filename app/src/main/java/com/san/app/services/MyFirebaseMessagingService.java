package com.san.app.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.san.app.R;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Dharmesh on 19/2/2019.
 */

//class extending FirebaseMessagingService
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

       // Log.d("MyNotification", remoteMessage.getNotification().toString());

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        Log.d("MyNotification","44444    " + remoteMessage.getData().toString());
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.d("MyNotification","999    " + object.toString());
        String order_id = object.optString("order_id");
        String message=object.optString("message");
        if (remoteMessage.getData().size() > 0) {
            //handle the data message here
        }

        //getting the title and the body
       // String title = remoteMessage.getNotification().getTitle();
       // String body = remoteMessage.getNotification().getBody();

        //then here we can use the title and body to build a notification

        MyNotificationManager.getInstance(getApplicationContext()).displayNotification(getString(R.string.app_name), message,order_id);
       // Log.d("MyNotification", remoteMessage.getNotification().toString());
       // Log.d("MyNotification", remoteMessage.getNotification().toString());
    }
}

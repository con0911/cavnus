package com.android.keepfocus.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.ChildSchedulerActivity;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.utils.MainUtils;
import com.google.android.gms.gcm.GcmListenerService;


/**
 * Created by sev_user on 9/20/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */
    private MainDatabaseHelper kFDHelper;

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String message = bundle.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        sendNotification(message);
        kFDHelper = new MainDatabaseHelper(
                getApplicationContext());
        ChildKeepFocusItem childProfile = kFDHelper.getAllKeepFocusFromDb().get(0);
        if(message.equals("TRUE")){
            if(childProfile != null) {
                Log.d(TAG, "ON childKeepFocusItem");
                childProfile.setActive(true);
                childProfile.setNameFocus(message);
            }

        } else {
            if(childProfile != null) {
                Log.d(TAG, "OFF childKeepFocusItem");
                childProfile.setActive(false);
                childProfile.setNameFocus(message);
            }
        }
        kFDHelper.updateFocusItem(childProfile);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, ChildSchedulerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.email)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}


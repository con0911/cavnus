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
import com.android.keepfocus.activity.DeviceMemberManagerment;
import com.android.keepfocus.activity.JoinGroupActivity;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.server.request.controllers.NotificationController;
import com.android.keepfocus.utils.MainUtils;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sev_user on 9/20/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    public static String DELETE_NOTI = "del";
    public static String CREATE_NOTI = "create";
    public static String UPDATE_NOTI = "update";
    public static String JOIN_GROUP = "join";
    public static String MANAGER = "manager";
    public static String CHILDREN = "child";
    private ChildKeepFocusItem childProfile;
    private String family_id;



    private MainDatabaseHelper mDataHelper;
    private String contentNotification = "";

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    private NotificationController notificationController;


    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String message = bundle.getString("message");
        String title = bundle.getString("tickerText");
        family_id = bundle.getString("Family_ID");

        if(title.equals("")) {
            title = bundle.getString("title");
        }
        //String title2 = bundle.getString("title");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "bundle: " + bundle);
        Log.d(TAG, "Message: " + message);
        mDataHelper = new MainDatabaseHelper(getApplicationContext());


        handleNotification(title, message);

        //sendNotification(message, title);



/*
        {"Data":{
            "Name": "ABDC",
                    "Active": "FALSE",
                    "Day": "Mon, Tue, Sun"
        }}*/

        //sendNotification(message);

        /*kFDHelper = new MainDatabaseHelper(
                getApplicationContext());
        ChildKeepFocusItem childProfile;
        if (kFDHelper.getAllKeepFocusFromDb().size() == 0) {
            childProfile = new ChildKeepFocusItem();
            kFDHelper.addNewFocusItem(childProfile);
        } else {
            childProfile = kFDHelper.getAllKeepFocusFromDb().get(0);
        }

                JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(message);
            JSONObject data = jsonObj.getJSONObject("Data");
            String name_Keepfocus = data.getString("Name");
            String active = data.getString("Active");
            String day = data.getString("Day");
            *//*String appPackage = data.getString("Application");
            ChildAppItem app1 = new ChildAppItem();
            try {
                String appName = appName = (String) getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(appPackage, PackageManager.GET_META_DATA));
                app1.setNamePackage(appName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            ArrayList<ChildAppItem> listApp = childProfile.getListAppFocus();
            boolean conflict = false;
            for(int i = 0; i < listApp.size(); i++){
                if(listApp.get(i).getNamePackage() == appPackage) conflict = true;
            }
            if(!conflict) kFDHelper.addAppItemToDb(app1, childProfile.getKeepFocusId());*//*

            childProfile.setNameFocus(name_Keepfocus);
            childProfile.setDayFocus(day);
            if(active.equals("TRUE")){
                childProfile.setActive(true);
            } else {
                childProfile.setActive(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        kFDHelper.updateFocusItem(childProfile);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);*/
    }

    public void handleNotification(String title, String message) {
        String titleText = title;
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(message);

            if(titleText.equals(DELETE_NOTI)){
                //Delete
            } else if (titleText.equals(CREATE_NOTI)){
                Log.d(TAG, "create: " + jsonObj);
                //Create
                createNewScheduler(message);
            } else if (titleText.equals(UPDATE_NOTI)){
                //Update
            } else if (titleText.equals(JOIN_GROUP)){
                //Join
                setJoinGroup(jsonObj);
            } else {
                //Another
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isActive(int state){
        if(state == 1) return true;
        else return false;
    }


    public void createNewScheduler(String message) throws JSONException {
        JSONObject data = new JSONObject(message);
        childProfile = new ChildKeepFocusItem();
        childProfile.setNameFocus(data.getString("scheduler_name"));
        childProfile.setActive(isActive(data.getInt("isActive")));
        childProfile.setDayFocus(data.getString("days"));
        childProfile.setKeepFocusId(data.getInt("id"));


        childProfile = new ChildKeepFocusItem();
        mDataHelper.addNewFocusItem(childProfile);
        //mDataHelper.updateFocusItem(childProfile);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);
        sendNotificationCreate("", "New scheduler create");
    }

    public void updateScheduler(String message) throws JSONException {
        JSONObject data = new JSONObject(message);
        childProfile = mDataHelper.getAllKeepFocusFromDb().get(0);
        childProfile.setNameFocus(data.getString("scheduler_name"));
        childProfile.setActive(isActive(data.getInt("isActive")));
        childProfile.setDayFocus(data.getString("days"));
        childProfile.setKeepFocusId(data.getInt("id"));


        childProfile = new ChildKeepFocusItem();
        mDataHelper.addNewFocusItem(childProfile);
        //mDataHelper.updateFocusItem(childProfile);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);
        sendNotificationCreate("", "New scheduler create");
    }



    public void setJoinGroup(JSONObject data) throws JSONException {

        ParentMemberItem joinDevice = new ParentMemberItem();
        //String group_code = data.getString("group_code");//not have now

        String group_code = "MKXS7E";//for test
        if(family_id !=null) {
            MainUtils.parentGroupItem = mDataHelper.getGroupByCode(family_id);

        }
        //MainUtils.parentGroupItem = mDataHelper.getGroupByCode(group_code);

        //for test

        if (MainUtils.parentGroupItem == null) {
            MainUtils.parentGroupItem = mDataHelper.getAllGroupItemParent().get(0);//add to first
        }
        //ParentGroupItem joinToGroup = new ParentGroupItem();
        Log.d(TAG,"join To Group "+MainUtils.parentGroupItem.getGroup_name());
        //MainUtils.parentGroupItem = joinToGroup;

        int type;
        if(data.getString("device_mode").equals(JoinGroupActivity.MANAGER)) {
            type = 1;
        } else type = 0;
        try {
            joinDevice.setId_member_server(data.getInt("id"));
            joinDevice.setName_member(data.getString("device_name"));
            joinDevice.setType_member(type);

            mDataHelper.addMemberItemParent(joinDevice,MainUtils.parentGroupItem.getId_group());
            MainUtils.parentGroupItem.getListMember().add(joinDevice);
            mDataHelper.makeDetailOneGroupItemParent(MainUtils.parentGroupItem);

            contentNotification = "Device "+ data.getString("device_name")
                    + ", Model " + data.getString("device_model")
                    + ", Mode " + data.getString("device_mode")
                    + ", Type " + data.getString("device_type");

            sendNotification(contentNotification, "A device join your family");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String message, String title) {
        Intent intent = new Intent(this, DeviceMemberManagerment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.email)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotificationCreate(String message, String title) {
        Intent intent = new Intent(this, ChildSchedulerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.email)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}


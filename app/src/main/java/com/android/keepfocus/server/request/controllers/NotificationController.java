package com.android.keepfocus.server.request.controllers;

import android.content.Context;

import com.android.keepfocus.activity.JoinGroupActivity;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sion on 11/10/2016.
 */
public class NotificationController {

    public static String DELETE_NOTI = "del";
    public static String CREATE_NOTI = "create";
    public static String UPDATE_NOTI = "update";
    public static String JOIN_GROUP = "join";
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private String contentNotification = "";

    public NotificationController(Context context){
        this.mContext = context;
        mDataHelper = new MainDatabaseHelper(context);

    }

    public void handleNotification(String title, String message) {
        String titleText = title;
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(message);

            if(titleText.equals(DELETE_NOTI)){
                //Delete
            } else if (titleText.equals(CREATE_NOTI)){
                //Create
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

    public void setJoinGroup(JSONObject data) throws JSONException {

        ParentMemberItem joinDevice = new ParentMemberItem();
        String group_code = "";//not have now
        ParentGroupItem joinToGroup = mDataHelper.getGroupByCode(group_code);

        int type;
        if(data.getString("device_mode").equals(JoinGroupActivity.MANAGER)) {
            type = 1;
        } else type = 0;
        try {
            joinDevice.setId_member_server(data.getInt("id"));
            joinDevice.setName_member(data.getString("device_name"));
            joinDevice.setType_member(type);
            //joinDevice.setId_member(joinToGroup.getId_group());

            joinToGroup.getListMember().add(joinDevice);

            contentNotification = "Device "+ data.getString("device_name")
                    + ", Model " + data.getString("device_model")
                    + ", Mode " + data.getString("device_mode")
                    + ", Type " + data.getString("device_type");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDataHelper.makeDetailOneGroupItemParent(joinToGroup);
    }

}

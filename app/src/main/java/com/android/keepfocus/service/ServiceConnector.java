package com.android.keepfocus.service;

import com.android.keepfocus.server.model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 9/11/2016.
 */
public class ServiceConnector {
    public static final String GROUP_BASE_URL = "104.156.224.47/api/group?pRequest=";
    static JSONObject account;
    public static boolean checkLogin(String email, String password, String deviceId){
        String tempEmail = "vinhbka92@gmail.com";
        String tempPassword = "02051992";
        String tempDeviceId = "abc123";

        //test to check account is exist or not
        account = new JSONObject();
        try {
            account.put("email", email);
            account.put("password", password);
            account.put("deviceId", "abc123");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(account);
        boolean isSuccessful = false;
        try {
            if (account.getString("email").compareTo(tempEmail) == 0
                    && account.getString("password").compareTo(tempPassword) == 0
                    && account.getString("deviceId").compareTo(tempDeviceId) == 0){
                isSuccessful = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }



    //=====================Block code for connect Group api==================================
    public ArrayList<Group>  getAllGroupByUser(String jsonRequest){
        ArrayList<Group> listGroup = new ArrayList<>();





        return listGroup;
    }

    public void createGroup(String jsonRequest){

    }


    //=====================End Block code for connect Group api==================================
}

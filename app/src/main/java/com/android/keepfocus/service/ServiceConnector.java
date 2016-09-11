package com.android.keepfocus.service;

import com.android.keepfocus.activity.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 9/11/2016.
 */
public class ServiceConnector {
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
            if (account.getString("email").compareTo(email) == 0
                    && account.getString("password").compareTo(password) == 0
                    && account.getString("deviceId").compareTo(deviceId) == 0){
                isSuccessful = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }

}

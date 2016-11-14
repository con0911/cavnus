package com.android.keepfocus.server.request.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Scheduler;
import com.android.keepfocus.server.model.TimeItems;
import com.android.keepfocus.server.request.model.SchedulerRequest;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sev_user on 11/12/2016.
 */
public class SchedulerRequestController {
    public static final String BASE_URL = "http://45.32.103.87/api/scheduler?pRequest=";
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 10000;
    private SchedulerRequest schedulerRequest;
    private String TAG = "SchedulerRequestController";
    private Context mContext;
    private MainDatabaseHelper mDataHelper;
    private SharedPreferences joinPref;
    private ArrayList <TimeItems> listTimeItems;

    public SchedulerRequestController(Context context) {
        this.mContext = context;
        mDataHelper = new MainDatabaseHelper(context);
        joinPref = PreferenceManager.getDefaultSharedPreferences(context);
        //registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
    }

    public void addNewScheduler() {
        AddSchedulerAsynTask updateAsyn = new AddSchedulerAsynTask(Constants.ActionTypeCreate);
        updateAsyn.execute();
    }

    public void updateScheduler() {
        AddSchedulerAsynTask updateAsyn = new AddSchedulerAsynTask(Constants.ActionTypeUpdate);
        updateAsyn.execute();
    }

    public void deleteScheduler() {
        AddSchedulerAsynTask updateAsyn = new AddSchedulerAsynTask(Constants.ActionTypeDelete);
        updateAsyn.execute();
    }

    private int isActive(boolean active){
        if(active) return 1;
        else return 2;
    }


    public String createScheduler(ParentProfileItem profileItem){

        Scheduler schedulerItem = new Scheduler(0, profileItem.getName_profile(),
                profileItem.getDay_profile(), isActive(profileItem.isActive()), 0);
        ParentMemberItem device = MainUtils.memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(),"samsung","android","","","child");
        ArrayList<TimeItems> timeItem = getListTime(profileItem);
        schedulerRequest = new SchedulerRequest(schedulerItem, deviceItem, timeItem, Constants.RequestTypeUpdate,Constants.ActionTypeCreate);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(schedulerRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String updateScheduler(ParentProfileItem profileItem){

        Scheduler schedulerItem = new Scheduler(profileItem.getId_profile_server(), profileItem.getName_profile(),
                profileItem.getDay_profile(), isActive(profileItem.isActive()), 0);
        ParentMemberItem device = MainUtils.memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(),"samsung","android","","","child");
        ArrayList<TimeItems> timeItem = getListTime(profileItem);
        schedulerRequest = new SchedulerRequest(schedulerItem, deviceItem, timeItem, Constants.RequestTypeUpdate,Constants.ActionTypeCreate);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(schedulerRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String deleteScheduler(ParentProfileItem profileItem){

        Scheduler schedulerItem = new Scheduler(profileItem.getId_profile_server(), profileItem.getName_profile(),
                profileItem.getDay_profile(), isActive(profileItem.isActive()), 0);
        ParentMemberItem device = MainUtils.memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(),"samsung","android","","","child");
        ArrayList<TimeItems> timeItem = getListTime(profileItem);
        schedulerRequest = new SchedulerRequest(schedulerItem, deviceItem, timeItem, Constants.RequestTypeUpdate,Constants.ActionTypeCreate);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(schedulerRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }


    private class AddSchedulerAsynTask extends AsyncTask<ParentProfileItem, Void, String> {
        ProgressDialog mDialog;
        String request = "";
        int typeRequest = 0;
        AddSchedulerAsynTask(int type){
            this.typeRequest = type;
            if (type == Constants.ActionTypeCreate){
                request = createScheduler(MainUtils.parentProfile);
            } else if (type == Constants.ActionTypeUpdate){
                request = updateScheduler(MainUtils.parentProfile);
            } else if (type == Constants.ActionTypeDelete) {
                request = deleteScheduler(MainUtils.parentProfile);
            }
        }
        @Override
        protected String doInBackground(ParentProfileItem... params) {
            String result = "";
            String link;
            link = BASE_URL + request;
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,createGroup());


            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG,"onPostExecute"+result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    int status = message.getInt("Status");
                    Log.d(TAG,"status1 = "+status);
                    //JSONObject data = jsonObj.getJSONObject("Data");
                    if(status == 1) {
                        if (typeRequest == Constants.ActionTypeCreate){//create
                            MainUtils.parentProfile.setId_profile(mDataHelper.addProfileItemParent(MainUtils.parentProfile,MainUtils.memberItem.getId_member()));
                            MainUtils.memberItem.getListProfile().add(MainUtils.parentProfile);
                            mDataHelper.updateProfileItem(MainUtils.parentProfile);
                        } else if (typeRequest == Constants.ActionTypeUpdate){ // update
                            mDataHelper.updateProfileItem(MainUtils.parentProfile);
                        } else if (typeRequest == Constants.ActionTypeDelete) {
                            mDataHelper.deleteProfileItemById(MainUtils.parentProfile.getId_profile());
                            MainUtils.memberItem.getListProfile().remove(MainUtils.parentProfile);
                        }

                        updateSuccess();
                    } else {
                        Toast.makeText(mContext, "Error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't create new scheduler! Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check internet!", Toast.LENGTH_SHORT).show();
            }
            mDialog.dismiss();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
            mDialog.setInverseBackgroundForced(false);
            mDialog.setMessage("Request to server...");
            mDialog.show();
        }
    }

    public void updateSuccess(){
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_SCHEDULER);
        mContext.sendBroadcast(intent);
    }



    public ArrayList <TimeItems> getListTime(ParentProfileItem scheduler){
        listTimeItems = new ArrayList<TimeItems>();
        ArrayList<ParentTimeItem> listTime = scheduler.getListTimer();
        for (int i =0; i< listTime.size(); i++) {
            ParentTimeItem item1 = listTime.get(i);
            TimeItems timeIt = new TimeItems(0,item1.getHourBegin(),item1.getMinusBegin(),item1.getHourEnd(),item1.getMinusEnd(),0);
            listTimeItems.add(timeIt);
        }
        return listTimeItems;
    }

    public String connectToServer(String urlRequest){
        try {
            URL url = new URL(urlRequest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
            connection.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            String streamToString = convertStreamToString(is);
            return streamToString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String convertStreamToString(InputStream is){

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

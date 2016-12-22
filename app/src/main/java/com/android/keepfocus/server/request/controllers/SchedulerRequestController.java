package com.android.keepfocus.server.request.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.activity.ChildSchedulerActivity;
import com.android.keepfocus.activity.JoinGroupActivity;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ChildTimeItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Scheduler;
import com.android.keepfocus.server.model.TimeItems;
import com.android.keepfocus.server.request.model.DeviceRequest;
import com.android.keepfocus.server.request.model.SchedulerRequest;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SchedulerRequestController {
    public static final String BASE_URL = "http://45.63.21.174/api/scheduler?pRequest=";
    private static final int NET_READ_TIMEOUT_MILLIS = 20000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 20000;
    private SchedulerRequest schedulerRequest;
    private DeviceRequest deviceRequest;
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

    public void restoreSchedulerForChild(){
        RestoreScheduleAsynTask restoreAsyn = new RestoreScheduleAsynTask();
        restoreAsyn.execute();
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
        schedulerRequest = new SchedulerRequest(schedulerItem, deviceItem, timeItem, Constants.RequestTypeUpdate,Constants.ActionTypeUpdate);

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
        schedulerRequest = new SchedulerRequest(schedulerItem, deviceItem, timeItem, Constants.RequestTypeUpdate,Constants.ActionTypeDelete);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(schedulerRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String restoreSchedulerForChild(ChildKeepFocusItem profileItem){
        ChildKeepFocusItem child = MainUtils.childKeepFocusItem;
        int id_profile_server = JoinGroupActivity.getChildProfileIdServer(mContext);
        Device deviceItem = new Device(id_profile_server, "","samsung","android","","","child");
        schedulerRequest = new SchedulerRequest(Constants.RequestTypeGet, deviceItem);

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
                            JSONObject data = jsonObj.getJSONObject("Data");
                            JSONObject scheduler = data.getJSONObject("Scheduler");
                            MainUtils.parentProfile.setId_profile_server(scheduler.getInt("id"));
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

    public void updateSuccess() {
        Log.d(TAG,"send broadcast UpdateCheduler");
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_SCHEDULER);
        mContext.sendBroadcast(intent);
    }

    //=============== Restore scheduler for Child replaced device ==================
    private class RestoreScheduleAsynTask extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + restoreSchedulerForChild(MainUtils.childKeepFocusItem);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG,"onPostExecute : "+result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    JSONArray data = jsonObj.getJSONArray("Data");
                    MainUtils.childKeepFocusItem = new ChildKeepFocusItem();
                    if(status == 1) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject scheduleItem = data.getJSONObject(i);
                            MainUtils.childKeepFocusItem.setNameFocus(scheduleItem.getString("scheduler_name"));
                            MainUtils.childKeepFocusItem.setDayFocus(scheduleItem.getString("days"));
                            MainUtils.childKeepFocusItem.setActive(scheduleItem.getString("isActive").equals("1"));
                            mDataHelper.addNewFocusItem(MainUtils.childKeepFocusItem);
                            Log.e(TAG, "MainUtils.childKeepFocusItem.getKeepFocusId() : " + MainUtils.childKeepFocusItem.getKeepFocusId());
                            JSONArray timeItems = scheduleItem.getJSONArray("timeitems");
                            for (int j = 0; j < timeItems.length(); j++){
                                JSONObject timeItem = timeItems.getJSONObject(j);
                                Log.e(TAG, "timeItem : "+ timeItems.getJSONObject(j));
                                ChildTimeItem childTimeItem = new ChildTimeItem();
                                childTimeItem.setHourBegin(timeItem.getInt("start_hours"));
                                childTimeItem.setMinusBegin(timeItem.getInt("start_minutes"));
                                childTimeItem.setHourEnd(timeItem.getInt("end_hours"));
                                childTimeItem.setMinusEnd(timeItem.getInt("end_minutes"));
                                mDataHelper.addTimeItemToDb(childTimeItem, MainUtils.childKeepFocusItem.getKeepFocusId());
                            }
                            //mDataHelper.addNewFocusItem(MainUtils.childKeepFocusItem);
                            Intent schedule = new Intent(mContext, ChildSchedulerActivity.class);
                            schedule.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(schedule);
                        }
                        updateSuccess();
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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
            Log.d(TAG,"err "+e);
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

    public void testBlockAllRequest(ParentMemberItem memberItem) {
        MainUtils.memberItemForBlockAll = memberItem;
        BlockAllAsynTask blockAsyn = new BlockAllAsynTask();
        blockAsyn.execute();
    }

    public void testUnBlockAllRequest(ParentMemberItem memberItem) {
        MainUtils.memberItemForBlockAll = memberItem;
        UnBlockAllAsynTask unBlockAsyn = new UnBlockAllAsynTask();
        unBlockAsyn.execute();
    }

    public void testAllowAllRequest(ParentMemberItem memberItem) {
        MainUtils.memberItemForBlockAll = memberItem;
        AllowAllAsynTask allowAllAsyn = new AllowAllAsynTask();
        allowAllAsyn.execute();
    }

    public void testUnAllowAllRequest(ParentMemberItem memberItem) {
        MainUtils.memberItemForBlockAll = memberItem;
        UnAllowAllAsynTask unAllowAsyn = new UnAllowAllAsynTask();
        unAllowAsyn.execute();
    }

    public void testBlockSettingsRequest(ParentMemberItem memberItem) {
        MainUtils.memberItemForBlockAll = memberItem;
        BlockSettings blocksettings = new BlockSettings();
        blocksettings.execute();
    }

    public void testUnBlockSettingsRequest(ParentMemberItem memberItem) {
        MainUtils.memberItemForBlockAll = memberItem;
        UnBlockSettings unBlockSettings = new UnBlockSettings();
        unBlockSettings.execute();
    }

    public String creatBlockAllRequest(ParentMemberItem memberItem) {
        ParentMemberItem device = memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(), "samsung", "android", "", "", "child");
        deviceRequest = new DeviceRequest(1, 9, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String creatUnBlockAllRequest(ParentMemberItem memberItem) {
        ParentMemberItem device = memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(), "samsung", "android", "", "", "child");
        deviceRequest = new DeviceRequest(1, 11, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String creatAllowAllRequest(ParentMemberItem memberItem) {
        ParentMemberItem device = memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(), "samsung", "android", "", "", "child");
        deviceRequest = new DeviceRequest(1, 10, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String creatUnAllowAllRequest(ParentMemberItem memberItem) {
        ParentMemberItem device = memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(), "samsung", "android", "", "", "child");
        deviceRequest = new DeviceRequest(1, 12, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String creatBlockSettingsRequest(ParentMemberItem memberItem) {
        ParentMemberItem device = memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(), "samsung", "android", "", "", "child");
        deviceRequest = new DeviceRequest(1, 13, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String creatUnBlockSettingsRequest(ParentMemberItem memberItem) {
        ParentMemberItem device = memberItem;
        Device deviceItem = new Device(device.getId_member_server(), device.getName_member(), "samsung", "android", "", "", "child");
        deviceRequest = new DeviceRequest(1, 14, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    //=====================Block code for BlockAll Device api==================================

    private class BlockAllAsynTask extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + creatBlockAllRequest(MainUtils.memberItemForBlockAll);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
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
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if(status == 1) {
                        //Handle Success
                        updateSuccess(MainUtils.BLOCK_ALL);
                        MainUtils.memberItemForBlockAll.setIs_blockall(1);
                        mDataHelper.updateMemberItem(MainUtils.memberItemForBlockAll);
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=====================Block code for BlockAll Device api==================================

    //=====================Block code for UnBlockAll Device api==================================

    private class UnBlockAllAsynTask extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + creatUnBlockAllRequest(MainUtils.memberItemForBlockAll);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
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
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if(status == 1) {
                        //Handle Success
                        updateSuccess(MainUtils.UNBLOCK_ALL);
                        MainUtils.memberItemForBlockAll.setIs_blockall(0);
                        mDataHelper.updateMemberItem(MainUtils.memberItemForBlockAll);
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=====================Block code for UnBlockAll Device api==================================

    //=====================Block code for AllowAll Device api==================================

    private class AllowAllAsynTask extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + creatAllowAllRequest(MainUtils.memberItemForBlockAll);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
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
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if(status == 1) {
                        //Handle Success
                        updateSuccess(MainUtils.ALLOW_ALL);
                        MainUtils.memberItemForBlockAll.setIs_alowall(1);
                        mDataHelper.updateMemberItem(MainUtils.memberItemForBlockAll);
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=====================Block code for AllowAll Device api==================================

    //=====================Block code for UnAllowAll Device api==================================

    private class UnAllowAllAsynTask extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + creatUnAllowAllRequest(MainUtils.memberItemForBlockAll);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
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
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if(status == 1) {
                        //Handle Success
                        updateSuccess(MainUtils.UNALLOW_ALL);
                        MainUtils.memberItemForBlockAll.setIs_alowall(0);
                        mDataHelper.updateMemberItem(MainUtils.memberItemForBlockAll);
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=====================Block code for UnAllowAll Device api==================================

    //=====================Block code for BlockSettings Device api==================================

    private class BlockSettings extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + creatBlockSettingsRequest(MainUtils.memberItemForBlockAll);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
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
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if(status == 1) {
                        //Handle Success
                        updateSuccess(MainUtils.BLOCK_SETTINGS);
                        MainUtils.memberItemForBlockAll.setIs_blocksettings(1);
                        Toast.makeText(mContext, "Block settings successful", Toast.LENGTH_SHORT).show();
                        mDataHelper.updateMemberItem(MainUtils.memberItemForBlockAll);
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=====================Block code for BlockSettings Device api==================================

    //=====================Block code for UnBlockSettings Device api==================================

    private class UnBlockSettings extends AsyncTask<ParentMemberItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + creatUnBlockSettingsRequest(MainUtils.memberItemForBlockAll);
            Log.d(TAG,"link: "+link);
            result = connectToServer(link);
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
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if(status == 1) {
                        //Handle Success
                        updateSuccess(MainUtils.UN_BLOCK_SETTINGS);
                        MainUtils.memberItemForBlockAll.setIs_blocksettings(0);
                        mDataHelper.updateMemberItem(MainUtils.memberItemForBlockAll);
                        Toast.makeText(mContext, "Unblock settings successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Error in server "+ description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=====================Block code for UnBlockSettings Device api==================================

    public void updateSuccess(String str){
        Intent intent = new Intent();
        intent.setAction(str);
        mContext.sendBroadcast(intent);
        Log.e("thong.nv","sendBroadcast" + intent);
    }



}

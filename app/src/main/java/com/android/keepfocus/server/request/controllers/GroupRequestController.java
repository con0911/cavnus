package com.android.keepfocus.server.request.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.activity.JoinGroupActivity;
import com.android.keepfocus.activity.LoginActivity;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ChildTimeItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.model.License;
import com.android.keepfocus.server.request.model.GroupRequest;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.android.keepfocus.utils.ServerUtils;
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

public class GroupRequestController {
    public static final String BASE_URL = "http://45.32.103.87/api/group?pRequest=";
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 10000;
    private Context mContext;
    private GroupRequest groupRequest;
    private String TAG = "GroupRequestController";
    private MainDatabaseHelper mDataHelper;
    private ServerUtils serverUtils;
    private SharedPreferences joinPref;
    public String deviceCode;
    private String registationId;
    private String testEmail = "";
    private String testPass = "";


    public GroupRequestController(Context context) {
        this.mContext = context;
        mDataHelper = new MainDatabaseHelper(context);
        serverUtils = new ServerUtils();
        joinPref = PreferenceManager.getDefaultSharedPreferences(context);
        getEmailandPass();
        //registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
        registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
        deviceCode = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void getEmailandPass() {
        testEmail = joinPref.getString(LoginActivity.EMAILLOGIN, "null");
        testPass = joinPref.getString(LoginActivity.PASSWORDLOGIN, "null");
    }

    public void testAddGroupInServer() {
        AddGroupAsynTask addAsyn = new AddGroupAsynTask();
        addAsyn.execute();
    }

    public void getGroupInServer() {
        GetGroupAsynTask getAsyn = new GetGroupAsynTask();
        getAsyn.execute();
    }

    public void deleteGroupInServer() {
        DeleteGroupAsynTask deleteAsyn = new DeleteGroupAsynTask();
        deleteAsyn.execute();
    }

    public void updateGroupInServer() {
        UpdateGroupAsynTask updateAsyn = new UpdateGroupAsynTask();
        updateAsyn.execute();
    }

    public void updateListDevice() {
        GetListDeviceAsynTask updateAsyn = new GetListDeviceAsynTask();
        updateAsyn.execute();
    }

    public void getListLicense(int type) {
        GetListLicenseAsynTask listLicenseAsynTask = new GetListLicenseAsynTask(type);
        listLicenseAsynTask.execute();
    }


    public String createGroup() {
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);
        ;//add data via contructor
        Group groupItem = new Group(MainUtils.parentGroupItem.getGroup_name());
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeUpdate, Constants.ActionTypeCreate, groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String updateGroup() {
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);//add data via contructor
        Group groupItem = new Group(MainUtils.parentGroupItem.getId_group_server(), MainUtils.parentGroupItem.getGroup_name());
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeUpdate, Constants.ActionTypeUpdate, groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String deleteGroup() {
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);//add data via contructor
        Group groupItem = new Group(MainUtils.parentGroupItem.getId_group_server());
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeUpdate, Constants.ActionTypeDelete, groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        return jsonRequest;
    }

    public String getListGroup() {
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeGet, Constants.ActionTypeGetList, null);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String getListDevice() {
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);
        Group groupItem = new Group(MainUtils.parentGroupItem.getId_group_server(), MainUtils.parentGroupItem.getGroup_code());
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeGet, Constants.ActionTypeGetDevice, groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String getListLicenseUsed(){
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeGet, Constants.ActionTypeGetDevice);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }


    //=====================Block code for create new Family api==================================

    private class AddGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = BASE_URL + createGroup();
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,createGroup());


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    JSONObject data = jsonObj.getJSONObject("Data");
                    String group_code = data.getString("group_code");
                    int group_id_server = data.getInt("id");
                    String group_name = data.getString("group_name");
                    if (description_result.equals("Success")) {
                        MainUtils.parentGroupItem.setGroup_code(group_code);
                        MainUtils.parentGroupItem.setId_group_server(group_id_server);
                        MainUtils.parentGroupItem.setGroup_name(group_name);
                        mDataHelper.addGroupItemParent(MainUtils.parentGroupItem);
                        updateSuccess();
                    } else {
                        Toast.makeText(mContext, "Error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't create new family! Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
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

    //=====================Block code for get Family api==================================

    private class GetGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = BASE_URL + getListGroup();
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    JSONArray data = jsonObj.getJSONArray("Data");
                    ArrayList<ParentGroupItem> listGroup = mDataHelper.getAllGroupItemParent();

                    if (description_result.equals("Success") && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject groupItem = data.getJSONObject(i);
                            boolean conflict = false;
                            if (listGroup.size() > 0) {
                                for (int j = 0; j < listGroup.size(); j++) {
                                    if (groupItem.getInt("id") == listGroup.get(j).getId_group_server()) {
                                        conflict = true;
                                        listGroup.get(j).setGroup_name(groupItem.getString("group_name"));
                                        listGroup.get(j).setGroup_code(groupItem.getString("group_code"));
                                        //listGroup.get(j).setGroup_name(groupItem.getString("create_by"));
                                        listGroup.get(j).setCreate_date(groupItem.getString("create_date"));
                                        listGroup.get(j).setId_group_server(groupItem.getInt("id"));
                                        mDataHelper.updateGroupItem(listGroup.get(j));
                                    }
                                }
                            }
                            if (!conflict) {
                                ParentGroupItem parentGroupItem = new ParentGroupItem();
                                parentGroupItem.setGroup_name(groupItem.getString("group_name"));
                                parentGroupItem.setGroup_code(groupItem.getString("group_code"));
                                parentGroupItem.setCreate_date(groupItem.getString("create_date"));
                                parentGroupItem.setId_group_server(groupItem.getInt("id"));
                                mDataHelper.addGroupItemParent(parentGroupItem);
                            }

                        }
                        updateSuccess();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(mContext, "Error in database", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //=====================Get List device==================================

    private class GetListDeviceAsynTask extends AsyncTask<ParentMemberItem, Void, String> {
        @Override
        protected String doInBackground(ParentMemberItem... params) {
            String result = "";
            String link;
            link = BASE_URL + getListDevice();
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    JSONArray data = jsonObj.getJSONArray("Data");
                   // ArrayList<ParentMemberItem> listDevice = MainUtils.parentGroupItem.getListMember();

                    if (description_result.equals("Success") && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject deviceItem = data.getJSONObject(i);
                            //restore schedule
                            JSONArray groupUser = deviceItem.getJSONArray("group_user");
                            ParentMemberItem restoreDevice = new ParentMemberItem();
                            if (MainUtils.parentGroupItem == null) {
                                MainUtils.parentGroupItem = mDataHelper.getAllGroupItemParent().get(0);//add to first
                            }
                            Log.d(TAG,"join To Group "+MainUtils.parentGroupItem.getGroup_name());

                            int type;
                            if(deviceItem.getString("device_mode").trim().equals(JoinGroupActivity.MANAGER)) {
                                Log.d(TAG,"type manager " + deviceItem.getString("device_mode").trim());
                                type = 1;
                            } else{
                                Log.d(TAG,"type child " + deviceItem.getString("device_mode").trim());
                                type = 0;
                            }

                            restoreDevice.setId_member_server(deviceItem.getInt("id"));
                            restoreDevice.setName_member(deviceItem.getString("device_name"));
                            restoreDevice.setImage_member(deviceItem.getString("device_model"));
                            restoreDevice.setType_member(type);

                            Log.e(TAG, "MainUtils.parentGroupItem.getListMember() before " + MainUtils.parentGroupItem.getListMember().size());
                            MainUtils.parentGroupItem.getListMember().add(restoreDevice);
                            int idMember = mDataHelper.addMemberItemParent(restoreDevice,  MainUtils.parentGroupItem.getId_group());
                            for (int j = 0; j < groupUser.length(); j++) {
                                JSONObject groupUserElement = groupUser.getJSONObject(j);
                                JSONArray schedulersArray = groupUserElement.getJSONArray("schedulers");

                                for (int k = 0; k < schedulersArray.length(); k++) {
                                    JSONObject scheduleItem = schedulersArray.getJSONObject(k);
                                    JSONArray timeItem = scheduleItem.getJSONArray("timeitems");
                                    Log.d(TAG,"timeItem "+timeItem);
                                    ParentProfileItem parentProfileItem = new ParentProfileItem();

                                    parentProfileItem.setName_profile(schedulersArray.getJSONObject(k).getString("scheduler_name"));
                                    parentProfileItem.setActive(isActive(schedulersArray.getJSONObject(k).getInt("isActive")));
                                    parentProfileItem.setDay_profile(schedulersArray.getJSONObject(k).getString("days"));
                                    parentProfileItem.setId_profile_server(schedulersArray.getJSONObject(k).getInt("id"));
                                    int idProfile = mDataHelper.addProfileItemParent(parentProfileItem, idMember);
                                    ArrayList<ParentTimeItem> arrayList = new ArrayList(timeItem.length());
                                    for(int ii=0;i < timeItem.length();i++){
                                        ParentTimeItem item1 = new ParentTimeItem();
                                        item1.setId_profile(timeItem.getJSONObject(ii).getInt("scheduler_id"));
                                        item1.setHourBegin(timeItem.getJSONObject(ii).getInt("start_hours"));
                                        item1.setHourEnd(timeItem.getJSONObject(ii).getInt("end_hours"));
                                        item1.setMinusBegin(timeItem.getJSONObject(ii).getInt("start_minutes"));
                                        item1.setMinusEnd(timeItem.getJSONObject(ii).getInt("end_minutes"));
                                        item1.setId_time_server(timeItem.getJSONObject(ii).getInt("id"));
                                        arrayList.add(item1);
                                        mDataHelper.addTimeItemParent(item1, idProfile);
                                    }
                                    parentProfileItem.setListTimer(arrayList);
                                }
                            }
                        }
                        updateSuccess();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(mContext, "Error in database", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean isActive(int state){
        if(state == 1) return true;
        else return false;
    }
    //=====================Block code for update Family api==================================


    private class UpdateGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = BASE_URL + updateGroup();
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,updateGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    if (description_result.equals("Update success")) {
                        mDataHelper.updateGroupItem(MainUtils.parentGroupItem);
                        updateSuccess();
                    } else {
                        MainUtils.mIsEditNameGroup = false;
                        Toast.makeText(mContext, "Error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MainUtils.mIsEditNameGroup = false;
                    Toast.makeText(mContext, "Can't update family! Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                MainUtils.mIsEditNameGroup = false;
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
            }
            mDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Request to server...");
            mDialog.setCancelable(false);
            mDialog.setInverseBackgroundForced(false);
            mDialog.show();
        }
    }
    //=====================Block code for delete Family api==================================

    private class DeleteGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = BASE_URL + deleteGroup();
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    int status = message.getInt("Status");
                    if (status == 1) {
                        mDataHelper.deleteGroupItemById(MainUtils.parentGroupItem.getId_group());
                        updateSuccess();
                    } else {
                        Toast.makeText(mContext, "Error in server " + description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't delete family! Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
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

    //=======================================================================================

    private class JoinGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = BASE_URL + deleteGroup();
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    if (description_result.equals("Success")) {
                        mDataHelper.deleteGroupItemById(MainUtils.parentGroupItem.getId_group());
                        updateSuccess();
                    } else {
                        Toast.makeText(mContext, "Error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't create new family! Error in database", Toast.LENGTH_SHORT).show();
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

    //=====================Get List device==================================

    private class GetListLicenseAsynTask extends AsyncTask<License, Void, String> {
        ProgressDialog mDialog;
        String request = "";
        int typeRequest = 0;
        ArrayList<License> listLicense = null;
        GetListLicenseAsynTask(int type){
            this.typeRequest = type;
            if (type == Constants.ActionTypeGetLicenseUsed){
                request = getListLicenseUsed();
            } else if (type == Constants.ActionTypeGetLicenseUnUsed){
                request = getListLicenseUsed();
            }
        }

        public ArrayList<License> getListLicense(){
            return listLicense;
        }


        @Override
        protected String doInBackground(License... params) {
            String result = "";
            String link;
            link = BASE_URL + request;
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
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
                        JSONArray data = jsonObj.getJSONArray("Data");
                        listLicense = new ArrayList(data.length());
                        for(int i=0;i < data.length();i++){
                            License licenseItem = new License();
                            licenseItem.setLicense_key(data.getJSONObject(i).getString("license_key"));
                            licenseItem.setId_groupuser(data.getJSONObject(i).getInt("id_groupuser"));
                            licenseItem.setIs_use(data.getJSONObject(i).getInt("is_use"));
                            listLicense.add(licenseItem);
                        }
                        JoinGroupActivity joinGroupActivity = (JoinGroupActivity) mContext;
                        joinGroupActivity.setLicenseList(listLicense);
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

    //=======================================================================================

    public void updateSuccess() {

        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_FAMILY_GROUP);
        mContext.sendBroadcast(intent);
    }

    public String connectToServer(String urlRequest) {
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


    private String convertStreamToString(InputStream is) {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

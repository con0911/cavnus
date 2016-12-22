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
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.request.model.GroupRequest;
import com.android.keepfocus.server.request.model.JoinGroupRequest;
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
    public static final String BASE_URL = "http://45.63.21.174/api/group?pRequest=";
    public static final String LICENSE_URL = "http://45.63.21.174/api/group?pRequest=";
    private static final int NET_READ_TIMEOUT_MILLIS = 20000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 20000;
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

    public void getListLicense(int type, String groupID) {
        GetListLicenseAsynTask listLicenseAsynTask = new GetListLicenseAsynTask(type, groupID);
        listLicenseAsynTask.execute();
    }

    public void managerJoinGroup(String device, String group, int type) {
        ManagerJoinGroupAsynTask managerJoinGroupAsynTask = new ManagerJoinGroupAsynTask(device, group, type);
        managerJoinGroupAsynTask.execute();
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
        Group groupItem = new Group(MainUtils.parentGroupItem.getId_group_server(), MainUtils.parentGroupItem.getGroup_name(), MainUtils.parentGroupItem.getGroup_code());
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

    public String managerGetListGroup() {
        String group_Id = joinPref.getString(MainUtils.GROUP_ID,"");
        Header headerItem = new Header(testEmail, deviceCode, registationId, testPass);
        Group groupItem = new Group(0, group_Id);
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeGet, Constants.ActionTypeGetList, groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String getListDevice() {
        Header headerItem = new Header("", deviceCode, registationId, testPass);
        Group groupItem = new Group(MainUtils.parentGroupItem.getId_group_server(), MainUtils.parentGroupItem.getGroup_code());
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeGet, Constants.ActionTypeGetDevice, groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String getListLicenseUsed(String groupId){
        Group groupItem = new Group(0, groupId);
        groupRequest = new GroupRequest(groupItem, Constants.RequestTypeGet, Constants.ActionTypeGetLicenseUsed);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String getListLicenseUnUsed(String groupId){
        Group groupItem = new Group(0, groupId);
        groupRequest = new GroupRequest(groupItem, Constants.RequestTypeGet, Constants.ActionTypeGetLicenseUnUsed);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String confirmManagerJoinGroup(String device, String group) throws JSONException {
        JSONObject groupObject = new JSONObject(group);
        JSONObject deviceObject = new JSONObject(device);
        Group groupItem = new Group(0, groupObject.getString("group_code"));
        Device deviceItem = new Device(0, deviceObject.getString("device_name"),
                deviceObject.getString("device_model"), deviceObject.getString("device_type"),
                deviceObject.getString("registation_id"),
                deviceObject.getString("device_code"), deviceObject.getString("device_mode"));
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest(Constants.ActionTypeManagerJoin, groupItem, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(joinGroupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    public String rejectManagerJoinGroup(String device, String group) throws JSONException {
        JSONObject groupObject = new JSONObject(group);
        JSONObject deviceObject = new JSONObject(device);
        Group groupItem = new Group(0, groupObject.getString("group_code"));
        Device deviceItem = new Device(0, deviceObject.getString("device_name"),
                deviceObject.getString("device_model"), deviceObject.getString("device_type"),
                deviceObject.getString("registation_id"),
                deviceObject.getString("device_code"), deviceObject.getString("device_mode"));
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest(Constants.ActionTypeManagerReject, groupItem, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(joinGroupRequest);
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
                    int status = message.getInt("Status");
                    if (status == 1 && data != null) {
                        String group_code = data.getString("group_code");
                        int group_id_server = data.getInt("id");
                        String group_name = data.getString("group_name");
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

    //=====================Block code for get Family api==================================

    private class GetGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = BASE_URL + managerGetListGroup();
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

                    int status = message.getInt("Status");
                    if (status == 1 && data != null) {
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
                                parentGroupItem.setIs_restore(1);
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
                    int status = message.getInt("Status");
                    if (status == 1 && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject deviceItem = data.getJSONObject(i);
                            int type;
                            if(deviceItem.getString("device_mode").trim().equals(JoinGroupActivity.MANAGER)) {
                                Log.d(TAG,"type manager " + deviceItem.getString("device_mode").trim());
                                type = 1;
                            } else{
                                Log.d(TAG,"type child " + deviceItem.getString("device_mode").trim());
                                type = 0;
                            }

                            if(type == 0) {
                                //restore schedule
                                JSONArray groupUser = deviceItem.getJSONArray("group_user");
                                ParentMemberItem restoreDevice = new ParentMemberItem();
                                if (MainUtils.parentGroupItem == null) {
                                    MainUtils.parentGroupItem = mDataHelper.getAllGroupItemParent().get(0);//add to first
                                }
                                Log.d(TAG,"restore info of Group "+MainUtils.parentGroupItem.getGroup_name());
                                restoreDevice.setId_member_server(deviceItem.getInt("id"));
                                restoreDevice.setName_member(deviceItem.getString("device_name"));
                               // restoreDevice.setImage_member(deviceItem.getString("device_model"));
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
                                        for(int ii=0;ii < timeItem.length();ii++){
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
                    int status = message.getInt("Status");
                    if (status == 1) {
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
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

    //=======================================================================================

    private class ManagerJoinGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        String deviceObject = "";
        String groupObject = "";
        int type = 1;
        ManagerJoinGroupAsynTask(String device, String group, int type){
            this.deviceObject = device;
            this.groupObject = group;
            this.type = type;
        }

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link = null;
            try {
                if(type == 0) {
                    link = BASE_URL + confirmManagerJoinGroup(deviceObject, groupObject);
                }else if (type == 1) {
                    link = BASE_URL + rejectManagerJoinGroup(deviceObject, groupObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "link: " + link);
            result = connectToServer(link);
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
                    Log.d(TAG,"status1 = "+status);
                    //JSONObject data = jsonObj.getJSONObject("Data");
                    if(status == 1) {
                        //success
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

/*    String test = "{\n" +
            "  \"Message\": {\n" +
            "    \"Status\": 1,\n" +
            "    \"Description\": \"Success\"\n" +
            "  },\n" +
            "  \"Data\": [\n" +
            "    {\n" +
            "      \"group_user\": null,\n" +
            "      \"id\": 1,\n" +
            "      \"license_key\": \"CVNSb09712c2-77b9-4d76-85c5-c1df4ce1c920\",\n" +
            "      \"activation_email\": \"conandoye@gmail.com\",\n" +
            "      \"create_date\": \"2016-12-12T10:42:48\",\n" +
            "      \"id_groupuser\": null,\n" +
            "      \"is_use\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"group_user\": null,\n" +
            "      \"id\": 2,\n" +
            "      \"license_key\": \"CVNS3206f71c-1978-4313-bb13-220321f895f1\",\n" +
            "      \"activation_email\": \"conandoye@gmail.com\",\n" +
            "      \"create_date\": \"2016-12-12T10:42:48\",\n" +
            "      \"id_groupuser\": null,\n" +
            "      \"is_use\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"group_user\": null,\n" +
            "      \"id\": 3,\n" +
            "      \"license_key\": \"CVNS3a4e0223-7159-4e4c-a19b-197b8f351f5d\",\n" +
            "      \"activation_email\": \"conandoye@gmail.com\",\n" +
            "      \"create_date\": \"2016-12-12T10:42:48\",\n" +
            "      \"id_groupuser\": null,\n" +
            "      \"is_use\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"group_user\": null,\n" +
            "      \"id\": 4,\n" +
            "      \"license_key\": \"CVNSb12ed19a-c0d2-4c2c-a5f2-d4a43351616d\",\n" +
            "      \"activation_email\": \"conandoye@gmail.com\",\n" +
            "      \"create_date\": \"2016-12-12T10:42:48\",\n" +
            "      \"id_groupuser\": null,\n" +
            "      \"is_use\": 0\n" +
            "    }\n" +
            "  ]\n" +
            "}";*/

    //=====================Get List device==================================

    private class GetListLicenseAsynTask extends AsyncTask<String, Void, String> {
        ProgressDialog mDialog;
        String request = "";
        int typeRequest = 0;
        ArrayList<String> listLicense = null;
        GetListLicenseAsynTask(int type, String groupId){
            this.typeRequest = type;
            if (type == Constants.ActionTypeGetLicenseUsed){
                request = getListLicenseUsed(groupId);
            } else if (type == Constants.ActionTypeGetLicenseUnUsed){
                request = getListLicenseUnUsed(groupId);
            }
        }

        public ArrayList<String> getListLicense(){
            return listLicense;
        }


        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String link;
            link = LICENSE_URL + request;
            Log.d(TAG, "link: " + link);
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
                    int status = message.getInt("Status");
                    Log.d(TAG,"status1 = "+status);
                    //JSONObject data = jsonObj.getJSONObject("Data");
                    if(status == 1) {
                        JSONArray data = jsonObj.getJSONArray("Data");
                        listLicense = new ArrayList(data.length());
                        for(int i=0;i < data.length();i++){
                            String licenseItem = data.getJSONObject(i).getString("license_key");
                            listLicense.add(licenseItem);
                        }
                        JoinGroupActivity joinGroupActivity = (JoinGroupActivity) mContext;
                        joinGroupActivity.setLicenseList(listLicense);
                    } else {
                        Toast.makeText(mContext, "Error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't get list license! Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check internet!", Toast.LENGTH_SHORT).show();
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
            Log.d(TAG,"Exception = "+e.toString());
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

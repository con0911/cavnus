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

import com.android.keepfocus.activity.LoginActivity;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.Header;
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
        Group groupItem = new Group(MainUtils.parentGroupItem.getId_group_server());
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeGet, Constants.ActionTypeGetDevice, groupItem);
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
                    ArrayList<ParentMemberItem> listDevice = MainUtils.parentGroupItem.getListMember();

                    if (description_result.equals("Success") && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject deviceItem = data.getJSONObject(i);
                            boolean conflict = false;
                            if (listDevice.size() > 0) {
                                for (int j = 0; j < listDevice.size(); j++) {
                                    if (deviceItem.getInt("id") == listDevice.get(j).getId_member_server()) {
                                        conflict = true;
                                        listDevice.get(j).setImage_member(deviceItem.getString("device_model"));
                                        listDevice.get(j).setName_member(deviceItem.getString("device_name"));
                                        mDataHelper.updateMemberItem(listDevice.get(j));
                                    }
                                }
                            }
                            if (!conflict) {
                                ParentMemberItem parentMemberItem = new ParentMemberItem();
                                parentMemberItem.setImage_member(deviceItem.getString("device_model"));
                                parentMemberItem.setName_member(deviceItem.getString("device_name"));
                                parentMemberItem.setId_member_server(deviceItem.getInt("id"));
                                MainUtils.parentGroupItem.getListMember().add(parentMemberItem);
                                mDataHelper.makeDetailOneGroupItemParent(MainUtils.parentGroupItem);
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
                        Toast.makeText(mContext, "Error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't update family! Error in database", Toast.LENGTH_SHORT).show();
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

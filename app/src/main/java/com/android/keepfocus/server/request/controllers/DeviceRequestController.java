package com.android.keepfocus.server.request.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.request.model.DeviceRequest;
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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sev_user on 9/23/2016.
 */
public class DeviceRequestController {
    public static final String DEVICE_BASE_URL = "http://45.63.21.174/api/device?pRequest=";
    private DeviceRequest deviceRequest;
    private static final int NET_READ_TIMEOUT_MILLIS = 20000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 20000;
    private String TAG = "DeviceRequestController";
    static boolean isSuccess;
    private Context mContext;
    private MainDatabaseHelper mDataHelper;

    public DeviceRequestController(Context context) {
        this.mContext = context;
        mDataHelper = new MainDatabaseHelper(context);
    }


/*    public String createDevice(){
        Device deviceItem = new Device(0, "","", "","","");
        deviceRequest = new DeviceRequest(Constants.ActionTypeCreate,deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }*/

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

    public String deleteDevice() {
        Device deviceItem = new Device(MainUtils.memberItem.getId_member_server());
        deviceRequest = new DeviceRequest(Constants.RequestTypeUpdate, Constants.ActionTypeDelete, deviceItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(deviceRequest);
        return jsonRequest;
    }

    public void deleteDeviceInServer() {
        DeleteDeviceAsynTask deleteAsyn = new DeleteDeviceAsynTask();
        deleteAsyn.execute();
    }

    //=====================Block code for delete Device api==================================

    private class DeleteDeviceAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = DEVICE_BASE_URL + deleteDevice();
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
                        mDataHelper.deleteMemberItemById(MainUtils.memberItem.getId_member());
                        updateSuccess();
                    } else {
                        Toast.makeText(mContext, "Error in server " + description_result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't delete device! Error in database", Toast.LENGTH_SHORT).show();
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

    public void updateSuccess() {
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_DEVICE);
        mContext.sendBroadcast(intent);
    }


    public boolean checkDeviceRequest(String jsonObject) {
        //final boolean[] isSuccess = {false};
        final String json = jsonObject;
        AsyncTask<Device, Void, String> deviceAsyncTask = new AsyncTask<Device, Void, String>() {

            @Override
            protected String doInBackground(Device... params) {
                URL url = null;
                String streamToString = null;
                try {
                    url = new URL(DEVICE_BASE_URL + json);
                    Log.e(TAG,"url Request : " + DEVICE_BASE_URL + json);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
                    connection.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    streamToString = convertStreamToString(is);
                    Log.e(TAG, "streamToString : " + streamToString);
                    return streamToString;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                String jsonStr = result;
                Log.d(TAG, "onPostExecute" + result);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONObject status = jsonObj.getJSONObject("Message");
                        String description_result = status.getString("Status");
                        Log.d(TAG, "description_result" + description_result);
                        if (description_result.equals("1")) {
                            isSuccess = true;
                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            isSuccess = false;
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        deviceAsyncTask.execute();

        return isSuccess;

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

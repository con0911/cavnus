package com.android.keepfocus.server.request.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.request.model.LoginRequest;
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
public class LoginRequestController {
    public static final String ACCOUNT_BASE_URL = "http://104.156.224.47/api/account?pRequest=";
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 10000;

    private LoginRequest mLoginRequest;
    private String TAG = "LoginRequestController";
    private Context mContext;
    static boolean isSuccess;

    public LoginRequestController(Context context) {
        this.mContext = context;
    }


    public String login() {
        Header header = new Header("test@gmail.com", "devicecode1", "registationId1", "password1");
        mLoginRequest = new LoginRequest(header);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(mLoginRequest);
        Log.e(TAG, " " + jsonRequest);
        return null;
    }

    public boolean checkLogin(String jsonObject) {
        //final boolean[] isSuccess = {false};
        final String json = jsonObject;
        AsyncTask<Header, Void, String> loginAsyncTask = new AsyncTask<Header, Void, String>() {

            @Override
            protected String doInBackground(Header... params) {
                URL url = null;
                String streamToString = null;
                try {
                    url = new URL(ACCOUNT_BASE_URL + json);
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
                Log.e(TAG, "onPostExecute : " + result);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONObject status = jsonObj.getJSONObject("Message");
                        String description_result = status.getString("Status");
                        Log.e(TAG, "description_result : " + description_result);
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
        loginAsyncTask.execute();
        Log.e(TAG, "isSuccess : " + isSuccess);
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

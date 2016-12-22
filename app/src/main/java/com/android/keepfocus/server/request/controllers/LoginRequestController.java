package com.android.keepfocus.server.request.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.activity.LoginActivity;
import com.android.keepfocus.activity.SetupWizardActivity;
import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.request.model.LoginRequest;
import com.android.keepfocus.utils.MainUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sev_user on 9/23/2016.
 */
public class LoginRequestController {
    public static final String ACCOUNT_BASE_URL = "http://45.63.21.174/api/account?pRequest=";
    private static final int NET_READ_TIMEOUT_MILLIS = 20000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 20000;

    private LoginRequest mLoginRequest;
    private String TAG = "LoginRequestController";
    private Context mContext;
    static boolean isSuccess;

    private SharedPreferences loginPref;

    public LoginRequestController(Context context) {
        this.mContext = context;
        loginPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }


//    public String login() {
//        Header header = new Header("testlogin3@gmail.com", "devicecode1", "registationId1", "testpass");
//        mLoginRequest = new LoginRequest(header);
//        Gson gson = new Gson();
//        String jsonRequest = gson.toJson(mLoginRequest);
//        Log.e(TAG, " " + jsonRequest);
//        return null;
//    }


    private class LoginAsynTask extends AsyncTask<Header, Void, String> {
        ProgressDialog mDialog;
        String jsonRequest;
        LoginAsynTask(String string){
            jsonRequest = string;
        }
        @Override
        protected String doInBackground(Header... params) {
            String result = "";
            String link;
            link = ACCOUNT_BASE_URL + jsonRequest;
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
                    JSONObject status = jsonObj.getJSONObject("Message");
                    String description_result = status.getString("Description");
                    if (description_result.equals("Success")) {
                        isSuccess = true;
                        //Toast.makeText(mContext, "Login successfully", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "current mode : " + SetupWizardActivity.getModeDevice(mContext));

                        //save email and pass
                        SharedPreferences.Editor editor = loginPref.edit();
                        editor.putString(LoginActivity.EMAILLOGIN, LoginActivity.emailLogin);
                        editor.putString(LoginActivity.PASSWORDLOGIN, LoginActivity.passwordLogin);
                        editor.commit();

                        SetupWizardActivity.setModeDevice(MainUtils.MODE_PARENT, mContext);
                        Intent groupManagement = new Intent(mContext, FamilyManagerment.class);
                        mContext.startActivity(groupManagement);
                    } else {
                        isSuccess = false;
                        Toast.makeText(mContext, "Your password or email is wrong", Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(login);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Can't create new family! Error in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please check the internet connection!", Toast.LENGTH_SHORT).show();
                LoginActivity loginActivity = (LoginActivity) mContext;
                loginActivity.finish();
                Intent login = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(login);
                //return;
            }
            //mDialog.dismiss();

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
            mDialog.setInverseBackgroundForced(false);
            mDialog.setMessage("Request to server...");
            mDialog.show();*/
        }
    }

    public void checkLogin(String jsonObject) {
        //final boolean[] isSuccess = {false};
        final String json = jsonObject;
        LoginAsynTask loginAsynTask = new LoginAsynTask(json);
        loginAsynTask.execute();
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
            Log.e(TAG, " exception " + e.toString());
            e.printStackTrace();
            return null;
        }
    }



}

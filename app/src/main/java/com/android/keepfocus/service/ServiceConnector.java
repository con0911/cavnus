package com.android.keepfocus.service;

import com.android.keepfocus.activity.LoginActivity;
import com.android.keepfocus.server.model.Group;
import android.os.AsyncTask;
import android.util.Log;

import com.android.keepfocus.server.model.Header;
import com.google.gson.Gson;

/*import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;*/
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 9/11/2016.
 */
public class ServiceConnector {
    public static final String GROUP_BASE_URL = "http://104.156.224.47/api/group?pRequest=";
    public static final String ACCOUNT_BASE_URL = "http://104.156.224.47/api/account?pRequest=";
    private static final String TAG = "ServiceConnector";
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 10000;


/*    public boolean checkLogin(final String email, final String deviceCode, final String registationId, final String password){
        boolean isSuccessful = false;
        final HeaderItem headerItem = new HeaderItem();
        AsyncTask<HeaderItem, Void, String> loginAsyncTask = new AsyncTask<HeaderItem, Void, String>() {
            @Override
            protected String doInBackground(HeaderItem... params) {
                String result = "2";

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();
                HttpGet get = new HttpGet(GROUP_BASE_URL);
                try {
                    Gson gson = new Gson();
                    json.put("Header", gson.toJson(headerItem).toString());
                    json.put("Email", email);
                    json.put("DeviceCode", deviceCode);
                    json.put("RegistationId", registationId);
                    json.put("Password", password);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    //get.set
                    response = client.execute(get);


                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "Excep = " + e.toString());

                }

                return result;
            }
        };

        loginAsyncTask.execute(headerItem);


        return isSuccessful;


    }*/

    public boolean checkLogin(String jsonObject){
        boolean isSuccess = false;
        final String json = jsonObject;
        AsyncTask<Header, Void, String> loginAsyncTask = new AsyncTask<Header, Void, String>() {

            @Override
            protected String doInBackground(Header... params) {
                String result = "2";

                URL url = null;
                try {
                    url = new URL(ACCOUNT_BASE_URL + json);
                if (url != null) {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
                    connection.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    String streamToString = convertStreamToString(is);
                    Log.e(TAG, "streamToString : "+streamToString);
                }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }

                return result;
            }
        };
        loginAsyncTask.execute();

            return isSuccess;

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




/*    public static boolean checkLogin(String email, String password, String deviceId){
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
    }*/



    //=====================Block code for connect Group api==================================
    public ArrayList<Group>  getAllGroupByUser(String jsonRequest){
        ArrayList<Group> listGroup = new ArrayList<>();





        return listGroup;
    }

    public void createGroup(String jsonRequest){

    }


    //=====================End Block code for connect Group api==================================
}

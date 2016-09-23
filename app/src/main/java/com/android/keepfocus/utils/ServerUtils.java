package com.android.keepfocus.utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by trung on 9/23/2016.
 */
public class ServerUtils {
    public String postData(String baseUrl, String jsonObject){
        Log.d("trungdh",jsonObject);
        String jsonResponse ="";
        //create a new HttpClient and Post Header
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpClient = new DefaultHttpClient(myParams);
        //String json = jsonObject.toString();

        try {
            HttpPost httpPost = new HttpPost(baseUrl.toString());
            httpPost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(jsonObject);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            jsonResponse = EntityUtils.toString(response.getEntity());
            Log.i("trungdh", jsonResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }
}

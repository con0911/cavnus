package com.android.keepfocus.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by trung on 9/23/2016.
 */
public class ServerUtils {
    public String postData(String baseUrl, String jsonObject){
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        Log.d("trungdh",jsonObject);
        String jsonResponse ="";
        HttpURLConnection conn = null;
        try {

            URL url = new URL(baseUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Host", "104.156.224.47");
            conn.connect();

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(jsonObject);
            out.close();

            int HttpResult =conn.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.d("trungdh", sb.toString());

            }else{
                Log.d("trungdh","getResponseMessage : " + conn.getResponseMessage() + HttpResult);
            }

            /*List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pRequest",jsonObject));*/

            /*OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();*/
        }catch (MalformedURLException e) {
            Log.d("trungdh", "MalformedURLException : " + e.toString() );
        } catch (IOException e) {
            Log.d("trungdh", "MalformedURLException : " + e.toString());
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }

        //1. create HttpClient
            /*HttpClient httpClient = new DefaultHttpClient();

            //2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(baseUrl);
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(jsonObject,HTTP.UTF_8);
            se.setContentType("application/json");
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            // 7. Set some headers to inform server about the type of the content
          *//*  httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");*//*

            BasicResponseHandler handler = new BasicResponseHandler();
            // 8. Execute POST request to the given URL
            Log.d("trungdh",httpPost.toString());
            HttpResponse response = httpClient.execute(httpPost);
            Log.d("trungdh",jsonResponse);
            if (response.getStatusLine().getStatusCode() != org.apache.http.HttpStatus.SC_OK) {
                Log.i("trungdh", "not send "+response.getStatusLine());
            }else{
                Log.i("trundh", "send ok "+response.getStatusLine());
            }
            return jsonResponse;
            // 9. receive response as inputStream
           // inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
           *//* if(inputStream != null)
                jsonResponse = convertInputStreamToString(inputStream);
            else
                jsonResponse = "Did not work!";*//*
        } catch (UnsupportedEncodingException e) {
            Log.d("trungdh", "UnsupportedEncodingException");
        } catch (ClientProtocolException e) {
            Log.d("trungdh", "ClientProtocolException");
        } catch (IOException e) {
            Log.d("trungdh", "IOException");
        }
        Log.d("trungdh", "unable connect to server");*/
        return "unable connect to server";
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        Log.d("trungdh", "getQuery: " + result );
        return result.toString();
    }
}

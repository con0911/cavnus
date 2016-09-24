package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.gcm.MainGcmActivity;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.service.KeepFocusMainService;
import com.android.keepfocus.service.ServiceBlockApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity implements View.OnClickListener {
    private ProgressBar progess;
    private ListView listview;
    private Context mContext;
    private AppCategolize[] listAppCate;
    Button parentBtn, childBtn, start, stop, addGroupServer, testGCM, testLogin, testDevice, testCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        mContext = this;
        parentBtn = (Button) findViewById(R.id.parentBtn);
        childBtn = (Button) findViewById(R.id.childBtn);
        start = (Button) findViewById(R.id.startSv);
        stop = (Button) findViewById(R.id.stopSv);
        addGroupServer = (Button) findViewById(R.id.addGroupServer);
        testGCM = (Button) findViewById(R.id.testGCM);
        testLogin = (Button) findViewById(R.id.testLogin);
        testDevice = (Button) findViewById(R.id.testDevice);
        testCategory = (Button) findViewById(R.id.testCategory);
        progess = (ProgressBar) findViewById(R.id.progressBarCate);
        listview = (ListView) findViewById(R.id.listViewCate);
        progess.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);

        parentBtn.setOnClickListener(this);
        childBtn.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        addGroupServer.setOnClickListener(this);
        testGCM.setOnClickListener(this);
        testLogin.setOnClickListener(this);
        testDevice.setOnClickListener(this);
        testCategory.setOnClickListener(this);

    }

    private void testAddGroup() {
        GroupRequestController mainServer = new GroupRequestController(this);
        mainServer.testAddGroupInServer();
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parentBtn:
                Intent parentIntent = new Intent(this, GroupManagermentActivity.class);
                startActivity(parentIntent);
                break;
            case R.id.childBtn:
                Intent childIntent = new Intent(this, ChildSchedulerActivity.class);
                startActivity(childIntent);
                break;
            case R.id.startSv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    startService(new Intent(this, KeepFocusMainService.class));
                }
                startService(new Intent(this, ServiceBlockApp.class));
                break;
            case R.id.stopSv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    stopService(new Intent(this, KeepFocusMainService.class));
                }
                stopService(new Intent(this, ServiceBlockApp.class));
                break;
            case R.id.addGroupServer:
                testAddGroup();
                break;
            case R.id.testGCM:
                Intent gcm = new Intent(this, MainGcmActivity.class);
                startActivity(gcm);
                break;
            case R.id.testLogin:
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                break;
            case R.id.testDevice:
                Intent deviceRequest = new Intent(this, SetupWizardActivity.class);
                startActivity(deviceRequest);
                break;
            case R.id.testCategory:
                testGetCatelory();
                break;
        }
    }

    private void testGetCatelory() {
        progess.setVisibility(View.VISIBLE);
        listview.setVisibility(View.GONE);
        listAppCate = new AppCategolize[1];
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    getAppCategories();
                } catch (IOException e) {
                    Log.d("thong.nv", "Net work error: " + e.getMessage(), e);
                } catch (JSONException e) {
                    Log.d("thong.nv", "JSON is not valid:  " + e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progess.setVisibility(View.GONE);
                AppCategoryAdapter adapter = new AppCategoryAdapter(mContext, R.layout.test_categolize, 0, listAppCate);
                //4. Đưa Data source vào ListView
                listview.setAdapter(adapter);
                listview.setVisibility(View.VISIBLE);
                parentBtn.setVisibility(View.GONE);
                childBtn.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                addGroupServer.setVisibility(View.GONE);
                testGCM.setVisibility(View.GONE);
                testLogin.setVisibility(View.GONE);
                testDevice.setVisibility(View.GONE);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void getAppCategories() throws IOException, JSONException {

        BufferedReader bufferedReader = null;
        HttpURLConnection urlConnection = null;
        BufferedWriter bufferedWriter = null;

        StringBuilder result = new StringBuilder();

        //Create JSON object to send to webservice
        JSONObject jsonObjectSend = new JSONObject();
        JSONArray jsonArrayPakages = new JSONArray();
        PackageManager packageManager;
        List<ResolveInfo> listApps; //this list store all app in device

        try {
            packageManager = getPackageManager();
            Intent filterApp = new Intent(Intent.ACTION_MAIN);
            filterApp.addCategory(Intent.CATEGORY_LAUNCHER);
            listApps = packageManager.queryIntentActivities(filterApp,
                    PackageManager.GET_META_DATA);

            for (ResolveInfo app : listApps) {
                jsonArrayPakages.put(app.activityInfo.packageName.trim());
            }

            jsonObjectSend.put("packages", jsonArrayPakages);

            Log.d("thong.nv", "json = " + jsonObjectSend.toString());

            URL url = new URL("http://getdatafor.appspot.com/data?key=5f8d270bf822c1df90bf617a8bc1b73c3b71d166");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(50000); /* milliseconds */
            urlConnection.setReadTimeout(50000); /* milliseconds */
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application-json");
            urlConnection.setDoOutput(true); /* allow output to send data */
            urlConnection.setDoInput(true);
            urlConnection.connect();

            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(jsonObjectSend.toString());
            bufferedWriter.flush();
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Read data
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }

        /*Parse JSON**********************************************************************************/
                JSONObject jsonObjectResult = new JSONObject(result.toString().trim());
                JSONArray jsonArrayApps = jsonObjectResult.getJSONArray("apps");
                listAppCate = new AppCategolize[jsonArrayApps.length()];
                for (int j = 0; j < jsonArrayApps.length(); j++) {

                    JSONObject jsonObjectApp = jsonArrayApps.getJSONObject(j);

                    String packageName = jsonObjectApp.getString("package").trim();
                    String cate = jsonObjectApp.getString("category").trim();

                    Log.d("thong.nv", (j + 1) + "---> : " + packageName + "---" + cate);
                    listAppCate[j] = new AppCategolize(packageName, cate);
                }
                sortCate();
                /***********************************************************************************/
            } else {
                Log.d("thong.nv", "responseCode = " + responseCode);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void sortCate() {
        for (int i = 0; i < listAppCate.length; i++)
            for (int j = i + 1; j < listAppCate.length; j++) {
                if (listAppCate[i].categolize.compareTo(listAppCate[j].categolize) > 0) {
                    AppCategolize temp = listAppCate[i];
                    listAppCate[i] = listAppCate[j];
                    listAppCate[j] = temp;
                }
            }
    }

    private class AppCategolize {
        public String packageName;
        public String categolize;

        public AppCategolize(String pack, String cate) {
            packageName = pack;
            categolize = cate;
        }
    }

    public class AppCategoryAdapter extends ArrayAdapter<AppCategolize> {
        AppCategolize[] listApp;
        Context mContext;

        public AppCategoryAdapter(Context context, int resource, int textViewResourceId, AppCategolize[] objects) {
            super(context, resource, textViewResourceId, objects);
            listApp = objects;
            mContext = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.test_categolize, null);
            TextView appName = (TextView) convertView.findViewById(R.id.item_nameApp);
            TextView category = (TextView) convertView.findViewById(R.id.item_categolize);
            ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_cate_image);
            appName.setText(listApp[position].packageName);
            Drawable iconApp = null;
            String appNameGet = null;
            try {
                iconApp = mContext.getApplicationContext().getPackageManager().
                        getApplicationIcon(listApp[position].packageName);
                ApplicationInfo app = mContext.getPackageManager().getApplicationInfo(listApp[position].packageName, 0);
                appNameGet = getPackageManager().getApplicationLabel(app).toString();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (iconApp != null) {
                appIcon.setImageDrawable(iconApp);
            }
            if (appNameGet != null) {
                appName.setText(appNameGet);
            }
            if (position == 0 || !listApp[position].categolize.equals(listApp[position - 1].categolize)) {
                category.setVisibility(View.VISIBLE);
                category.setText(listApp[position].categolize);
            } else {
                category.setVisibility(View.GONE);
            }
            return convertView;
        }

    }
}

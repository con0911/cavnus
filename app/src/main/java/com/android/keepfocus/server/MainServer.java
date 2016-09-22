package com.android.keepfocus.server;


/**
 * Created by sev_user on 9/21/2016.
 */
public class MainServer {
    /*private Context mContext;
    private static final String TAG = "MainServer";
    public static final String BASE_URL = "104.156.224.47/api/group?pRequest=";

    public MainServer(Context context) {
        this.mContext = context;
    }

    public void testAddGroupInServer(ParentGroupItem parentGroupItem) {
        AddGroupAsynTask addAsyn = new AddGroupAsynTask();
        addAsyn.execute(parentGroupItem);
    }

    private JSONObject getJsonObject(ParentGroupItem parentGroupItem) throws JSONException {
        Header headerItem = new Header();
        headerItem.setDeviceCode("S7");
        headerItem.setRegistationId("AB12C");
        headerItem.setPassword("02051992");
        headerItem.setEmail("abc@gmail.com");
        Group group = new Group();
        group.setId(parentGroupItem.getId_group_server());
        group.setCreate_by("nguyethong");
        group.setGroup_name(parentGroupItem.getGroup_name());
        group.setCreate_date(parentGroupItem.getCreate_date());
        String [] groupUsers = {};
        group.setGroup_user(groupUsers);
        group.setGroup_code(parentGroupItem.getGroup_code());

        //
        Gson gson = new Gson();
        JSONObject json = new JSONObject();
        json.put("Header", gson.toJson(headerItem).toString());
        json.put("Type", 1);
        json.put("Action", 1);
        json.put("Group", gson.toJson(groupItem).toString());
        return json;
    }

    private class AddGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "Fail!";
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();
            try {
                //First import library apache
                // For code connecting with server
                HttpPost post = new HttpPost(BASE_URL);
                json = getJsonObject(params[0]);
                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                    *//*Checking response *//*
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(in));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                    result = builder.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Excep = " + e.toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mContext, "respond = " + result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }*/
}

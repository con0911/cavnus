package com.android.keepfocus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ChildAppItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.utils.MainUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import android.app.SearchableInfo;


public class InstallAppActivityForChild extends Activity implements OnClickListener {

    static final int REQUEST_CODE = 101;

    private PackageManager mPm;

    private AppListAdapter mAppListAdapter;

    private MainDatabaseHelper databaseHelper;

    private ArrayList<ChildAppItem> listAppBlock;
    private ArrayList<ChildAppItem> listAppCheck;

    private ListView listView;
    private Button btn_OK;
    private Button btn_Cancel;
    private SearchView mSearchView;
//    private SearchableInfo mSearchableInfo;
    private boolean mSkippedFirst;

    List<AppListItem> appList;
    List<AppListItem> storeAppList;

    private static class AppListItem {
        AppListItem(String packagename, String activityname, String label, Drawable icon) {
            mPackageName = packagename;
            mActivityName = activityname;
            mLabel = label;
            mIcon = icon;
        }

        public String mPackageName;

        public String mActivityName;

        public String mLabel;

        public Drawable mIcon;

        public String getmLabel() {
            return mLabel;
        }

        public void setmLabel(String mLabel) {
            this.mLabel = mLabel;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_install_layout);
        mPm = getPackageManager();
        listAppCheck = new ArrayList<ChildAppItem>();
        databaseHelper = new MainDatabaseHelper(this);
        final Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> launcherApps = mPm.queryIntentActivities(launcherIntent, 0);
        appList = new ArrayList<AppListItem>();
        listAppBlock = MainUtils.childKeepFocusItem.getListAppFocus();
        for (ResolveInfo app : launcherApps) {
            boolean isBlock = false; // this app is block or not
            AppListItem appListItem = new AppListItem((String) app.activityInfo.packageName,
                    (String) app.activityInfo.name, (String) app.activityInfo.loadLabel(mPm),
                    app.activityInfo.loadIcon(mPm));
            for (int i = 0; i < listAppBlock.size(); i++) { // scan for app was block
                if(app.activityInfo.packageName.toString().equals(listAppBlock.get(i).getNamePackage())) {
                    isBlock = true;
                }
            }
            if(!isBlock && !app.activityInfo.packageName.toString().equals("com.android.keepfocus")) {
                //if app has been chosen or is S Keep Focus will not display
                appList.add(appListItem);
            }
        }
        AlphaComparator alphaComparator = new AlphaComparator();
        Collections.sort(appList, alphaComparator);
        // store the current list apps
        storeAppList = new ArrayList<AppListItem>();
        storeAppList.addAll(appList);

        mAppListAdapter = new AppListAdapter(this, R.layout.show_installed_app_main, 0, appList);
        listView = (ListView) findViewById(R.id.listApp);
        listView.setAdapter(mAppListAdapter);
        btn_OK = (Button) findViewById(R.id.btnOK);
        btn_Cancel = (Button) findViewById(R.id.btnCancel);
        btn_OK.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);
        // listView.setOnItemClickListener(this);

        // add left inset to divider under icon [
        //Resources resources = getResources();
        //int divider_inset_size = resources.getDimensionPixelSize(R.dimen.list_item_start_padding)
        //        + resources.getDimensionPixelSize(R.dimen.list_app_icon_size);
        //InsetDrawable insetdivider = new InsetDrawable(getListView().getDivider(), 0, 0, 0, 0);
        //getListView().setDivider(insetdivider);
        // add left inset to divider under icon ]

        setTitle(R.string.selected_app_title);
        ActionBar actionBar = getActionBar();
        //actionBar.hide();
        if (actionBar != null) { // show Navigate up button
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public class AppListAdapter extends ArrayAdapter<AppListItem> {
        SearchFilter mFilter;

        String mSearchText;
        public void setSearchText(String search) {
            mSearchText = search;
        }

        public AppListAdapter(Context context, int resource, int textViewResourceId, List<AppListItem> objects) {
            super(context, resource, textViewResourceId, objects);
            mFilter = new SearchFilter();
            mSearchText = "";
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.show_installed_app_main, null);

            TextView appName = (TextView) convertView.findViewById(R.id.app_name);
            ImageView appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            final String appPackageName = getItem(position).mPackageName;
            final String appLabelName = getItem(position).mLabel;
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.list_item_checkbox);
            for (int i = 0; i < listAppCheck.size(); i++) {
                if(appPackageName.equals(listAppCheck.get(i).getNamePackage())
                        && appLabelName.equals(listAppCheck.get(i).getNameApp())) {
                    checkBox.setChecked(true);
                }
            }
            View v = convertView;
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

            if (getItem(position).mLabel != null) {
                appName.setText(getItem(position).mLabel);
            }
            if (getItem(position).mIcon != null) {
                appIcon.setImageDrawable(getItem(position).mIcon);
            }
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ChildAppItem appItem = new ChildAppItem();
                    appItem.setNamePackage(appPackageName);
                    appItem.setNameApp(appLabelName);
                    buttonView.playSoundEffect(0);
                    if (isChecked) {
                        for (int i = 0; i < listAppCheck.size(); i++) {
                            if(appItem.getNamePackage().equals(listAppCheck.get(i).getNamePackage())
                                    && appItem.getNameApp().equals(listAppCheck.get(i).getNameApp())) {
                                listAppCheck.remove(listAppCheck.get(i));
                            }
                        }
                        listAppCheck.add(appItem);
                    } else {
                        for (int i = 0; i < listAppCheck.size(); i++) {
                            if(appItem.getNamePackage().equals(listAppCheck.get(i).getNamePackage())
                                    && appItem.getNameApp().equals(listAppCheck.get(i).getNameApp())) {
                                listAppCheck.remove(listAppCheck.get(i));
                            }
                        }
                    }
                }
            });
            
            return convertView;
        }
  

        public void runFilter(){
            storeAppList = mFilter.filterApps();
            notifyDataSetChanged();
        }
    }

/*    @Override
    protected void onPause() {
        for (int i = 0; i < listAppCheck.size(); i++) {
            databaseHelper.addAppItemToDb(listAppCheck.get(i), Utils.keepTempFocus.getKeepFocusId());
        }
        listAppBlock = databaseHelper.getListAppById(Utils.keepTempFocus.getKeepFocusId());
        Utils.keepTempFocus.setListAppFocus(listAppBlock);
        super.onPause();
    }*/
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
    
    public void updateApplistAdapter(){
        mAppListAdapter = new AppListAdapter(this, R.layout.show_installed_app_main, 0, storeAppList);
        listView.setAdapter(mAppListAdapter);
    }

    public void refeshSearch(){
        mAppListAdapter = new AppListAdapter(this, R.layout.show_installed_app_main, 0, appList);
        listView.setAdapter(mAppListAdapter);
    }
    private static class AlphaComparator implements Comparator<AppListItem> {
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppListItem a, AppListItem b) {
            // Check for null app names, to avoid NPE in rare cases
            if (a == null || a.mLabel == null) {
                return -1;
            }
            if (b == null || b.mLabel == null) {
                return 1;
            }
            return sCollator.compare(a.mLabel, b.mLabel);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnOK:
            for (int i = 0; i < listAppCheck.size(); i++) {
                databaseHelper.addAppItemToDb(listAppCheck.get(i), MainUtils.childKeepFocusItem.getKeepFocusId());
            }
            listAppBlock = databaseHelper.getListAppById(MainUtils.childKeepFocusItem.getKeepFocusId());
            MainUtils.childKeepFocusItem.setListAppFocus(listAppBlock);
            finish();
            break;

        case R.id.btnCancel:
            finish();
            break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_installed_app, menu);
        MenuItem searchItem = menu.findItem(R.id.search_app);
        mSearchView = (SearchView) searchItem.getActionView();
        //mSearchView.setQueryHint("Search");
        mSearchView.setQuery("", false);
        //mSearchView.setIconifiedByDefault(false);
        //mSearchView.onActionViewExpanded();
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        //mSearchView.setSearchableInfo(mSearchableInfo);
        mSkippedFirst = false;

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                if (mSearchView != null) {
                    mSearchView.clearFocus();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                if (newText.isEmpty() || newText == null){
                    refeshSearch();
                }
                else if (mSkippedFirst || !newText.isEmpty()){
                    mAppListAdapter.runFilter();
                    updateApplistAdapter();
                }

               mSkippedFirst = true;
               return false;
            }

        });

        return true;
    }

    
    public static interface Filter {
        ArrayList<AppListItem> filterApps();
    }
    
    class SearchFilter implements Filter {
        @Override
        public ArrayList<AppListItem> filterApps() {

            ArrayList<AppListItem> filterApps = new ArrayList<AppListItem>();
            String search = "";
            if (mSearchView != null)
                search = mSearchView.getQuery().toString().toUpperCase();
            if (mAppListAdapter != null)
                mAppListAdapter.setSearchText(search);

            Log.e("vinh", " size of appList : "+storeAppList.size());
            for (AppListItem apps : appList) {
                if (apps.getmLabel().toUpperCase().indexOf(search) != -1) {
                    filterApps.add(apps);
                    Log.e("vinh", " name of filterApps : "+apps.getmLabel());
                }
            }
            Log.e("vinh", " size of filterApps : "+filterApps.size());
            return filterApps;
        }
    }

}

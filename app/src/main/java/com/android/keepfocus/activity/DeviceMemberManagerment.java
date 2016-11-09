package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.ChildTimeListAdapter;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.settings.CoverFlowAdapterDevice;
import com.android.keepfocus.settings.CoverFlowAdapterDevice2;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

/**
 * Created by Sion on 11/8/2016.
 */
public class DeviceMemberManagerment extends Activity implements View.OnClickListener{
    private HorizontalListView listProperties;
    private LinearLayout headerView;
    //private ImageView mFABBtnCreate;
    private TextView mTextNoGroup;
    public ArrayList<ParentMemberItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private TextView mIDText;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    private GroupRequestController groupRequestController;
    private GroupManagermentActivity.GetDatabaseReceiver getDatabaseReceiver;
    private IntentFilter intentFilter;

    private ArrayList<ParentProfileItem> listProfileItem;
    private ListView listTime;
    private ChildTimeListAdapter timeListAdapter;


    private LinearLayout detailLayout;
    private ImageButton layoutClose;
    private Animation bottomUp;
    private Animation bottomDown;
    private EditText editName;
    private ImageButton doneEdit;

    private ArrayList<TextView> listStatus;
    private RelativeLayout layoutList;
    private ArrayList<ParentMemberItem> listDefault;
    private FeatureCoverFlow coverFlow;
    private CoverFlowAdapterDevice adapter;
    private boolean lable[];
    private final static long DURATION_SHORT = 200;
    private LinearLayout btnOrange;
    private LinearLayout btnYellow;
    private LinearLayout btnGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_member_layout);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        coverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);
        mContext = this;
        createDefault();//for 0 item
        listProperties = (HorizontalListView) findViewById(R.id.listMember);
        listProperties.setVisibility(View.GONE);
        setTitle(MainUtils.parentGroupItem.getGroup_name());
        mDataHelper = new MainDatabaseHelper(mContext);
        groupRequestController = new GroupRequestController(this);

        layoutList = (RelativeLayout) findViewById(R.id.layout_list);

        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);


        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        //editName = (EditText) findViewById(R.id.editMemberName);

        //doneEdit = (ImageButton) findViewById(R.id.layoutEditDone);
        //doneEdit.setOnClickListener(this);

        listTime = (ListView) findViewById(R.id.listTime);


        detailLayout.setVisibility(View.GONE);
        //layoutClose = (ImageButton) findViewById(R.id.layoutClose);
        //layoutClose.setOnClickListener(this);
        bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

        displayMember();
        coverFlow.setOnScrollPositionListener(onScrollListener());
        coverFlow.setOnItemSelectedListener(onItemSelectedListener());
        getDatabaseReceiver = new GroupManagermentActivity.GetDatabaseReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                displayMember();
                setTitle(MainUtils.parentGroupItem.getGroup_name());
            }
        };
    }

    public void createDefault(){
        ParentMemberItem defaultDevice = new ParentMemberItem();
        defaultDevice.setName_member("Default");//care null parent
        listDefault = new ArrayList<ParentMemberItem>(1);
        listDefault.add(defaultDevice);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title +"'s Family");
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupRequestController.updateListDevice();
        displayMember();
        registerReceiver(getDatabaseReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getDatabaseReceiver);
        if(detailLayout.getVisibility() == View.VISIBLE) {
            //hideKeyboard();
            //detailLayout.startAnimation(bottomDown);
            //detailLayout.setVisibility(View.GONE);
        }
    }

    private FeatureCoverFlow.OnItemSelectedListener onItemSelectedListener() {
        return new FeatureCoverFlow.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("trungdh", "view : " + view + " pos : " + i + " id : " + l);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActivity", "position: " + position);
                View currentView = coverFlow.findViewWithTag(String.valueOf(position));
                if (currentView != null) {
                    lable[position] = true;
                    onMainButtonClicked(currentView);
                }

            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
            }
        };
    }

    private View getItemId(int position) {
        return adapter.getView(position, null, null);
    }

    View preView;

    public void onMainButtonClicked(View btn) {
        btnGreen = (LinearLayout) btn.findViewById(R.id.btn_green);
        btnOrange = (LinearLayout) btn.findViewById(R.id.btn_orange);
        btnYellow = (LinearLayout) btn.findViewById(R.id.btn_yellow);

        if (btnGreen.getVisibility() != View.VISIBLE && btnOrange.getVisibility() != View.VISIBLE && btnYellow.getVisibility() != View.VISIBLE) {
            show(btnYellow, 1, 0);
            show(btnGreen, 2, 0);
            show(btnOrange, 3, 0);
            btn.playSoundEffect(SoundEffectConstants.CLICK);
        }

        if (preView != null && preView != btn) {
            btnGreen = (LinearLayout) preView.findViewById(R.id.btn_green);
            btnOrange = (LinearLayout) preView.findViewById(R.id.btn_orange);
            btnYellow = (LinearLayout) preView.findViewById(R.id.btn_yellow);
            hide(btnOrange);
            hide(btnYellow);
            hide(btnGreen);
        }
        preView = btn;
    }

    private final void hide(final View child) {
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(0)
                .translationY(0)
                .start();
        child.setVisibility(View.INVISIBLE);
    }

    private final void show(final View child, final int position, final int radius) {
        float angleDeg = 180.f;
        int dist = radius;
        switch (position) {
            case 1:
                angleDeg += 0.f;
                child.setVisibility(View.VISIBLE);
                break;
            case 2:
                angleDeg += 90.f;
                child.setVisibility(View.VISIBLE);
                break;
            case 3:
                angleDeg += 180.f;
                child.setVisibility(View.VISIBLE);
                break;
        }

        final float angleRad = (float) (angleDeg * Math.PI / 180.f);

        final Float x = dist * (float) Math.cos(angleRad);
        final Float y = dist * (float) Math.sin(angleRad);
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(x)
                .translationY(y)
                .start();
    }


    public void addNewMember() {
        mView = getLayoutInflater().inflate(R.layout.add_member_layout, null);
        mIDText = (TextView) mView.findViewById(R.id.add_member_ID);
        mIDText.setText("ID Family : "+MainUtils.parentGroupItem.getGroup_code());
        mTextMsg = (TextView) mView.findViewById(R.id.add_member_text);
        mTextMsg.setText(getResources().getString(R.string.add_member_text));

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new device")
                .setPositiveButton("Create Test", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParentMemberItem childItem = new ParentMemberItem();
                        childItem.setName_member("Test device");
                        childItem.setType_member(0);
                        childItem.setId_member(mDataHelper.addMemberItemParent(childItem,MainUtils.parentGroupItem.getId_group()));
                        MainUtils.parentGroupItem.getListMember().add(childItem);

                        displayMember();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();
    }

    public void displayMember() {
        mDataHelper.makeDetailOneGroupItemParent(MainUtils.parentGroupItem);
        listBlockPropertiesArr = MainUtils.parentGroupItem.getListMember();
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_member);
            adapter = new CoverFlowAdapterDevice(this, listDefault);
            layoutList.setVisibility(View.GONE);
            listProperties.setVisibility(View.GONE);
        }else if (listBlockPropertiesArr.size() > 0 && listBlockPropertiesArr.size() <= 3) {
            mTextNoGroup.setText(" ");
            layoutList.setVisibility(View.GONE);
            listProperties.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapterDevice(this, listBlockPropertiesArr);
            CoverFlowAdapterDevice2 adapter2 = new CoverFlowAdapterDevice2(this, listBlockPropertiesArr);
            listProperties.setAdapter(adapter2);
        } else if (listBlockPropertiesArr.size() > 3) {
            mTextNoGroup.setText(" ");
            listProperties.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapterDevice(this, listBlockPropertiesArr);
        }
        coverFlow.setAdapter(adapter);
        lable = new boolean[adapter.getCount()];

    }


    public void displayDetailTime(){
        mDataHelper.makeDetailOneMemberItemParent(MainUtils.memberItem);
        listProfileItem = MainUtils.memberItem.getListProfile();
        timeListAdapter = new ChildTimeListAdapter(this, R.layout.time_adapter, 0, listProfileItem);
        listTime.setAdapter(timeListAdapter);
    }

    public void goToScheduler(int position){
        MainUtils.parentProfile = timeListAdapter.getItem(position);
        Intent intent = new Intent(DeviceMemberManagerment.this, SchedulerConfigActivity.class);
        startActivity(intent);
    }

    public void onItemClick(int position) {
        MainUtils.memberItem = adapter.getItem(position);
        Intent intent = new Intent(DeviceMemberManagerment.this, ParentSchedulerActivity.class);
        startActivity(intent);
    }

    public void listScheduler(int position) {
        MainUtils.memberItem = adapter.getItem(position);
        Intent intent = new Intent(DeviceMemberManagerment.this, ParentSchedulerActivity.class);
        startActivity(intent);
    }

    public void onItemLongClick(int position) {
        deleteMember(position);
    }

    public void deleteMember(int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to remove this device?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Delete device")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mDataHelper.deleteMemberItemById(adapter.getItem(mPosition).getId_member());
                        MainUtils.parentGroupItem.getListMember().remove(mPosition);
                        displayMember();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mDeleteDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.add :
                addNewMember();
                break;
            case R.id.rename :
                renameGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void renameGroup() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);
        mTextMsg.setText("Name family:");

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Edit name : ")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            MainUtils.parentGroupItem.setGroup_name(mEditText.getText().toString());
                            groupRequestController.updateGroupInServer();
                        } else {
                            dialog.cancel();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();

        mAlertDialog.show();
    }

    public void showDetail(int position) {
        MainUtils.memberItem = adapter.getItem(position);
        //editName.setText(MainUtils.memberItem.getName_member().toString());
        if(detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        } else {
            detailLayout.setVisibility(View.GONE);
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }
        displayDetailTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layoutClose:
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    detailLayout.setVisibility(View.GONE);
                }

            case R.id.layoutEditDone:
                String name = editName.getText().toString();
                MainUtils.memberItem.setName_member(name);
                mDataHelper.updateMemberItem(MainUtils.memberItem);
                displayMember();
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    detailLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.editFamilyName:
                Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();

            default:
                break;
        }
    }



    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

}

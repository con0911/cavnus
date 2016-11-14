package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.settings.CoverFlowAdapter;
import com.android.keepfocus.settings.CoverFlowAdapter2;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

/**
 * Created by sev_user on 11/7/2016.
 */
public class FamilyManagerment extends Activity implements View.OnClickListener{

    private FeatureCoverFlow coverFlow;
    private CoverFlowAdapter adapter;
    private boolean lable[];

    private final static long DURATION_SHORT = 200;
    private LinearLayout btnOrange;
    private LinearLayout btnYellow;
    private LinearLayout btnGreen;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private TextView mTextNoGroup;
    public ArrayList<ParentGroupItem> listFamily;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    static Button notifCount;
    static int mNotifCount = 0;
    private GroupRequestController groupRequestController;
    private IntentFilter intentFilter;
    private LinearLayout detailLayout;
    private Animation bottomUp;
    private Animation bottomDown;
    private RelativeLayout layoutList;
    private ArrayList<ParentGroupItem> listDefault;
    private HorizontalListView listTwoFamily;
    private TextView textName;
    private TextView nameFamily;
    private TextView listDeviceName;
    private TextView textFamilyID;
    private static int PICK_IMAGE = 1;
    private static int positionNow = 0;


    private BroadcastReceiver getDatabaseReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            displayProfile();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_family_layout);
        coverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);
        mContext = this;
        mDataHelper = new MainDatabaseHelper(mContext);
        ParentGroupItem defaultParent = new ParentGroupItem();
        defaultParent.setGroup_name("Default");//care null parent
        listDefault = new ArrayList<ParentGroupItem>(1);
        listDefault.add(defaultParent);

        listTwoFamily = (HorizontalListView) findViewById(R.id.listTwoFamily);
        listTwoFamily.setVisibility(View.GONE);

        setTitle("Family management");
        layoutList = (RelativeLayout) findViewById(R.id.layout_list);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        nameFamily = (TextView) findViewById(R.id.nameFamily);
        listDeviceName = (TextView) findViewById(R.id.listDeviceName);
        textFamilyID = (TextView) findViewById(R.id.family_id);
        detailLayout.setVisibility(View.GONE);
        //layoutClose = (ImageButton) findViewById(R.id.layoutClose);
        bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this,
                R.anim.bottom_down);

        displayProfile();
        coverFlow.setOnScrollPositionListener(onScrollListener());
        coverFlow.setOnItemSelectedListener(onItemSelectedListener());


        groupRequestController = new GroupRequestController(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);

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
                    MainUtils.parentGroupItem = adapter.getItem(position);
                    nameFamily.setText(MainUtils.parentGroupItem.getGroup_name() + " Family");
                    ArrayList<ParentMemberItem> listDevice = MainUtils.parentGroupItem.getListMember();
                    if (listDevice.size() > 0) {
                        String listDeviceText = " ";
                        for (int i =0; i < listDevice.size(); i++){
                            listDeviceText += listDevice.get(i).getName_member() + ", ";
                        }
                        listDeviceText = listDeviceText.substring(0,listDeviceText.length()-2);
                        listDeviceName.setText(listDeviceText);
                    } else {
                        listDeviceName.setText(" ");
                    }
                    showAddMember(position);
                }

            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
            }
        };
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


    public void displayProfile() {
        listFamily = mDataHelper.getAllGroupItemParent();
        if (listFamily.size() == 0){
            mTextNoGroup.setText(R.string.text_no_group);
            adapter = new CoverFlowAdapter(this, listDefault);
            layoutList.setVisibility(View.GONE);
            listTwoFamily.setVisibility(View.GONE);
            detailLayout.setVisibility(View.GONE);
            if(detailLayout.getVisibility() == View.VISIBLE) {
                detailLayout.setVisibility(View.GONE);
            }

        }else if (listFamily.size() > 0 && listFamily.size() <= 3) {
            mTextNoGroup.setText(" ");
            layoutList.setVisibility(View.GONE);
            listTwoFamily.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapter(this, listFamily);
            CoverFlowAdapter2 adapter2 = new CoverFlowAdapter2(this, listFamily);
            listTwoFamily.setAdapter(adapter2);
            if(detailLayout.getVisibility() == View.VISIBLE) {
                detailLayout.setVisibility(View.GONE);
            }
        } else if (listFamily.size() > 3) {
            mTextNoGroup.setText(" ");
            listTwoFamily.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapter(this, listFamily);
            if(detailLayout.getVisibility() == View.VISIBLE) {
                detailLayout.setVisibility(View.GONE);
            }
        }
        coverFlow.setAdapter(adapter);
        lable = new boolean[adapter.getCount()];
    }

    private View getItemId(int position) {
        return adapter.getView(position, null, null);
    }

    View preView;

    public void onMainButtonClicked(View btn) {
        btnGreen = (LinearLayout) btn.findViewById(R.id.btn_green);
        btnOrange = (LinearLayout) btn.findViewById(R.id.btn_orange);
        btnYellow = (LinearLayout) btn.findViewById(R.id.btn_yellow);
        textName = (TextView) btn.findViewById(R.id.family_name);

        if (btnGreen.getVisibility() != View.VISIBLE && btnOrange.getVisibility() != View.VISIBLE && btnYellow.getVisibility() != View.VISIBLE) {
            show(btnYellow, 1, 0);
            show(btnGreen, 2, 0);
            show(btnOrange, 3, 0);
            btn.playSoundEffect(SoundEffectConstants.CLICK);
            textName.setVisibility(View.GONE);
        }

        if (preView != null && preView != btn) {
            btnGreen = (LinearLayout) preView.findViewById(R.id.btn_green);
            btnOrange = (LinearLayout) preView.findViewById(R.id.btn_orange);
            btnYellow = (LinearLayout) preView.findViewById(R.id.btn_yellow);
            textName = (TextView) preView.findViewById(R.id.family_name);
            hide(btnOrange);
            hide(btnYellow);
            hide(btnGreen);
            textName.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            /*case R.id.editFamilyIcon:
                Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();
                *//*Intent intent = new Intent();
                intent.setType("image*//**//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*//*
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE);*/

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("contt","onActivityResult= " + data);
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data!=null) {
            Uri selectedImage = data.getData();
            boolean success = true;
            Bitmap bitmap = null;
            try
            {
                success = true;
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver() , selectedImage);
            }
            catch (Exception e)
            {
                success = false;
            }
            if(success) {
                MainUtils.parentGroupItem.setIcon_uri(selectedImage.toString());
                mDataHelper.updateGroupItem(MainUtils.parentGroupItem);
            }
            //familyIconEdit.setImageBitmap(bitmap);
            if(MainUtils.parentGroupItem!=null){
                coverFlow.scrollToPosition(positionNow);
            }
            displayProfile();


            //familyIconEdit.setImageURI(selectedImage);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //displayProfile();
        registerReceiver(getDatabaseReceiver,intentFilter);
        //groupRequestController.getGroupInServer();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getDatabaseReceiver);

    }

    public void createNewGroup() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new family").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            //ParentGroupItem parentItem = new ParentGroupItem();
                            //parentItem.setGroup_name(mEditText.getText().toString());
                            //mDataHelper.addGroupItemParent(parentItem);
                            displayProfile();
                            MainUtils.parentGroupItem = new ParentGroupItem();
                            MainUtils.parentGroupItem.setGroup_name(mEditText.getText().toString());
                            //MainUtils.parentGroupItem.setGroup_code("registationId");
                            groupRequestController.testAddGroupInServer();
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


    public void changeIcon(int position) {
        MainUtils.parentGroupItem = adapter.getItem(position);
        Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        positionNow = position;
        startActivityForResult(i, PICK_IMAGE);
    }


    public void showDetail(int position) {
        MainUtils.parentGroupItem = adapter.getItem(position);
        Intent intent = new Intent(this, DeviceMemberManagerment.class);
        startActivity(intent);
    }

    public void showAddMember(int position) {
        MainUtils.parentGroupItem = adapter.getItem(position);


        if(detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }

        textFamilyID.setText(MainUtils.parentGroupItem.getGroup_code());
    }


    public void onItemLongClick(int position) {
        deleteProfile(position);
    }

    public void deleteProfile(final int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to remove this family?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Remove family")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainUtils.parentGroupItem =listFamily.get(position);
                        //mDataHelper.deleteGroupItemById(MainUtils.parentGroupItem.getId_group());
                        //displayProfile();
                        groupRequestController.deleteGroupInServer();
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
        inflater.inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_add :
                createNewGroup();
                break;
            case R.id.action_update :
                groupRequestController.getGroupInServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewMember(int position) {
        detailLayout.startAnimation(bottomUp);
        detailLayout.setVisibility(View.VISIBLE);
        textFamilyID.setText(listFamily.get(position).getGroup_code());
    }

}

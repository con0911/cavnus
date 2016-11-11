package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.settings.CoverFlowAdapter;
import com.android.keepfocus.settings.CoverFlowAdapter2;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.io.IOException;
import java.io.InputStream;
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

    private ImageView mFABBtnCreate;
    private TextView mTextNoGroup;
    public ArrayList<ParentGroupItem> listFamily;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    static Button notifCount;
    static int mNotifCount = 0;
    private GroupRequestController groupRequestController;
    private GroupManagermentActivity.GetDatabaseReceiver getDatabaseReceiver;
    private IntentFilter intentFilter;
    private LinearLayout detailLayout;
    private ImageButton layoutClose;
    private Animation bottomUp;
    private Animation bottomDown;
    private EditText editName;
    private Button doneEdit;
    private ImageView familyIconEdit;
    private RelativeLayout layoutList;
    private ArrayList<ParentGroupItem> listDefault;
    private HorizontalListView listTwoFamily;
    private TextView textName;
    private static int PICK_IMAGE = 1;
    private static final int REQUEST_READ_FILE = 0;

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
        mFABBtnCreate = (ImageView) findViewById(R.id.createButton);
        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        editName = (EditText) findViewById(R.id.editFamilyName);
        familyIconEdit = (ImageView) findViewById(R.id.editFamilyIcon);
        familyIconEdit.setOnClickListener((View.OnClickListener) this);

        doneEdit = (Button) findViewById(R.id.layoutEditDone);
        doneEdit.setOnClickListener(this);
        detailLayout.setVisibility(View.GONE);
        layoutClose = (ImageButton) findViewById(R.id.layoutClose);
        layoutClose.setOnClickListener(this);
        bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this,
                R.anim.bottom_down);
        mFABBtnCreate.setOnClickListener(this);

        displayProfile();
        coverFlow.setOnScrollPositionListener(onScrollListener());
        coverFlow.setOnItemSelectedListener(onItemSelectedListener());

        groupRequestController = new GroupRequestController(this);
        getDatabaseReceiver = new GroupManagermentActivity.GetDatabaseReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                displayProfile();
            }
        };
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
                    showDetail(position);
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

        }else if (listFamily.size() > 0 && listFamily.size() <= 3) {
            mTextNoGroup.setText(" ");
            layoutList.setVisibility(View.GONE);
            listTwoFamily.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapter(this, listFamily);
            CoverFlowAdapter2 adapter2 = new CoverFlowAdapter2(this, listFamily);
            listTwoFamily.setAdapter(adapter2);
        } else if (listFamily.size() > 3) {
            mTextNoGroup.setText(" ");
            listTwoFamily.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapter(this, listFamily);
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
            case R.id.createButton:
                createNewGroup();
                break;

            case R.id.layoutClose:
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    //detailLayout.setVisibility(View.GONE);
                }

            case R.id.layoutEditDone:
                String name = editName.getText().toString();
                MainUtils.parentGroupItem.setGroup_name(name);
                mDataHelper.updateGroupItem(MainUtils.parentGroupItem);
                displayProfile();
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    //detailLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.editFamilyIcon:
                Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE);

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("contt","onActivityResult= "+ requestCode +"-"+ resultCode +"-"+ data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data!=null) {
            Uri selectedImage = data.getData();
            boolean success = true;
            Log.d("contt","Uri= " + selectedImage);
            Bitmap bitmap = null;
            try
            {
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
            displayProfile();
            //familyIconEdit.setImageURI(selectedImage);
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayProfile();
        registerReceiver(getDatabaseReceiver,intentFilter);
        //groupRequestController.getGroupInServer();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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

    public void createDefaultFamily(){
        ParentGroupItem parentItem = new ParentGroupItem();
        parentItem.setGroup_name("Default");
        parentItem.setId_group(12345);
        MainUtils.parentGroupItem = parentItem;
        mDataHelper.addGroupItemParent(parentItem);
    }

    public void showListMember(int position) {
        MainUtils.parentGroupItem = adapter.getItem(position);
        Intent intent = new Intent(this, DeviceMemberManagerment.class);
        startActivity(intent);
    }
    public void showDetail(int position) {
        MainUtils.parentGroupItem = adapter.getItem(position);
        editName.setText(MainUtils.parentGroupItem.getGroup_name().toString());
        //Intent intent = new Intent(this, GroupDetail.class);
        //startActivity(intent);
        if(detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        } else {
            detailLayout.setVisibility(View.GONE);
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }

        Uri selectedImage = Uri.parse(MainUtils.parentGroupItem.getIcon_uri());
        InputStream is = null;
        try {
            is = mContext.getContentResolver().openInputStream(selectedImage);
        }catch (Exception e){
            Log.d("TAG", "Exception " + e);
        }
        if (is!=null) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver() , selectedImage);
                familyIconEdit.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.images);
                familyIconEdit.setImageBitmap(icon);
            }
        } else {
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.images);
            familyIconEdit.setImageBitmap(icon);
        }
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

    public static class GetDatabaseReceiver extends BroadcastReceiver {//must be static
        @Override
        public void onReceive(Context context, Intent intent) {
        }
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
            case R.id.action_settings :
                groupRequestController.getGroupInServer();
                break;
            case R.id.menu_settings :
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewMember(int position) {
        mView = getLayoutInflater().inflate(R.layout.add_member_layout, null);
        TextView mIDText = (TextView) mView.findViewById(R.id.add_member_ID);
        mIDText.setText("ID Family : " + listFamily.get(position).getGroup_code());
        mTextMsg = (TextView) mView.findViewById(R.id.add_member_text);
        mTextMsg.setText(getResources().getString(R.string.add_member_text));

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new device").setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();
    }
}

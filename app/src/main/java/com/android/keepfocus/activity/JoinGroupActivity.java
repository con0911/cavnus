package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.receive.DevicePolicyReceiver;

public class JoinGroupActivity extends Activity {
    private static final String TAG = "JoinGroupActivity";
    private Button btnImageDone;
    private String[] listMemberType;
    private Spinner spinnerType;
    private static final int REQUEST_ADMIN_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private final int SHOW_DIALOG_REQUEST_ADMIN_PERMISSION = 1;
    private boolean isTurnOnAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_member_qr_code);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DevicePolicyReceiver.class);

        btnImageDone = (Button) findViewById(R.id.doneImageBtn);
        btnImageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRequestDialog();

            }
        });

    }

    @Override
    protected void onResume() {
        try {

            if (!isTurnOnAdmin) {
                if (!mDPM.isAdminActive(mAdminName)) {
                /*Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to enable admin permission.");
                startActivityForResult(intent, REQUEST_ADMIN_CODE);*/
                    showAdminDialog(SHOW_DIALOG_REQUEST_ADMIN_PERMISSION);
                } else {
                    mDPM.lockNow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isTurnOnAdmin = true;
        super.onResume();
    }

    private void showAdminDialog(int dialogId) {
        View view = getLayoutInflater().inflate(R.layout.request_admin_popup, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.request_text);
        switch (dialogId) {
            case SHOW_DIALOG_REQUEST_ADMIN_PERMISSION:
                txtMsg.setText(R.string.request_admin_text);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle(getString(R.string.title_request_admin))
                        .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isTurnOnAdmin = true;
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to enable admin permission.");
                                startActivityForResult(intent, REQUEST_ADMIN_CODE);

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        }).create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }

    public void createRequestDialog() {
        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(
                JoinGroupActivity.this);
        requestBuilder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)
                JoinGroupActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.request_join_dialog, null);
        spinnerType = (Spinner) view.findViewById(R.id.spinerType);
        listMemberType = new String[2];
        listMemberType[0] = "Manager";
        listMemberType[1] = "Children";
        ArrayAdapter adapterType = new ArrayAdapter(this, android.R.layout.simple_spinner_item,listMemberType);
        spinnerType.setAdapter(adapterType);

        final LinearLayout linceseLayout = (LinearLayout) view.findViewById(R.id.linceseLayout);
        linceseLayout.setVisibility(View.GONE);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    linceseLayout.setVisibility(View.GONE);
                } else if (position==1) {
                    linceseLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        requestBuilder.setView(view);
        requestBuilder.setMessage("Request Join Family");
        requestBuilder.setCancelable(true);
        requestBuilder.setPositiveButton("Accept",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        requestBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog requestDialog = requestBuilder.create();
        requestDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADMIN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Administration is enabled");
            } else {

            }
        }
    }
}
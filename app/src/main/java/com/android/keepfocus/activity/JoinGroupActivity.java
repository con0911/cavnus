package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.keepfocus.R;

public class JoinGroupActivity extends Activity {
    private ImageButton btnImageDone;
    private String[] listMemberType;
    private Spinner spinnerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_member_qr_code);
        btnImageDone = (ImageButton) findViewById(R.id.doneImageBtn);
        btnImageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRequestDialog();

            }
        });

    }

    private void createRequestDialog() {
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
        requestBuilder.setMessage("Request Join Group");
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
}
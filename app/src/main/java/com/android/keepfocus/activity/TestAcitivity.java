package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.fancycoverflow.FancyCoverFlow;
import com.android.keepfocus.fancycoverflow.FancyCoverFlowAdapter;

import java.util.ArrayList;


/**
 * Created by sev_user on 11/23/2016.
 */

public class TestAcitivity extends Activity {
    private ArrayList<ParentMemberItem> testdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        FancyCoverFlow fancyCoverFlow = (FancyCoverFlow) findViewById(R.id.fancyCoverFlow);
        //fancyCoverFlow.setUnselectedAlpha(1.0f);
        fancyCoverFlow.setUnselectedSaturation(0.0f);
        fancyCoverFlow.setUnselectedScale(0.5f);
        fancyCoverFlow.setSpacing(50);
        fancyCoverFlow.setMaxRotation(0);
        fancyCoverFlow.setScaleDownGravity(0.2f);
        //fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
        testdata = new ArrayList<ParentMemberItem>();
        ParentMemberItem paren1 = new ParentMemberItem();
        ParentMemberItem paren12 = new ParentMemberItem();
        ParentMemberItem paren123 = new ParentMemberItem();
        testdata.add(paren1);
        testdata.add(paren12);
        testdata.add(paren123);
        fancyCoverFlow.setAdapter(new ViewGroupExampleAdapter(this, testdata));
//        fancyCoverFlow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d("trungdh", "view : " + v + " hasFocus : " + hasFocus);
//            }
//        });
//        fancyCoverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("trung", " position : " + position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }


    private static class ViewGroupExampleAdapter extends FancyCoverFlowAdapter {

        private ArrayList<ParentMemberItem> data;
        private Activity activity;

        public ViewGroupExampleAdapter(Activity activity, ArrayList<ParentMemberItem> objects) {
            this.activity = activity;
            this.data = objects;
        }
        // =============================================================================
        // Private members
        // =============================================================================

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ParentMemberItem getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getCoverFlowItem(int position, View convertView, ViewGroup parent) {

            final TextView blockall;
            final TextView allowAll;
            final TextView addmember;
            final TextView name;
            final ImageView iconFamily;
            final LinearLayout blockallLayout;
            final LinearLayout allowallLayout;
            final LinearLayout addMemberLayout;

            final ParentMemberItem profileItem = getItem(position);

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_adapter_layout, null, false);

            iconFamily = (ImageView) convertView.findViewById(R.id.img_center_child);
            blockall = (TextView) convertView.findViewById(R.id.txt_left_side);
            addmember = (TextView) convertView.findViewById(R.id.txt_center_side);
            allowAll = (TextView) convertView.findViewById(R.id.txt_right_side);
            name = (TextView) convertView.findViewById(R.id.family_name);
            name.setText(position + "");
            blockall.setText("Block all");
            allowAll.setText("Allow all");
            addmember.setText("Add member");
            iconFamily.setImageResource(R.drawable.icon_app);
            blockallLayout = (LinearLayout) convertView.findViewById(R.id.btn_left_side);
            allowallLayout = (LinearLayout) convertView.findViewById(R.id.btn_right_side);
            addMemberLayout = (LinearLayout) convertView.findViewById(R.id.btn_center_side);
            blockallLayout.setVisibility(View.VISIBLE);
            allowallLayout.setVisibility(View.VISIBLE);
            addMemberLayout.setVisibility(View.VISIBLE);
            convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(500,
                    500));
            blockall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity,"Block all",Toast.LENGTH_LONG).show();
                    blockallLayout.setBackgroundResource(R.drawable.circle_left_side_press);
                    allowallLayout.setBackgroundResource(R.drawable.circle_right_side_no_press);
                }
            });

            allowAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity,"allowAll all",Toast.LENGTH_LONG).show();
                    blockallLayout.setBackgroundResource(R.drawable.circle_left_side_no_press);
                    allowallLayout.setBackgroundResource(R.drawable.circle_right_side_press);
                }
            });

            addmember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity,"addmember all",Toast.LENGTH_LONG).show();
                }
            });
            return convertView;
        }
    }
}

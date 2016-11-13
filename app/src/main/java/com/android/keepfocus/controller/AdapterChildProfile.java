package com.android.keepfocus.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.ChildSchedulerActivity;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.MainDatabaseHelper;

import java.util.ArrayList;

public class AdapterChildProfile extends ArrayAdapter<ChildKeepFocusItem> {
    private Activity activity;
    private static LayoutInflater inflater = null;

    public AdapterChildProfile(Activity activity, int resource,
                               int textViewResourceId, ArrayList<ChildKeepFocusItem> objects) {
        super(activity, resource, textViewResourceId, objects);
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }
    MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
            getContext());
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.tab_profile_schedule, parent, false);
        View vi = convertView;

        TextView nameProfile = (TextView) convertView.findViewById(R.id.titleProperties);
        TextView dayBlock = (TextView) convertView.findViewById(R.id.textTab);
        LinearLayout listAppBlock = (LinearLayout) convertView.findViewById(R.id.showAppBlock);
        Switch isActive = (Switch) convertView.findViewById(R.id.switchTab);
        final int mPosition = position;
        final ChildKeepFocusItem profileItem = getItem(mPosition);
        nameProfile.setText(profileItem.getNameFocus());
        dayBlock.setText(profileItem.getDayFocus());
        isActive.setOnCheckedChangeListener(null);//issue scroll view lost checkbox
        isActive.setChecked(profileItem.isActive());
        isActive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kFDHelper.updateFocusItem(profileItem);//issue scroll view lost checkbox
            }
        });
        isActive.setEnabled(false);
        listAppBlock.removeAllViews();
        for (int i = 0; i < profileItem.getListAppFocus().size(); i++) {
            Drawable iconApp = null;
            try
            {
                iconApp = activity.getApplicationContext().getPackageManager().
                        getApplicationIcon(profileItem.getListAppFocus().get(i).getNamePackage());
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
            if(iconApp != null){
                CustomIcon appIconView = new CustomIcon(activity);
                appIconView.setImageDrawable(iconApp);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 5, 5, 5);// icon margin
                listAppBlock.addView(appIconView, layoutParams);
                final String appBlockName = profileItem.getListAppFocus().get(i).getNameApp();
                appIconView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Toast.makeText(activity.getApplicationContext(), "" + appBlockName, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        vi.setOnClickListener(new OnItemClickListener( position ));
        return convertView;
    }

    public class CustomIcon extends ImageView {

        public CustomIcon(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomIcon(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomIcon(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (width != height) {
                width = height;
            }
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            );
        }
    }

    private class OnItemClickListener  implements OnClickListener{
        private int mPosition;
        OnItemClickListener(int position){
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
            ChildSchedulerActivity childSchedulerActivity = (ChildSchedulerActivity)activity;
            childSchedulerActivity.goToSchedule(mPosition);
        }
    }
}

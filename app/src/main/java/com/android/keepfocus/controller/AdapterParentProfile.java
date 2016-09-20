package com.android.keepfocus.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.keepfocus.R;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.keepfocus.activity.ParentSchedulerActivity;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentProfileItem;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class AdapterParentProfile extends ArrayAdapter<ParentProfileItem> {
    private Activity activity;
    private static LayoutInflater inflater = null;

    public AdapterParentProfile(Activity activity, int resource,
                                int textViewResourceId, ArrayList<ParentProfileItem> objects) {
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
        final ParentProfileItem profileItem = getItem(mPosition);
        nameProfile.setText(profileItem.getName_profile());
        dayBlock.setText(profileItem.getDay_profile());
        isActive.setOnCheckedChangeListener(null);//issue scroll view lost checkbox
        isActive.setChecked(profileItem.isActive());
        isActive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profileItem.setActive(isChecked);
                kFDHelper.updateProfileItem(profileItem);//issue scroll view lost checkbox
            }
        });
        listAppBlock.removeAllViews();
        for (int i = 0; i < profileItem.getListAppBlock().size(); i++) {
            Drawable iconApp = null;
            try
            {
                iconApp = activity.getApplicationContext().getPackageManager().
                        getApplicationIcon(profileItem.getListAppBlock().get(i).getNamePackage());
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
                final String appBlockName = profileItem.getListAppBlock().get(i).getNameApp();
                appIconView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Toast.makeText(activity.getApplicationContext(), "" + appBlockName, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        vi.setOnClickListener(new OnItemClickListener( position ));
        vi.setOnLongClickListener(new OnItemLongClickListener(position) );
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
            ParentSchedulerActivity parentSchedulerActivity = (ParentSchedulerActivity)activity;
            parentSchedulerActivity.onItemClick(mPosition);
        }
    }

    private class OnItemLongClickListener  implements OnLongClickListener{
        private int mPosition;
        OnItemLongClickListener(int position){
            mPosition = position;
        }
        @Override
        public boolean onLongClick(View v) {
            Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake);
            v.startAnimation(shake);
            ParentSchedulerActivity parentSchedulerActivity = (ParentSchedulerActivity)activity;
            parentSchedulerActivity.onItemLongClick(mPosition);
            return false;
        }
    }
}

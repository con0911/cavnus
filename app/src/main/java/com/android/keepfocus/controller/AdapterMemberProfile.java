package com.android.keepfocus.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.GroupDetail;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;

import java.util.ArrayList;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class AdapterMemberProfile extends ArrayAdapter<ParentMemberItem> {
    private Activity activity;
    private static LayoutInflater inflater = null;

    public AdapterMemberProfile(Activity activity, int resource,
                                int textViewResourceId, ArrayList<ParentMemberItem> objects) {
        super(activity, resource, textViewResourceId, objects);
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }
    MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
            getContext());
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.tab_group, parent, false);
        View vi = convertView;

        TextView nameProfile = (TextView) convertView.findViewById(R.id.titleProperties);
        TextView dayBlock = (TextView) convertView.findViewById(R.id.textTab);
        LinearLayout listAppBlock = (LinearLayout) convertView.findViewById(R.id.showAppBlock);
        final int mPosition = position;
        final ParentMemberItem profileItem = getItem(mPosition);
        nameProfile.setText(profileItem.getName_member());
        ImageButton closeBtn = (ImageButton) convertView.findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupDetail groupDetail = (GroupDetail)activity;
                groupDetail.onItemLongClick(mPosition);
            }
        });
        //dayBlock.setText(profileItem.getType_member());
        listAppBlock.removeAllViews();
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
            GroupDetail groupDetailActivity = (GroupDetail)activity;
            groupDetailActivity.onItemClick(mPosition);
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
            GroupDetail groupDetailActivity = (GroupDetail)activity;
            groupDetailActivity.onItemLongClick(mPosition);
            return false;
        }
    }
}

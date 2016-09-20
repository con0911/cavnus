package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.utils.MainUtils;


public class BlockLauncher extends Activity {
    public static boolean isPause = false;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        isPause = false;
        context = this;
        setContentView(R.layout.block_launcher);
        Button back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

            }
        });
        ImageView block = (ImageView) findViewById(R.id.imageBlock);
        TextView appName = (TextView) findViewById(R.id.nameAppBlock);
        if (MainUtils.namePackageBlock != null) {
            Drawable appIcon = null;
            try {
                appIcon = context.getPackageManager().getApplicationIcon(
                        MainUtils.namePackageBlock);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (appIcon != null) {
                block.setImageDrawable(appIcon);
            }
            PackageManager packageManager = getApplicationContext()
                    .getPackageManager();
            String nameApp = null;
            try {
                nameApp = (String) packageManager
                        .getApplicationLabel(packageManager.getApplicationInfo(
                                MainUtils.namePackageBlock,
                                PackageManager.GET_META_DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nameApp != null) {
                appName.setText(nameApp + " " +getString(R.string.block_app_description));
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        isPause = true;
    }

    public static void finishBlock() {
        isPause = false;
        ((Activity) context).finish();
    }

}

package top.honer.qt;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class HBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "HBroadcastReceiver";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, ACTION_BOOT);
        // 开机自启动
        if (intent.getAction().equals(ACTION_BOOT)) {
            boolean enableAutoStart = false;
            try {
                ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(
                        new ComponentName(context, HBroadcastReceiver.class),
                        PackageManager.GET_META_DATA);
                Bundle bundle = activityInfo.metaData;
                if (bundle != null) {
                    enableAutoStart = bundle.getBoolean("android.app.autostart", false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (enableAutoStart) {
                Intent StartIntent = new Intent(context, HActivity.class);
                StartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(StartIntent);
            }
        }
    }
}

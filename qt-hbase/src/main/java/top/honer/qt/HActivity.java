package top.honer.qt;

import org.qtproject.qt5.android.QtNative;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;
import android.util.Log;

public class HActivity extends org.qtproject.qt5.android.bindings.QtActivity {

    private long lastTime = 0;
    // 是否保持屏幕常亮
    private boolean alwaysOn = false;
    // 是否全屏
    private boolean showFull = false;
    // 是否让Qt处理KeyBackPressed事件
    private boolean qtBackPressed = false;

    private static final String ACTION_CUSTOM_BROADCAST = "QT.CUSTOM_BROADCAST";

    public HActivity() {

    }

    public void sendBroadcast(String message) {
        Intent intent = new Intent();
        intent.setAction(ACTION_CUSTOM_BROADCAST);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(this.getComponentName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = activityInfo.metaData;
            if (bundle != null) {
                alwaysOn = bundle.getBoolean("android.app.always_on", false);
                showFull = bundle.getBoolean("android.app.show_full", false);
                qtBackPressed = bundle.getBoolean("android.app.qt_back_pressed", false);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (alwaysOn) {
            keepScreenOn();
        }
        if (showFull) {
            setFullScreen();
        }
        Log.d(QtNative.QtTAG, "HActivity is Create");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(QtNative.QtTAG, "HActivity is Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(QtNative.QtTAG, "HActivity is Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(QtNative.QtTAG, "HActivity is Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(QtNative.QtTAG, "HActivity is Restart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(QtNative.QtTAG, "HActivity is Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(QtNative.QtTAG, "HActivity is Destroy");
    }

    // 屏幕常亮
    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // 全屏

    @SuppressWarnings("deprecation")
    private void setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置全屏//稳定布局//隐藏导航栏
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
            // 允许绘制系统栏背景
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 设置状态栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            // 隐藏状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int newKeyCode = keyCode;
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            newKeyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
            if (qtBackPressed) {
                return super.onKeyDown(newKeyCode, event);
            } else {
                if (System.currentTimeMillis() - lastTime > 2000) {
                    Toast.makeText(this, "再按一次回到桌面", Toast.LENGTH_SHORT).show();
                    lastTime = System.currentTimeMillis();
                } else {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        int newKeyCode = keyCode;
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            newKeyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
        }
        return super.onKeyMultiple(newKeyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int newKeyCode = keyCode;
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            newKeyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
        }
        return super.onKeyUp(newKeyCode, event);
    }
}

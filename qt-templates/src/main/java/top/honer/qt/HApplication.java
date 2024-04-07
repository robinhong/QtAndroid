package top.honer.qt;

import org.qtproject.qt5.android.QtNative;

import android.util.Log;

public class HApplication extends org.qtproject.qt5.android.bindings.QtApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(QtNative.QtTAG, "Start HApplication");
        HCrash.init(new HCrash.CrashHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.d(QtNative.QtTAG, Log.getStackTraceString(e));
                // e.printStackTrace();
                HUtil.showToast(e.getMessage());
            }
        });
    }
}

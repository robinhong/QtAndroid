package top.honer.qt;

import android.os.Handler;
import android.os.Looper;

public class HCrash {

    private CrashHandler mCrashHandler;

    private static HCrash mInstance;

    private HCrash(){

    }

    private static HCrash getInstance(){
        if(mInstance == null){
            synchronized (HCrash.class){
                if(mInstance == null){
                    mInstance = new HCrash();
                }
            }
        }
        return mInstance;
    }

    public static void init(CrashHandler crashHandler){
        getInstance().setCrashHandler(crashHandler);
    }

    private void setCrashHandler(CrashHandler crashHandler){

        mCrashHandler = crashHandler;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        if (mCrashHandler != null) {//捕获异常处理
                            mCrashHandler.uncaughtException(Looper.getMainLooper().getThread(), e);
                        }
                    }
                }
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if(mCrashHandler!=null){//捕获异常处理
                    mCrashHandler.uncaughtException(t,e);
                }
            }
        });

    }

    public interface CrashHandler{
        void uncaughtException(Thread t,Throwable e);
    }
}

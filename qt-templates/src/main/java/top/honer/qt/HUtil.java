package top.honer.qt;

import org.qtproject.qt5.android.QtNative;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;

public class HUtil {

    private static final String FileProviderString = ".hprovider";

    private HUtil() {
        throw new AssertionError();
    }

    public static int getStatusBarHeight() {
        int resourceId = QtNative.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return QtNative.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavigationBarHeight() {
        int resourceId = QtNative.getContext().getResources().getIdentifier("navigation_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            return QtNative.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /*
     * 回到桌面
     * QAndroidJniObject::callStaticMethod<void>("top/honer/qt/HUtil",
     * "returnHome","()V");
     */
    public static void returnHome() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        QtNative.getContext().startActivity(home);
    }

    // 版本号
    @SuppressWarnings("deprecation")
    public static long getVersionCode() {
        long versionCode = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                versionCode = QtNative.getContext().getPackageManager()
                        .getPackageInfo(QtNative.getContext().getPackageName(), 0).versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                versionCode = QtNative.getContext().getPackageManager()
                        .getPackageInfo(QtNative.getContext().getPackageName(), 0).getLongVersionCode();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }

    // 版本名称
    public static String getVersionName() {
        String versionName = "demo";
        try {
            versionName = QtNative.getContext().getPackageManager().getPackageInfo(
                    QtNative.getContext().getPackageName(),
                    0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    // 安装apk
    public static int installApp(String apkName) {
        if (QtNative.activity() == null)
            return 1;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File apk = new File(QtNative.getContext().getCacheDir(), apkName);
            Log.i(QtNative.QtTAG, apk.toString());
            Uri apkUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(QtNative.getContext(),
                        QtNative.getContext().getPackageName() + FileProviderString, apk);
            } else {
                apkUri = Uri.fromFile(apk);
            }
            Log.i(QtNative.QtTAG, apkUri.toString());
            try {
                ContentResolver resolver = QtNative.getContext().getContentResolver();
                ParcelFileDescriptor fileDescriptor = resolver.openFileDescriptor(apkUri, "r");
                if (fileDescriptor != null) {
                    fileDescriptor.close();
                }
            } catch (FileNotFoundException e) {
                return 2;
            } catch (Exception e) {
                return 3;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            QtNative.getContext().startActivity(intent);
            return 0;
        } catch (android.content.ActivityNotFoundException anfe) {
            return 4;
        }
    }

    // 十六进制转字节数组
    private static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        // 若长度不是偶数加一个前导0
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len++;
        }
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

    // 字节数组转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    // Md5加密
    public static String Md5(String content) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(content.getBytes("utf-8"));
        return bytesToHex(bytes).toUpperCase();
    }

    // key先转成16位Md5值
    private static SecretKeySpec AesKey(String key) throws Exception {
        return new SecretKeySpec(Md5(key).substring(8, 24).toUpperCase().getBytes(), "AES");
    }

    /*
     * 加密
     * Qt调用示例
     * QAndroidJniObject encryptedData =
     * QAndroidJniObject::callStaticObjectMethod("top/honer/qt/HUtil", "AesEncrypt",
     * "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
     * QAndroidJniObject::fromString("hello world").object(),
     * QAndroidJniObject::fromString("anyaeskey").object<jstring>());
     * qDebug() << "加密结果是=" + encryptedData.toString();
     */
    public static String AesEncrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, AesKey(key));
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("utf-8"));
        return bytesToHex(encryptedBytes).toUpperCase();
    }

    // 解密
    public static String AesDecrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, AesKey(key));
        byte[] decryptedBytes = cipher.doFinal(hexToBytes(data));
        data = new String(decryptedBytes, "utf-8");
        return data;
    }

    public static void callTel(String tel) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("tel:" + tel));
        QtNative.getContext().startActivity(intent);
    }

    public static void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QtNative.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showAlertDialog(String title, String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(QtNative.getContext());
                normalDialog.setTitle(title);
                normalDialog.setMessage(msg);
                // normalDialog.setCancelable(false);
                normalDialog.show();
            }
        });
    }
}

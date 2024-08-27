改用gradle8.0编译的安卓模板

# 包名

<font color="red">直接修改AndroidManifest.xml</font>

优先读取manifest标签的 package="top.honer.qt"

其次build.gradle的namespace或applicationId

[相关说明](https://blog.csdn.net/aikongmeng/article/details/130864654)

# android权限

<font color="red">直接修改AndroidManifest.xml</font>


# 图标

android:icon="@drawable/icon"

图片在drawable文件夹<font color="red">替换掉即可</font>

# app名称

android:label
有两个,一个是application,一个是activity

原来是在Qt的pro文件里设置TARGET,androiddeployqt会替换掉AndroidManifest.xml的-- %%INSERT_APP_NAME%% --

为了避免直接修改AndroidManifest.xml的android:label

现在修改为application设置android:label="@string/app_name"
activity设置android:label="@string/activity_name"

<font color="red">在values/strings.xml文件里设置</font>

# android版本号

* 优先使用在build.gradle的defaultConfig处的设置
```
defaultConfig {
    versionCode 2
    versionName "2.0"
}
```
* 如果没有上述配置,也可以在Qt项目的pro文件里里设置
```
ANDROID_VERSION_CODE = 1
ANDROID_VERSION_NAME = 1.0
```
这样避免直接修改AndroidManifest.xml的
android:versionName="-- %%INSERT_VERSION_NAME%% --" android:versionCode="-- %%INSERT_VERSION_CODE%% --"

# SDK编译版本

* 修改了\5.14.2\android\mkspecs\features\android\android_deployment_settings.prf

可以在pro文件里还可以设置
```
ANDROID_MIN_SDK_VERSION = 28
ANDROID_TARGET_SDK_VERSION = 33
```
androiddeployqt会替换掉对应build.gradle的
```
defaultConfig {
    minSdkVersion qtMinSdkVersion
    targetSdkVersion qtTargetSdkVersion
}
```

# 其他说明

applicaition修改了android:name="top.honer.qt.HApplication"

默认activity修改了android:name="top.honer.qt.HActivity"

增加了
```
<!-- 全屏 -->
<meta-data android:name="android.app.show_full" android:value="false"/>
<!-- 常亮 -->
<meta-data android:name="android.app.always_on" android:value="false"/>
<!-- 允许Qt控制返回键 -->
<meta-data android:name="android.app.qt_back_pressed" android:value="false"/>
```

增加广播设置开机启动
```
<receiver android:name="top.honer.qt.HBroadcastReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
    <!-- 开机自动启动 -->
    <meta-data android:name="android.app.autostart" android:value="false"/>
</receiver>
```

增加provider,文件操作需要,比如安装更新
```
<provider android:name="androidx.core.content.FileProvider" android:authorities="${applicationId}.hprovider" android:exported="false" android:grantUriPermissions="true">
    <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/provider_paths"/>
</provider>
```
# QtAndroid

* qt-src 是Qt5.14.2自带的android文件，来自5.14.2\android\src\android\java\src。

* qt-hbase 是改进的封装库，编译成jar放在Qt项目的android目录下libs中即可使用。

* 本项目gradle版本采用5.14.2一致的5.5.1版本。

* 重新编译了5.14.2/android的androiddeployqt适应gradle8.0

* templates是5.14.2\android\src\android目录下的，QtAndroid项目会复制这个目录到项目中编译

* 3rdparty是5.14.2\android\src目录下的，如果用gradle8.0版本，复制这个目录到项目中编译

cd androiddeployqt

@echo off
title mingw qmake prompt
set MINGWDIR=D:\AppServ\Qt\Qt5.14.2\Tools\mingw730_64
set QTDIR=D:\AppServ\Qt\Qt5.14.2\5.14.2\mingw73_32
set PATH=%MINGWDIR%\bin;%QTDIR%\bin;%PATH%

::shadow build
set file = build
if NOT exist %file% ( mkdir build
cd build

qmake.exe ../androiddeployqt.pro -spec win32-g++ "CONFIG+=release"
@rem mingw32-make.exe -f Makefile qmake_all
mingw32-make.exe -j16

cd..
package com.lz.baidumapdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * -----------作者----------日期----------变更内容-----
 * -          刘泽      2018-11-08       创建class
 */
public class App extends Application {

    public static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        SDKInitializer.initialize(this);

    }
}

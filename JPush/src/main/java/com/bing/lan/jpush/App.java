package com.bing.lan.jpush;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 520 on 2017/6/21.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //SpeechUtility.createUtility(getApplicationContext(), "appid=59e566ff");
        //AudioUtils.getInstance().init(getApplicationContext());


    }
}

package com.bing.lan.jpush;

import android.app.Application;

import com.bing.lan.voice.AudioUtils;
import com.iflytek.cloud.SpeechUtility;

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

        //接了科大讯飞 就无法收到推送 是 .so文件的问题
        SpeechUtility.createUtility(getApplicationContext(), "appid=59e566ff");
        AudioUtils.getInstance().init(getApplicationContext());
    }
}

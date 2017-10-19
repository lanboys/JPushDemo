package com.bing.lan.upush;

import android.app.Application;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * Created by 520 on 2017/6/21.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initPush();
    }

    private void initPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token

                Log.e("umeng onSuccess:", " deviceToken:  " + Thread.currentThread().getName() + " -- " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("umeng onFailure:", Thread.currentThread().getName() + " -- " + s + " -- " + s1);
            }
        });
    }
}

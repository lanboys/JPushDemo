package com.bing.lan.upush;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bing.lan.voice.AudioUtils;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by 520 on 2017/6/21.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initPush();

        //接了科大讯飞 就无法收到推送 是 .so文件的问题
        SpeechUtility.createUtility(getApplicationContext(), "appid=59e566ff");
        AudioUtils.getInstance().init(getApplicationContext());

    }

    private void initPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //mPushAgent.setDebugMode(false);

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                Log.e("umeng onSuccess:", " oppo:  " + msg.custom);
            }
        };

        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 0:
                        //Notification.Builder builder = new Notification.Builder(context);
                        //RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                        //        R.layout.notification_view);
                        //myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        //myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        //myNotificationView.setImageViewBitmap(R.id.notification_large_icon,
                        //        getLargeIcon(context, msg));
                        //myNotificationView.setImageViewResource(R.id.notification_small_icon,
                        //        getSmallIconId(context, msg));
                        //builder.setContent(myNotificationView)
                        //        .setSmallIcon(getSmallIconId(context, msg))
                        //        .setTicker(msg.ticker)
                        //        .setAutoCancel(true);

                        //return builder.getNotification();

                        Log.e("getNotification:", "msg: " + msg.getRaw().toString());
                        if (!TextUtils.isEmpty(msg.text)) {
                            AudioUtils.getInstance().speakText(msg.text);
                        }

                        return null;
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

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

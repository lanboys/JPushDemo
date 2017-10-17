package com.bing.lan.jpush.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bing.lan.jpush.MainActivity;
import com.bing.lan.jpush.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 * private void registerCmdReceiver() {
 * // Initialize the intent filter and each action
 * //注册广播接收者,接受外界的播放,暂停等等的广播
 * final IntentFilter filter = new IntentFilter();
 * filter.addAction("cn.jpush.android.intent.REGISTRATION");
 * filter.addAction("cn.jpush.android.intent.MESSAGE_RECEIVED");
 * filter.addAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");
 * filter.addAction("cn.jpush.android.intent.NOTIFICATION_OPENED");
 * filter.addAction("cn.jpush.android.intent.CONNECTION");
 * <p>
 * filter.addCategory("com.bing.lan.jpush");
 * // Attach the broadcast listener
 * registerReceiver(mIntentReceiver, filter);
 * }
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "fmapp";

    @Override
    public void onReceive(Context context, Intent intent) {
        onHandlerReceive(context, intent);
    }

    //只要后台的PushService在就能收到通知，不管是后台还是锁屏，如果收不到就是手机关闭通知(系统通知或app应用通知)
    private void onHandlerReceive(Context context, Intent intent) {
        Log.d(TAG, "[MyReceiver] onReceive -----------------------------------------------------------");

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ",\n extras:\n " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(bundle);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\n1--key:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\n2--key:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\n3--key:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }
            } else {
                sb.append("\n4--key:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        Log.e("fmapp", "processCustomMessage: " + "准备将JPUSH传递的消息发送给Activity");
        //if (!MainActivity.isForeground) {
        Log.e("fmapp isForeground", MainActivity.isForeground + "");
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        //Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
        Intent msgIntent = new Intent(context, MainActivity.class);
        msgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //msgIntent.addCategory("android.intent.category.LAUNCHER");
        msgIntent.putExtra(JPushInterface.EXTRA_MESSAGE, message);

        if (!TextUtils.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (extraJson.length() > 0) {
                    msgIntent.putExtra(JPushInterface.EXTRA_EXTRA, extras);
                }
            } catch (JSONException e) {
                Log.e("fmapp", e.getLocalizedMessage());
            }
        }
        //context.sendBroadcast(msgIntent);
        //context.startActivity(msgIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("短暂弹出的提示内容....")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("message: " + message)
                .setContentText("extras: " + extras);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                msgIntent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();
        manager.notify(0x002, notification);

        //if (!TextUtils.isEmpty(message)) {
        //    AudioUtils.getInstance().speakText(message);
        //}

        //}
    }
}

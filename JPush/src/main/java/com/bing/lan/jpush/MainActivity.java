package com.bing.lan.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    public static boolean isForeground = false;

    public static final String MESSAGE_RECEIVED_ACTION = "com.bing.lan.jpush.MainActivity";
    public static final String KEY_MESSAGE = "key_message";
    public static final String KEY_EXTRAS = "KEY_EXTRAS";

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        Log.e("jpush isForeground", MainActivity.isForeground + "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        Log.e("jpush isForeground", MainActivity.isForeground + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.jpush);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {

            String msg = intent.getStringExtra(JPushInterface.EXTRA_MESSAGE);
            String extra = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);

            String s = null;

            if (msg != null) {
                s = "msg: " + msg;
            }
            if (extra != null) {
                s = "\nextra: " + extra;
            }
            if (s != null) {
                mTextView.setText(s);
            }
        }

        //registerCmdReceiver();
    }

    public void myClick01(View v) {

        // 1.获取提示框的系统服务
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // id 手机上有各种提示 为了控制提示信息 我们会为每个提示添加一个id标识
        // Notification 封装了提示的界面 该界面分为两部分
        // 1.刚开始提示的界面
        //@SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.ic_launcher,
                "短暂弹出的提示内容....", System.currentTimeMillis());
        // 2.用户下拉的界面 同一个应用可能有多个提示 那么他们应该归并到一起 一般显示最后的那条信息
        // 意图 想干什么事
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ONE_SHOT 弹出了很多对话框 点击的时候只能跳进界面1次
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        //notification.setLatestEventInfo(this, "顶部标题", "描述信息", contentIntent);
        // 4.让手机提示该信息
        manager.notify(0x002, notification);
    }

    // 11--->3.0
    public void myClick02(View v) {
        //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Notification.Builder builder = new Notification.Builder(this)
        //        .setSmallIcon(R.drawable.ic_launcher)
        //        .setTicker("短暂弹出的提示内容....")
        //        .setWhen(System.currentTimeMillis())
        //        .setContentTitle("高版本顶部标题")
        //        .setContentText("描述信息.....");
        //
        ////PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
        ////        msgIntent, PendingIntent.FLAG_ONE_SHOT);
        ////
        ////builder.setContentIntent(contentIntent);
        //
        //Notification notification = builder.build();
        //manager.notify(0x002, notification);
        sendNotification();
    }

    public void myClick03(View v) {
        // 1.获取提示框的系统服务
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 2.取消服务
        manager.cancel(0x002);
        // 3.取消所有提示(自己应用内部)
        manager.cancelAll();
    }

    //http://www.jianshu.com/p/e1e20e0ee18c
    private void sendNotification() {
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Build并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setTicker("短暂弹出的提示内容....")
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(R.mipmap.ic_launcher)
                //设置通知标题
                .setContentTitle("最简单的Notification")
                //设置通知内容
                .setContentText("只有小图标、标题、内容")
                //点击通知后自动清除
                .setAutoCancel(true)
                //ledARGB 表示灯光颜色、 ledOnMS 亮持续时间、ledOffMS 暗的时间
                .setLights(0xFF0000, 3000, 3000);

        Notification notification = builder.build();

        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, notification);
    }

    // 判断是否前后台

    //ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    //List<ActivityManager.RunningTaskInfo> listInfo = am.getRunningTasks(2);
    //ActivityManager.RunningTaskInfo taskInfo = listInfo.get(0);//it throws exception in this line
    //
    //TextView title = (TextView) findViewById(R.id.textView_Settings_Title);
    //title.setText(taskInfo.topActivity.getClassName().toString());
}

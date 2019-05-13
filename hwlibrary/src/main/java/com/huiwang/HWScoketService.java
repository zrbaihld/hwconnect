package com.huiwang;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huiwang.net.API;
import com.huiwang.net.OkHttpUtils;
import com.huiwang.net.ParameterMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HWScoketService extends Service {
    private final String TAG = "HWScoketService";
    private static final String BUNDLE_KEY_UID = "bundle_key_uid";
    private static final String BUNDLE_KEY_PHONE = "bundle_key_phone";
    private static final String BUNDLE_KEY_NAME = "bundle_key_name";

    public static final String LOCAL_BROADCAST_ACTION = "local_broadcast_action";

    //    private static final String HOST = "msg.xmfree.net";//服务器地址
    private static final String HOST = "http://apitest.xmfree.net:2120";//服务器地址
    private static final int PORT = 2120;//连接端口号

    private String mPhone;
    private String mName;
    private String mUid;


    public static void serviceStart(Context context, String uid, String phone, String name) {
        Intent intent = new Intent(context, HWScoketService.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_UID, uid);
        bundle.putString(BUNDLE_KEY_PHONE, phone);
        bundle.putString(BUNDLE_KEY_NAME, name);
        intent.putExtras(bundle);
        context.startService(intent);
    }


    private Socket mSocket;


    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LoggerUtils.e(msg.obj.toString());
                    break;
                case 1:
                    LoggerUtils.e(msg.obj.toString());
                    break;
            }

        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUid=intent.getStringExtra(BUNDLE_KEY_UID);
        mPhone=intent.getStringExtra(BUNDLE_KEY_PHONE);
        mName=intent.getStringExtra(BUNDLE_KEY_NAME);
        if (mSocket != null && mSocket.connected()) {
            mSocket.close();
            destory();
        }
        connection();
        return super.onStartCommand(intent, flags, startId);
    }


    public void destory() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("new_msg", onNewMessage);
            mSocket.off("update_online_count", onUpdateOnlineCount);
            mSocket.off("user joined", onUserJoined);
            mSocket.off("user left", onUserLeft);
            mSocket.off("typing", onTyping);
            mSocket.off("stop typing", onStopTyping);
        }
    }

    /**
     * 连接服务器
     */
    public void connection() {
        try {
            mSocket = IO.socket(HOST);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("new_msg", onNewMessage);
            mSocket.on("update_online_count", onUpdateOnlineCount);
            mSocket.on("user joined", onUserJoined);
            mSocket.on("user left", onUserLeft);
            mSocket.on("typing", onTyping);
            mSocket.on("stop typing", onStopTyping);
            mSocket.connect();
            login();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            output("连接服务器失败：" + ex.getMessage());

            Intent intent = new Intent();
            intent.setAction(LOCAL_BROADCAST_ACTION);
            intent.putExtra("action", "failed");
            intent.putExtra("msg", "连接服务器失败：" + ex.getMessage());

            LocalBroadcastManager.getInstance(this).sendBroadcast(
                    intent
            );
        }
    }


    private void output(final String text) {
        Message message = new Message();
        message.obj = text;
        message.what = 0;
        mHandler.sendMessage(message);//通知UI更新=

    }




    /**
     * 登陆
     * 商户号_手机号
     */
    private void login() {

        if (mSocket != null && mSocket.connected())
            mSocket.emit("login", mUid);

        Intent intent = new Intent();
        intent.setAction(LOCAL_BROADCAST_ACTION);
        intent.putExtra("action", "login");

        LocalBroadcastManager.getInstance(this).sendBroadcast(
                intent
        );


    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            log("onConnect",args);
            login();
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            log("onDisconnect",args);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            log("onConnectError",args);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            log("onNewMessage",args);

            if (args.length > 0 && args[0] != null) {

                Intent intent = new Intent();
                intent.setAction(LOCAL_BROADCAST_ACTION);
                intent.putExtra("action", "new_msg");
                intent.putExtra("msg", new Gson().toJson(args[0]));

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                        intent);
            }

        }
    };
    private Emitter.Listener onUpdateOnlineCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            log("onStopTyping",args);
        }
    };


    private void log(String name,Object... args){
        LoggerUtils.e("onUpdateOnlineCount "+name+new Gson().toJson(args));
    }
}

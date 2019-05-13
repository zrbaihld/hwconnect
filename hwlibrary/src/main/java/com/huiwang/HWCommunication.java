package com.huiwang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.google.gson.Gson;
import com.huiwang.entity.ScoketNewMessageEntity;
import com.huiwang.net.API;
import com.huiwang.net.NetCallBackListence;
import com.huiwang.net.RequestUtils;

import java.util.HashMap;
import java.util.Map;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class HWCommunication {

    private static volatile HWCommunication mHWCommunication;

    private static Context mContext;

    private String mPhone;
    private String mName;

    private RequestUtils mRequestUtils;
    private RtcEngineUtils mRtcEngineUtils;

    private ScoketListener mScoketListener;
    private Map<String, ScoketNewMessageEntity> mSocketMessageList=new HashMap<>();

    public static SurfaceView CreateRendererView(Context context) {
        return RtcEngine.CreateRendererView(context);
    }

    public static void destroy() {
        RtcEngine.destroy();
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getStringExtra("action");
                if (TextUtils.equals(action, "login")) {
                    mRequestUtils.loginApi(mPhone, mName, mScoketListener);
                } else if (TextUtils.equals(action, "new_msg")) {
                    try {
                        String jsonStr = intent.getStringExtra("msg");
                        jsonStr = jsonStr.replaceAll("\\\\", "");
                        if (jsonStr.startsWith("\"")) {
                            jsonStr = jsonStr.substring(1);
                        }
                        if (jsonStr.endsWith("\"")) {
                            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
                        }
                        LoggerUtils.e(jsonStr);
                        ScoketNewMessageEntity entity = new Gson().fromJson(jsonStr, ScoketNewMessageEntity.class);
                        mSocketMessageList.put(entity.getContent().getId(), entity);
                        if (mScoketListener != null){
                            if (TextUtils.equals(entity.getType(),"lineapply")){
                                mScoketListener.onConnectApply(entity.getApplyType(),
                                        entity.getContent().getName(),
                                        entity.getContent().getPhone(),
                                        entity.getContent().getId());
                            }else if (TextUtils.equals(entity.getType(),"lineagree")){
                               //sdk中处理
                            }else if (TextUtils.equals(entity.getType(),"linerefuse")){
                                mScoketListener.onRefuseConnectApply(entity.getApplyType(),
                                        entity.getContent().getName(),
                                        entity.getContent().getPhone(),
                                        entity.getContent().getId());
                            }else if (TextUtils.equals(entity.getType(),"friendapply")){
                                mScoketListener.onApplyFriend(
                                        entity.getContent().getName(),
                                        entity.getContent().getPhone(),
                                        entity.getContent().getId());
                            }else if (TextUtils.equals(entity.getType(),"friendagree")){
                                mScoketListener.onApplyFriendAgree(
                                        entity.getContent().getName(),
                                        entity.getContent().getPhone(),
                                        entity.getContent().getId());
                            }else if (TextUtils.equals(entity.getType(),"linerefuse")){
                                mScoketListener.onApplyFriendRefuce(
                                        entity.getContent().getName(),
                                        entity.getContent().getPhone(),
                                        entity.getContent().getId());
                            }else if (TextUtils.equals(entity.getType(),"messages")){

                            }
                        }
                    } catch (Exception e) {
                        LoggerUtils.e(e.getMessage());
                    }
                } else if (TextUtils.equals(action, "failed")) {
                    if (mScoketListener != null) {
                        String msg = intent.getStringExtra("msg");
                        mScoketListener.onLoginFailed(msg);
                    }

                }
            }
        }
    };


    private HWCommunication() {
    }


    private HWCommunication(Context context, String key, String orgno) {
        API.key = key;
        API.orgno = orgno;
        mContext = context.getApplicationContext();
        mRequestUtils = new RequestUtils();
        mRtcEngineUtils = new RtcEngineUtils(context);
        mRequestUtils.setNetCallBackListence(mRtcEngineUtils.mCallback);


        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(HWScoketService.LOCAL_BROADCAST_ACTION);
        localBroadcastManager.registerReceiver(mBroadcastReceiver, filter);
    }


    public static HWCommunication getInstance() {
        if (mHWCommunication == null) {
            new RuntimeException("please init HWCommunication first");
        }
        return mHWCommunication;

    }


    public static void init(Context context, String key, String orgno) {
        mHWCommunication = new HWCommunication(context, key, orgno);
    }


    /**
     * 调试模式
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        LoggerUtils.debug = debug;
    }

    /**
     * 登陆入口
     */
    public void login(String phone, String name, ScoketListener mScoketListener) {
        API.uid = API.orgno + "_" + phone;
        mPhone = phone;
        mName = name;
        this.mScoketListener = mScoketListener;
        if (mContext == null)
            throw new RuntimeException("Context is null");
        HWScoketService.serviceStart(mContext, API.uid, phone, name);

    }

    public void refuceConnect(String id, NetCallBackListence mNetCallBackListence) {
        ScoketNewMessageEntity entity = mSocketMessageList.get(id);
        if (entity == null)
            return;
        mRequestUtils.refuseApplyVoice(entity.getContent().getRoomno(), entity.getContent().getPhone(), mNetCallBackListence, entity.getApplyType());
        mSocketMessageList.remove(id);
    }


    /**
     * 查询用户
     *
     * @param phone
     * @param name
     */
    public void queryUser(String phone, String name, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.queryUser(phone, name, mNetCallBackListence);
    }

    /**
     * 好友申请
     */
    public void applyUser(String tophone, String memo, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.applyUser(mPhone, tophone, memo, mNetCallBackListence);
    }

    /**
     * 是否同意好友申请
     *
     * @param tophone
     * @param isAgree
     */
    public void handlerUserApply(String tophone, boolean isAgree, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.handlerUserApply(mPhone, tophone, isAgree, mNetCallBackListence);
    }

    /**
     * 发送文本
     *
     * @param tophone
     * @param message
     */
    public void sendTextMessage(String tophone, String message, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.sendTextMessage(mPhone, tophone, message, mNetCallBackListence);
    }

    /**
     * 发送语音
     *
     * @param tophone
     * @param message
     */
    public void sendVoiceMessage(String tophone, String message, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.sendVoiceMessage(mPhone, tophone, message, mNetCallBackListence);

    }

    /**
     * 发起语音
     *
     * @param tophone
     * @param type
     */
    public void appalyVoice(String tophone, final ApplyType type, RtcStateHandlerListener listener) {
        mRtcEngineUtils.setRtcStateHandlerListener(listener);
        mRequestUtils.appalyVoice(mPhone, tophone, listener, type);
    }

    public void appalyVoice(String tophone, final ApplyType type, VideoEncoderConfiguration configuration, RtcStateHandlerListener listener) {
        mRtcEngineUtils.setRtcStateHandlerListener(listener);

        mRequestUtils.appalyVoice(mPhone, tophone, listener, type);
    }

    /**
     * 同意语音
     *
     * @param id
     */
    public void agreeApplyVoice(String id, ApplyType type, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.agreeApplyVoice(id, mNetCallBackListence, type);
    }

    /**
     * 拒绝语音
     *
     * @param id
     * @param userphone
     */
    public void refuseApplyVoice(String id, String userphone, ApplyType type, NetCallBackListence mNetCallBackListence) {
        mRequestUtils.refuseApplyVoice(id, userphone, mNetCallBackListence, type);
    }


    /**
     * 获取好友列表
     */
    public void getfriendlist(NetCallBackListence mNetCallBackListence) {
        mRequestUtils.getfriendlist(mPhone, mNetCallBackListence);
    }

    /**
     * 获取好友申请列表
     */
    public void getfriendapplylist(NetCallBackListence mNetCallBackListence) {
        mRequestUtils.getfriendapplylist(mPhone, mNetCallBackListence);
    }


//    public RtcEngine getRtcEngine() {
//        return mRtcEngineUtils.getRtcEngine();
//    }

    /**
     * 加入频道
     *
     * @param scoketNewMessageID
     * @param mRtcStateHandlerListener
     */
    public void joinChannel(String scoketNewMessageID, RtcStateHandlerListener mRtcStateHandlerListener) {
        ScoketNewMessageEntity entity = mSocketMessageList.get(scoketNewMessageID);
        if (entity == null)
            return;

        if (TextUtils.equals(entity.getContent().getLinetype(), "0")) {
            mRtcEngineUtils.setRtcStateHandlerListener(mRtcStateHandlerListener);
            mRtcEngineUtils.joinChannel(ApplyType.APPLY_VOICE,
                    entity.getContent().getKey(),
                    entity.getContent().getRoomno());

        } else if (TextUtils.equals(entity.getContent().getLinetype(), "1")) {
            mRtcEngineUtils.setRtcStateHandlerListener(mRtcStateHandlerListener);
            mRtcEngineUtils.joinChannel(ApplyType.APPLY_VIDEO, entity.getContent().getKey(), entity.getContent().getRoomno());
        }
        mSocketMessageList.remove(scoketNewMessageID);
    }


    public void muteLocalVideoStream(boolean muted) {
        mRtcEngineUtils.muteLocalVideoStream(muted);
    }

    public void muteLocalAudioStream(boolean muted) {
        mRtcEngineUtils.muteLocalAudioStream(muted);
    }

    public void setEnableSpeakerphone(boolean muted) {
        mRtcEngineUtils.setEnableSpeakerphone(muted);
    }

    public void switchCamera() {
        mRtcEngineUtils.switchCamera();
    }

    public void setupRemoteVideo(VideoCanvas remote) {
        mRtcEngineUtils.setupRemoteVideo(remote);
    }

    /**
     * 离开频道
     */
    public void leaveChannel() {
        if (mRtcEngineUtils.getRtcEngine() != null)
            mRtcEngineUtils.getRtcEngine().leaveChannel();
    }

}

package com.huiwang.net;

import android.util.Log;

import com.google.gson.Gson;
import com.huiwang.ApplyType;
import com.huiwang.EncryptionUtil;
import com.huiwang.LoggerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RequestUtils {

    private OkHttpUtils mOkHttpUtils;
    //    private List<NetCallBackListence> mCallBackList = new ArrayList<>();
    private NetCallBackListence mNetCallBackListence;

    private Gson mGson = new Gson();

    public RequestUtils() {
        mOkHttpUtils = new OkHttpUtils();
    }


    /**
     * 查询用户
     *
     * @param phone
     * @param name
     */
    public void queryUser(String phone, String name, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("phone", phone);
        request.put("name", name);
        baseHttp(API.USER_QUERY_URL, request, mNetCallBackListence);
    }

    /**
     * 好友申请
     */
    public void applyUser(String fromphone, String tophone, String memo, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("fromphone", fromphone);
        request.put("tophone", tophone);
        request.put("memo", memo);
        baseHttp(API.APPLY_USER_URL, request, mNetCallBackListence);
    }

    /**
     * 是否同意好友申请
     *
     * @param fromphone
     * @param tophone
     * @param isAgree
     */
    public void handlerUserApply(String fromphone, String tophone, boolean isAgree, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("fromphone", fromphone);
        request.put("tophone", tophone);
        baseHttp(isAgree ? API.APPLY_AGREE_URL : API.APPLY_REFUSE_URL, request, mNetCallBackListence);
    }

    /**
     * 发送文本
     *
     * @param fromphone
     * @param tophone
     * @param message
     */
    public void sendTextMessage(String fromphone, String tophone, String message, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("fromphone", fromphone);
        request.put("tophone", tophone);
        request.put("message", message);
        baseHttp(API.SEND_TEXT_URL, request, mNetCallBackListence);
    }

    /**
     * 发送语音
     *
     * @param fromphone
     * @param tophone
     * @param message
     */
    public void sendVoiceMessage(String fromphone, String tophone, String message, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("fromphone", fromphone);
        request.put("tophone", tophone);
        request.put("message", message);
        baseHttp(API.SEND_VOID_URL, request, mNetCallBackListence);
    }

    /**
     * 发起语音
     *
     * @param fromphone
     * @param tophone
     * @param type
     */
    public void appalyVoice(String fromphone, String tophone, NetCallBackListence mNetCallBackListence, final ApplyType type) {
        final Map<String, String> request = new HashMap<>();
        request.put("fromphone", fromphone);
        request.put("tophone", tophone);
        request.put("type", type == ApplyType.APPLY_VOICE ? "0" : "1");
        baseHttp(API.APPLY_VOID_URL, request, mNetCallBackListence, type);
    }


    /**
     * 同意语音
     *
     * @param id
     */
    public void agreeApplyVoice(String id, NetCallBackListence mNetCallBackListence, ApplyType type) {
        Map<String, String> request = new HashMap<>();
        request.put("id", id);
        baseHttp(API.AGREE_VOID_URL, request, mNetCallBackListence, type);
    }

    /**
     * 拒绝语音
     *
     * @param id
     * @param userphone
     */
    public void refuseApplyVoice(String id, String userphone, NetCallBackListence mNetCallBackListence, ApplyType type) {
        Map<String, String> request = new HashMap<>();
        request.put("roomno", id);
        request.put("userphone", userphone);
        baseHttp(API.REFUSE_VOID_URL, request, mNetCallBackListence, type);
    }

    /**
     * 登陆
     */
    public void loginApi(String mPhone, String mName, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("phone", mPhone);
        request.put("name", mName);
        baseHttp(API.LOGIN_URL, request, mNetCallBackListence);
    }

    /**
     * 获取好友列表
     *
     * @param userphone
     */
    public void getfriendlist(String userphone, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("userphone", userphone);
        baseHttp(API.GET_FRIEND_LIST_URL, request, mNetCallBackListence);
    }

    /**
     * 获取好友申请列表
     *
     * @param userphone
     */
    public void getfriendapplylist(String userphone, NetCallBackListence mNetCallBackListence) {
        Map<String, String> request = new HashMap<>();
        request.put("userphone", userphone);
        baseHttp(API.GET_FRIENDAPPLY_LIST_URL, request, mNetCallBackListence);
    }


    private void baseHttp(String url, Map<String, String> request, final NetCallBackListence mNetCallBackListence, final ApplyType type) {
        LoggerUtils.e(mGson.toJson(request));
        mOkHttpUtils.asyncPost(url,
                EncryptionUtil.encryptionRequest(ParameterMap.getMap(request), API.key),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (mNetCallBackListence != null)
                            mNetCallBackListence.onFailure(call, e);
                        if (RequestUtils.this.mNetCallBackListence instanceof ApiRtcEngineListence && type != ApplyType.APPLY_NULL) {
                            ((ApiRtcEngineListence) RequestUtils.this.mNetCallBackListence).onFailure(call, e);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String data = response.body().string();
                        LoggerUtils.e(data);
                        if (mNetCallBackListence != null)
                            mNetCallBackListence.onResponse(call, data);
                        if (RequestUtils.this.mNetCallBackListence instanceof ApiRtcEngineListence && type != ApplyType.APPLY_NULL) {
                            ((ApiRtcEngineListence) RequestUtils.this.mNetCallBackListence).onResponse(call, data, type);
                        }


                    }
                });
    }


    private void baseHttp(String url, Map<String, String> request, NetCallBackListence mNetCallBackListence) {
        baseHttp(url, request, mNetCallBackListence, ApplyType.APPLY_NULL);
    }

    public void setNetCallBackListence(NetCallBackListence mNetCallBackListence) {
        this.mNetCallBackListence = mNetCallBackListence;

    }
}

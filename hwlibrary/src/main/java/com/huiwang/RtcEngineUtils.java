package com.huiwang;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huiwang.entity.BaseEntity;
import com.huiwang.net.ApiRtcEngineListence;

import java.io.IOException;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import okhttp3.Call;

public class RtcEngineUtils {
    private RtcEngine mRtcEngine;
    private Context mContext;
    private Gson mGson;
    private RtcStateHandlerListener mRtcStateHandlerListener;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.obj instanceof DataEntity) {
                    DataEntity data = (DataEntity) msg.obj;
                    if (msg.what == 0) {
                        initVideoRoom(data.key, data.roomno);
                    } else if (msg.what == 1) {
                        initVoiceRoom(data.key, data.roomno);
                    }
                }
            } catch (Exception e) {
                if (mRtcStateHandlerListener != null)
                    mRtcStateHandlerListener.startFailed(-1, e.getMessage());
            }

        }
    };

    public ApiRtcEngineListence mCallback = new ApiRtcEngineListence() {
        @Override
        public void onResponse(Call call, String response, ApplyType type) throws IOException {

            BaseEntity entity = mGson.fromJson(response, BaseEntity.class);
            String jsonStr = mGson.toJson(entity.getData());

            LoggerUtils.e(jsonStr);
            DataEntity data = null;
            try {
                data = mGson.fromJson(jsonStr, DataEntity.class);
            } catch (Exception e) {

            }


            if (TextUtils.equals(entity.getStatus(), "900")) {
                Message message = new Message();
                message.obj = data;
                if (type == ApplyType.APPLY_VIDEO) {
                    message.what = 0;

                } else if (type == ApplyType.APPLY_VOICE) {
                    message.what = 1;

                }
                mHandler.sendMessage(message);
            }


        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, String response) throws IOException {

        }
    };

    public RtcEngineUtils(Context mContext) {
        this.mContext = mContext;
        mGson = new GsonBuilder().disableHtmlEscaping().create();

    }


    /**
     * 加入视频
     *
     * @param appid
     * @param channelId
     */
    private void initVideoRoom(String appid, String channelId) {
        if (mRtcStateHandlerListener != null)
            initVideoRoom(appid, channelId, mRtcStateHandlerListener.getVideoConfiguration());
        else
            initVideoRoom(appid, channelId, new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }


    private void initVideoRoom(String appid, String channelId, VideoEncoderConfiguration configuration) {

        initRtcEngine(appid, mRtcStateHandlerListener);
        if (mRtcEngine == null)
            return;
        setupVideoProfile(configuration);
        setupLocalVideo();
        if (mRtcStateHandlerListener != null)
            mRtcStateHandlerListener.startSuccess();
        joinChannel(channelId);
    }

    private void initVoiceRoom(String appid, String channelId) {

        initRtcEngine(appid, mRtcStateHandlerListener);
        if (mRtcEngine == null)
            return;
        joinChannel(channelId);
        if (mRtcStateHandlerListener != null)
            mRtcStateHandlerListener.startSuccess();
    }

    private void joinChannel(String channelId) {
        mRtcEngine.joinChannel(null, channelId, "Extra Optional Data", 0);


    }

    private void initRtcEngine(String appid, IRtcEngineEventHandler iRtcEngineEventHandler) {
        try {
            mRtcEngine = RtcEngine.create(mContext, appid, iRtcEngineEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频初始化
     */
    private void setupVideoProfile(VideoEncoderConfiguration configuration) {
        mRtcEngine.enableVideo();
//      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false); // Earlier than 2.3.0
        mRtcEngine.setVideoEncoderConfiguration(configuration);
    }


    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }


    /**
     * 加载本地视频
     */
    private void setupLocalVideo() {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(mContext);
        surfaceView.setZOrderMediaOverlay(true);
        if (mRtcStateHandlerListener != null)
            mRtcStateHandlerListener.initVideoSuccess(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    public void setRtcStateHandlerListener(RtcStateHandlerListener mRtcStateHandlerListener) {
        this.mRtcStateHandlerListener = mRtcStateHandlerListener;
    }

    public static class DataEntity {
        public String roomno;
        public String key;
    }

    public void joinChannel(ApplyType type, String key, String roomNo) {
        if (type == ApplyType.APPLY_VIDEO) {
            initVideoRoom(key, roomNo);
        } else if (type == ApplyType.APPLY_VOICE) {
            initVoiceRoom(key, roomNo);
        }

    }


    public void muteLocalVideoStream(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.muteLocalVideoStream(muted);
    }

    public void muteLocalAudioStream(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.muteLocalAudioStream(muted);
    }

    public void setEnableSpeakerphone(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.setEnableSpeakerphone(muted);
    }

    public void switchCamera() {
        if (mRtcEngine != null)
            mRtcEngine.switchCamera();
    }

    public void setupRemoteVideo(VideoCanvas remote) {
        if (mRtcEngine != null)
            mRtcEngine.setupRemoteVideo(remote);
    }

    public void leaveChannel() {
        if (mRtcEngine != null)
            mRtcEngine.leaveChannel();
    }


}

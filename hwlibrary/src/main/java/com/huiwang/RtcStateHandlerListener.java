package com.huiwang;

import android.view.SurfaceView;

import com.huiwang.net.NetCallBackListence;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public abstract class RtcStateHandlerListener extends IRtcEngineEventHandler implements NetCallBackListence {


    public abstract void startSuccess();

    public abstract void startFailed(int status,String info);

    public void initVideoSuccess(SurfaceView surfaceView){

    };

    public VideoEncoderConfiguration getVideoConfiguration(){
        return new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);
    }

}

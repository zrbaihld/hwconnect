package io.agora.tutorials1v1acall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.widget.TextView;

import com.huiwang.ApplyType;
import com.huiwang.HWCommunication;
import com.huiwang.RtcStateHandlerListener;
import com.huiwang.net.NetCallBackListence;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class APITestResultActivity extends AppCompatActivity {

    private String mMessage;
    private TextView textView;

    private NetCallBackListence mNetCallBackListence = new NetCallBackListence() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, String response) throws IOException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(response);
                }
            });

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = new TextView(this);
        setContentView(textView);
        senAPI();
    }

    private void senAPI() {
        String name = getIntent().getStringExtra("name");
        String mFromPhone = getIntent().getStringExtra("mFromPhone");
        String mToPhone = getIntent().getStringExtra("mToPhone");


        if (TextUtils.equals("用户查询", name)) {
            HWCommunication.getInstance().queryUser(mToPhone, "张睿彬",mNetCallBackListence);
        } else if (TextUtils.equals("好友申请", name)) {
            HWCommunication.getInstance().applyUser(mToPhone, "sss",mNetCallBackListence);
        } else if (TextUtils.equals("好友申请同意", name)) {
            HWCommunication.getInstance().handlerUserApply(mToPhone, true,mNetCallBackListence);
        } else if (TextUtils.equals("好友申请拒绝", name)) {
            HWCommunication.getInstance().handlerUserApply( mToPhone, false,mNetCallBackListence);
        } else if (TextUtils.equals("文本发送接口", name)) {
            HWCommunication.getInstance().sendTextMessage( mToPhone, mMessage,mNetCallBackListence);
        } else if (TextUtils.equals("语音发送接口", name)) {
            HWCommunication.getInstance().sendVoiceMessage( mToPhone, mMessage,mNetCallBackListence);
        } else if (TextUtils.equals("发起语音电话", name)) {
//
//
//            HWCommunication.getInstance().appalyVoice(mFromPhone, mToPhone, ApplyType.APPLY_VOICE, new RtcStateHandlerListener() {
//                @Override
//                public void startSuccess() {
//                    startActivity(new Intent(TestApiActivity.this,VoiceChatViewActivity.class));
//                }
//            });
//
//

        } else if (TextUtils.equals("发起视频", name)) {
//            HWCommunication.getInstance().appalyVoice(mFromPhone, mToPhone, ApplyType.APPLY_VIDEO, new RtcStateHandlerListener() {
//                @Override
//                public void startSuccess() {
//
//                }
//
//                @Override
//                public void initVideoSuccess(SurfaceView surfaceView) {
//
//                }
//            });
        } else if (TextUtils.equals("接通语音", name)) {
//            HWCommunication.getInstance().agreeApplyVoice(roomId, ApplyType.APPLY_VOICE);
        } else if (TextUtils.equals("接通视频", name)) {
//            HWCommunication.getInstance().agreeApplyVoice(roomId, ApplyType.APPLY_VIDEO);
        } else if (TextUtils.equals("挂断视频", name)) {
//            HWCommunication.getInstance().refuseApplyVoice(roomId, mFromPhone, ApplyType.APPLY_VIDEO);
        } else if (TextUtils.equals("挂断视频", name)) {
//            HWCommunication.getInstance().refuseApplyVoice(roomId, mFromPhone, ApplyType.APPLY_VIDEO);
        } else if (TextUtils.equals("获取好友列表", name)) {
            HWCommunication.getInstance().getfriendlist(mNetCallBackListence);
        } else if (TextUtils.equals("获取好友申请列表", name)) {
            HWCommunication.getInstance().getfriendapplylist(mNetCallBackListence);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

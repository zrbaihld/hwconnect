package io.agora.tutorials1v1acall;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwang.ApplyType;
import com.huiwang.HWCommunication;
import com.huiwang.RtcStateHandlerListener;
import com.huiwang.ScoketListener;
import com.huiwang.entity.ScoketNewMessageEntity;
import com.huiwang.net.NetCallBackListence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class TestApiActivity extends AppCompatActivity {
    private List<String> mNameList = new ArrayList<>();
    private String mFromPhone = "15659810042";
    private String mFromName = "张三";
    private String mToPhone = "15659810041";
    private String mMessage = "msg";
    private String roomId = "msg";

    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(this);
        editText=new EditText(this);
        editText.setHint("请输入对方手机号");
        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        linearLayout.addView(recyclerView);
        setContentView(linearLayout);
        String f = getIntent().getStringExtra("frome_phone");
        String frome_name = getIntent().getStringExtra("frome_name");
        String t = getIntent().getStringExtra("toPhone");
        if (!TextUtils.isEmpty(f)) {
            mFromPhone = f;
        }
        if (!TextUtils.isEmpty(t)) {
            mToPhone = t;
        }
        HWCommunication.getInstance().setDebug(true);
        HWCommunication.getInstance().login(mFromPhone, frome_name, new ScoketListener() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, String response) throws IOException {

            }

            @Override
            public void onConnectApply(ApplyType applyType, String name, String phone, String applyId) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TestApiActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage(String.format("%s找你%s，是否接通？", name,
                                applyType == ApplyType.APPLY_VOICE ? "通话" :
                                        applyType == ApplyType.APPLY_VIDEO ? " 视频" : ""));
                        builder.setPositiveButton("接通", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                if (applyType == ApplyType.APPLY_VOICE) {
                                    intent = new Intent(TestApiActivity.this, VoiceChatViewActivity.class);
                                    intent.putExtra("applyId", applyId);
                                    startActivity(intent);
                                } else if (applyType == ApplyType.APPLY_VIDEO) {
                                    intent = new Intent(TestApiActivity.this, VideoChatViewActivity.class);
                                    intent.putExtra("applyId", applyId);
                                    startActivity(intent);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HWCommunication.getInstance().refuceConnect(applyId, mNetCallBackListence);
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
            }

            @Override
            public void onRefuseConnectApply(ApplyType applyType, String name, String phone, String applyId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestApiActivity.this, "对方拒绝了您的通话", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onLoginFailed(String msg) {

            }
        });


        mNameList.add("用户查询");
        mNameList.add("好友申请");
        mNameList.add("好友申请同意");
        mNameList.add("好友申请拒绝");
        mNameList.add("文本发送接口");
        mNameList.add("语音发送接口");
        mNameList.add("发起语音电话");
        mNameList.add("发起视频");
        mNameList.add("接通语音");
        mNameList.add("接通视频");
        mNameList.add("挂断视频");
        mNameList.add("挂断视频");
        mNameList.add("获取好友列表");
        mNameList.add("获取好友申请列表");

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter());

    }

    public class MyAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(TestApiActivity.this);
//            tv.setTextColor(0xff000000);
            tv.setHeight(150);
            tv.setGravity(Gravity.CENTER);
            return new RecyclerView.ViewHolder(tv) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            String name = mNameList.get(position);
            TextView tvName = (TextView) holder.itemView;
            tvName.setText(name);
            tvName.setOnClickListener(onClickListener);
        }

        @Override
        public int getItemCount() {
            return mNameList.size();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tvName = (TextView) v;
            String mToPhone=editText.getText().toString();

            String name = tvName.getText().toString();
            if (TextUtils.equals("发起语音电话", name)) {
                Intent intent = new Intent(TestApiActivity.this, VoiceChatViewActivity.class);
                intent.putExtra("mFromPhone", mFromPhone);
                intent.putExtra("mToPhone", mToPhone);

                startActivity(intent);


            } else if (TextUtils.equals("发起视频", name)) {
                Intent intent = new Intent(TestApiActivity.this, VideoChatViewActivity.class);
                intent.putExtra("mFromPhone", mFromPhone);
                intent.putExtra("mToPhone", mToPhone);

                startActivity(intent);


            } else if (TextUtils.equals("接通语音", name)) {
                HWCommunication.getInstance().agreeApplyVoice(roomId, ApplyType.APPLY_VOICE, mNetCallBackListence);
            } else if (TextUtils.equals("接通视频", name)) {
                HWCommunication.getInstance().agreeApplyVoice(roomId, ApplyType.APPLY_VIDEO, mNetCallBackListence);
            } else if (TextUtils.equals("挂断视频", name)) {
                HWCommunication.getInstance().refuseApplyVoice(roomId, mFromPhone, ApplyType.APPLY_VIDEO, mNetCallBackListence);
            } else if (TextUtils.equals("挂断视频", name)) {
                HWCommunication.getInstance().refuseApplyVoice(roomId, mFromPhone, ApplyType.APPLY_VIDEO, mNetCallBackListence);
            } else {
                toTestActivity(name,mToPhone);
            }
        }
    };

    private void toTestActivity(String name,String toPhone) {
        Intent intent = new Intent(this, APITestResultActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("mFromPhone", mFromPhone);
        intent.putExtra("mToPhone", toPhone);

        startActivity(intent);

    }


    private NetCallBackListence mNetCallBackListence = new NetCallBackListence() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, String response) throws IOException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        }
    };

}

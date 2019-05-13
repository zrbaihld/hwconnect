package com.huiwang.net;

import com.huiwang.ApplyType;

import java.io.IOException;

import okhttp3.Call;

public interface ApiRtcEngineListence extends NetCallBackListence {
    void onResponse(Call call, String response, ApplyType type) throws IOException;

}

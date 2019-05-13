package com.huiwang;

import com.huiwang.net.NetCallBackListence;

public abstract class ScoketListener implements NetCallBackListence {
    public abstract void onConnectApply(ApplyType applyType, String name, String phone, String applyId);

    public abstract void onRefuseConnectApply(ApplyType applyType, String name, String phone, String applyId);

    public void onApplyFriend(String name, String phone, String applyId) {

    }


    public void onApplyFriendAgree(String name, String phone, String applyId) {

    }


    public void onApplyFriendRefuce(String name, String phone, String applyId) {

    }


    public void onLoginFailed(String msg) {

    }


}

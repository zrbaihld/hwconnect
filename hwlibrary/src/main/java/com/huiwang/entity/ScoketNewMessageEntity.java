package com.huiwang.entity;

import android.text.TextUtils;

import com.huiwang.ApplyType;

import java.io.Serializable;

public class ScoketNewMessageEntity implements Serializable {
    private String type;
    private String createtime;
    private ScoketNewMessageContentEntity content = new ScoketNewMessageContentEntity();

    public String getType() {
        return type;
    }

    public ApplyType getApplyType() {
        if (TextUtils.equals(content.getLinetype(), "0")) {
            return ApplyType.APPLY_VOICE;
        } else if (TextUtils.equals(content.getLinetype(), "1")) {
            return ApplyType.APPLY_VIDEO;
        }
        return ApplyType.APPLY_NULL;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public ScoketNewMessageContentEntity getContent() {
        return content;
    }

    public void setContent(ScoketNewMessageContentEntity content) {
        this.content = content;
    }

    public static class ScoketNewMessageContentEntity implements Serializable {
        private String id;
        private String phone;
        private String name;
        private String linetype;
        private String roomno;
        private String key;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLinetype() {
            return linetype;
        }

        public void setLinetype(String linetype) {
            this.linetype = linetype;
        }

        public String getRoomno() {
            return roomno;
        }

        public void setRoomno(String roomno) {
            this.roomno = roomno;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}


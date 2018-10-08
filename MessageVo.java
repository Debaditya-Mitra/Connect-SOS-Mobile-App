package com.chatdemo;

import android.graphics.Bitmap;

public class MessageVo {
    String message, userId,map;
    Bitmap Picture;

    public Bitmap getPicture() {
        return Picture;
    }

    public void setPicture(Bitmap picture) {
        Picture = picture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void build() {
    }

    public static class Builder extends MessageVo {
    }
}

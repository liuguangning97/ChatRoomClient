package com.android.chatroomclient.db;

import org.litepal.crud.LitePalSupport;

public class AddFriend extends LitePalSupport {

    public static final int TYPE_WAIT = 0;
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_FAILED = 2;
    public static final int TYPE_RESPONSE = 3;

    private int order;
    private String userName;
    private String friendName;
    private String friendAccount;
    private String portraitUri;
    private int type;
    private boolean isWatch;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isWatch() {
        return isWatch;
    }

    public void setWatch(boolean watch) {
        isWatch = watch;
    }

    @Override
    public String toString() {
        return "AddFriend{" +
                "order=" + order +
                ", userName='" + userName + '\'' +
                ", friendName='" + friendName + '\'' +
                ", friendAccount='" + friendAccount + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                ", type=" + type +
                ", isWatch=" + isWatch +
                '}';
    }
}

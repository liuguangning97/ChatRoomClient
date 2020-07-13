package com.android.chatroomclient.db;

import org.litepal.crud.LitePalSupport;

public class Talk extends LitePalSupport {

    private String userName;
    private String userAccount;
    private String friendName;
    private String friendAccount;
    private String text;
    private boolean isSend;
    private boolean isWatch;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public boolean isWatch() {
        return isWatch;
    }

    public void setWatch(boolean watch) {
        isWatch = watch;
    }

    @Override
    public String toString() {
        return "Talk{" +
                "userName='" + userName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", friendName='" + friendName + '\'' +
                ", friendAccount='" + friendAccount + '\'' +
                ", text='" + text + '\'' +
                ", isSend=" + isSend +
                ", isWatch=" + isWatch +
                '}';
    }
}

package com.android.chatroomclient.db;

import org.litepal.crud.LitePalSupport;

public class Friend extends LitePalSupport {

    private int order;
    private String userName;
    private String friendName;
    private String friendAccount;
    private String portraitUri;

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

    @Override
    public String toString() {
        return "Friend{" +
                "order=" + order +
                ", userName='" + userName + '\'' +
                ", friendName='" + friendName + '\'' +
                ", friendAccount='" + friendAccount + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                '}';
    }

}

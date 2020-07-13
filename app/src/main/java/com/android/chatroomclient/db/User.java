package com.android.chatroomclient.db;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class User extends LitePalSupport {

    private int order;
    private String account;
    private String password;
    private String userName;
    private String portraitUri;
    private boolean isRemember;


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitPath) {
        this.portraitUri = portraitPath;
    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }

    @Override
    public String toString() {
        return "User{" +
                "order=" + order +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                ", isRemember=" + isRemember +
                '}';
    }

//    private List<Friend> friendList = new ArrayList<>();

}

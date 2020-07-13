package com.android.chatroomclient.Util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.chatroomclient.Activity.AddFriendActivity;
import com.android.chatroomclient.Activity.BaseActivity;
import com.android.chatroomclient.Activity.ChatActivity;
import com.android.chatroomclient.Activity.LoginActivity;
import com.android.chatroomclient.Activity.MainActivity;
import com.android.chatroomclient.Fragment.ChangeFragment;
import com.android.chatroomclient.Fragment.ChatFragment;
import com.android.chatroomclient.Fragment.CreateFragment;
import com.android.chatroomclient.Fragment.FriendFragment;
import com.android.chatroomclient.Fragment.LoginFragment;
import com.android.chatroomclient.Fragment.UserFragment;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.AddFriend;
import com.android.chatroomclient.db.Friend;
import com.android.chatroomclient.db.Talk;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static final int TYPE_DISCONNECT = 0;
    public static final int TYPE_CONNECT = 1;
    public static final int TYPE_CREATE = 2;
    public static final int TYPE_CHANGE = 3;
    public static final int TYPE_LOGIN = 4;
    public static final int TYPE_MESSAGE = 5;
    public static final int TYPE_FRIEND = 6;
    public static final int TYPE_REQUEST = 7;
    public static final int TYPE_RESPONSE = 8;
    public static final int TYPE_CHAT = 9;
    public static final int TYPE_OFF_LINE = 10;
    public static final int TYPE_DELETE_FRIEND = 11;
    public static final int TYPE_CHAT_ERROR = 12;
    public static final int TYPE_GET_FINISH = 13;
    public static final int TYPE_NO_FILE = 14;
    public static final int TYPE_CHANGE_PORTRAIT = 15;

    //设置正则表达式规范账号与密码的格式
    public static final String accountRegex = "\\d{5,15}";           //5~15位数字字符串
    public static final String passwordRegex = "[a~zA~Z]\\w[^_]{4,}";    //以字母位开头的且只有字母和数字的最低6位字符串

    public static CreateFragment createFragment;
    public static ChangeFragment changeFragment;
    public static LoginFragment loginFragment;
    public static ChatFragment chatFragment;
    public static FriendFragment friendFragment;
    public static UserFragment userFragment;
    public static Fragment lastFragment;

    public static BaseActivity runningActivity;
    public static List<BaseActivity> activityList = new ArrayList<>();
    public static List<String> messageList = new ArrayList<>();

    public static String account;
    public static String userName;
    public static String ipAddress;
    public static AlertDialog progressBar;

    public static String path;

    public static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String[] str;
            String filePath = null;
            String idKey = null;
            String messageText = null;
            String title = null;
            Intent intent = null;
            AddFriend addFriend = null;
            switch (msg.what){
                case TYPE_DISCONNECT:
                    if (progressBar == null || !progressBar.isShowing()){
                        progressBar = ViewUtil.waitNetWorkProgressBar(runningActivity);
                        progressBar.show();
                        NetworkService.startNetWork();
                    }
                    break;
                case TYPE_CONNECT:
                    if (progressBar != null && progressBar.isShowing()){
                        progressBar.dismiss();
                    }
                    Toast.makeText(runningActivity,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case TYPE_CREATE:
                    switch ((String)msg.obj){
                        case "1":
                            DataUtil.createFragment.createFailed("创建账号失败，账号或昵称已存在，请重试！");
                            break;
                        case "2":
                            DataUtil.createFragment.createSuccess("创建账号成功！");
                            break;
                        default:
                            break;
                    }
                    break;
                case TYPE_CHANGE:
                    switch ((String)msg.obj){
                        case "1":
                            DataUtil.changeFragment.changeFailed("修改密码失败，账号不存在，请重试！");
                            break;
                        case "2":
                            DataUtil.changeFragment.changeFailed("修改密码失败，原密码错误，请重试！");
                            break;
                        case "3":
                            DataUtil.changeFragment.changeSuccess("修改密码成功！");
                            break;
                        default:
                            break;
                    }
                    break;
                case TYPE_LOGIN:
                    str = ((String)msg.obj).split(",");     //str[0]:type,str[1]:account,str[2]:userName
                    switch (str[0]){
                        case "1":
                            DataUtil.loginFragment.loginFailed("登录失败，账号不存在，请重试！");
                            break;
                        case "2":
                            DataUtil.loginFragment.loginFailed("登录失败，密码错误，请重试！");
                            break;
                        case "3":
                            DataUtil.loginFragment.loginSuccess(str[1],str[2],str[1] + "/portrait.jpg");
                            break;
                        default:
                            break;
                    }
                    break;
                case TYPE_MESSAGE:
                    str = ((String)msg.obj).split(",");    //str[0]:type,str[1]:friendName,str[2]:friendAccount,str[3]:messageText
                    switch (str[0]){
                        case "0":
                            DBUtil.setTalk(userName,account,str[1],str[2],str[3],false,false);
                            chatFragment.refreshAdapter();
                            idKey = "Talk" + str[1];
                            title = str[1] + ":";
                            intent = new Intent(runningActivity,ChatActivity.class);
                            intent.putExtra("friendName",str[1]);
                            break;
                        case "1":
                            DBUtil.setAddFriend(userName,str[1],str[2],str[2] + "/portrait.jpg",false,AddFriend.TYPE_RESPONSE);
                            idKey = "AddFriend" + str[1];
                            title = "收到" + str[1] + "的好友申请：";
                            intent = new Intent(runningActivity, AddFriendActivity.class);
                            break;
                        case "2":
                            if (str[3].endsWith("同意添加你为好友。")){
                                addFriend = DBUtil.updateAddFriend(userName,str[1],new String[]{"type","isWatch"},null,false,AddFriend.TYPE_SUCCESS);
                                Log.d("TAG",addFriend.toString());
                                DBUtil.setFriend(userName,str[1],addFriend.getFriendAccount(),addFriend.getPortraitUri());
                            }else {
                                DBUtil.updateAddFriend(userName,str[1],new String[]{"type","isWatch"},null,false,AddFriend.TYPE_FAILED);
                            }
                            idKey = "AddFriend" + str[1];
                            title = "好友申请结果：";
                            intent = new Intent(runningActivity,MainActivity.class);
                            break;
                        default:
                            break;
                    }
                    ((MainActivity)runningActivity).refreshRedDoc();
                    ViewUtil.createNotification(idKey,runningActivity,str[2],title,intent);
                    break;
                case TYPE_FRIEND:
                    for (String message : ((String)msg.obj).split("<#&#>")){
                        str = message.split(",");
                        DBUtil.setFriend(userName,str[0],str[1],str[1] + "/portrait.jpg");
                    }
                    break;
                case TYPE_REQUEST:
                    str = ((String)msg.obj).split(",");    //str[0]:type,str[1]:userName,str[2]:friendName,str[3]:portraitUri
                    String friendAccount;
                    switch (str[0]){
                        case "0":
                            friendAccount = str[3].substring(0,str[3].indexOf("/"));
                            addFriend = LitePal.where("userName = ? and friendName = ?",str[1],str[2]).findFirst(AddFriend.class);
                            if (addFriend == null){
                                DBUtil.setAddFriend(userName,str[2],friendAccount,str[3],true,AddFriend.TYPE_WAIT);
                                ((AddFriendActivity)runningActivity).refreshAdapter();
                            }
                            Toast.makeText(runningActivity,"已申请过添加该好友，正等待对方回复！",Toast.LENGTH_SHORT).show();
                            break;
                        case "1":
                            Toast.makeText(runningActivity,"昵称错误，找不到该对象！",Toast.LENGTH_SHORT).show();
                            break;
                        case "2":
                            friendAccount = str[3].substring(0,str[3].indexOf("/"));
                            if (str[1].equals(userName)){
                                DBUtil.setAddFriend(userName,str[2],friendAccount,str[3],true,AddFriend.TYPE_WAIT);
                                Toast.makeText(runningActivity,"提交申请成功，等待对方回应！",Toast.LENGTH_SHORT).show();
                                ((AddFriendActivity)runningActivity).refreshAdapter();
                            }else {
                                DBUtil.setAddFriend(userName,str[1],friendAccount,str[3],false,AddFriend.TYPE_RESPONSE);
                                if (runningActivity instanceof AddFriendActivity ){
                                    ((AddFriendActivity)runningActivity).refreshAdapter();
                                }else {
                                    if (runningActivity instanceof MainActivity){
                                        ((MainActivity)runningActivity).refreshRedDoc();
                                    }
                                    idKey = "AddFriend" + str[1];
                                    messageText = str[1] + "请求添加你为好友。";
                                    title = "收到" + str[1] + "的好友申请：";
                                    intent = new Intent(runningActivity, AddFriendActivity.class);
                                    ViewUtil.createNotification(idKey,runningActivity,messageText,title,intent);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case TYPE_RESPONSE:
                    str = ((String)msg.obj).split(",");    //str[0]:userName,str[1]:friendName,str[2]:response
                    String response;
                    if (str[0].equals(userName)){
                        idKey = "AddFriend" + str[1];
                        if (str[2].equals("0")){
                            addFriend = DBUtil.updateAddFriend(userName,str[1],new String[]{"type","isWatch"},null,true,AddFriend.TYPE_SUCCESS);
                            response ="你已同意添加" + str[1] + "为好友";
                            DBUtil.setFriend(userName,str[1],addFriend.getFriendAccount(),addFriend.getPortraitUri());
                        }else {
                            DBUtil.updateAddFriend(userName,str[1],new String[]{"type","isWatch"},null,true,AddFriend.TYPE_FAILED);
                            response ="你已拒绝添加" + str[1] + "为好友";
                        }
                    }else {
                        idKey = "AddFriend" + str[0];
                        if (str[2].equals("0")){
                            addFriend = DBUtil.updateAddFriend(userName,str[0],new String[]{"type","isWatch"},null,false,AddFriend.TYPE_SUCCESS);
                            response = str[0] +  "同意添加你为好友";
                            DBUtil.setFriend(userName,str[0],addFriend.getFriendAccount(),addFriend.getPortraitUri());
                        }else {
                            DBUtil.updateAddFriend(userName,str[0],new String[]{"type","isWatch"},null,false,AddFriend.TYPE_FAILED);
                            response = str[0] +  "拒绝添加你为好友";
                        }
                    }
                    if (runningActivity instanceof AddFriendActivity){
                        ((AddFriendActivity)runningActivity).refreshAdapter();
                        Toast.makeText(runningActivity,response,Toast.LENGTH_SHORT).show();
                    }else {
                        if (runningActivity instanceof MainActivity){
                            ((MainActivity)runningActivity).refreshRedDoc();
                        }
                        title = "好友申请结果：";
                        intent = new Intent(runningActivity,MainActivity.class);
                        ViewUtil.createNotification(idKey,runningActivity,response,title,intent);
                    }
                    break;
                case TYPE_CHAT:
                    //str[0]:userName,str[1]:userAccount,str[2]:friendName,str[3]:friendAccount,str[4]:text
                    str = ((String)msg.obj).split(",");
                    Talk talk;
                    if (userName.equals(str[0])){
                        talk = DBUtil.setTalk(str[0],str[1],str[2],str[3],str[4],true,true);
                    }else {
                        talk = DBUtil.setTalk(str[2],str[3],str[0],str[1],str[4],false,false);
                    }
                    if (runningActivity instanceof ChatActivity && ((ChatActivity)runningActivity).friendName.equals(talk.getFriendName())){
                        Log.d("TAG","refresh");
                        ((ChatActivity)runningActivity).refreshAdapter();
                    }else {
                        Log.d("TAG","no");
                        if (runningActivity instanceof MainActivity){
                            ((MainActivity)runningActivity).refreshRedDoc();
                        }
                        if (!talk.isSend()){
                            idKey = "Talk" + talk.getFriendName();
                            title = talk.getFriendName() + ":";
                            intent = new Intent(runningActivity,ChatActivity.class);
                            intent.putExtra("friendName",talk.getFriendName());
                            ViewUtil.createNotification(idKey,runningActivity,talk.getText(),title,intent);
                        }
                        chatFragment.refreshAdapter();
                    }
                    break;
                case TYPE_OFF_LINE:
                    if ( !(runningActivity instanceof LoginActivity) ){
                        AlertDialog.Builder builder = new AlertDialog.Builder(runningActivity);
                        builder.setTitle("异地登录提醒：")
                                .setMessage(DataUtil.userName + "正在别处登录，请下线！")
                                .setCancelable(false)
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataUtil.activityList.clear();
                                        Intent intent = new Intent(runningActivity,LoginActivity.class);
                                        runningActivity.startActivity(intent);
                                        runningActivity.finish();
                                    }
                                }).show();
                    }
                    break;
                case TYPE_DELETE_FRIEND:
                    DBUtil.deleteAddFriendDB(userName,(String)msg.obj);
                    Toast.makeText(runningActivity,"已删除好友：" + (String)msg.obj,Toast.LENGTH_SHORT).show();
                    friendFragment.refreshAdapter();
                    break;
                case TYPE_CHAT_ERROR:
                    DBUtil.deleteAddFriendDB(userName,(String)msg.obj);
                    ((ChatActivity)runningActivity).chatError((String)msg.obj);
                    break;
                case TYPE_GET_FINISH:
                    if (runningActivity instanceof ChatActivity){
                        ((ChatActivity) runningActivity).refreshAdapter();
                    }else if (runningActivity instanceof AddFriendActivity){
                        ((AddFriendActivity) runningActivity).refreshAdapter();
                    }
                    break;
                case TYPE_NO_FILE:
                    filePath = (String)msg.obj;
                    String name = filePath.substring(0,filePath.lastIndexOf("/"));
                    String fileName = filePath.substring(filePath.indexOf("/")+1);
                    Log.d("TAG",name + "," + fileName);
                    if (fileName.endsWith("portrait.jpg")){
                        FileUtil.setDefaultPortrait(name);
                    }else {
                        FileUtil.isSend = true;
                        if (runningActivity instanceof ChatActivity){
                            ((ChatActivity) runningActivity).refreshAdapter();
                        }
                    }
                    break;
                case TYPE_CHANGE_PORTRAIT:
                    filePath = (String)msg.obj;
                    if (lastFragment instanceof UserFragment){
                        ((UserFragment) lastFragment).setPortrait(filePath);
                    }else if (lastFragment instanceof ChatFragment){
                        ((ChatFragment) lastFragment).refreshAdapter();
                    }else if (lastFragment instanceof FriendFragment){
                        ((FriendFragment) lastFragment).refreshAdapter();
                    }
                default:
                    break;
            }
        }
    };

    public static void setMessage(int what,String message){
        Message msg = new Message();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }

    public static void setNotification(){
        if (messageList.size() != 0){
            for (String message : messageList){
                setMessage(TYPE_MESSAGE,message);
            }
            messageList.clear();
        }
    }

    public static void clearData(){
        chatFragment = null;
        friendFragment = null;
        userFragment = null;
        lastFragment = null;
        ViewUtil.map.clear();
        ViewUtil.idCount = 1;
    }

}

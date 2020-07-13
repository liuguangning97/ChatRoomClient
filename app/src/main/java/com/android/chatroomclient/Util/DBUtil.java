package com.android.chatroomclient.Util;

import android.util.Log;

import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.db.AddFriend;
import com.android.chatroomclient.db.Friend;
import com.android.chatroomclient.db.Talk;
import com.android.chatroomclient.db.User;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

public class DBUtil {

    public static User setUser(String account,String password,String userName,String portraitUri,boolean isRemember){
        int order = 0;
        User lastUser = LitePal.order("order").findLast(User.class);
        if (lastUser != null){
            order = lastUser.getOrder()+1;
        }
        User user = LitePal.where("userName = ?",userName).findFirst(User.class);
        if (user == null){
            user = new User();
            user.setAccount(account);
            user.setPassword(password);
            user.setUserName(userName);
            user.setPortraitUri(portraitUri);
            user.setRemember(isRemember);
        }else {
            user.setRemember(isRemember);
            if (isRemember){
                user.setPassword(password);
            }else {
                user.setPassword(null);
            }
        }
        user.setOrder(order);
        user.save();
        return user;
    }

    public static User updateUser(String account,String item,String data){
        User user = LitePal.where("account = ?",account).findFirst(User.class);
        if (user != null){
            switch (item){
                case "password":
                    if (user.isRemember()){
                        user.setPassword(data);
                    }
                    break;
                case "portraitUri":
                    user.setPortraitUri(data);
                    break;
                default:
                    break;
            }
            user.save();
        }
        return user;
    }

    public static void deleteUserDB(String userName,String account){
        FileUtil.deleteDirAndFile(new File(DataUtil.path,account));
        LitePal.deleteAll(User.class,"userName = ?",userName);
        List<Friend> friendList = LitePal.where("userName = ?",userName).find(Friend.class);
        for (Friend friend :friendList){
            deleteFriendDB(userName,friend.getFriendName());
        }
    }

    public static AddFriend setAddFriend(String userName,String friendName,String friendAccount,String portraitUri,boolean isWatch,int type){
        int order = 0;
        AddFriend lastAddFriend = LitePal.where("userName = ?",userName).order("order").findLast(AddFriend.class);
        if (lastAddFriend != null){
            order = lastAddFriend.getOrder()+1;
        }
        AddFriend addFriend = LitePal.where("userName = ? and friendName = ?",userName,friendName).findFirst(AddFriend.class);
        if (addFriend == null){
            addFriend = new AddFriend();
            addFriend.setUserName(userName);
            addFriend.setFriendName(friendName);
            addFriend.setFriendAccount(friendAccount);
            addFriend.setPortraitUri(portraitUri);
            addFriend.setWatch(isWatch);
            addFriend.setType(type);
        }
        addFriend.setOrder(order);
        addFriend.save();
        return addFriend;
    }

    public static AddFriend updateAddFriend(String userName,String friendName,String[] items,String portraitUri,boolean isWatch,int type){
        AddFriend addFriend = LitePal.where("userName = ? and friendName = ?",userName,friendName).findFirst(AddFriend.class);
        if (addFriend != null){
            for (String item : items){
                switch (item){
                    case "portraitUri":
                        addFriend.setPortraitUri(portraitUri);
                        break;
                    case "isWatch":
                        addFriend.setWatch(isWatch);
                        break;
                    case "type":
                        addFriend.setType(type);
                        break;
                    default:
                        break;
                }
            }
            addFriend.save();
        }
        return addFriend;
    }

    public static void deleteAddFriendTable(String userName){
        LitePal.deleteAll(AddFriend.class,"userName = ?",userName);
    }

    public static void deleteAddFriendDB(String userName,String friendName){
        LitePal.deleteAll(AddFriend.class,"userName = ? and friendName = ?",userName,friendName);
    }

    public static Friend setFriend(String userName,String friendName,String friendAccount,String portraitUri){
        int order = 0;
        Friend lastFriend = LitePal.where("userName = ?",userName).order("order").findLast(Friend.class);
        if (lastFriend != null){
            order = lastFriend.getOrder()+1;
        }
        Friend friend = LitePal.where("userName = ? and friendName = ?",userName,friendName).findFirst(Friend.class);
        if (friend == null){
            friend = new Friend();
            friend.setUserName(userName);
            friend.setFriendName(friendName);
            friend.setFriendAccount(friendAccount);
            friend.setPortraitUri(portraitUri);
            friend.setOrder(order);
            friend.save();
        }
        return friend;
    }

    public static Friend updateFriendOrder(String userName,String friendName){
        Friend friend = LitePal.where("userName = ? and friendName = ?",userName,friendName).findFirst(Friend.class);
        if (friend != null){
            int order = 0;
            Friend lastFriend = LitePal.where("userName = ?",userName).order("order").findLast(Friend.class);
            if (lastFriend != null){
                order = lastFriend.getOrder()+1;
            }
            friend.setOrder(order);
            friend.save();
        }
        return friend;
    }

    public static void deleteFriendDB(String userName,String friendName){
        LitePal.deleteAll(Friend.class,"userName = ? and friendName = ?",userName,friendName);
        deleteTalkDB(userName,friendName);
    }

    public static Talk setTalk(String userName,String userAccount,String friendName,String friendAccount,String text,boolean isSend,boolean isWatch){
        Talk talk = new Talk();
        talk.setUserName(userName);
        talk.setUserAccount(userAccount);
        talk.setFriendName(friendName);
        talk.setFriendAccount(friendAccount);
        talk.setText(text);
        talk.setSend(isSend);
        talk.setWatch(isWatch);
        talk.save();
        return talk;
    }

    public static void updateTalk(String userName,String friendName,boolean isWatch){
        List<Talk> talkList = LitePal.where("userName = ? and friendName = ?",userName,friendName).find(Talk.class);
        if (talkList.size() != 0){
            for (Talk talk : talkList){
                talk.setWatch(isWatch);
                talk.save();
            }
        }
    }

    public static void deleteTalkDB(String userName,String friendName){
        List<Talk> talkList = LitePal.where("userName = ? and friendName = ?",userName,friendName).find(Talk.class);
        String userAccount = talkList.get(0).getUserAccount();
        String friendAccount = talkList.get(0).getFriendAccount();
        for (Talk talk : talkList){
            if (talk.getText().startsWith("<#FILE_")){
                String filePath = talk.getText().substring(talk.getText().indexOf(">",1)+1);
                NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,"<#DELETE_FILE#>" + filePath,null);
            }
        }
        FileUtil.deleteDirAndFile(new File(DataUtil.path,userAccount + "/" + friendAccount));
        LitePal.deleteAll(Talk.class,"userName = ? and friendName = ?",userName,friendName);
    }

}

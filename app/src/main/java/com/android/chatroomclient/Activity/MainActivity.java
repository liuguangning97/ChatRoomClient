package com.android.chatroomclient.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.chatroomclient.Fragment.ChatFragment;
import com.android.chatroomclient.Fragment.FriendFragment;
import com.android.chatroomclient.Fragment.UserFragment;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.AddFriend;
import com.android.chatroomclient.db.Talk;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.litepal.LitePal;

import java.util.List;

import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomBar;

    public QBadgeView chatDoc;
    public QBadgeView friendDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataUtil.setNotification();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        bottomBar = (BottomNavigationView)findViewById(R.id.bottom_bar);
        DataUtil.chatFragment = new ChatFragment(this);
        DataUtil.friendFragment = new FriendFragment(this);
        DataUtil.userFragment = new UserFragment(this);
        refreshRedDoc();

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.chat:
                        setFragment("聊天室",DataUtil.chatFragment);
                        return true;
                    case R.id.friend:
                        setFragment("好友",DataUtil.friendFragment);
                        return true;
                    case R.id.user:
                        setFragment("个人中心",DataUtil.userFragment);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        if (DataUtil.lastFragment == null){
            bottomBar.setSelectedItemId(R.id.chat);
        }else if (DataUtil.lastFragment instanceof ChatFragment){
            bottomBar.setSelectedItemId(R.id.chat);
        }else if (DataUtil.lastFragment instanceof FriendFragment){
            bottomBar.setSelectedItemId(R.id.friend);
        }else if (DataUtil.lastFragment instanceof UserFragment){
            bottomBar.setSelectedItemId(R.id.user);
        }

    }

    public void refreshRedDoc(){
        chatDoc = setRedDoc(Talk.class,bottomBar.findViewById(R.id.chat));
        friendDoc = setRedDoc(AddFriend.class,bottomBar.findViewById(R.id.friend));
    }

    public QBadgeView setRedDoc(Class myClass, View view){
        List<?> list = LitePal.where("userName = ? and isWatch = ?",DataUtil.userName,"0").find(myClass);
        QBadgeView redDoc = new QBadgeView(this);
        if (list.size() != 0){
            redDoc.bindTarget(view).setBadgeNumber(list.size())
                    .setGravityOffset(60,5,false).setExactMode(false);
        }
        return redDoc;
    }

    public void setFragment(String title, Fragment fragment){
        if (DataUtil.lastFragment == null || DataUtil.lastFragment != fragment){
            DataUtil.lastFragment = fragment;
            toolbar.setTitle(title);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).addToBackStack(null).commit();
        }
        if (fragment instanceof FriendFragment){
            friendDoc.setVisibility(View.GONE);
        }else {
            friendDoc.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,"<#OFF_LINE#>" + DataUtil.userName,null);
    }
}

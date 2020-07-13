package com.android.chatroomclient.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DBUtil;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.Adapter.TalkAdapter;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.Friend;
import com.android.chatroomclient.db.Talk;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView delete;
    private RecyclerView recyclerView;
    private EditText messageEdit;
    private Button add;
    private Button send;
    private LinearLayout addBar;
    private LinearLayout sendCammer;
    private LinearLayout sendPic;
    private LinearLayout sendMusic;
    public String friendName;
    private TalkAdapter adapter;
    public List<Talk> talkList = new ArrayList<>();
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendName");
        friend = LitePal.where("friendName = ?",friendName).findFirst(Friend.class);
        ViewUtil.removeMapItem("Talk" + friendName);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        delete = (ImageView)findViewById(R.id.delete);
        recyclerView = (RecyclerView)findViewById(R.id.chat_recycler);
        toolbar.setTitle(friendName);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        messageEdit = (EditText)findViewById(R.id.message);
        send = (Button)findViewById(R.id.send);
        add = (Button)findViewById(R.id.add);
        addBar = (LinearLayout)findViewById(R.id.add_bar);
        sendCammer = (LinearLayout)findViewById(R.id.send_camera);
        sendPic = (LinearLayout)findViewById(R.id.send_pic);
        sendMusic = (LinearLayout)findViewById(R.id.send_music);

        delete.setOnClickListener(this);
        send.setOnClickListener(this);
        add.setOnClickListener(this);
        sendCammer.setOnClickListener(this);
        sendPic.setOnClickListener(this);
        sendMusic.setOnClickListener(this);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (addBar.getVisibility() == View.VISIBLE){
                    addBar.setVisibility(View.GONE);
                }
                return false;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        refreshAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete:
                addBar.setVisibility(View.GONE);
                AlertDialog.Builder builder = ViewUtil.createAlertDialogBuilder(ChatActivity.this,"是否删除与该好友的聊天记录？");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBUtil.deleteTalkDB(DataUtil.userName,friendName);
                        refreshAdapter();
                    }
                });
                builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                break;
            case R.id.send:
                addBar.setVisibility(View.GONE);
                String message = messageEdit.getText().toString();
                if (!message.equals("")){
                    NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,
                            "<#CHAT_MESSAGE#>" + DataUtil.account + "," + friend.getFriendAccount() + ",<#MESSAGE#>" + message,null);
                    messageEdit.setText("");
                    ViewUtil.closeSoftKeyboard();
                }else {
                    Toast.makeText(ChatActivity.this,"请输入发送内容。",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add:
                ViewUtil.closeSoftKeyboard();
                if (addBar.getVisibility() == View.VISIBLE){
                    addBar.setVisibility(View.GONE);
                }else {
                    addBar.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.send_camera:
                FileUtil.selectFile("camera",friend.getFriendAccount());
                addBar.setVisibility(View.GONE);
                break;
            case R.id.send_pic:
                FileUtil.selectFile("image",friend.getFriendAccount());
                addBar.setVisibility(View.GONE);
                break;
            case R.id.send_music:
                addBar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void refreshAdapter(){
        talkList = LitePal.where("userName = ? and friendName = ?",DataUtil.userName,friendName).find(Talk.class);
        if (talkList.size() == 0){
            Log.d("TAG",DataUtil.userName + "," + friendName);
        }
        adapter = new TalkAdapter(talkList);
        recyclerView.setAdapter(adapter);
        if (talkList.size() > 0){
            recyclerView.scrollToPosition(talkList.size()-1);
        }
        DBUtil.updateTalk(DataUtil.userName,friendName,true);
    }

    public void chatError(String friendName){
        AlertDialog.Builder builder = ViewUtil.createAlertDialogBuilder(this,friendName + "已经不是你的好友，请退出聊天。");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

}

package com.android.chatroomclient.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.Adapter.AddFriendAdapter;
import com.android.chatroomclient.Util.DBUtil;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.AddFriend;
import com.android.chatroomclient.db.Friend;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends BaseActivity {

    private Toolbar toolbar;
    private ImageView delete;
    private EditText friendNameEdit;
    private Button add;
    private RecyclerView addFriendRecycler;

    private AddFriendAdapter adapter;
    private List<AddFriend> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("新的朋友");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriendActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        delete = (ImageView)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AddFriend> addFriendList = LitePal.where("userName = ?",DataUtil.userName).find(AddFriend.class);
                if (addFriendList.size() != 0 ){
                    AlertDialog.Builder builder = ViewUtil.createAlertDialogBuilder(AddFriendActivity.this,"是否删除添加好友记录？");
                    builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBUtil.deleteAddFriendTable(DataUtil.userName);
                            refreshAdapter();
                        }
                    });
                    builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }else {
                    Toast.makeText(AddFriendActivity.this,"没有添加好友信息！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        friendNameEdit = (EditText)findViewById(R.id.friend_name);
        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendName = friendNameEdit.getText().toString();
                if (friendName.equals("")){
                    Toast.makeText(AddFriendActivity.this,"请输入完整信息！",Toast.LENGTH_SHORT).show();
                }else if (friendName.equals(DataUtil.userName)){
                    Toast.makeText(AddFriendActivity.this,"不能添加自己为好友！",Toast.LENGTH_SHORT).show();
                }else {
                    Friend friend = LitePal.where("userName = ? and friendName = ?",DataUtil.userName,friendName).findFirst(Friend.class);
                    if (friend != null ){
                        Toast.makeText(AddFriendActivity.this,"添加好友失败，" +   friendName + "已经是你的好友！",Toast.LENGTH_SHORT).show();
                    }else {
                        NetworkService.sendMessage(NetworkService.TYPE_MESSAGE, "<#ADD_FRIEND#>" + DataUtil.userName + "," + friendName,null);
                        ViewUtil.closeSoftKeyboard();
                    }
                }
                friendNameEdit.setText("");
            }
        });

        addFriendRecycler = (RecyclerView)findViewById(R.id.add_friend_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        addFriendRecycler.setLayoutManager(manager);
        refreshAdapter();
    }

    public void refreshAdapter(){
        dataList = LitePal.where("userName = ?",DataUtil.userName).find(AddFriend.class);
        adapter = new AddFriendAdapter(dataList);
        addFriendRecycler.setAdapter(adapter);
        ViewUtil.removeMapItem(dataList);
    }

}

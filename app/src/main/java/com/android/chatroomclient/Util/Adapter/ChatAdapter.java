package com.android.chatroomclient.Util.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.Activity.ChatActivity;
import com.android.chatroomclient.Activity.MainActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.DBUtil;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.Friend;
import com.android.chatroomclient.db.Talk;

import org.litepal.LitePal;

import java.util.List;

import q.rorbin.badgeview.QBadgeView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private MainActivity activity;
    private List<Talk> talkList;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout chatLayout;
        private ImageView portrait;
        private TextView name;
        private TextView chatText;

        public ViewHolder(@NonNull View view) {
            super(view);
            chatLayout = (RelativeLayout)view.findViewById(R.id.chat_layout);
            portrait = (ImageView)view.findViewById(R.id.portrait);
            name = (TextView)view.findViewById(R.id.name);
            chatText = (TextView)view.findViewById(R.id.chat_text);
        }
    }

    public ChatAdapter(List<Talk> talkList,MainActivity activity) {
        this.talkList = talkList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_chat,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtil.updateFriendOrder(DataUtil.userName,holder.name.getText().toString());
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("friendName",holder.name.getText());
                activity.startActivity(intent);
            }
        });
        holder.chatLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = ViewUtil.createAlertDialogBuilder(activity,"是否删除与该好友的聊天记录？");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBUtil.deleteTalkDB(DataUtil.userName,holder.name.getText().toString());
                        DataUtil.chatFragment.refreshAdapter();
                    }
                });
                builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Talk talk = talkList.get(position);
        Friend friend = LitePal.where("userName = ? and friendName = ?",talk.getUserName(),talk.getFriendName()).findFirst(Friend.class);
        holder.portrait.setImageBitmap(FileUtil.getPortrait(friend.getPortraitUri()));
        holder.name.setText(talk.getFriendName());
        if (talk.isSend()){
            if (talk.getText().startsWith("<#MESSAGE#>")){
                holder.chatText.setText(talk.getUserName() + ":" + talk.getText().substring(11));
            }else if (talk.getText().startsWith("<#FILE_IMAGE#>")){
                holder.chatText.setText(talk.getUserName() + ":[图片]");
            }
        }else {
            if (talk.getText().startsWith("<#MESSAGE#>")){
                holder.chatText.setText(talk.getFriendName() + ":" + talk.getText().substring(11));
            }else if (talk.getText().startsWith("<#FILE_IMAGE#>")){
                holder.chatText.setText(talk.getFriendName() + ":[图片]");
            }
        }
        List<Talk> talkList = LitePal.where("userName = ? and friendName = ? and isWatch = ?",
                                                        DataUtil.userName,talk.getFriendName(),"0").find(Talk.class);
        if (talkList.size() != 0){
            QBadgeView redDoc = new QBadgeView(activity);
            redDoc.bindTarget(holder.portrait).setBadgeNumber(talkList.size()).setBadgeGravity(Gravity.END|Gravity.TOP)
                    .setGravityOffset(0,0,false).setExactMode(false);
        }
    }


    @Override
    public int getItemCount() {
        return talkList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

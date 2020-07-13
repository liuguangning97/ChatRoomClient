package com.android.chatroomclient.Util.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.Activity.BaseActivity;
import com.android.chatroomclient.Activity.ChatActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.Friend;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Friend> friendList;
    private BaseActivity activity;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout friendItem;
        private ImageView friendPortrait;
        private TextView friendName;

        public ViewHolder(@NonNull View view) {
            super(view);
            friendItem = (RelativeLayout)view.findViewById(R.id.friend_item);
            friendPortrait = (ImageView)view.findViewById(R.id.friend_portrait);
            friendName = (TextView)view.findViewById(R.id.friend_name);
        }
    }

    public FriendAdapter(List<Friend> friendList,BaseActivity activity) {
        this.friendList = friendList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        final Bitmap bitmap = FileUtil.getPortrait(friend.getPortraitUri());
        holder.friendPortrait.setImageBitmap(bitmap);
        holder.friendPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.showBigImage(activity,bitmap);
            }
        });
        final String friendName = friend.getFriendName();
        holder.friendName.setText(friendName);
        holder.friendItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("friendName",friendName);
                activity.startActivity(intent);
            }
        });
        holder.friendItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = ViewUtil.createAlertDialogBuilder(activity,"是否删除好友？");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,
                                            "<#DELETE_FRIEND#>" + DataUtil.userName + "," + friendName,null);
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
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

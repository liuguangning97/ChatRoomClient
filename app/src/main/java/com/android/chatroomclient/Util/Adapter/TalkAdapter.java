package com.android.chatroomclient.Util.Adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.Talk;
import com.android.chatroomclient.db.User;

import org.litepal.LitePal;

import java.util.List;

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.ViewHolder> {

    private List<Talk> talkList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout left;
        private RelativeLayout right;
        private TextView userName;
        private TextView userText;
        private ImageView userPortrait;
        private ImageView userPic;
        private TextView friendName;
        private TextView friendText;
        private ImageView friendPortrait;
        private ImageView friendPic;

        public ViewHolder(@NonNull View view) {
            super(view);
            left = (RelativeLayout)view.findViewById(R.id.talk_left);
            right = (RelativeLayout)view.findViewById(R.id.talk_right);
            userName = (TextView)view.findViewById(R.id.user_name);
            userText = (TextView)view.findViewById(R.id.user_text);
            userPortrait = (ImageView)view.findViewById(R.id.user_portrait);
            userPic =  (ImageView)view.findViewById(R.id.user_pic);
            friendName = (TextView)view.findViewById(R.id.friend_name);
            friendText = (TextView)view.findViewById(R.id.friend_text);
            friendPortrait = (ImageView)view.findViewById(R.id.friend_portrait);
            friendPic = (ImageView)view.findViewById(R.id.friend_pic);
        }
    }

    public TalkAdapter(List<Talk> talkList) {
        this.talkList = talkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_talk,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Talk talk = talkList.get(position);
        if (talk.isSend()){
            holder.right.setVisibility(View.VISIBLE);
            holder.userName.setText(talk.getUserName());
            User user = LitePal.where("userName = ?",talk.getUserName()).findFirst(User.class);
            final Bitmap portrait = FileUtil.getPortrait(user.getPortraitUri());
            holder.userPortrait.setImageBitmap(portrait);
            holder.userPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.showBigImage(DataUtil.runningActivity,portrait);
                }
            });
            if (talk.getText().startsWith("<#MESSAGE#>")){
                holder.userText.setVisibility(View.VISIBLE);
                holder.userText.setText(talk.getText().substring(11));
            }else if (talk.getText().startsWith("<#FILE_IMAGE#>")){
                holder.userPic.setVisibility(View.VISIBLE);
                final Bitmap bitmap = FileUtil.getPortrait(talk.getText().substring(14));
                holder.userPic.setImageBitmap(bitmap);
                holder.userPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.showBigImage(DataUtil.runningActivity,bitmap);
                    }
                });
            }
        }else {
            holder.left.setVisibility(View.VISIBLE);
            holder.friendName.setText(talk.getFriendName());
            User user = LitePal.where("userName = ?",talk.getFriendName()).findFirst(User.class);
            final Bitmap portrait = FileUtil.getPortrait(user.getPortraitUri());
            holder.friendPortrait.setImageBitmap(portrait);
            holder.friendPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.showBigImage(DataUtil.runningActivity,portrait);
                }
            });
            if (talk.getText().startsWith("<#MESSAGE#>")){
                holder.friendText.setVisibility(View.VISIBLE);
                holder.friendText.setText(talk.getText().substring(11));
            }else if (talk.getText().startsWith("<#FILE_IMAGE#>")){
                holder.friendPic.setVisibility(View.VISIBLE);
                final Bitmap bitmap = FileUtil.getPortrait(talk.getText().substring(14));
                holder.friendPic.setImageBitmap(bitmap);
                holder.friendPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.showBigImage(DataUtil.runningActivity,bitmap);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return talkList.size();
    }
}

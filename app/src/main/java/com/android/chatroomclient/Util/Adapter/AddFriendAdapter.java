package com.android.chatroomclient.Util.Adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.db.AddFriend;

import java.util.List;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {

    private List<AddFriend> dataList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView friendName;
        private TextView type;
        private LinearLayout buttonLayout;
        private Button agree;
        private Button refuse;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.friend_portrait);
            friendName = (TextView)view.findViewById(R.id.friend_name);
            type = (TextView)view.findViewById(R.id.type);
            buttonLayout = (LinearLayout)view.findViewById(R.id.button_layout);
            agree = (Button)view.findViewById(R.id.agree);
            refuse = (Button)view.findViewById(R.id.refuse);
        }
    }

    public AddFriendAdapter(List<AddFriend> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friend,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AddFriend addFriend = dataList.get(position);
        Bitmap bitmap = FileUtil.getPortrait(addFriend.getPortraitUri());
        if (bitmap != null){
            holder.imageView.setImageBitmap(bitmap);
        }
        holder.friendName.setText(addFriend.getFriendName());
        switch (addFriend.getType()){
            case AddFriend.TYPE_WAIT:
                holder.buttonLayout.setVisibility(View.GONE);
                holder.type.setText("等待确认");
                setWatch(addFriend.getFriendName());
                break;
            case AddFriend.TYPE_SUCCESS:
                holder.buttonLayout.setVisibility(View.GONE);
                holder.type.setText("已通过");
                holder.type.setTextColor(Color.GREEN);
                setWatch(addFriend.getFriendName());
                break;
            case AddFriend.TYPE_FAILED:
                holder.buttonLayout.setVisibility(View.GONE);
                holder.type.setText("已拒绝");
                holder.type.setTextColor(Color.RED);
                setWatch(addFriend.getFriendName());
                break;
            case AddFriend.TYPE_RESPONSE:
                holder.type.setVisibility(View.GONE);
                holder.agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,
                                    "<#ADD_FRIEND_RESPONSE#>" + DataUtil.userName + "," + addFriend.getFriendName() + "," + 0,null);
                        setWatch(addFriend.getFriendName());
                    }
                });
                holder.refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,
                                    "<#ADD_FRIEND_RESPONSE#>" + DataUtil.userName + "," + addFriend.getFriendName() + "," + 1,null);
                        setWatch(addFriend.getFriendName());
                    }
                });
                break;
            default:
                break;
        }
    }

    private void setWatch(String friendName){
        AddFriend addFriend = new AddFriend();
        addFriend.setWatch(true);
        addFriend.updateAll("userName = ? and friendName = ?",DataUtil.userName,friendName);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

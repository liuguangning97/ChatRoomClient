package com.android.chatroomclient.Util.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.DBUtil;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.MySpinner;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.User;

import java.util.List;

public class MySpinnerAdapter extends RecyclerView.Adapter<MySpinnerAdapter.ViewHolder> {

    private List<User> userList;
    private MySpinner mySpinner;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layout;
        private ImageView portrait;
        private TextView account;
        private TextView name;
        private ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (RelativeLayout)itemView.findViewById(R.id.layout);
            portrait = (ImageView)itemView.findViewById(R.id.portrait);
            account = (TextView) itemView.findViewById(R.id.account);
            name = (TextView)itemView.findViewById(R.id.name);
            delete = (ImageView)itemView.findViewById(R.id.delete);
        }
    }

    public MySpinnerAdapter(List<User> userList, MySpinner mySpinner) {
        this.userList = userList;
        this.mySpinner = mySpinner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.account.setText(user.getAccount());
        holder.name.setText(user.getUserName());
        holder.portrait.setImageBitmap(FileUtil.getPortrait(user.getPortraitUri()));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.loginFragment.setEdit(user);
                mySpinner.popupWindow.dismiss();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = ViewUtil.createAlertDialogBuilder(DataUtil.runningActivity,"是否要删除该用户？");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBUtil.deleteUserDB(user.getUserName(),user.getAccount());
                        mySpinner.popupWindow.dismiss();
                        DataUtil.loginFragment.refreshSpinner();
                    }
                });
                builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

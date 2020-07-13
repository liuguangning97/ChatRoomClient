package com.android.chatroomclient.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chatroomclient.Activity.LoginActivity;
import com.android.chatroomclient.Activity.MainActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.User;

import org.litepal.LitePal;


public class UserFragment extends Fragment {

    private TextView userName;
    private TextView account;
    private Button exit;

    private MainActivity activity;

    public ImageView portrait;

    public UserFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragnemt_user,container,false);
        userName = (TextView)view.findViewById(R.id.user_name);
        portrait = (ImageView) view.findViewById(R.id.portrait);
        final String portraitUri = LitePal.where("userName = ?",DataUtil.userName).findFirst(User.class).getPortraitUri();
        setPortrait(portraitUri);
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  builder = ViewUtil.createAlertDialogBuilder(activity,"是否更改头像？");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtil.selectPortrait(activity);
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
        account = (TextView)view.findViewById(R.id.account);
        userName.setText(DataUtil.userName);
        account.setText(DataUtil.account);
        exit = (Button)view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                DataUtil.clearData();
                activity.finish();
            }
        });

        Button text = (Button)view.findViewById(R.id.text);

        return view;
    }

    public void setPortrait(String filePath){
        Bitmap bitmap = FileUtil.getPortrait(filePath);
        if (bitmap != null){
            portrait.setImageBitmap(bitmap);
        }
    }

}

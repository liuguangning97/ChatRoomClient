package com.android.chatroomclient.Util.Widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.chatroomclient.Activity.BaseActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.db.AddFriend;

import java.util.List;
import java.util.Map;

public class ViewUtil {

    public static Map<String,Integer> map = new ArrayMap<>();
    public static int idCount = 1;


    public static void createNotification(String idKey , Activity activity , String messageText, String title, Intent intent){
        int id;
        if (map.get(idKey) == null){
            map.put(idKey,idCount);
            id = idCount++;
        }else {
            id = map.get(idKey);
        }
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(activity,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(activity)
                .setSmallIcon(R.mipmap.ic_chat)
                .setContentTitle(title)
                .setContentText(messageText)
                .setContentIntent(pi)
                .setAutoCancel(true);
        manager.notify(id,builder.build());
    }

    public static void removeMapItem(String key){
        if (map.get(key) != null){
            NotificationManager manager = (NotificationManager) DataUtil.runningActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(map.get(key));
            map.remove(key);
        }
    }

    public static void removeMapItem(List<AddFriend> addFriendList){
        if (map.size() != 0){
            NotificationManager manager = (NotificationManager) DataUtil.runningActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            for (AddFriend addFriend : addFriendList){
                Integer id = map.get("AddFriend" + addFriend.getFriendName());
                if (id != null){
                    manager.cancel(id);
                    map.remove(id);
                }
            }
        }
    }

    public static AlertDialog waitNetWorkProgressBar(BaseActivity activity){
        AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.ProgressBar).create();
        View view = LayoutInflater.from(activity).inflate(R.layout.widget_progress_bar,null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    public static AlertDialog.Builder createAlertDialogBuilder(Activity activity,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false)
                .setMessage(message);
        return builder;
    }

    public static void closeSoftKeyboard(){
        int screenHeight = DataUtil.runningActivity.getWindow().getDecorView().getHeight();     //获取当前屏幕内容高度;
        Rect rect = new Rect();
        DataUtil.runningActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        if (screenHeight*2/3 > rect.bottom){                  //当屏幕内容高度的2/3大于顶层view的可见区域高度时，键盘处于显示状态
            InputMethodManager imm = (InputMethodManager)DataUtil.runningActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(DataUtil.runningActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showBigImage(Activity activity,Bitmap bitmap){
        ImageView imageView = new ImageView(activity);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageBitmap(bitmap);
        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

}

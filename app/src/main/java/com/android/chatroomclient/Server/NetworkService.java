package com.android.chatroomclient.Server;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.chatroomclient.Activity.BaseActivity;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkService extends Service {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_FILE = 1;

    public static Socket socket;
    public static DataInputStream din;
    public static DataOutputStream dou;

    @Override
    public void onCreate() {
        super.onCreate();
        DataUtil.setMessage(DataUtil.TYPE_DISCONNECT,"等待连接...");
    }

    public static void startNetWork(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                while (true){
                    try {
                        socket = new Socket(DataUtil.ipAddress,8989);
                        if (socket.isConnected()){
                            din = new DataInputStream(socket.getInputStream());
                            dou = new DataOutputStream(socket.getOutputStream());
                            flag = true;
                            sendMessage(TYPE_MESSAGE,"<#CONNECT#>",null);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        DataUtil.setMessage(DataUtil.TYPE_DISCONNECT,"等待连接...");
                    }
                }
                while (flag){
                    try {
                        byte type = din.readByte();
                        if ((int)type == TYPE_MESSAGE){            //收到的时文字内容
                            int length = din.readInt();
                            byte[] data = new byte[length];
                            din.readFully(data);
                            String msg = new String(data);
                            requestMessage(msg);
                        }else if ((int)type == TYPE_FILE){          //收到的时文件
                            int pathLength = din.readInt();
                            int fileLength = din.readInt();
                            byte[] pathData = new byte[pathLength];
                            byte[] fileData = new byte[fileLength];
                            din.read(pathData);
                            din.readFully(fileData);
                            String filePath = new String(pathData);
                            FileUtil.saveFile(filePath,fileData);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        flag = false;
                        DataUtil.setMessage(DataUtil.TYPE_DISCONNECT,"等待连接...");
                    }
                }
            }
        }).start();
    }

    private static void requestMessage(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (msg.startsWith("<#CONNECT#>")){
                    DataUtil.setMessage(DataUtil.TYPE_CONNECT,"连接成功");
                }else if (msg.startsWith("<#CREATE#>")){
                    DataUtil.setMessage(DataUtil.TYPE_CREATE,msg.substring(10));
                }else if (msg.startsWith("<#CHANGE#>")){
                    DataUtil.setMessage(DataUtil.TYPE_CHANGE,msg.substring(10));
                }else if (msg.startsWith("<#LOGIN#>")){
                    DataUtil.setMessage(DataUtil.TYPE_LOGIN,msg.substring(9));
                }else if (msg.startsWith("<#OFFLINE_MESSAGE#>")){
                   String[] str = msg.substring(19).split("<#&#>");
                   for (String string : str){
                       DataUtil.messageList.add(string);
                   }
                }else if (msg.startsWith("<#FRIEND_LIST#>")){
                    DataUtil.setMessage(DataUtil.TYPE_FRIEND,msg.substring(15));
                }else if (msg.startsWith("<#REQUEST#>")){
                    DataUtil.setMessage(DataUtil.TYPE_REQUEST,msg.substring(11));
                }else if (msg.startsWith("<#RESPONSE#>")){
                    DataUtil.setMessage(DataUtil.TYPE_RESPONSE,msg.substring(12));
                }else if (msg.startsWith("<#CHAT_MESSAGE#>")){
                    DataUtil.setMessage(DataUtil.TYPE_CHAT,msg.substring(16));
                }else if (msg.startsWith("<#OFF_LINE#>")){
                    DataUtil.setMessage(DataUtil.TYPE_OFF_LINE,msg);
                }else if (msg.startsWith("<#DELETE_FRIEND#>")){
                    DataUtil.setMessage(DataUtil.TYPE_DELETE_FRIEND,msg.substring(17));
                }else if (msg.startsWith("<#CHAT_ERROR#>")){
                    DataUtil.setMessage(DataUtil.TYPE_CHAT_ERROR,msg.substring(14));
                }else if (msg.startsWith("<#NO_FILE#>")){
                    DataUtil.setMessage(DataUtil.TYPE_NO_FILE,msg.substring(11));
                }
            }
        }).start();
    }

    public static void sendMessage(final int type,final String message,final byte[] fileBytes){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (type == TYPE_MESSAGE){
                        dou.writeByte(type);
                        byte[] data = message.getBytes();
                        int length = data.length;
                        dou.writeInt(length);
                        dou.write(data);
                        dou.flush();
                    }else if (type == TYPE_FILE){
                        FileUtil.sendFile(dou,type,message,fileBytes);
                    }
                }catch (EOFException e){
                    DataUtil.setMessage(DataUtil.TYPE_DISCONNECT,"等待连接...");
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

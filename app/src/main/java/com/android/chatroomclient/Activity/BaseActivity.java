package com.android.chatroomclient.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

public class BaseActivity extends AppCompatActivity {

    public Intent service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataUtil.runningActivity = this;
        DataUtil.activityList.add(this);
        if (DataUtil.ipAddress == null){
            DataUtil.ipAddress = getIPAddress();
        }
        service = new Intent(this,NetworkService.class);
        startService(service);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public String getIPAddress(){
        String ip;
        ConnectivityManager conMann = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileNetworkInfo.isConnected()) {
            ip = getLocalIpAddress();
            return ip;
        }else if(wifiNetworkInfo.isConnected()) {
            int address;
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (isWifiApEnabled(wifiManager)){
                address = dhcpInfo.ipAddress;     //连接wifi后手机获得的IP地址
            }else {
                address = dhcpInfo.serverAddress;       //wifi的IP地址
            }
            ip = intToIp(address);
            return ip;
        }
        return null;
    }


    public String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni: nilist){
                ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address: ialist){
                    if (!address.isLoopbackAddress()) {
                        ipv4=address.getHostAddress();
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DataUtil.runningActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataUtil.activityList.remove(this);
    }

    private boolean isWifiApEnabled(WifiManager wifiManager){       //判断手机是否打开热点
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            Bitmap bitmap = null;
            switch (requestCode){
                case FileUtil.TAKE_PHOTO:
                    bitmap = BitmapFactory.decodeFile(FileUtil.file.getPath());
                    NetworkService.sendMessage(NetworkService.TYPE_FILE,FileUtil.filePath,FileUtil.getBitmapBytes(bitmap));
                    break;
                case FileUtil.CUT_PICTURE:
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("aspectX", 400);
                    intent.putExtra("aspectY", 400);
                    intent.setDataAndType(data.getData(),"image/*");
                    intent.putExtra("scale",true);
                    intent.putExtra("return-data", true);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(FileUtil.file));
                    startActivityForResult(intent,FileUtil.SHOE_PICTURE);
                    break;
                case FileUtil.SHOE_PICTURE:
                    bitmap = BitmapFactory.decodeFile(FileUtil.file.getPath());
                    NetworkService.sendMessage(NetworkService.TYPE_FILE,FileUtil.portraitPath,FileUtil.getBitmapBytes(bitmap));
                    DataUtil.userFragment.setPortrait(FileUtil.portraitPath);
                    break;
                case FileUtil.SEND_FILE:
                    bitmap = BitmapFactory.decodeFile(FileUtil.getTrueFileUri(data.getData()).getPath());
                    NetworkService.sendMessage(NetworkService.TYPE_FILE,FileUtil.filePath,FileUtil.getBitmapBytes(bitmap));
                    break;
                default:
                    break;
            }
        }
    }
}

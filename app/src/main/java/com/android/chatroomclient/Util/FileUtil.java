package com.android.chatroomclient.Util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.android.chatroomclient.Activity.BaseActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FileUtil {

    public static final int TAKE_PHOTO = 0;
    public static final int CUT_PICTURE = 1;
    public static final int SHOE_PICTURE = 2;
    public static final int SEND_FILE = 3;
    public static File file;

    public static String portraitPath;
    public static String filePath;

    public static boolean isSend = false;

    public static File createDirAndFile(String path,String filePath){
        String dirName = filePath.substring(0,filePath.lastIndexOf("/"));
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
        File dir = new File(path,dirName);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir,fileName);
        if (file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void deleteDirAndFile(File parentFile){
        if (parentFile.exists()){
            if (parentFile.isDirectory()){
                File[] files = parentFile.listFiles();
                for (File file : files){
                   deleteDirAndFile(file);
                }
            }
            parentFile.delete();
        }
    }

    public static void setDefaultPortrait(String account){
        try {
            String filePath = account + "/portrait.jpg";
            File file = new File(DataUtil.path,filePath);
            if (!file.exists()){
                file = createDirAndFile(DataUtil.path,filePath);
                FileOutputStream fis = new FileOutputStream(file);
                Bitmap bitmap = BitmapFactory.decodeResource(DataUtil.runningActivity.getResources(),R.mipmap.nav_icon);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] bytes = baos.toByteArray();
                fis.write(bytes);
                fis.flush();
                fis.close();
                baos.close();
                NetworkService.sendMessage(NetworkService.TYPE_FILE,filePath,getBitmapBytes(bitmap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void selectPortrait(BaseActivity activity){
        portraitPath = DataUtil.account + "/portrait.jpg";
        file = createDirAndFile(DataUtil.path,portraitPath);
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        activity.startActivityForResult(intent,CUT_PICTURE);
    }

    public static void selectFile(String type,String account){
        filePath = DataUtil.account + "/" + account + "/" + getRandomFileName(".jpg");
        Intent intent = null;
        switch (type){
            case "camera":
                String path = Environment.getExternalStorageDirectory().getPath();
                String fileName = "DCIM/Camera/IMG_" + System.currentTimeMillis() + ".jpg";
                file = createDirAndFile(path,fileName);
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,FileProviderUri(file));
                DataUtil.runningActivity.startActivityForResult(intent,TAKE_PHOTO);
                break;
            case "image":
                intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                DataUtil.runningActivity.startActivityForResult(intent,SEND_FILE);
                break;
            default:
                break;
        }
    }

    public static Bitmap getPortrait(String filePath){
        String path = DataUtil.path + "/" + filePath;
        File file = new File(path);
        if (file.exists()){
            return BitmapFactory.decodeFile(path);
        }else {
            if (isSend){
                isSend = false;
                return BitmapFactory.decodeResource(DataUtil.runningActivity.getResources(),R.mipmap.no_image);
            }else {
                NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,"<#GET_FILE#>"+filePath,null);
                return null;
            }
        }
    }

    public static void saveFile(String filePath,byte[] bytes){
        try {
            File file = createDirAndFile(DataUtil.path,filePath);
            DataOutputStream dou = new DataOutputStream(new FileOutputStream(file));
            dou.write(bytes);
            dou.flush();
            if (filePath.endsWith("portrait.jpg")){
                DataUtil.setMessage(DataUtil.TYPE_CHANGE_PORTRAIT,filePath);
            }else {
                DataUtil.setMessage(DataUtil.TYPE_GET_FINISH,null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFile(DataOutputStream dou,int type,String filePath,byte[] fileBytes) throws IOException{
        byte[] pathData = filePath.getBytes();
        dou.writeByte(type);
        dou.writeInt(pathData.length);
        dou.writeInt(fileBytes.length);
        dou.write(pathData);
        dou.write(fileBytes);
        dou.flush();
    }

    public static byte[] getBitmapBytes(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        return baos.toByteArray();
    }

    public static String getRandomFileName(String type){      //当前年月日时分秒+两个随机小写字母+后缀名
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        sb.append(simpleDateFormat.format(date));
        Random random = new Random();
        sb.append((char)(random.nextInt(27) + 'a'));
        sb.append((char)(random.nextInt(27) + 'a'));
        sb.append(type);
        return sb.toString() ;
    }

    public static Uri FileProviderUri(File file){
        return FileProvider.getUriForFile(DataUtil.runningActivity,"com.android.chatroomclient.fileprovider",file);
    }

    public static Uri getTrueFileUri(Uri uri){
        String[] strings = {MediaStore.Audio.Media.DATA};
        Cursor cursor = DataUtil.runningActivity.getContentResolver().query(uri,strings,null,null,null);
        cursor.moveToFirst();
        String filePath = cursor.getString(cursor.getColumnIndex(strings[0]));
        cursor.close();
        return Uri.parse(filePath);
    }

}

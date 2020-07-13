package com.android.chatroomclient.Activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.chatroomclient.Fragment.LoginFragment;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.DataUtil;

public class LoginActivity extends BaseActivity {

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DataUtil.path = getExternalFilesDir(null).getPath();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        DataUtil.loginFragment = new LoginFragment(this);
        setFragment(DataUtil.loginFragment);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"没有权限无法使用本程序！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void setBackIcon(final Fragment fragment){
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.loginFragment = new LoginFragment(LoginActivity.this);
                setFragment(DataUtil.loginFragment);
            }
        });
    }

    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.set_fragment,fragment).addToBackStack(null).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataUtil.createFragment = null;
        DataUtil.changeFragment = null;
        DataUtil.loginFragment = null;
    }
}

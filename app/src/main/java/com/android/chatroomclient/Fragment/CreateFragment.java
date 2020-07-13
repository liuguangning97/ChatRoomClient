package com.android.chatroomclient.Fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chatroomclient.Activity.LoginActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DBUtil;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.FileUtil;
import com.android.chatroomclient.Util.Widget.MyPasswordEdit;
import com.android.chatroomclient.Util.Widget.ViewUtil;

public class CreateFragment extends Fragment{

    private EditText accountEdit;
    private TextView accountMessage;
    private MyPasswordEdit myPasswordEdit;
    private TextView passwordMessage;
    private MyPasswordEdit myPasswordAgainEdit;
    private TextView passwordAgainMessage;
    private EditText nameEdit;
    private Button create;

    private LoginActivity activity;
    private String account;
    private String password;
    private String passwordAgain;
    private String name;

    public CreateFragment(LoginActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create,container,false);
        accountEdit = (EditText)view.findViewById(R.id.account_edit);
        accountMessage = (TextView)view.findViewById(R.id.account_message);
        myPasswordEdit = (MyPasswordEdit) view.findViewById(R.id.password_edit);
        passwordMessage = (TextView)view.findViewById(R.id.password_message);
        myPasswordAgainEdit = (MyPasswordEdit) view.findViewById(R.id.password_again_edit);
        passwordAgainMessage = (TextView)view.findViewById(R.id.password_again_message);
        nameEdit = (EditText)view.findViewById(R.id.name_edit);

        accountEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    accountEdit.setSelection(accountEdit.getText().length());
                    accountMessage.setVisibility(View.VISIBLE);
                    passwordMessage.setVisibility(View.INVISIBLE);
                    passwordAgainMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        accountEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    accountMessage.setVisibility(View.INVISIBLE);
                    myPasswordEdit.requestEditFocus();
                }
                return false;
            }
        });

        myPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    myPasswordEdit.setSelection(myPasswordEdit.getText().length());
                    accountMessage.setVisibility(View.INVISIBLE);
                    passwordMessage.setVisibility(View.VISIBLE);
                    passwordAgainMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        myPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    passwordMessage.setVisibility(View.INVISIBLE);
                    myPasswordAgainEdit.requestEditFocus();
                }
                return false;
            }
        });

        myPasswordAgainEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    myPasswordAgainEdit.setSelection(myPasswordAgainEdit.getText().length());
                    accountMessage.setVisibility(View.INVISIBLE);
                    passwordMessage.setVisibility(View.INVISIBLE);
                    passwordAgainMessage.setVisibility(View.VISIBLE);
                }
            }
        });
        myPasswordAgainEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    passwordAgainMessage.setVisibility(View.INVISIBLE);
                    nameEdit.requestFocus();
                }
                return false;
            }
        });

        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    nameEdit.setSelection(nameEdit.getText().length());
                    accountMessage.setVisibility(View.INVISIBLE);
                    passwordMessage.setVisibility(View.INVISIBLE);
                    passwordAgainMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        nameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    ViewUtil.closeSoftKeyboard();
                }
                return false;
            }
        });

        activity.setBackIcon(this);
        create = (Button)view.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                password = myPasswordEdit.getText();
                passwordAgain = myPasswordAgainEdit.getText();
                name = nameEdit.getText().toString();
                if (account.equals("") || password.equals("") || passwordAgain.equals("") || name.equals("")){
                    Toast.makeText(activity,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else if (!password.equals(passwordAgain)){
                    Toast.makeText(activity,"两次密码不同，请重新输入!",Toast.LENGTH_SHORT).show();
                    cleanEdit();
                }else if(!account.matches(DataUtil.accountRegex) || !password.matches(DataUtil.passwordRegex)){
                    Toast.makeText(activity,"账号或密码格式错误，请重新输入！",Toast.LENGTH_SHORT).show();
                    cleanEdit();
                }else {
                    NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,"<#CREATE#>" + account + "," + password + "," + name,null);
                }
            }
        });
        return view;
    }

    public void cleanEdit() {
        accountEdit.setText("");
        myPasswordEdit.setText("");
        myPasswordAgainEdit.setText("");
        nameEdit.setText("");
    }

    public void createSuccess(String msg){
        DBUtil.setUser(account,null,name,account + "/portrait.jpg",false);
        FileUtil.setDefaultPortrait(account);
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        activity.toolbar.setTitle("聊天室");
        activity.setFragment(DataUtil.loginFragment);
    }

    public void createFailed(String msg){
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        cleanEdit();
    }

}

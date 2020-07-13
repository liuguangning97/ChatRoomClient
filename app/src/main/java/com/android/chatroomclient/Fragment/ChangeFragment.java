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
import com.android.chatroomclient.Util.Widget.MyPasswordEdit;
import com.android.chatroomclient.Util.Widget.ViewUtil;

public class ChangeFragment extends Fragment {

    private EditText accountEdit;
    private MyPasswordEdit passwordOldEdit;
    private MyPasswordEdit passwordNewEdit;
    private TextView passwordMessage;
    private Button change;

    private LoginActivity activity;
    private String account;
    private String passwordNew;
    private String passwordOld;

    public ChangeFragment(LoginActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change,container,false);
        accountEdit = (EditText)view.findViewById(R.id.account_edit);
        passwordOldEdit = (MyPasswordEdit) view.findViewById(R.id.password_old_edit);
        passwordNewEdit = (MyPasswordEdit) view.findViewById(R.id.password_new_edit);
        passwordMessage = (TextView)view.findViewById(R.id.password_again_message);

        accountEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    accountEdit.setSelection(accountEdit.getText().length());
                    passwordMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        accountEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    passwordOldEdit.requestEditFocus();
                }
                return false;
            }
        });

        passwordOldEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    passwordOldEdit.setSelection(passwordOldEdit.getText().length());
                    passwordMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        passwordOldEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    passwordNewEdit.requestEditFocus();
                }
                return false;
            }
        });

        passwordNewEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    passwordNewEdit.setSelection(passwordNewEdit.getText().length());
                    passwordMessage.setVisibility(View.VISIBLE);
                }
            }
        });
        passwordNewEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    passwordMessage.setVisibility(View.INVISIBLE);
                    ViewUtil.closeSoftKeyboard();
                }
                return false;
            }
        });

        activity.setBackIcon(this);
        change = (Button)view.findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                passwordOld = passwordOldEdit.getText().toString();
                passwordNew = passwordNewEdit.getText().toString();
                if (account.equals("") || passwordOld.equals("") || passwordNew.equals("")){
                    Toast.makeText(activity,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else if (passwordOld.equals(passwordNew)){
                    Toast.makeText(activity,"两次密码相同，请重新输入!",Toast.LENGTH_SHORT).show();
                    cleanEdit();
                }else if (!passwordNew.matches(DataUtil.passwordRegex)){
                    Toast.makeText(activity,"新密码格式错误，请重新输入！",Toast.LENGTH_SHORT).show();
                    cleanEdit();
                }else {
                    NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,"<#CHANGE#>" + account + "," + passwordOld + "," + passwordNew,null);
                }
            }
        });
        return view;
    }

    public void cleanEdit() {
        accountEdit.setText("");
        passwordOldEdit.setText("");
        passwordNewEdit.setText("");
    }

    public void changeSuccess(String msg){
        DBUtil.updateUser(account,"password",passwordNew);
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        activity.toolbar.setTitle("聊天室");
        activity.setFragment(DataUtil.loginFragment);
    }

    public void changeFailed(String msg){
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        cleanEdit();
    }

}

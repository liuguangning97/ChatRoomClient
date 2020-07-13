package com.android.chatroomclient.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.Activity.LoginActivity;
import com.android.chatroomclient.Activity.MainActivity;
import com.android.chatroomclient.Util.Adapter.MySpinnerAdapter;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Server.NetworkService;
import com.android.chatroomclient.Util.DBUtil;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.Util.Widget.MyPasswordEdit;
import com.android.chatroomclient.Util.Widget.MySpinner;
import com.android.chatroomclient.Util.Widget.ViewUtil;
import com.android.chatroomclient.db.Friend;
import com.android.chatroomclient.db.User;

import org.litepal.LitePal;

import java.util.List;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private MySpinner spinner;
    private MyPasswordEdit myPasswordEdit;
    private TextView passwordMessage;
    private CheckBox remember;
    private Button create;
    private Button change;
    private Button login;

    private LoginActivity activity;
    private String account;
    private String password;
    private RecyclerView recyclerView;

    public LoginFragment(LoginActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        DataUtil.createFragment = new CreateFragment(activity);
        DataUtil.changeFragment = new ChangeFragment(activity);
        spinner = (MySpinner)view.findViewById(R.id.spinner);
        recyclerView = (RecyclerView) LayoutInflater.from(activity).inflate(R.layout.item_popup_window,null);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager);
        refreshSpinner();
        myPasswordEdit = (MyPasswordEdit) view.findViewById(R.id.password_edit);
        passwordMessage = (TextView)view.findViewById(R.id.password_message);

        spinner.accountEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    spinner.accountEdit.setSelection(spinner.accountEdit.getText().length());
                    passwordMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        spinner.accountEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
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
                    passwordMessage.setVisibility(View.VISIBLE);
                }
            }
        });
        myPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    ViewUtil.closeSoftKeyboard();
                    passwordMessage.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        remember = (CheckBox)view.findViewById(R.id.remember);
        create = (Button)view.findViewById(R.id.create);
        change = (Button)view.findViewById(R.id.change);
        login = (Button)view.findViewById(R.id.login);
        create.setOnClickListener(this);
        change.setOnClickListener(this);
        login.setOnClickListener(this);
        refreshLoginFragment();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create:
                activity.toolbar.setTitle("创建账号");
                activity.setFragment(DataUtil.createFragment);
                break;
            case R.id.change:
                activity.toolbar.setTitle("修改密码");
                activity.setFragment(DataUtil.changeFragment);
                break;
            case R.id.login:
                account = spinner.getText();
                password = myPasswordEdit.getText();
                if (account.equals("") || password.equals("")){
                    Toast.makeText(activity,"请输入完整信息！",Toast.LENGTH_SHORT).show();
                }else {
                    NetworkService.sendMessage(NetworkService.TYPE_MESSAGE,"<#LOGIN#>" + account +"," + password,null);
                }
                break;
            default:
                break;
        }
    }

    public void refreshSpinner(){
        List<User> userList = LitePal.order("order desc").find(User.class);
        MySpinnerAdapter adapter = new MySpinnerAdapter(userList,spinner);
        recyclerView.setAdapter(adapter);
        spinner.setRecyclerView(activity,recyclerView);
    }

    public void setEdit(User user){
        spinner.setText(user.getAccount());
        if (user.isRemember()){
            myPasswordEdit.setText(user.getPassword());
            remember.setChecked(true);
        }
        passwordMessage.setVisibility(View.INVISIBLE);
    }

    public void cleanEdit() {
        spinner.setText("");
        myPasswordEdit.setText("");
    }

    public void refreshLoginFragment(){
        activity.toolbar.setTitle("聊天室");
        activity.toolbar.setNavigationIcon(null);
        cleanEdit();
        DataUtil.loginFragment = this;

    }
    public void loginSuccess(String account,String userName,String filePath){
        DBUtil.setUser(account,password,userName,filePath,remember.isChecked());
        DataUtil.account = account;
        DataUtil.userName = userName;
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        activity.finish();
    }

    public void loginFailed(String msg){
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        cleanEdit();
    }

}

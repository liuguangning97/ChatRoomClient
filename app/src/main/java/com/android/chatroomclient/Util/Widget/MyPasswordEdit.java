package com.android.chatroomclient.Util.Widget;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.chatroomclient.R;

public class MyPasswordEdit extends RelativeLayout{

    public EditText passwordEdit;
    private ImageView inSee;
    private ImageView see;

    public MyPasswordEdit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_password_edit,this);
        passwordEdit = (EditText)findViewById(R.id.my_password_edit);
        inSee = (ImageView)findViewById(R.id.in_see);
        see = (ImageView)findViewById(R.id.see);
        inSee.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEdit.setInputType(128);             //设置密码可视
                inSee.setVisibility(GONE);
                see.setVisibility(VISIBLE);
            }
        });
        see.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEdit.setInputType(129);             //设置密码不可视
                see.setVisibility(GONE);
                inSee.setVisibility(VISIBLE);
            }
        });
    }

    public void setText(String text){
        passwordEdit.setText(text);
    }

    public void setSelection(int index){
        passwordEdit.setSelection(index);
    }

    public String getText(){
        return passwordEdit.getText().toString();
    }

    public void requestEditFocus(){
        passwordEdit.requestFocus();
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l){
        passwordEdit.setOnFocusChangeListener(l);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener l){
        passwordEdit.setOnEditorActionListener(l);
    }

}

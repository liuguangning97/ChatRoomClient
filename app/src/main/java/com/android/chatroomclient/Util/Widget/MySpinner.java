package com.android.chatroomclient.Util.Widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.Adapter.MySpinnerAdapter;
import com.android.chatroomclient.db.User;

import org.litepal.LitePal;

import java.util.List;

public class MySpinner extends RelativeLayout {

    public EditText accountEdit;
    private ImageView popup;
    public PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private Activity activity;

    public MySpinner(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_spinner,this);
        accountEdit = (EditText)findViewById(R.id.account);
        popup = (ImageView)findViewById(R.id.popup);
        popup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.closeSoftKeyboard();
                if (popupWindow != null &&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else {
                    popupWindow = new PopupWindow(recyclerView, 650,500,true);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    popupWindow.setAnimationStyle(R.style.popup_style);
                    popupWindow.showAsDropDown(accountEdit);
                    popupWindow.setTouchable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(false);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            setBackgroundAlpha(1.0f);
                        }
                    });
                    setBackgroundAlpha(0.5f);
                }
            }
        });
    }

    public void setRecyclerView(Activity activity,RecyclerView recyclerView){
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    private void setBackgroundAlpha(float bgAlpha){
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = bgAlpha;
        (activity).getWindow().setAttributes(params);
    }

    public void setText(String text){
        accountEdit.setText(text);
    }

    public String getText(){
        return accountEdit.getText().toString();
    }

}

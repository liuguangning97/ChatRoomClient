package com.android.chatroomclient.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.Activity.AddFriendActivity;
import com.android.chatroomclient.Activity.MainActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.Adapter.FriendAdapter;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.db.AddFriend;
import com.android.chatroomclient.db.Friend;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


public class FriendFragment extends Fragment {

    private RelativeLayout addFriend;
    private RecyclerView recyclerView;
    private MainActivity activity;
    private FriendAdapter adapter;
    private List<Friend> friendList = new ArrayList<>();

    public FriendFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        addFriend = (RelativeLayout)view.findViewById(R.id.add_friend);
        recyclerView = (RecyclerView) view.findViewById(R.id.friend_list);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager);
        refreshAdapter();

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddFriendActivity.class);
                startActivity(intent);
            }
        });
        activity.setRedDoc(AddFriend.class,addFriend).setBadgeGravity(Gravity.START|Gravity.TOP)
                .setGravityOffset(10,0,false).setExactMode(false);
        return view;
    }

    public void refreshAdapter(){
        friendList.clear();
        friendList = LitePal.where("userName = ?",DataUtil.userName).find(Friend.class);
        adapter = new FriendAdapter(friendList,activity);
        recyclerView.setAdapter(adapter);
    }

}

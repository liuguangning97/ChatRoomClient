package com.android.chatroomclient.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatroomclient.Activity.MainActivity;
import com.android.chatroomclient.R;
import com.android.chatroomclient.Util.Adapter.ChatAdapter;
import com.android.chatroomclient.Util.DataUtil;
import com.android.chatroomclient.db.Friend;
import com.android.chatroomclient.db.Talk;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private MainActivity activity;
    private RecyclerView chatRecycler;
    private ChatAdapter adapter;
    private List<Talk> talkList = new ArrayList<>();

    public ChatFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        chatRecycler = (RecyclerView)view.findViewById(R.id.chat_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        chatRecycler.setLayoutManager(manager);
        setTalkList();
        if (talkList.size() != 0){
            adapter = new ChatAdapter(talkList,activity);
            chatRecycler.setAdapter(adapter);
        }
        return view;
    }

    private void setTalkList(){
        talkList.clear();
        List<Friend> friendList = LitePal.where("userName = ?",DataUtil.userName).order("order desc").find(Friend.class);
        for (Friend friend : friendList){
            Talk talk = LitePal.where("userName = ? and friendName = ?",DataUtil.userName,friend.getFriendName()).findLast(Talk.class);
            if (talk != null){
                talkList.add(talk);
            }
        }
    }

    public void refreshAdapter(){
        setTalkList();
        adapter = new ChatAdapter(talkList,activity);
        chatRecycler.setAdapter(adapter);
    }

}

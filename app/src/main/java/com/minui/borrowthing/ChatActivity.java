package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minui.borrowthing.adapter.ChatAdapter;
import com.minui.borrowthing.model.ChatData;
import com.minui.borrowthing.model.ChatRoom;
import com.minui.borrowthing.model.item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    public  RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ChatData> chatList;
    //    private String nick = "nick1";
//    private String chatRoom = "chatRoom2";
    private EditText editChat;
    private ImageView btnSend;
    private DatabaseReference myRef;

    item item;

    ChatRoom chatRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        btnSend = findViewById(R.id.btnSend);
        editChat = findViewById(R.id.editChat);

        String type = "";
        item = (item) getIntent().getSerializableExtra("item");
        chatRoom = (ChatRoom) getIntent().getSerializableExtra("chatRoom");
        type = getIntent().getStringExtra("type");
        String opponentNickname = getIntent().getStringExtra("opponentNickname");

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        if(type.equals("seller")){
            // 내가 판매자 일 때
            ac.setTitle(chatRoom.getBuyerNickname());
        } else if(type.equals("buyer")){
            // 내가 구매자 일 때
            ac.setTitle(item.getNickname());
        } else {
            ac.setTitle(opponentNickname);
        }
        ac.setDisplayHomeAsUpEnabled(true);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editChat.getText().toString().trim(); //msg

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String updatedAt = formatter.format(calendar.getTime()).toString();

                if (msg.isEmpty()){
                    Toast.makeText(getApplication(), "메세지를 입력하세요.", Toast.LENGTH_SHORT ).show();
                    return;
                }

                if(msg != null) {
                    ChatData chat = new ChatData();

                    chat.setNickname(chatRoom.getMyId()+"");

                    chat.setMsg(msg);
                    chat.setUpdatedAt(updatedAt);
                    myRef.push().setValue(chat);

                    editChat.setText("");
                }


            }
        });



        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        chatList = new ArrayList<>();
        mAdapter = new ChatAdapter(chatList, ChatActivity.this, chatRoom.getMyId()+"");

        mRecyclerView.setAdapter(mAdapter);



        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("chat").child(chatRoom.getId()+"");



        //caution!!!

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHATCHAT", dataSnapshot.getValue().toString());
                ChatData chat = dataSnapshot.getValue(ChatData.class);
                ((ChatAdapter) mAdapter).addChat(chat);
                mRecyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public  boolean onSupportNavigateUp(){
        // 1. finish() 이용
//        finish();

        // 2. 기계의 백버튼 눌렀을 때 호출되는 콜백 함수를 이용
        onBackPressed();
        return true;
    }
}
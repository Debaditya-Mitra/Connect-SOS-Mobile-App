package com.chatdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageListAcitivity extends AppCompatActivity {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    TextView messageList;
    TextView noMessage;
    ArrayList<String> al = new ArrayList<>();
    int messages = 0;
    ProgressDialog pd;
    RecyclerView recyclerMessageList;
    String userId;
    ArrayList<MessageVo> MessageList;
    Context context;

 @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





                    }


    private void setAdapter() {
 /*       MessageAdapter messageAdapter = new MessageAdapter(context, MessageList, this);
        recyclerMessageList.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessageList.setAdapter(messageAdapter);*/
    }

}


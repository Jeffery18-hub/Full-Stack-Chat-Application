package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private String room_username_ = "";
    public static ListView lv_billboard_;
    public static ListView lv_messageBox_;
    public static ArrayAdapter adapterBoard;
    public static ArrayAdapter adapterMessageBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            room_username_ = extras.getString("room_user");
        }

        TextView textView = (TextView) findViewById(R.id.chat_room_name);
        textView.setText(room_username_);

        // Set adapter for listview of billboard
        lv_billboard_ = findViewById(R.id.simple_list_billboard);
        adapterBoard = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, MyAdapter.billboardArr);

        lv_billboard_.setAdapter(adapterBoard);

        // Set adapter for listview of messages
        lv_messageBox_ = findViewById(R.id.simple_list_messageBox);
        adapterMessageBox = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1,
                MyAdapter.messageBoxArr);
        lv_messageBox_.setAdapter(adapterMessageBox);

    }

    public void handleSendBtn(View view) {
        EditText editText = (EditText) findViewById(R.id.send_msg);
        String msg = editText.getText().toString();
        String[] username = room_username_.split(" ");
        String user = username[1];

        if (msg != null) {
            MainActivity.webSocket_.sendText(user + " " + msg);
        }
    }


}



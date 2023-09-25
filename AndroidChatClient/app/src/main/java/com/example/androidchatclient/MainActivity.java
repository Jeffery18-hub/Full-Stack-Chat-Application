package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String strUser = "";
    String strRoom = "";
    public static WebSocket webSocket_ = null;
    private static final String SERVER = "ws://10.0.2.2:8080/chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            webSocket_ = new WebSocketFactory().createSocket(SERVER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webSocket_.connectAsynchronously();

        // Register a listener to receive WebSocket events.
        webSocket_.addListener(new MyAdapter());

    }

    public void handleEnterRoomClick(View view) {

//        EditText editText1 = (EditText) findViewById(R.id.user_tx);
//        EditText editText2 = (EditText) findViewById(R.id.room_tx);
//        strUser = editText1.getText().toString();
//        strRoom = editText2.getText().toString();
//        webSocket_.sendText("join " + strUser + " " + strRoom);

        //show the alert dialog on the main activity when connection error happens
        if (MyAdapter.log_error != null) {
            AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this).setMessage(MyAdapter.log_error).create();
            alertDialog.show();
        } else {

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("room_user", strRoom + " " + strUser);
            startActivity(intent);
        }
    }
}
package com.example.androidchatclient;

import static com.example.androidchatclient.ChatActivity.adapterBoard;
import static com.example.androidchatclient.ChatActivity.adapterMessageBox;
import static com.example.androidchatclient.ChatActivity.lv_billboard_;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterOutputStream;

class MyAdapter extends WebSocketAdapter {

    public static ArrayList<String> messageBoxArr = new ArrayList<>();
    public static ArrayList<String> billboardArr = new ArrayList<>();
    public static String log_ = null;
    public static String log_error = null;


    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        // Called after the opening handshake succeeded.
        String upgrade = String.valueOf(headers.get("Upgrade"));
        String connection = String.valueOf(headers.get("Connection"));
        String accept = String.valueOf(headers.get("Sec-WebSocket-Accept"));
        Log.i("DD: handshake", "upgrade: " + upgrade + "\nconnection:" + connection +
                "accept: " + accept);

        if ( accept != null || accept!=""){
            log_ = "network connected!";
        }

    }


    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception
    {
        //Log.i("DD:", "Connection Error");
        log_error = "your network is not working well!";
    }


    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception
    // receive the message from the server
    {
        // parse JSON
        Gson gson = new Gson();

        HashMap<String,String> info = gson.fromJson(text, HashMap.class);
        String type = info.get("type");
        String room = info.get("room");
        String user = info.get("user");
        String message = info.get("message");

        Log.i("DD: JASON FORMAT", text);
        Log.i("DD: JASON PARSE", "type:"+type+" "+ "room:"+room+" "+ "username:"+user+" "+"msgContent:"+message);


        if (message != null){
            messageBoxArr.add(user +":" + message);}
        else{
            billboardArr.add(user+" "+ type + " " + room);
        }

        Log.i("DD:POST MSG","LOG POST START");
        ChatActivity.lv_billboard_.post(new Runnable() {
            public void run() {
                ChatActivity.adapterBoard.notifyDataSetChanged();
                ChatActivity.lv_billboard_.smoothScrollToPosition( adapterBoard.getCount());
            }
        });
        Log.i("DD:POST MSG","LOG POST END");

        Log.i("DD:POST MSG","MSG POST START");
        ChatActivity.lv_messageBox_.post(new Runnable() {
            public void run() {
                ChatActivity.adapterMessageBox.notifyDataSetChanged();
                ChatActivity.lv_messageBox_.smoothScrollToPosition( adapterMessageBox.getCount());
            }
        });
        Log.i("DD:POST MSG","MSG POST END");
    }

}
















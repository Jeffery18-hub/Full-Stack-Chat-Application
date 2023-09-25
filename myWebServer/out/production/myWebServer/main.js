"use strict";
let ws = new WebSocket("ws://localhost:8080");
let isConnected = false;
let isRoomCreated = false;

ws.onopen = function () {
  isConnected = true;
  alert("ws connected!")
}

ws.onmessage = function (event) {
  let msg = event.data;
  let msgObj = JSON.parse(msg);
  let father1 = document.getElementById("part2_div_whoEnterRoom");
  let father2 = document.getElementById("part2_div_allMessageToShow");

  if (msgObj.type === "join") {
    let son1 = document.createElement("p");
    son1.style.clear = "both"; // floating the elements are not allowed to float on both sides.
    son1.style.float = "left";
    son1.innerText = msgObj.user + " " + msgObj.type + " Room " + msgObj.room;
    father1.appendChild(son1);
  }
  if (msgObj.type === "leave") {
    let son2 = document.createElement("p");
    son2.style.clear = "both";
    son2.style.float = "left";
    son2.innerText = msgObj.user + " " + msgObj.type + " Room " + msgObj.room;
    father1.appendChild(son2);
  }
  if (msgObj.message.size !== 0) {
    let son3 = document.createElement("p");
    son3.style.clear = "both";
    son3.style.float = "left";
    son3.style.marginRight = "5px";
    son3.innerText = msgObj.user + ": " + msgObj.message;
    father2.appendChild(son3);
  }
}

// join <username> <roomName>
// leave <username> <roomName>
// <username> <the message that was sent>
let user = {};
user.userName = document.getElementById("usernameTextArea");
//console.log(user.name);
user.roomName = document.getElementById("roomTextArea");
user.message = document.getElementById("messageTextArea");


//username input add even listener
user.userName.addEventListener("keypress",handleUserEnterPressCB);
user.roomName.addEventListener("keypress",handleUserEnterPressCB);
user.message.addEventListener("keypress",handleMessageEnterPressCB);


//check username should not be empty and check room name should be lowercase and
//no spaces
function isUppercase(string_) {
  if ( string_ !== string_.toLowerCase()){
    return true;
  }
}

function handleUserEnterPressCB(event) {
  if ( isConnected && (event.keyCode === 13)){
    if( user.userName.value.length === 0) {
      alert("please enter username");}
    if ( user.roomName.value.length === 0) {
      alert("please enter room name");}
    if ( isUppercase(user.roomName.value)){
      alert("the room name should be lower case")
    }
    if ( (user.userName.value.length !== 0) && (!isUppercase(user.roomName.value)) &&(user.roomName.value.length !== 0)) {
      ws.send("join " + user.userName.value + " " + user.roomName.value);
      console.log("join " + user.userName.value + " " + user.roomName.value);
      isRoomCreated = true;
    }
  }

  if ( !isConnected && event.keyCode ===13){
    alert("The web server is not connected!");
  }
}


function handleMessageEnterPressCB(event) {
  if (isConnected  && isRoomCreated && (user.message.value.length!==0) && (event.keyCode===13)){
    ws.send(user.userName.value+" "+user.message.value);
    user.message.value = null;
  }
  if (isConnected && !isRoomCreated && (event.keyCode===13)){
    alert("room is not created");
  }
  if ( !isConnected && event.keyCode ===13){
    alert("The web server is not connected!");
  }
}


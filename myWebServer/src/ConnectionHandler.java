import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;

class ConnectionHandler implements Runnable {
    private Socket socket_;
    private OutputStream outputStream_;
    private InputStream inputStream_;
    private HttpRequest httpRequest_;
    private HttpResponse httpResponse_;
    private Room room_;

    public String getUserName_() {
        return userName_;
    }

    private String userName_;
    private boolean isLeaving = false;

    public ConnectionHandler(Socket socket) throws IOException {
        socket_ = socket;
        outputStream_ = socket_.getOutputStream();
        inputStream_ = socket_.getInputStream();
    }

    @Override
    public void run() {
        try {
            httpRequest_ = new HttpRequest(socket_);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            httpResponse_ = new HttpResponse(socket_);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        System.out.println("request protocol: " + httpRequest_.getHeader("Upgrade"));
//        System.out.println("request Sec-WebSocket-Key: " + httpRequest_.getHeader("Sec-WebSocket-Key"));

        // One time handshake and keep reading messages from client
        if (httpRequest_.getHeader("Sec-WebSocket-Key") != null) {
            try {
                handleWebSocket();// handshake here
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            sendAllToClient();// http response : Status line + response headers + files(html\css\js)
        }

        //Stop the connection to the client
        try {
            socket_.close();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleWebSocket() throws Exception {

        // First: handshake
        String key = httpRequest_.getHeader("Sec-WebSocket-Key");
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String encodeToStr = Base64.getEncoder().encodeToString(md.digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes()));
        System.out.println(encodeToStr);
        outputStream_.write(
                ("HTTP/1.1 101 Switching Protocols\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Accept: "
                        + encodeToStr + "\r\n\r\n").getBytes());
        outputStream_.flush();

        //After handshake, two-way communication
        try {
            while (true) {
                readMessageFromClient();
            }
        } catch (RuntimeException e) {
            // handle the leaving client
            room_.removeUser(this); // remove the user from this room
            String jasonMsg = JasonGenerator.createLeaveMessage(userName_, room_.getName());
            room_.sendMessage(jasonMsg, false); // send leaving message to everyone in this room
        }
    }

    private void sendAllToClient() {
        String url = httpRequest_.getUrl();
        System.out.println(url);
        File file = new File("src" + url);
        if (file.exists()) {
            //System.out.println( url+ "200 OK!");
            //response to the client
            httpResponse_.setFile(file);
            try {
                httpResponse_.sendAll();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // The message from client side is web socket message, parse the data frame and send
    // back the web socket message back to all the users in the same room
    private void readMessageFromClient() throws Exception {
        DataInputStream msgBytes = new DataInputStream(this.inputStream_);
        byte[] finToPayload = msgBytes.readNBytes(2); // data frame: first 2 bytes
        //FIN,should be 1
        boolean fin = (short) (finToPayload[0] >> 7) == 1;
        //OPCODE
        short opcode = (short) (finToPayload[0] & 0x0F);
        if (opcode == 8) { // 8-> bin: 1000 means the web socket on the client is closed
            System.out.println("Someone goes away");
        }
        //MASK,should be 1
        boolean mask = (short) (finToPayload[1] >> 7) == 1;
        //PAYLOAD LEN
        long payloadLen = finToPayload[1] & 0x7F;  // 0x7F-> decimal: 127 -> bin:0111 1111
        if (payloadLen == 126) {
            payloadLen = (long) msgBytes.readUnsignedShort();
        } else if (payloadLen == 127) {
            payloadLen = msgBytes.readLong();
        }


        byte[] maskKey = msgBytes.readNBytes(4); // 前四个字节
        byte[] result = msgBytes.readNBytes((int) payloadLen); // 假设100，前100个字节

        for (int i = 0; (long) i < payloadLen; ++i) {
            result[i] = (byte) (result[i] ^ maskKey[i % 4]); // i%4-> 0,1,2,3,0,1,2,3,0,1,2,3......
        }

        // turn bytes to string
        String strMsg = new String(result);
        String[] arrMsg = strMsg.split(" ", 2); // join username room  leave username room   user msg
        String type_ = arrMsg[0]; // join  leave  user
        if (type_.equals("join")) {
            var var1 = arrMsg[1].split(" ", 2);
            this.userName_ = var1[0];
            String roomName = var1[1];
            // get room : 1. if no such room,create one for me( Room constructor );
            // 2. if it exists, just return the existing room
            Room room = Room.getRoom(roomName);
            this.room_ = room;
            room.addUser(this); // send message to users in this function
        } else { // handle user message
            String userName = arrMsg[0];
            String msg = arrMsg[1];
            String jasonMsg = JasonGenerator.createMessage(this.userName_, this.room_.getName(), msg);
            this.room_.sendMessage(jasonMsg, true);
        }
    }


    public synchronized void generateWebSocketMsg(String jasonMsg) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream_);
        dataOutputStream.writeByte(129); // para-> 1 byte->1000 0001 -> opcode:text
        if (jasonMsg.length() < 126) {
            dataOutputStream.writeByte(jasonMsg.length());
        } else if (jasonMsg.length() < 65535) {
            dataOutputStream.writeByte(126); // fixed：126 -> 0111 1110
            dataOutputStream.writeShort(jasonMsg.length());// real length of message, occupy 2 bytes
        } else {
            dataOutputStream.writeByte(127); // fixed: 127 -> 1111 1111
            dataOutputStream.writeLong(jasonMsg.length()); // 8bytes
        }

        dataOutputStream.write(jasonMsg.getBytes());
        dataOutputStream.flush();
    }

}

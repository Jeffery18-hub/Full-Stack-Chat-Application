
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Room {
    public static HashMap<String, Room> roomMap_ = new HashMap<>();
    private String roomName_ = new String();
    private ArrayList<ConnectionHandler> users_ = new ArrayList();
    private ArrayList<String> roomLog_ = new ArrayList<>();

    private Room(String roomName) {
        roomName_ = roomName;
    }

    public static Room getRoom(String roomName) {
        synchronized (roomMap_) {
            Room room = roomMap_.get(roomName);
            if (room == null) {
                room = new Room(roomName);
                roomMap_.put(roomName, room);
            }
            return room;
        }
    }

    public void addUser(ConnectionHandler connectionHandler) throws IOException {
        Iterator iterator = users_.iterator();

        while (iterator.hasNext()) {
            ConnectionHandler handler = (ConnectionHandler) iterator.next();
            String joinMessage = JasonGenerator.createJoinMessage(handler.getUserName_(), roomName_);
            connectionHandler.generateWebSocketMsg(joinMessage);
        }

        iterator = roomLog_.iterator();

        while (iterator.hasNext()) {
            String var1 = (String) iterator.next();
            connectionHandler.generateWebSocketMsg(var1);
        }

        this.users_.add(connectionHandler);
        String jsMsg = JasonGenerator.createJoinMessage(connectionHandler.getUserName_(), roomName_);
        this.sendMessage(jsMsg, false);

    }

    public String getName() {
        return roomName_;

    }

    public synchronized void sendMessage(String jasonMsg, boolean isMessageLog) throws IOException {
        Iterator iterator = users_.iterator();
        while (iterator.hasNext()) {
            ConnectionHandler handler = (ConnectionHandler) iterator.next();
            handler.generateWebSocketMsg(jasonMsg);
        }

        if (isMessageLog) {
            this.roomLog_.add(jasonMsg);
            File log = new File(roomName_+"log.txt");
            try{
                if(log.exists()==false){
                    log.createNewFile();
                }
                PrintWriter out = new PrintWriter(new FileWriter(log, true));
                out.append(jasonMsg+"\n");
                out.close();
            }catch(IOException e){
                System.out.println("COULD NOT LOG!!");
            }
        }

    }

    public synchronized void removeUser(ConnectionHandler connectionHandler) {
        users_.remove(connectionHandler);

    }
}

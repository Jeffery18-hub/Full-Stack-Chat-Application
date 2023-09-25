import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AngryServer {
    public static void main(String[] args) throws IOException {
        System.out.println("wait for clients");
        ServerSocket ss = new ServerSocket(8080);

        while (true) {
            Socket socket = ss.accept(); // create a bunch of sockets
            ConnectionHandler connectionHandler = new ConnectionHandler(socket);
            Thread thread = new Thread(connectionHandler);
            thread.start();
        }    }
}

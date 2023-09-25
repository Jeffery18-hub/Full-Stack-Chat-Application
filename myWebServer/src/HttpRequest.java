import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

class HttpRequest {
    private String method;    //get or post
    private String url;       //path
    private String protocol;  //protocol
    // hashmap to store the headers information
    private HashMap<String, String> headers = new HashMap<String, String>();

    // information to the client
    private Socket socket;
    private InputStream in;

    //constructor
    public HttpRequest(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();

        /*
         * process to parse the client request：
         * 1. request line
         * 2. headers
         */
        parseRequest();
    }

    public void parseRequest() throws IOException {
        Scanner sc = new Scanner(this.in);

        //request line
        String firstLine = sc.nextLine();
        //System.out.println(firstLine);
        // spilt into 3 parts
        String[] data = firstLine.split("\\s");

        this.method = data[0];
        this.url = data[1];
        this.protocol = data[2];

        // request headers
        while (true) {
            String line = sc.nextLine();
            if ("".equals(line)) {
                break;
            }

            String[] data2 = line.split(":\\s");
            headers.put(data2[0], data2[1]);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String name) {  // get the value of header
        return headers.get(name);
    }
}

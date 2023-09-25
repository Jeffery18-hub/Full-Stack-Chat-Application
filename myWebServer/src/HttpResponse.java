import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class HttpResponse {
    private Socket socket;
    private OutputStream outputStream;

    //response file
    private File file;

    //response headers
    private HashMap<String, String> headers = new HashMap<String, String>();

    public HttpResponse(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
    }

    // send response to the client
    public void sendAll() throws IOException, InterruptedException {
        /*
         * 1. status line
         * 2. headers
         * 3. body--->file
         */
        sendStatusLine();
        sendHeaders();
        sendContent();
    }

    public void sendStatusLine() throws IOException {
        String line = "HTTP/1.1 200 ok";
        println(line);
    }

    public void sendHeaders() throws IOException {
        //iterate headers,send each header to the client
        for (Map.Entry<String, String> header : headers.entrySet()) {
            String line = header.getKey() + ": " + header.getValue();
            println(line);
        }

        //line feed to show that all the headers sent.
        println("");
    }

    public void sendContent() throws IOException, InterruptedException {
        FileInputStream fileInputStream = new FileInputStream(file);
        int bytesNum = 0;
        byte[] data = new byte[1024*1024]; // data buffer
        while (true) {
            bytesNum = fileInputStream.read(data);
            if (bytesNum == -1) {
                break;
            }
            outputStream.write(data, 0, bytesNum);
        }
    }

    //send one line string to client （end with ""）
    private void println(String line) throws IOException {
        outputStream.write(line.getBytes());
        outputStream.write(13); // /r carriage return
        outputStream.write(10); // /n new
    }


    /**
     * set file entity and meanwhile the two important headers：
     * Content-Type、Content-Length
     */
    public void setFile(File file) {
        this.file = file;
        putHeaderByFile();
    }

    /**
     * two important headers
     * Content-Type和Content-Length
     */
    private void putHeaderByFile() {
        String fileName = file.getName();

        //get the filename extension
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        String type = "text/" + ext;
        headers.put("Content-Type", type);
        headers.put("Content-Length", file.length() + "");
    }
}
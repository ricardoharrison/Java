import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlServer {

    private static final int DEFAULT_PORT = 8765;
    private static final int RESOURCE_POSITION = 1;
    private static final String DEFAULT_FILE = "index.html";

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server is listening on port " + DEFAULT_PORT + ".");

            while (true) {
                try (Socket clientConnection = server.accept();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(clientConnection.getInputStream()));
                        OutputStream outputStream = clientConnection.getOutputStream()) {

                    String header = reader.readLine();
                    String info = extractInfo(header);

                    String filePath = info.equals("/") ? DEFAULT_FILE : info.substring(1);
                    File file = new File(filePath);
                    String absolutePath = file.getAbsolutePath();

                    serveFile(absolutePath, outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serveFile(String filePath, OutputStream outputStream) throws IOException {
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            String contentType = Files.probeContentType(Paths.get(filePath));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            try (InputStream fileInputStream = new FileInputStream(file)) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                writer.write("HTTP/1.1 200 OK\r\n");
                writer.write("Content-Type: " + contentType + "\r\n");
                writer.write("Content-Length: " + file.length() + "\r\n");
                writer.write("Connection: close\r\n");
                writer.write("\r\n");
                writer.flush();

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        } else {
            send404NotFound(outputStream);
        }
    }

    private static void send404NotFound(OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write("HTTP/1.1 404 Not Found\r\n");
        writer.write("Content-Type: text/html\r\n");
        writer.write("Connection: close\r\n");
        writer.write("\r\n");
        writer.write("<html><body><h1>404 Not Found</h1></body></html>");
        writer.flush();
    }

    private static String extractInfo(String header) {
        return header.split(" ")[RESOURCE_POSITION];
    }
}

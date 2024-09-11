package htmlserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HtmlServer {

    private static final int DEFAULT_PORT = 8765;
    private static final int RESOURCE_POSITION = 1;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(DEFAULT_PORT);

        while (true) {
            Socket clientConnection = server.accept();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            clientConnection.getInputStream()));
            String header = reader.readLine();
            System.out.println("Request: " + header);
            String info = extractInfo(header);
            String html = generateWebsite(info);

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            clientConnection.getOutputStream()));

            writer.write(html);
            writer.flush();

            reader.close();
            writer.close();
            clientConnection.close();
        }
    }

    private static String generateWebsite(String info) {
        String str = "HTTP/1.1 200 OK\n\n";
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(info))) {
            while ((line = reader.readLine()) != null) {
                str += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            str = "HTTP/1.1 404 Not Found\n\n";
        }
        return str;
    }

    private static String extractInfo(String header) {
        return header.split(" ")[RESOURCE_POSITION];
    }
}
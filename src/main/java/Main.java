import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LoggingPermission;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    //  Uncomment this block to pass the first stage
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
          serverSocket = new ServerSocket(port);
          // Since the tester restarts your program quite often, setting SO_REUSEADDR
          // ensures that we don't run into 'Address already in use' errors
          //serverSocket.setReuseAddress(true);
          // Wait for connection from client.
            while(true) {
                clientSocket = serverSocket.accept();
                Thread t = new Thread(new ClientSocketHandler(clientSocket, logger));
                t.start();
            }

        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        } finally {
          try {
            if (serverSocket != null) {
              serverSocket.close();
            }
          } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
          }
        }
  }
}


class ClientSocketHandler implements Runnable {

    private final Socket clientSocket;
    private final Logger logger;
    public ClientSocketHandler(Socket clientSocket, Logger logger) {
        this.clientSocket = clientSocket;
        this.logger = logger;
    }
    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                ) {
            System.out.println("Connected");
            System.out.println(this.clientSocket.getPort());
            String message;
            List<String> strings = new ArrayList<>();
            Serializer serializer = new Serializer(logger);


            Commands commands = serializer.handleInput(reader);

            if(commands.getStrValue() != null) {
                System.out.println(commands.getStrValue());
            } else if(commands.getListValue() != null) {
                var list = commands.getListValue();
                String firstCommand = list.get(0).getStrValue();
                if(firstCommand.equals("PING")) {
                    writer.write("+PONG\r\n");
                    writer.flush();
                } else if(firstCommand.equals("ECHO")) {
                    System.out.println("here");
                    String secondCommand = list.get(1).getStrValue();
                    StringBuilder response = new StringBuilder(secondCommand.length() + 2);
                    response.append(secondCommand);
                    response.append("\r\n");
                    writer.write("+" + secondCommand + "\r\n");
                    writer.flush();
                }

            }


        } catch (IOException e) {
            System.out.println("exception : " + e.getMessage());
        } finally {
            try {
                if(clientSocket != null && !clientSocket.isClosed() ) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("error closing client socket " + e.getMessage());
            }
        }

    }
}



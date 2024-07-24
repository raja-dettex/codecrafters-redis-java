import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientSocketHandler implements Runnable {

    private MemoryMap map;

    public boolean isRunning = true;

    private Socket clientSocket;
    private final Logger logger;
    public ClientSocketHandler(Socket clientSocket, Logger logger, MemoryMap map) {
        this.clientSocket = clientSocket;
        this.logger = logger;
        this.map = map;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public void setMap(MemoryMap map) {
        if (map != null) {
            this.map = map;
        }
    }
    @Override
    public void run() {
        isRunning = true;
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        ) {
            System.out.println("Connected");
            System.out.println(this.clientSocket.getPort());
            String message;
            List<String> strings = new ArrayList<>();
            Serializer serializer = new Serializer(logger);


            Dtype dtype = serializer.handleInput(reader);
            if(dtype.getStrValue() != null) {
                System.out.println(dtype.getStrValue());
            } else if(dtype.getListValue() != null) {
                var list = dtype.getListValue();
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
                } else if(firstCommand.equals("SET")) {
                    if(map.set(list.get(1), list.get(2)) == 1) {
                        writer.write("+OK\r\n");
                        writer.flush();
                    } else {
                        writer.write("-ERROR\r\n");
                        writer.flush();
                    }
                    writer.flush();
                } else if(firstCommand.equals("GET")) {
                    String val = map.get(list.get(1));
                    if(val == null) {
                        writer.write("-ERROR GET key:" + list.get(1).getStrValue() +  " does not exit in key table" + "\r\n");
                        writer.flush();
                        throw new RMemException("key does not exist");
                    }
                    writer.write(serializer.toBulkString(val));
                    writer.flush();

                }

            }


        } catch (IOException | RMemException | NullPointerException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if(clientSocket != null && !clientSocket.isClosed() ) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("error closing client socket " + e.getMessage());
            }
        }
        isRunning = false;

    }

}

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Serializer {
    private final Logger logger;

    public Serializer(Logger logger) {
        this.logger = logger;
    }

    public Commands handleInput(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine().trim();
        return parseLine(line, reader);
    }

    private Commands parseLine(String line, BufferedReader reader) throws IOException {
        Commands command = new Commands();
        switch (line.charAt(0)) {
            case '*':
                // Handle arrays
                int length = Integer.parseInt(line.substring(1));
                //System.out.println(length);
                List<Commands> list = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    line = reader.readLine();
                    //System.out.println("line is " + line);
                    Commands subCommand = parseLine(line, reader);
                    list.add(subCommand);
                }
                command.setListValue(list);
                break;

            case '$':
                // Handle bulk strings
                int bulkLength = Integer.parseInt(line.substring(1));
                StringBuilder bulkString = new StringBuilder();
                String str = reader.readLine();
                bulkString.append(str.toCharArray(), 0, bulkLength);

                command.setStrValue(bulkString.toString());

                break;

            case ':':
                // Handle integers
                command.setIntValue(Integer.parseInt(line.trim().substring(1)));
                break;

            case '+':
                // Handle simple strings
                command.setStrValue(line.trim().substring(1));
                break;

            case '-':
                // Handle errors
                command.setStrValue("Error: " + line.substring(1));
                break;

            default:

                break;
        }
        return command;
    }
}
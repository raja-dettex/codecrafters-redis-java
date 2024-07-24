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

    public String toBulkString(String s){
        int length = s.length();
        StringBuilder builder = new StringBuilder();
        builder.append("$").append(length).append("\r\n").append(s).append("\r\n");
        return builder.toString();
    }

    public Dtype handleInput(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine().trim();
        return parseLine(line, reader);
    }

    private Dtype parseLine(String line, BufferedReader reader) throws IOException {
        Dtype command = new Dtype();
        switch (line.charAt(0)) {
            case '*':
                // Handle arrays
                int length = Integer.parseInt(line.substring(1));
                //System.out.println(length);
                List<Dtype> list = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    line = reader.readLine();
                    //System.out.println("line is " + line);
                    Dtype subCommand = parseLine(line, reader);

                    list.add(subCommand);
                }
                command.setListValue(list);
                command.setType('*');
                break;

            case '$':
                // Handle bulk strings
                int bulkLength = Integer.parseInt(line.substring(1));
                StringBuilder bulkString = new StringBuilder();
                String str = reader.readLine();
                bulkString.append(str.toCharArray(), 0, bulkLength);

                command.setStrValue(bulkString.toString());
                command.setType('$');
                break;

            case ':':
                // Handle integers
                command.setIntValue(Integer.parseInt(line.trim().substring(1)));
                command.setType(':');
                break;

            case '+':
                // Handle simple strings
                command.setStrValue(line.trim().substring(1));
                command.setType('+');
                break;

            case '-':
                // Handle errors
                command.setStrValue("Error: " + line.substring(1));
                command.setType('-');
                break;

            default:

                break;
        }
        return command;
    }
}
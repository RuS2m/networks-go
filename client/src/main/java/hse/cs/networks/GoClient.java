package hse.cs.networks;

import hse.cs.networks.common.MessageBuilder;
import hse.cs.networks.common.MessageBuilder.ServerCommands;
import hse.cs.networks.game.GameHistory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import static hse.cs.networks.common.MessageBuilder.*;

public class GoClient {
    public static void main(String[] args) {

        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            Console console = System.console();
            String text;

            do {
                text = console.readLine();
                text = text.replaceAll(" ", MESSAGE_DELIMITER);

                writer.println(text);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String command = reader.readLine();

                if (command != null) {
                    if (isRightCommand(command, ServerCommands.WRONG_COMMAND)) {
                        System.out.println("Invalid hse.cs.networks.command: " + command.split(MESSAGE_DELIMITER)[1]);
                    } else if (isRightCommand(command, ServerCommands.NO_USER)) {
                        System.out.println("There is no user with username " + command.split(MESSAGE_DELIMITER)[1] + ".");
                    } else if (isRightCommand(command, ServerCommands.FAILED_AUTH)) {
                        System.out.println("Wrong password. Try again.");
                    } else if (isRightCommand(command, ServerCommands.ALREADY_USER)) {
                        System.out.println("There is already user with username " + command.split(MESSAGE_DELIMITER)[1] + ". Try another one.");
                    } else if (isRightCommand(command, ServerCommands.SUCCESS_AUTH)) {
                        System.out.println("Successful authorization.");
                    } else if (isRightCommand(command, ServerCommands.BOARD)) {
                        var gameHistory = command.split(MESSAGE_DELIMITER)[1];
                        var board = GameHistory.fromMovesRecordsQueue(Arrays.asList(gameHistory.split(MessageBuilder.MESSAGE_ARRAY_DELIMITER))).toBoard();
                        System.out.println(board);
                    }
                }

            } while (!text.equals("QUIT"));

            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

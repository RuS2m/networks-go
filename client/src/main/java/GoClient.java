import game.GameHistory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

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
                text = console.readLine("Enter command: ");

                writer.println(text);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String command = reader.readLine();

                System.out.println(command);
                if (command != null) {
                    if (command.startsWith("BOARD") && command.split("#").length > 1) {
                        var gameHistory = command.split("#")[1];
                        var board = GameHistory.fromMovesRecordsQueue(Arrays.asList(gameHistory.split("\\$"))).toBoard();
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

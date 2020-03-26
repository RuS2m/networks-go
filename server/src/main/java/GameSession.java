import game.*;
import utils.UnsupportedCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

public class GameSession {

    private PrintWriter writer;

    private BufferedReader reader;

    private Connection connection;

    private GameHistory gameHistory;

    public GameSession(PrintWriter writer, BufferedReader reader, Connection connection) {
        this.writer = writer;
        this.reader = reader;
        this.connection = connection;
        this.gameHistory = new GameHistory();
    }

    public void start() throws IOException {
        do {
            var command = reader.readLine();
            if (command != null) {
                try {
                    if (command.startsWith("PASS")) {
                        this.gameHistory.addMove(new Pass());
                    } else if (command.startsWith("MOVE")) {
                        System.out.println("inside MOVE");
                        this.gameHistory.addMove(parseMoveCommand(command));
                    } else if (command.startsWith("QUIT")) {
                        break;
                    }
                } catch (UnsupportedCommandException e) {
                    System.out.println(e.getMessage());
                }
                writer.println("BOARD#" + String.join("$", this.gameHistory.movesRecordsQueue()));
            }
        } while (true);
    }

    private static Move parseMoveCommand(String moveCommand) throws UnsupportedCommandException {
        var commandTokens = moveCommand.split("#");
        if (commandTokens.length == 2) {
            System.out.println("user pass: " + commandTokens[1]);
            return new Pass();
        }
        if (commandTokens.length > 3) {
            System.out.println("user move: " + commandTokens[1]);
            var xPosition = -1;
            var yPosition = -1;
            try {
                xPosition = Integer.parseInt(commandTokens[2]);
                yPosition = Integer.parseInt(commandTokens[3]);
            } catch (NumberFormatException e) {
                // nop
            }
            if (xPosition != -1 && yPosition != -1) {
                System.out.println("move: x: " + xPosition + ", y: " + yPosition);
                return new StoneMove(new Board.Stone(xPosition, yPosition, 0));
            }
        }
        throw new UnsupportedCommandException("Unknown command: " + moveCommand);
    }
}

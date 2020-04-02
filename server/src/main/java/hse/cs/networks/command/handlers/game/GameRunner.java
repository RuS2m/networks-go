package hse.cs.networks.command.handlers.game;

import hse.cs.networks.command.handlers.CommandHandler;
import hse.cs.networks.game.*;
import hse.cs.networks.utils.ServerInternalException;
import hse.cs.networks.utils.UnsupportedCommandException;
import static hse.cs.networks.common.MessageBuilder.*;
import static hse.cs.networks.common.MessageBuilder.ClientCommands.*;
import static hse.cs.networks.common.MessageBuilder.ServerCommands.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class GameRunner extends CommandHandler {

    private int gameOrder;

    private String username;

    private long lobbyId;

    private long sessionId;

    private GameQueryService gameQueryService;

    private GameObserver gameObserver;

    public GameRunner(PrintWriter writer,
                      BufferedReader reader,
                      Connection connection,
                      String username,
                      long lobbyId,
                      long sessionId,
                      int gameOrder) {
        super(writer, reader, connection);
        this.gameQueryService = new GameQueryService(connection);
        this.gameObserver = new GameObserver(sessionId, gameOrder, writer, this.gameQueryService);
    }

    @Override
    public void handle() throws IOException {
        do {
            var command = this.getReader().readLine();
            if (command != null) {
                var message = "";
                try {
                    if ((isRightCommand(command, QUIT))) {
                        if (this.gameObserver != null) {
                            this.gameObserver.interrupt();
                        }
                        break;
                    } else if (isRightCommand(command, PASS)) {
                        message = handleMoveCommand(new Pass());
                    } else if (isRightCommand(command, MOVE)) {
                        Move move = new Pass();
                        boolean isParsed = true;
                        try {
                            move = parseMoveCommand(command);
                        } catch (UnsupportedCommandException e) {
                            isParsed = false;
                            message = message(WRONG_COMMAND, command.replaceAll(MESSAGE_DELIMITER, " "));
                        }
                        if (isParsed) {
                            message = handleMoveCommand(parseMoveCommand(command));
                        }
                    }
                    else {
                        message = message(WRONG_COMMAND, command.replaceAll(MESSAGE_DELIMITER, " "));
                    }
                } catch (ServerInternalException e) {
                    System.out.println(e.getMessage());
                }
                if (!message.isEmpty()) {
                    this.getWriter().println(message);
                }
            }
        } while (true);
    }

    private String handleMoveCommand(Move move) throws ServerInternalException {
        var isRightGameOrder = false;
        try {
            isRightGameOrder = this.gameQueryService.isRightGameOrder(sessionId, gameOrder);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        if (isRightGameOrder) {
            var board = "";
            try {
                this.gameQueryService.makeMove(sessionId, move);
                board = this.gameQueryService.history(sessionId);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                throw new ServerInternalException("Something went wrong during command execution");
            }
            return message(BOARD, board);
        } else {
            return message(FAILED_MOVE, "not your turn");
        }
    }

    private static Move parseMoveCommand(String moveCommand) throws UnsupportedCommandException {
        var xPosition = -1;
        var yPosition = -1;
        try {
            xPosition = Integer.parseInt(moveCommand.split(MESSAGE_DELIMITER)[1]);
            yPosition = Integer.parseInt(moveCommand.split(MESSAGE_DELIMITER)[2]);
        } catch (NumberFormatException e) {
            // nop
        }
        if (xPosition != -1 && yPosition != -1) {
            System.out.println("move: x: " + xPosition + ", y: " + yPosition);
            return new StoneMove(new Board.Stone(xPosition, yPosition, 0));
        }
        throw new UnsupportedCommandException("Unknown command: " + moveCommand);
    }
}

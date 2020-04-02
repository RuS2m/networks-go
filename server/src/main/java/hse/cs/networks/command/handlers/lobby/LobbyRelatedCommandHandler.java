package hse.cs.networks.command.handlers.lobby;

import hse.cs.networks.command.handlers.CommandHandler;
import hse.cs.networks.utils.ServerInternalException;

import static hse.cs.networks.common.MessageBuilder.*;
import static hse.cs.networks.common.MessageBuilder.ClientCommands.*;
import static hse.cs.networks.common.MessageBuilder.ServerCommands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class LobbyRelatedCommandHandler extends CommandHandler {

    private String username;

    private Long lobbyId;

    private boolean isSessionActive = false;

    private Long sessionId;

    private int gameOrder;

    private LobbyQueryService lobbyQueryService;

    private LobbyObserver lobbyObserver;

    public LobbyRelatedCommandHandler(
            PrintWriter writer,
            BufferedReader reader,
            Connection connection,
            String username) {
        super(writer, reader, connection);
        this.lobbyQueryService = new LobbyQueryService(connection);
        this.username = username;
    }

    @Override
    public void handle() throws IOException {
        do {
            var command = this.getReader().readLine();
            if (command != null) {
                var message = "";
                try {
                    if ((isRightCommand(command, QUIT))) {
                        break;
                    }
                    if (this.lobbyId != null) {
                        if (isRightCommand(command, READY)) {
                            message = handleReadyCommand(command);
                        } else if (isRightCommand(command, START)) {
                            message = handleStartCommand(command);
                        } else if (isRightCommand(command, QUIT_LOBBY)) {
                            message = handleQuitCommand(command);
                        } else {
                            message = message(WRONG_COMMAND, command.replaceAll(MESSAGE_DELIMITER, " "));
                        }
                    } else {
                        if (isRightCommand(command, JOIN_LOBBY)) {
                            message = handleJoinCommand(command);
                        } else if (isRightCommand(command, CREATE_LOBBY)) {
                            message = handleCreateCommand(command);
                        } else {
                            message = message(WRONG_COMMAND, command.replaceAll(MESSAGE_DELIMITER, " "));
                        }
                    }
                } catch (ServerInternalException e) {
                    System.out.println(e.getMessage());
                }
                if (!message.isEmpty()) {
                    this.getWriter().println(message);
                }
            }
        } while (!this.isSessionActive);
    }

    private String handleJoinCommand(String command) throws ServerInternalException {
        var lobbyId = command.split(MESSAGE_DELIMITER)[1];
        var parsedLobbyId = Long.parseLong(lobbyId);
        boolean isLobbyExist = false;
        boolean isLobbyFull = false;
        if (this.lobbyId != null) {
            return message(FAILED_JOIN, lobbyId, "You already in a lobby. Quit first, for joining another one");
        }
        try {
            isLobbyExist = this.lobbyQueryService.isLobbyExist(parsedLobbyId);
            isLobbyFull = this.lobbyQueryService.isLobbyFull(parsedLobbyId);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        if (!isLobbyExist) {
            return message(FAILED_JOIN, lobbyId, "There is no lobby with such id");
        } else if (isLobbyFull) {
            return message(FAILED_JOIN, lobbyId, "Lobby is full. Try to get into another one");
        } else {
            try {
                this.lobbyQueryService.joinLobby(parsedLobbyId, this.username);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                throw new ServerInternalException("Something went wrong during command execution");
            }
            this.lobbyId = parsedLobbyId;
            if (this.lobbyObserver != null) {
                this.lobbyObserver.interrupt();
            }
            this.lobbyObserver = new LobbyObserver(this.lobbyId, this.getWriter(), this.lobbyQueryService);
            this.lobbyObserver.start();
            return message(SUCCESS_JOIN, lobbyId);
        }
    }

    private String handleCreateCommand(String command) throws ServerInternalException {
        var lobbyName = command.split(MESSAGE_DELIMITER)[1];
        if (this.lobbyId != null) {
            return message(FAILED_JOIN, "You already in a lobby. Quit first, for joining another one");
        }
        long lobbyId = 0L;
        try {
            lobbyId = this.lobbyQueryService.createLobby(lobbyName, username);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        this.lobbyId = lobbyId;
        if (this.lobbyObserver != null) {
            this.lobbyObserver.interrupt();
        }
        this.lobbyObserver = new LobbyObserver(this.lobbyId, this.getWriter(), this.lobbyQueryService);
        this.lobbyObserver.start();
        return message(SUCCESS_JOIN, Long.toString(lobbyId));
    }

    private String handleQuitCommand(String command) throws ServerInternalException {
        try {
            this.lobbyQueryService.quitLobby(username);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        if (this.lobbyObserver != null) {
            this.lobbyObserver.interrupt();
        }
        this.sessionId = null;
        this.lobbyId = null;
        return message(DEBUG_INFO, "Successful quit");
    }

    private String handleReadyCommand(String command) throws ServerInternalException {
        try {
            this.lobbyQueryService.setReady(username);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        return message(DEBUG_INFO, "Successful ready");
    }

    private String handleStartCommand(String command) throws ServerInternalException {
        if (this.lobbyObserver != null && this.lobbyObserver.isAllReady()) {
            try {
                this.sessionId = this.lobbyQueryService.startSession(this.lobbyId);
                this.gameOrder = this.lobbyQueryService.gameOrder(this.lobbyId, this.username);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                throw new ServerInternalException("Something went wrong during command execution");
            }
            System.out.println(sessionId);
            return message(GAME_STARTED);
        } else {
            return message(FAILED_START);
        }
    }

    public boolean isSessionActive() {
        return isSessionActive;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public int getGameOrder() {
        return gameOrder;
    }
}

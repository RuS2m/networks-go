package command.handlers.auth;

import command.MessageBuilder.*;
import command.handlers.CommandHandler;
import utils.ServerInternalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import static command.MessageBuilder.*;

public class Authenticator extends CommandHandler {

    private AuthenticationQueryService authenticationQueryService;

    public Authenticator(PrintWriter writer, BufferedReader reader, Connection connection) {
        super(writer, reader, connection);
        this.authenticationQueryService = new AuthenticationQueryService(connection);
    }

    @Override
    public void handle() throws IOException {
        do {
            var command = this.getReader().readLine();
            if (command != null) {
                var message = "";
                try {
                    if (isRightCommand(command, ClientCommands.LOGIN)) {
                        message = handleLoginCommand(command);
                    } else if (isRightCommand(command, ClientCommands.SIGN_UP)) {
                        message = handleSignUpCommand(command);
                    } else if ((isRightCommand(command, ClientCommands.QUIT))) {
                        break;
                    } else {
                        message = message(ServerCommands.WRONG_COMMAND, command);
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

    private String handleLoginCommand(String command) throws ServerInternalException {
        var username = command.split(MESSAGE_DELIMITER)[1];
        var password = command.split(MESSAGE_DELIMITER)[2];
        boolean isUsernameExist = false;
        boolean areCredentialsCorrect = false;
        try {
            isUsernameExist = this.authenticationQueryService.isUsernameExist(username);
            areCredentialsCorrect = this.authenticationQueryService.areCredentialsCorrect(username, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        if (!isUsernameExist) {
            return message(ServerCommands.NO_USER, username);
        } else if (!areCredentialsCorrect) {
            return message(ServerCommands.FAILED_AUTH);
        } else {
            return message(ServerCommands.SUCCESS_AUTH);
        }
    }

    private String handleSignUpCommand(String command) throws ServerInternalException {
        var username = command.split(MESSAGE_DELIMITER)[1];
        var password = command.split(MESSAGE_DELIMITER)[2];
        boolean isUsernameExist = false;
        try {
            isUsernameExist = this.authenticationQueryService.isUsernameExist(username);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new ServerInternalException("Something went wrong during command execution");
        }
        if (isUsernameExist) {
            return message(ServerCommands.ALREADY_USER, username);
        } else {
            try {
                this.authenticationQueryService.signUp(username, password);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                throw new ServerInternalException("Something went wrong during command execution");
            }
            return message(ServerCommands.SUCCESS_AUTH);
        }
    }
}

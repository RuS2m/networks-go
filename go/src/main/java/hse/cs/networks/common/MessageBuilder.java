package hse.cs.networks.common;

import java.util.List;

public class MessageBuilder {

    public MessageBuilder() {
        // nop
    }

    public static String MESSAGE_DELIMITER = "#";

    public static String MESSAGE_ARRAY_DELIMITER = "\\$";

    public static boolean isRightCommand(String textCommand, ClientCommands command) {
        return textCommand.startsWith(command.commandText)
                && command.argumentsNumbers.contains(textCommand.split(MESSAGE_DELIMITER).length - 1);
    }

    public static boolean isRightCommand(String textCommand, ServerCommands command) {
        return textCommand.startsWith(command.commandText)
                && command.argumentsNumbers.contains(textCommand.split(MESSAGE_DELIMITER).length - 1);
    }

    public static String message(ServerCommands command, String... arguments) {
        var messageTokens = new String[arguments.length + 1];
        messageTokens[0] = command.getCommandText();
        for (var i = 1; i != messageTokens.length; ++i) {
            messageTokens[i] = arguments[i - 1];
        }
        return String.join(MESSAGE_DELIMITER, messageTokens);
    }

    public static String message(ClientCommands command, String... arguments) {
        var messageTokens = new String[arguments.length + 1];
        messageTokens[0] = command.getCommandText();
        for (var i = 1; i != messageTokens.length; ++i) {
            messageTokens[i] = arguments[i - 1];
        }
        return String.join(MESSAGE_DELIMITER, messageTokens);
    }

    public static String message(String... messageTokens) {
        return String.join(MESSAGE_DELIMITER, messageTokens);
    }

    public enum ServerCommands {
        WRONG_COMMAND("WRONG_COMMAND", 1),
        FAILED_AUTH("FAILED_AUTH", 0),
        NO_USER("NO_USER", 1),
        ALREADY_USER("ALREADY_USER", 1),
        SUCCESS_AUTH("SUCCESS_AUTH", 0),
        FAILED_JOIN("FAILED_JOIN", List.of(1, 2)),
        SUCCESS_JOIN("SUCCESS_JOIN", 1),
        USER_JOINED("USER_JOINED", 1),
        USER_QUIT("USER_QUIT", 1),
        USER_READY("USER_READY", 1),
        USER_NOT_READY("USER_NOT_READY", 1),
        ALL_READY("START", 0),
        DEBUG_INFO("DEBUG_INFO", 1),
        FAILED_START("FAILED_START", 0),
        GAME_STARTED("GAME_STARTED", 0),
        BOARD("BOARD", 1);

        private String commandText;

        private List<Integer> argumentsNumbers;

        ServerCommands(String commandText, List<Integer> argumentsNumbers) {
            this.commandText = commandText;
            this.argumentsNumbers = argumentsNumbers;
        }

        ServerCommands(String commandText, int argumentNumber) {
            this.commandText = commandText;
            this.argumentsNumbers = List.of(argumentNumber);
        }

        public String getCommandText() {
            return commandText;
        }
    }

    public enum ClientCommands {
        MOVE("MOVE", List.of(1, 2)),
        LOGIN("LOGIN", 2),
        SIGN_UP("SIGN_UP", 2),
        JOIN_LOBBY("JOIN_LOBBY", 1),
        CREATE_LOBBY("CREATE_LOBBY", 1),
        QUIT_LOBBY("QUIT_LOBBY", 0),
        READY("READY", 0),
        START("START", 0),
        QUIT("QUIT", 0);

        private String commandText;

        private List<Integer> argumentsNumbers;

        ClientCommands(String commandText, List<Integer> argumentsNumbers) {
            this.commandText = commandText;
            this.argumentsNumbers = argumentsNumbers;
        }

        ClientCommands(String commandText, int argumentsNumber) {
            this.commandText = commandText;
            this.argumentsNumbers = List.of(argumentsNumber);
        }

        public String getCommandText() {
            return commandText;
        }
    }
}

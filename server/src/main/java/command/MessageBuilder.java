package command;

import java.util.List;

public class MessageBuilder {

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
        BOARD("BOARD", 1)
        ;

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

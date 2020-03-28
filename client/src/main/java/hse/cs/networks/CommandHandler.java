package hse.cs.networks;

import hse.cs.networks.common.MessageBuilder;
import hse.cs.networks.game.GameHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import static hse.cs.networks.common.MessageBuilder.MESSAGE_DELIMITER;
import static hse.cs.networks.common.MessageBuilder.ServerCommands.*;
import static hse.cs.networks.common.MessageBuilder.isRightCommand;

public class CommandHandler extends Thread {

    private GoClientSocket socket;

    CommandHandler(GoClientSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean isAuthorised = false;
        boolean isWaitingInLobby = false;
        boolean isPlayingGame = false;
        do {
            try {
                this.socket.getSemaphore().acquire();
                InputStream input = this.socket.getSocket().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String command = reader.readLine();
                System.out.println(command);
                if (command != null) {
                    if (isRightCommand(command, WRONG_COMMAND)) {
                        System.out.println("Invalid command: " + command.split(MESSAGE_DELIMITER)[1]);
                    }
                    if (!isAuthorised) {
                        if (isRightCommand(command, NO_USER)) {
                            System.out.println("There is no user with username " + command.split(MESSAGE_DELIMITER)[1] + ".");
                        } else if (isRightCommand(command, FAILED_AUTH)) {
                            System.out.println("Wrong password. Try again.");
                        } else if (isRightCommand(command, ALREADY_USER)) {
                            System.out.println("There is already user with username " + command.split(MESSAGE_DELIMITER)[1] + ". Try another one.");
                        } else if (isRightCommand(command, SUCCESS_AUTH)) {
                            System.out.println("Successful authorization.");
                            isAuthorised = true;
                            isPlayingGame = true;
                            isWaitingInLobby = true;
                        }
                    } else if (isPlayingGame) {
                        if (isRightCommand(command, BOARD)) {
                            var gameHistory = command.split(MESSAGE_DELIMITER)[1];
                            var board = GameHistory.fromMovesRecordsQueue(Arrays.asList(gameHistory.split(MessageBuilder.MESSAGE_ARRAY_DELIMITER))).toBoard();
                            System.out.println(board);
                        }
                    }
                }


            } catch (IOException e) {
                System.out.println("I/O error while writing: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Socket acquiring was interrupted while writing: " + e.getMessage());
                break;
            } finally {
                this.socket.getSemaphore().release();
            }

            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong while attempt to sleep");
            }
        } while (true);
    }
}

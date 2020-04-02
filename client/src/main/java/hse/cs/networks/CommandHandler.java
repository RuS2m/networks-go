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
        boolean isWaitingInLobbyMenu = false;
        boolean isPlayingGame = false;
        do {
            try {
                this.socket.getSemaphore().acquire();
                InputStream input = this.socket.getSocket().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String command = reader.readLine();
//                System.out.println(command);
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
                            isAuthorised = true;
                            isWaitingInLobbyMenu = true;
                            System.out.println("Successful authorization.");
                        }
                    } else if (isWaitingInLobbyMenu) {
                        if (isRightCommand(command, FAILED_JOIN)) {
                            if (command.split(MESSAGE_DELIMITER).length == 3) {
                                System.out.println("Failed to join lobby " + command.split(MESSAGE_DELIMITER)[1] + ":\n\t" + command.split(MESSAGE_DELIMITER)[2]);
                            } else {
                                System.out.println("Failed to create lobby:\n\t" + command.split(MESSAGE_DELIMITER)[1]);
                            }
                        } else if (isRightCommand(command, SUCCESS_JOIN)) {
                            System.out.println("Success join to lobby " + command.split(MESSAGE_DELIMITER)[1]);
                        } else if (isRightCommand(command, USER_JOINED)) {
                            System.out.println("Lobby log: User " + command.split(MESSAGE_DELIMITER)[1] + " joined");
                        } else if (isRightCommand(command, USER_QUIT)) {
                            System.out.println("Lobby log: User " + command.split(MESSAGE_DELIMITER)[1] + " quited");
                        } else if (isRightCommand(command, USER_READY)) {
                            System.out.println("Lobby log: User " + command.split(MESSAGE_DELIMITER)[1] + " is ready");
                        } else if (isRightCommand(command, USER_NOT_READY)) {
                            System.out.println("Lobby log: User " + command.split(MESSAGE_DELIMITER)[1] + " is not ready");
                        } else if (isRightCommand(command, ALL_READY)) {
                            System.out.println("Lobby log: All users are ready. Press START to start.");
                        } else if (isRightCommand(command, GAME_STARTED)) {
                            System.out.println("Lobby log: Game started");
                            isWaitingInLobbyMenu = false;
                            isPlayingGame = true;
                        } else if (isRightCommand(command, FAILED_START)) {
                            System.out.println("Lobby log: Was failed to start. Not everybody is ready.");
                        } else if (isRightCommand(command, DEBUG_INFO)) {
                            System.out.println(command.split(MESSAGE_DELIMITER)[1]);
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

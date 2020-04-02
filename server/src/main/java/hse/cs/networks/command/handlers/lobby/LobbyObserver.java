package hse.cs.networks.command.handlers.lobby;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

import static hse.cs.networks.common.MessageBuilder.ServerCommands.*;
import static hse.cs.networks.common.MessageBuilder.message;

public class LobbyObserver extends Thread {

    private Long lobbyId;

    volatile private Map<String, Boolean> userToReady;

    private boolean allReady = false;

    private PrintWriter writer;

    private LobbyQueryService lobbyQueryService;

    LobbyObserver(Long lobbyId, PrintWriter writer, LobbyQueryService lobbyQueryService) {
        this.lobbyId = lobbyId;
        this.writer = writer;
        this.lobbyQueryService = lobbyQueryService;
        this.userToReady = new HashMap<>();
    }

    @Override
    public void run() {
        do {
            Map<String, Boolean> updatedUsers = null;
            try {
                updatedUsers = this.lobbyQueryService.usersInLobby(this.lobbyId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (updatedUsers == null) {
                this.userToReady = updatedUsers;
            }
            if (!updatedUsers.equals(this.userToReady)) {
                var intersection = this.userToReady.keySet();
                intersection.retainAll(updatedUsers.keySet());
                for (var user : intersection) {
                    if (!updatedUsers.get(user) && this.userToReady.get(user)) {
                        this.writer.println(message(USER_NOT_READY, user));
                    } else if (updatedUsers.get(user) && !this.userToReady.get(user)) {
                        this.writer.println(message(USER_READY, user));
                    }
                }
                var newUsers = new HashSet<>(updatedUsers.keySet());
                for (var user : intersection) {
                    newUsers.remove(user);
                }
                for (var user : newUsers) {
                    this.writer.println(message(USER_JOINED, user));
                }
                var oldUsers = new HashSet<>(this.userToReady.keySet());
                for (var user : intersection) {
                    oldUsers.remove(user);
                }
                for (var user : oldUsers) {
                    this.writer.println(message(USER_QUIT, user));
                }
                this.userToReady = updatedUsers;
            }
            List<Boolean> usersReady = null;
            if (this.userToReady != null) {
                usersReady = new ArrayList<>(this.userToReady.values());
            }
            if (usersReady != null
                    && usersReady.size() == 2
                    && usersReady.get(0)
                    && usersReady.get(1)) {
                this.writer.println(message(ALL_READY));
                this.allReady = true;
            }
            try {
                 Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(!this.isAllReady());
    }

    public boolean isAllReady() {
        return allReady;
    }

}

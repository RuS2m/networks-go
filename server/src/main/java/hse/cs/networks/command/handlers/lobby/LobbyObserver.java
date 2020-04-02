package hse.cs.networks.command.handlers.lobby;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LobbyObserver extends Thread {

    private Long lobbyId;

    private Map<String, Boolean> userToReady;

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
        try {
            Thread.sleep(20L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isAllReady() {
        return allReady;
    }

}

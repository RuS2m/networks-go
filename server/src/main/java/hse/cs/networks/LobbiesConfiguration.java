package hse.cs.networks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class LobbiesConfiguration {

    private Semaphore semaphore;

    private Map<Long, LobbyConfiguration> lobbiesConfiguration;

    public static class LobbyConfiguration {

        private int lobbyId;

        private int maxNumberOfPlayers = 2;

        private List<String> usersInLobby;
    }
}

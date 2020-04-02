package hse.cs.networks;

import java.util.Map;
import java.util.concurrent.Semaphore;

public class GamesConfiguration {

    private Semaphore semaphore;

    private Map<Long, GameSessionConfiguraition> gamesConfiguration;

    public static class GameSessionConfiguraition {

        private int stepNumber;
    }
}

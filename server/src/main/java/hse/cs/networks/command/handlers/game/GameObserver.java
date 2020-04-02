package hse.cs.networks.command.handlers.game;

import static hse.cs.networks.common.MessageBuilder.ServerCommands.*;
import static hse.cs.networks.common.MessageBuilder.message;
import java.io.PrintWriter;
import java.sql.SQLException;

public class GameObserver extends Thread {

    private Long sessionId;

    private int gameOrder;

    private boolean isStep = true;

    private PrintWriter writer;

    private GameQueryService lobbyQueryService;

    public GameObserver(Long sessionId, int gameOrder, PrintWriter writer, GameQueryService lobbyQueryService) {
        this.sessionId = sessionId;
        this.gameOrder = gameOrder;
        this.writer = writer;
        this.lobbyQueryService = lobbyQueryService;
    }

    @Override
    public void run() {
        do {
            boolean isRightGameOrder = false;
            try {
                isRightGameOrder = this.lobbyQueryService.isRightGameOrder(sessionId, gameOrder);
            } catch (SQLException e) {
                // nop
            }
            if (isRightGameOrder)
                try {
                    isRightGameOrder = this.lobbyQueryService.isRightGameOrder(sessionId, gameOrder);
                } catch (SQLException e) {
                    // nop
                }
//                writer.println(message(BOARD, ));
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        } while (true);
    }
}

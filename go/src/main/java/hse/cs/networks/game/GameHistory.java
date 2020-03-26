package hse.cs.networks.game;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {

    private static String PASS_RECORD = "P";

    private List<Move> movesQueue;

    public GameHistory() {
        this.movesQueue = new ArrayList<>();
    }

    private GameHistory(List<Move> movesQueue) {
        this.movesQueue = movesQueue;
    }

    public static GameHistory fromMovesRecordsQueue(List<String> movesRecordsQueue) {
        var movesQueue = new ArrayList<Move>();
        int color = 0;
        for (var moveRecord : movesRecordsQueue) {
            var parsedInteger = -1;
            try {
                parsedInteger = Integer.parseInt(moveRecord);
            } catch (NumberFormatException e) {
                // nop
            }
            if (moveRecord.equals(PASS_RECORD)) {
                movesQueue.add(new Pass());
            } else if (parsedInteger >= 0 && parsedInteger < Board.BOARD_SIZE * Board.BOARD_SIZE) {
                int yPosition = parsedInteger / Board.BOARD_SIZE;
                int xPosition = parsedInteger % Board.BOARD_SIZE;
                movesQueue.add(new StoneMove(new Board.Stone(xPosition, yPosition, color)));
            } else {
                    throw new IllegalArgumentException("movesQueue contains not valid moves records");
            }
            color = 1 - color;
        }
        return new GameHistory(movesQueue);
    }

    public List<Move> getMovesQueue() {
        return movesQueue;
    }

    public void addMove(Move move) {
        this.movesQueue.add(move);
    }

    public List<String> movesRecordsQueue() {
        var movesRecordsQueue = new ArrayList<String>();
        for (var move : this.movesQueue) {
            if (move.isPassive()) {
                movesRecordsQueue.add(PASS_RECORD);
            }
            move.stonePosition().ifPresent(stone -> movesRecordsQueue.add(Integer.toString(stone.getyPosition() * Board.BOARD_SIZE + stone.getxPosition())));
        }
        return movesRecordsQueue;
    }

    public Board toBoard() {
        var board = new Board();
        for (var move : this.movesQueue) {
            move.stonePosition().ifPresent(stone -> board.addStone(stone.getxPosition(), stone.getyPosition(), stone.getColor()));
        }
        return board;
    }

    public boolean isEnd() {
        return this.movesQueue.size() > 1
                && this.movesQueue.get(0).equals(new Pass())
                && this.movesQueue.get(1).equals(new Pass());
    }
}

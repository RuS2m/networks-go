package hse.cs.networks.command.handlers.game;

import hse.cs.networks.common.MessageBuilder;
import hse.cs.networks.game.GameHistory;
import hse.cs.networks.game.Move;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GameQueryService {

    private Connection connection;

    public GameQueryService(Connection connection) {
        this.connection = connection;
    }

    public boolean isRightGameOrder(long sessionId, int gameOrder) throws SQLException {
        var pst = connection.prepareStatement("SELECT step FROM games WHERE session_id = ?;");
        pst.setLong(1, sessionId);
        var rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) % 2 == gameOrder;
        }
        return false;
    }

    public void makeMove(long sessionId, Move move) throws SQLException {
        var pst = connection.prepareStatement("SELECT game_history, step FROM games WHERE session_id = ?;");
        pst.setLong(1, sessionId);
        var rs = pst.executeQuery();
        var history = "";
        var step = 0;
        if (rs.next()) {
            history = rs.getString(1);
            step = rs.getInt(2);
        }
        var oldHistory = GameHistory.fromMovesRecordsQueue(List.of(history.split(MessageBuilder.MESSAGE_ARRAY_DELIMITER)));
        oldHistory.addMove(move);
        var newHistory = String.join(MessageBuilder.MESSAGE_ARRAY_DELIMITER, oldHistory.movesRecordsQueue());
        var pst2 = connection.prepareStatement("UPDATE games SET game_history = ? AND step = ? WHERE session_id = ?;");
        pst.setString(  1, newHistory);
        pst.setLong(2, sessionId);
        pst.setLong(3, Integer.valueOf(step).longValue());
        pst.executeUpdate();
    }

    public String history(long sessionId) throws SQLException {
        var pst = connection.prepareStatement("SELECT game_history FROM games WHERE session_id = ?;");
        pst.setLong(1, sessionId);
        var rs = pst.executeQuery();
        var history = "";
        if (rs.next()) {
            history = rs.getString(1);
        }
        return history;
    }
}

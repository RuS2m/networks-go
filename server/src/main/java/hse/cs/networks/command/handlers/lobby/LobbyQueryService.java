package hse.cs.networks.command.handlers.lobby;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LobbyQueryService {

    private Connection connection;

    public LobbyQueryService(Connection connection) {
        this.connection = connection;
    }

    public void createLobby(String lobbyName, String username) throws SQLException {
        var lobbyId = new Random().nextLong();
        while (!isLobbyExist(lobbyId)) {
            lobbyId = new Random().nextLong();
        }
        var pst = connection.prepareStatement("INSERT INTO lobbies(lobby_id, lobby_name, state) VALUES(?, ?, ?);");
        pst.setLong(1, lobbyId);
        pst.setString(2, lobbyName);
        pst.setString(3, LobbyState.NOT_FULL.getStateName());
        pst.executeUpdate();
        joinLobby(lobbyId, username);
    }

    public boolean isLobbyExist(Long lobbyId) throws SQLException {
        var pst = connection.prepareStatement("SELECT count(*) FROM lobbies WHERE lobby_id = ?;");
        pst.setLong(1, lobbyId);
        var rs =  pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) != 0;
        }
        return false;
    }

    public boolean isLobbyFull(Long lobbyId) throws SQLException {
        var pst = connection.prepareStatement("SELECT count(*) FROM participations WHERE lobby_id = ?;");
        pst.setLong(1, lobbyId);
        var rs =  pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) >= 2;
        }
        return false;
    }

    public void joinLobby(Long lobbyId, String username) throws SQLException {
        var pst = connection.prepareStatement("INSERT INTO participations(username, lobby_id, state) VALUES(?, ?, ?)");
        pst.setString(1, username);
        pst.setLong(2, lobbyId);
        pst.setString(3, ParticipationState.NOT_READY.getStateName());
        pst.executeUpdate();
    }

    public void quitLobby(String username) throws SQLException {
        var pst = connection.prepareStatement("DELETE FROM participations WHERE username = ?;");
        pst.setString(1, username);
        pst.executeUpdate();
    }

    public void setReady(String username) throws SQLException {
        var pst = connection.prepareStatement("UPDATE participations SET state = ? WHERE username = ?;");
        pst.setString(1, ParticipationState.READY.getStateName());
        pst.setString(2, username);
        pst.executeUpdate();
    }

    public Map<String, Boolean> usersInLobby(Long lobbyId) throws SQLException {
        var pst = connection.prepareStatement("SELECT username, state FROM participations WHERE lobby_id = ?;");
        pst.setLong(1, lobbyId);
        var rs = pst.executeQuery();
        var usersInLobby = new HashMap<String, Boolean>();
        while(rs.next()){
            var username = rs.getString(1);
            var state = rs.getString(2);
            usersInLobby.put(username, state.equals(ParticipationState.READY.getStateName()));
        }
        return usersInLobby;
    }

    enum LobbyState {
        PLAYING("PLAYING"),
        ALL_READY("ALL_READY"),
        WAITING_FOR_READY("WAITING_FOR_READY"),
        NOT_FULL("NOT_FULL");

        private String stateName;

        LobbyState(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }
    }

    enum ParticipationState {
        NOT_READY("WAITING"),
        READY("READY"),
        PLAYING("PLAYING"),
        NOT_IN_LOBBY("NOT_IN_LOBBY");

        private String stateName;

        ParticipationState(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }
    }
}

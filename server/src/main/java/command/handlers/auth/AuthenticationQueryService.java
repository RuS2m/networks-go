package command.handlers.auth;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthenticationQueryService {

    private Connection connection;

    public AuthenticationQueryService(Connection connection) {
        this.connection = connection;
    }

    public void signUp(String username, String password) throws SQLException {
        var pst = connection.prepareStatement("INSERT INTO users(username, password) VALUES(?, ?);");
        pst.setString(1, username);
        pst.setString(2, password);
        pst.executeUpdate();
    }

    public boolean isUsernameExist(String username) throws SQLException {
        var pst = connection.prepareStatement("SELECT count(*) FROM users WHERE username = ?;");
        pst.setString(1, username);
        var rs =  pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) != 0;
        }
        return false;
    }

    public boolean areCredentialsCorrect(String username, String password) throws SQLException {
        var pst = connection.prepareStatement("SELECT count(*) FROM users WHERE username = ? AND password = ?;");
        pst.setString(1, username);
        pst.setString(2, password);
        var rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) != 0;
        }
        return false;
    }
}

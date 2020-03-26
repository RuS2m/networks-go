import java.io.*;
import java.net.Socket;
import java.sql.Connection;

public class GoServerThread extends Thread {

    private Connection connection;

    private Socket socket;

    public GoServerThread(Connection connection, Socket socket) {
        this.connection = connection;
        this.socket = socket;
    }

    public void run() {
        try {
            var input = this.socket.getInputStream();
            var reader = new BufferedReader(new InputStreamReader(input));

            var output = this.socket.getOutputStream();
            var writer = new PrintWriter(output, true);


            GameSession gameSession = new GameSession(writer, reader, connection);
            gameSession.start();

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

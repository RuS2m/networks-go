import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Driver;
import java.sql.DriverManager;

public class GoServer {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        Driver driver = new org.postgresql.Driver();
        DriverManager.registerDriver(driver);

        var conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "rus2m", "");

//        var psst = conn.prepareStatement("INSERT INTO users(username, password, role, additional_info) VALUES ('admin', 'admin', 'admin', 'admin')");
//        psst.executeUpdate();
        try (var serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                var socket = serverSocket.accept();
                System.out.println("New client connected");

                new GoServerThread(conn, socket).start();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

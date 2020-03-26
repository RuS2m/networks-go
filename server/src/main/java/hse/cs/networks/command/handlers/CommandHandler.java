package hse.cs.networks.command.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

public abstract class CommandHandler {

    private PrintWriter writer;

    private BufferedReader reader;

    private Connection connection;

    public CommandHandler(PrintWriter writer, BufferedReader reader, Connection connection) {
        this.writer = writer;
        this.reader = reader;
        this.connection = connection;
    }

    public abstract void handle() throws IOException;

    public PrintWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Connection getConnection() {
        return connection;
    }
}

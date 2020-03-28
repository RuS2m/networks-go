package hse.cs.networks;

import hse.cs.networks.common.MessageBuilder;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;

import static hse.cs.networks.common.MessageBuilder.MESSAGE_DELIMITER;

public class ConsoleReader extends Thread {

    private PrintWriter writer;

    private GoClientSocket socket;

    ConsoleReader(PrintWriter writer, GoClientSocket socket) {
        this.writer = writer;
        this.socket = socket;
    }

    @Override
    public void run() {
        Console console = System.console();
        String text;
        do {
            text = console.readLine();
            text = text.replaceAll(" ", MESSAGE_DELIMITER);
            writer.println(text);
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong while attempt to sleep");
            }
        } while (!text.equals(MessageBuilder.ClientCommands.QUIT.getCommandText()));

        try {
            this.socket.getSemaphore().acquire();
            this.socket.getSocket().close();
        } catch (IOException e) {
            System.out.println("I/O error while reading: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Socket acquiring was interrupted while reading: ");
        } finally {
            this.socket.getSemaphore().release();
        }

    }
}

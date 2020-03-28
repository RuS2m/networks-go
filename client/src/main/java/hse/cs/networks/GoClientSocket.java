package hse.cs.networks;

import java.net.Socket;
import java.util.concurrent.Semaphore;

class GoClientSocket {

    private Semaphore semaphore;

    private Socket socket;

    GoClientSocket(Semaphore semaphore, Socket socket) {
        this.semaphore = semaphore;
        this.socket = socket;
    }

    Semaphore getSemaphore() {
        return semaphore;
    }

    Socket getSocket() {
        return socket;
    }
}

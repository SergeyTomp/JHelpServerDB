package jhelp;

import jhelp.repos.TermRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerDB implements Runnable {

    private ServerSocket serverSocket;
    private TermRepository termRepository;
    private int PORT;
    private static Logger log = LoggerFactory.getLogger(ServerDB.class);

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public int getPORT() {
        return PORT;
    }

    ServerDB(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    @Override
    public void run() {

        ExecutorService es = Executors.newCachedThreadPool();
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                es.submit(new RequestHandler(clientSocket, termRepository));
            }
        } catch (IOException e) {
           log.info("socket closed");
        } finally {
            try {
                es.shutdown();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

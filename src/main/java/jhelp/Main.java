package jhelp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static void showInfo(String message, String title){
        showMessageDialog(null, message, title, INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(JavaConfig.class);
        ServerDB serverDB = ctx.getBean(ServerDB.class);
        TableSaver tableSaver = ctx.getBean(TableSaver.class);
        int PORT = serverDB.getPORT();


        try (ServerSocket serverSocket = new ServerSocket(PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            serverDB.setServerSocket(serverSocket);
            executorService.submit(serverDB);
            executorService.shutdown();

            log.info("Local server is launched on port: {}", PORT);
            System.out.println("\nTo shutdown server please type \"exit\"");
            showInfo("To shutdown server please type \"exit\" in terminal window", "Server started");
            String line;
            while ((line = reader.readLine()) != null)
                if (line.equals("exit")) {
                    serverSocket.close();
                    tableSaver.saveTables();
                    break;
                }
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

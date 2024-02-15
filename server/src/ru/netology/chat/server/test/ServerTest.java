package ru.netology.chat.server.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import ru.netology.chat.network.TCPConnection;
import ru.netology.chat.server.Server;

import java.io.*;
import java.util.Scanner;

public class ServerTest {

    private TCPConnection tcpConnection = null;
    private Server server = null;
    private BufferedWriter logFile = null;

    @Before
    public void setUp() {
        int port = 8080;
        String name = "serverTest";
        String logPath = "server/src/ru/netology/chat/server/log.txt";
        String ipAddress = "127.0.0.1";

        try {
            server = new Server(port, logPath, name);
            tcpConnection = new TCPConnection(server, ipAddress, port, logPath, name);
            logFile = new BufferedWriter(new FileWriter(logPath, true));
        } catch (Exception e) {
            Assertions.fail("Failed to create server and TCPConnection: " + e.getMessage());
        }
    }

    @Test
    public void testConnectionCreation() {
        Assertions.assertNotNull(tcpConnection);
        Assertions.assertTrue(tcpConnection.getSocket().isConnected());
        Assertions.assertTrue(tcpConnection.getReceptionThread().isAlive());

        server.onStop();
        Assertions.assertFalse(tcpConnection.getSocket().isConnected());
        Assertions.assertFalse(tcpConnection.getReceptionThread().isAlive());
    }

    @Test
    public void testSendMessage() throws FileNotFoundException {
        String messageToSend = "Test message";
        String expectedMessage = "Client connected";

        tcpConnection.sendMessage(messageToSend, logFile, "TestSender");

        String lastLogLine = getLastLineFromFile();
        Assertions.assertTrue(lastLogLine.contains(expectedMessage));
    }

    @Test
    public void testDisconnection() {
        tcpConnection.disconnect();
        Assertions.assertTrue(tcpConnection.getSocket().isClosed());
        Assertions.assertFalse(tcpConnection.getReceptionThread().isAlive());
    }

    @Test
    public void testErrorHandling() {
        Assertions.assertThrows(IOException.class, () -> {
            new TCPConnection(server, "invalid_ip", 9999, "invalid_log_path", "TestSender");
        });
    }

    private String getLastLineFromFile() throws FileNotFoundException {
        File file = new File("server/src/ru/netology/chat/server/log.txt");
        Scanner scanner = new Scanner(file);
        String lastLine = "";
        while (scanner.hasNextLine()) {
            lastLine = scanner.nextLine();
        }
        scanner.close();
        return lastLine;
    }
}
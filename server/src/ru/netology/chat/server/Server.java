package ru.netology.chat.server;

import ru.netology.chat.network.TCPConnection;
import ru.netology.chat.network.TCPConnectionCatcher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Server implements TCPConnectionCatcher {

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
        int port = getPort("server/src/ru/netology/chat/server/settings.txt");
        String logPath = "server/src/ru/netology/chat/server/log.txt";
        new Server(port, logPath);
    }

    public Server(int port, String logPath) {
        System.out.println("The server is running.");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                try {
                    new TCPConnection(this, serverSocket.accept(), logPath);
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection, BufferedWriter log) {
        connections.add(tcpConnection);
        sendToClients("Client connected: " + tcpConnection, log);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String message, BufferedWriter log) {
        sendToClients(message, log);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection, BufferedWriter log) {
        connections.remove(tcpConnection);
        sendToClients("Client disconnected: " + tcpConnection, log);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToClients(String message, BufferedWriter log) {
        System.out.println(message);
        for (TCPConnection connection : connections) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            connection.sendMessage(message, currentDateTime, log);
        }
    }

    public static int getPort(String inputFilePath) {
        int port = 0;
        try {
            Path path = Paths.get(inputFilePath);
            String line = Files.readString(path);
            port = Integer.parseInt(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

}

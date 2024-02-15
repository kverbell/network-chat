package ru.netology.chat.server;

import ru.netology.chat.network.TCPConnection;
import ru.netology.chat.network.TCPConnectionCatcher;
import ru.netology.chat.network.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionCatcher {

    private final ArrayList<TCPConnection> connections = new ArrayList<>();
    private boolean isRunning = true;
    private TCPConnection tcpConnection;


    public static void main(String[] args) {
        int port = Integer.parseInt(Util.getSettings("server/src/ru/netology/chat/server/settings.txt", 1));
        String logPath = "server/src/ru/netology/chat/server/log.txt";
        new Server(port, logPath, "myServer");
    }

    public Server(int port, String logPath, String serverName) {
        System.out.println("The server is running.");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(isRunning) {
                try {
                    tcpConnection = new TCPConnection(this, serverSocket.accept(),
                            logPath, serverName);
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection, BufferedWriter logFile,
                                               String senderName) {
        connections.add(tcpConnection);
        sendToClients("Client connected: " + tcpConnection, logFile, senderName);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String message,
                                             BufferedWriter logFile, String senderName) {
        sendToClients(message, logFile, senderName);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection, BufferedWriter logFile,
                                          String senderName) {
        sendToClients("Client disconnected: " + tcpConnection, logFile, senderName);
        connections.remove(tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    public synchronized void closeConnections() {
        for (TCPConnection connection : connections) {
            connection.disconnect();
        }
        connections.clear();
    }

    @Override
    public synchronized void onStop() {
        tcpConnection.disconnect();
        closeConnections();
        isRunning = false;
        System.out.println("Server stopping.");
    }

    private void sendToClients(String message, BufferedWriter logFile, String senderName) {
        System.out.println(message);
        for (TCPConnection connection : connections) {
            connection.sendMessage(message, logFile, senderName);
        }
    }
}

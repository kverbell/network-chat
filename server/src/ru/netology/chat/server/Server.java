package ru.netology.chat.server;

import ru.netology.chat.network.TCPConnection;
import ru.netology.chat.network.TCPConnectionCatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionCatcher {

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
        //TODO номер порта из файла settings.txt
        int port = 8080;
        new Server(port);
    }

    public Server(int port) {
        System.out.println("The server is running.");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToClients("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String message) {
        sendToClients(message);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToClients("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToClients(String message) {
        System.out.println(message);
        for (TCPConnection connection : connections) {
            connection.sendMessage(message);
        }
    }

}

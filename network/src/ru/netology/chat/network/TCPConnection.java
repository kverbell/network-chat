package ru.netology.chat.network;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class TCPConnection {
    private final Socket socket;
    private final Thread receptionThread;
    private final TCPConnectionCatcher connectionCatcher;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionCatcher connectionCatcher, String ipAddress, int port,
                         String logPath, String senderName) throws IOException {
        this(connectionCatcher, new Socket(ipAddress, port), logPath, senderName);
    }

    public TCPConnection(TCPConnectionCatcher connectionCatcher, Socket socket,
                         String logPath, String senderName) throws IOException {
        BufferedWriter logFile = new BufferedWriter(new FileWriter(logPath, true));
        this.socket = socket;
        this.connectionCatcher = connectionCatcher;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.receptionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionCatcher.onConnectionReady(TCPConnection.this, logFile, senderName);
                    while(!receptionThread.isInterrupted()) {
                        String message = in.readLine();
                        if (!message.equals("/exit")) {
                            connectionCatcher.onReceiveString(TCPConnection.this, message, logFile, senderName);
                        } else {
                            connectionCatcher.onDisconnect(TCPConnection.this, logFile, senderName);
                        }
                    }
                } catch (IOException e) {
                    connectionCatcher.onException(TCPConnection.this, e);
                    disconnect();
                }
            }
        });
        receptionThread.start();
    }

    public synchronized void sendMessage(String message, BufferedWriter logFile, String senderName) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            String str = currentDateTime + " — " + senderName + ": " + message + "\r\n";
            out.write(str);
            out.flush();
            logFile.write(str);
            logFile.flush();
        } catch (IOException e) {
            connectionCatcher.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        System.out.println("Disconnecting TCPConnection.");
        receptionThread.interrupt();
        try {
            socket.close();
            System.out.println("Socket closed successfully.");
        } catch (IOException e) {
            connectionCatcher.onException(TCPConnection.this, e);
            System.out.println("Error while closing socket: " + e.getMessage());
        }
    }

    public Thread getReceptionThread() {
        return receptionThread;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

}

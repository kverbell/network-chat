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
        BufferedWriter log = new BufferedWriter(new FileWriter(logPath, true));
        this.socket = socket;
        this.connectionCatcher = connectionCatcher;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.receptionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionCatcher.onConnectionReady(TCPConnection.this, log, senderName);
                    while(!receptionThread.isInterrupted()) {
                        String message = in.readLine();
                        if (!message.equals("/exit")) {
                            connectionCatcher.onReceiveString(TCPConnection.this, message, log, senderName);
                        } else {
                            connectionCatcher.onDisconnect(TCPConnection.this, log, senderName);
                            disconnect();
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

    public synchronized void sendMessage(String message, LocalDateTime currentDateTime,
                                         BufferedWriter log, String senderName) {
        try {
            String str = currentDateTime + " — " + senderName + ": " + message + "\r\n";
            out.write(str);
            out.flush();
            log.write(str);
            log.flush();
        } catch (IOException e) {
            connectionCatcher.onException(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect() {
        receptionThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connectionCatcher.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

}

package ru.netology.chat.network;

import java.io.BufferedWriter;

public interface TCPConnectionCatcher {

    void onConnectionReady (TCPConnection tcpConnection, BufferedWriter logFile, String senderName);

    void onReceiveString (TCPConnection tcpConnection, String message, BufferedWriter logFile,
                          String senderName);

    void onDisconnect (TCPConnection tcpConnection, BufferedWriter logFile, String senderName);

    void onException(TCPConnection tcpConnection, Exception e);

    void onStop();

}

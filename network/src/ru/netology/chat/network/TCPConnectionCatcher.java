package ru.netology.chat.network;

import java.io.BufferedWriter;

public interface TCPConnectionCatcher {

    void onConnectionReady (TCPConnection tcpConnection, BufferedWriter log, String senderName);

    void onReceiveString (TCPConnection tcpConnection, String value, BufferedWriter log,
                          String senderName);

    void onDisconnect (TCPConnection tcpConnection, BufferedWriter log, String senderName);

    void onException(TCPConnection tcpConnection, Exception e);

}

package ru.netology.chat.network;

import java.io.BufferedWriter;

public interface TCPConnectionCatcher {

    void onConnectionReady (TCPConnection tcpConnection, BufferedWriter log);

    void onReceiveString (TCPConnection tcpConnection, String value, BufferedWriter log);

    void onDisconnect (TCPConnection tcpConnection, BufferedWriter log);

    void onException(TCPConnection tcpConnection, Exception e);

}

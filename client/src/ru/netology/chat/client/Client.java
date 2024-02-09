package ru.netology.chat.client;

import ru.netology.chat.network.TCPConnection;
import ru.netology.chat.network.TCPConnectionCatcher;
import ru.netology.chat.network.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, TCPConnectionCatcher {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final String SETTINGS_PATH = "server/src/ru/netology/chat/server/settings.txt";
    private static final String LOG_PATH = "server/src/ru/netology/chat/server/log.txt";
    private static final String IP = Util.getSettings(SETTINGS_PATH,1);
    private static final int PORT = Integer.parseInt(Util.getSettings(SETTINGS_PATH,2));

    private final JTextArea log = new JTextArea();
    private final JTextField inputField = new JTextField();
    private TCPConnection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client("John");
            }
        });
    }

    private Client(String name) {
        JTextField nickNameField = new JTextField(name);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        inputField.addActionListener(this);
        add(log, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        add(nickNameField, BorderLayout.NORTH);
        setVisible(true);
        try {
            connection = new TCPConnection(this, IP, PORT, LOG_PATH, name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection, BufferedWriter log, String senderName) {

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value, BufferedWriter log, String senderName) {

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection, BufferedWriter log, String senderName) {

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

    }

}

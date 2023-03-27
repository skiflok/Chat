package app.client;


import app.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    protected Connection connection;

    private volatile boolean clientConnected;


    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Введите адрес сервера:");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Введите порт сервера:");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Введите ваше имя:");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSentTextFromConsole() {
        return true;
    }

    SocketThread getSocketThread() {
        return new SocketThread();
    }

    void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось отправить сообщение");
            clientConnected = false;
            ConsoleHelper.writeMessage("Не удалось отправить сообщение");
        }
    }


    public class SocketThread extends Thread {


    }


}

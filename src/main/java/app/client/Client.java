package app.client;


import app.*;

import java.io.IOException;
import java.net.Socket;
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

    protected boolean shouldSendTextFromConsole() {
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


    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Произошла ошибка во время работы клиента.");
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
            return;
        }

        if (clientConnected)
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        else
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");

        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if (text.equalsIgnoreCase("exit"))
                break;

            if (shouldSendTextFromConsole())
                sendTextMessage(text);
        }
    }


    public class SocketThread extends Thread {

        void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник '" + userName + "' присоединился к чату.");
        }

        void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник '" + userName + "' покинул чат.");
        }

        void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }


        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.NAME_REQUEST) { // Сервер запросил имя пользователя
                    // Запрашиваем ввод имени с консоли
                    String name = getUserName();
                    // Отправляем имя на сервер
                    connection.send(new Message(MessageType.USER_NAME, name));

                } else if (message.getMessageType() == MessageType.NAME_ACCEPTED) { // Сервер принял имя пользователя
                    // Сообщаем главному потоку, что он может продолжить работу
                    notifyConnectionStatusChanged(true);
                    return;

                } else {
                    logger.log(Level.SEVERE, "Unexpected MessageType.");
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {

            // Цикл обработки сообщений сервера
            while (true) {
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.TEXT) { // Сервер прислал сообщение с текстом
                    processIncomingMessage(message.getMessage());
                } else if (MessageType.USER_ADDED == message.getMessageType()) {
                    informAboutAddingNewUser(message.getMessage());
                } else if (MessageType.USER_REMOVED == message.getMessageType()) {
                    informAboutDeletingNewUser(message.getMessage());
                } else {
                    logger.log(Level.SEVERE, "Unexpected MessageType.");
                    throw new IOException("Unexpected MessageType");
                }
            }
        }


    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run();

    }

}
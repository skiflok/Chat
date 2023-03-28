package app;

import com.sun.javafx.binding.StringFormatter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        ConsoleHelper.writeMessage("Введите порт сервера:");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Чат сервер запущен.");
            while (true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка при запуске или работе сервера {0}", e.getMessage());
            ConsoleHelper.writeMessage("Произошла ошибка при запуске или работе сервера.");
        }


    }

    /**
     * Рассылает сообщение всем участникам чата
     *
     * @param message
     */
    public static void sendBroadcastMessage(Message message) {
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Не смогли отправить сообщение {0}", e.getMessage());
                logger.log(Level.SEVERE, "RemoteSocketAddress {0}", connection.getRemoteSocketAddress());
                ConsoleHelper.writeMessage("Не смогли отправить сообщение " + connection.getRemoteSocketAddress());
            }
        }
    }

    /**
     * Обработчик сообщений
     */
    private static class Handler extends Thread {
        private final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Авторизация пользователя по имени, отправляет запросы,
         * вносит в базу участников чата для рассылки
         * авторизация по уникальному имени пользователя
         *
         * @param connection соединение
         * @return имя пользователя
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {

                connection.send(new Message(MessageType.NAME_REQUEST));

                Message message = connection.receive();

                if (message.getMessageType() != MessageType.USER_NAME) {
                    logger.log(Level.SEVERE, "Получено сообщение от " +
                            socket.getRemoteSocketAddress() +
                            ". Тип сообщения не соответствует протоколу.");
                    ConsoleHelper.writeMessage("Получено сообщение от " +
                            socket.getRemoteSocketAddress() +
                            ". Тип сообщения не соответствует протоколу.");
                    continue;
                }

                String userName = message.getMessage();

                if (userName.isEmpty()) {
                    logger.log(Level.SEVERE, "Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    logger.log(Level.SEVERE, "Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }

                connectionMap.put(message.getMessage(), connection);

                connection.send(new Message(MessageType.NAME_ACCEPTED));

                return userName;
            }

        }


        private void notifyUsers(Connection connection, String userName) throws IOException {

            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if (!entry.getKey().equalsIgnoreCase(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, userName));
                }
            }


        }

        /**
         * главный цикл обработки сообщений сервером,
         * принимает сообщения от клиента и рассылает всем клиентам
         *
         * @param connection Соединение
         * @param userName   имя отправителя
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {

            while (true) {

                Message message = connection.receive();

                if (message.getMessageType() == MessageType.TEXT) {
                    String textMessage = String.format("%s: %s", userName, message.getMessage());
                    sendBroadcastMessage(new Message(MessageType.TEXT, textMessage));
                } else {
                    logger.log(Level.SEVERE, "Получено сообщение от " +
                            socket.getRemoteSocketAddress() +
                            ". Тип сообщения не соответствует протоколу.");
                    ConsoleHelper.writeMessage("Получено сообщение от " +
                            socket.getRemoteSocketAddress() +
                            ". Тип сообщения не соответствует протоколу.");
                }
            }
        }


        @Override
        public void run() {

            logger.log(Level.INFO, "run() socket {0}", socket.getRemoteSocketAddress());
            ConsoleHelper.writeMessage("Установлено новое соединение с " + socket.getRemoteSocketAddress());

            String userName = null;

            try (Connection connection = new Connection(socket)) {

                userName = serverHandshake(connection);

                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));

                notifyUsers(connection, userName);

                serverMainLoop(connection, userName);

            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Ошибка при обмене данными с " + socket.getRemoteSocketAddress());
                ConsoleHelper.writeMessage("Ошибка при обмене данными с " + socket.getRemoteSocketAddress());
            }

            if (userName != null) {
                connectionMap.remove(userName);
                logger.log(Level.INFO, "Пользователь {0} удален", userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }

            logger.log(Level.INFO, "Соединение с " + socket.getRemoteSocketAddress() + " закрыто.");
            ConsoleHelper.writeMessage("Соединение с " + socket.getRemoteSocketAddress() + " закрыто.");
        }
    }

}

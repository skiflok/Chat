package app;

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

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

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


        private void sendListOfUsers(Connection connection, String userName) throws IOException {

            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if (!entry.getKey().equalsIgnoreCase(userName)) {
                    entry.getValue().send(new Message(MessageType.USER_ADDED, userName));
                }
            }


        }
        @Override
        public void run() {
            logger.log(Level.INFO, "run() socket {0}", socket.getPort());
            Connection connection;
            try {
                connection = new Connection(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Message message;
            while (!isInterrupted()) {
                try {
                    message = connection.receive();
                    ConsoleHelper.writeMessage(message.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}

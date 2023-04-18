package app.netty_chat.dao;

import app.netty_chat.Server;
import app.netty_chat.service.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.TreeSet;

public class UserStorage implements Serializable {

    private static final Logger logger
            = LoggerFactory.getLogger(UserStorage.class);

    private static UserStorage instance = new UserStorage();
    private TreeSet<User> users;

    private String filename;

    {
        filename = PropertiesLoader.getPropertiesLoader().getProperty("server.users");
        try {
            loadFromFile(filename);
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Ошибка загрузки файла c пользователями");
            users = new TreeSet<>();
//            throw new RuntimeException(e);
        }
    }
    private UserStorage() {}

    public static UserStorage getInstance() {
        return instance;
    }

    public TreeSet<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void saveToFile(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            filename = "users.dat";
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(users);
        }
    }

    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        if (filename == null || filename.isEmpty()) {
            filename = "users.dat";
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            users = (TreeSet<User>) ois.readObject();
        }
    }

}

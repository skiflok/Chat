package app.netty_chat.dao;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable, Comparable<User> {

    private final String userName;
    private String pass;

    public User(String userName) {
        this.userName = userName;
        this.pass = null;
    }

    public String getUserName() {
        return userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public int compareTo(User o) {
        return this.userName.compareTo(o.userName);
    }
}

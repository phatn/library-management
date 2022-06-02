package edu.miu.mpp.library.model;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable {

    private static final long serialVersionUID = 5147265048973262104L;
    private String username;

    private char[] password;

    private Role authorization;

    public User(String username, char[] password, Role authorization) {
        this.username = username;
        this.password = password;
        this.authorization = authorization;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public Role getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Role authorization) {
        this.authorization = authorization;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password=" + Arrays.toString(password) +
                ", authorization=" + authorization +
                '}';
    }
}

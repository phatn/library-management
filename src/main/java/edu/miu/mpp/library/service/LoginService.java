package edu.miu.mpp.library.service;

import edu.miu.mpp.library.exception.LoginException;
import edu.miu.mpp.library.model.Role;
import edu.miu.mpp.library.model.User;

import java.util.Arrays;
import java.util.Map;

public class LoginService extends AbstractService {
    public Role login(String username, char[] password) throws LoginException {
        Map<String, User> map = dataAccess.readUserMap();
        if(!map.containsKey(username)) {
            throw new LoginException("Username " + username + " not found");
        }
        char[] passwordFound = map.get(username).getPassword();

        if(!Arrays.equals(passwordFound, password)) {
            throw new LoginException("Password incorrect");
        }
        return map.get(username).getAuthorization();
    }
}

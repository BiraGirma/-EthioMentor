package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String identifier, String password) {
        return userDAO.authenticate(identifier, password);
    }
}

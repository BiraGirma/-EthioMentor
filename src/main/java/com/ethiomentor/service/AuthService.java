package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;
<<<<<<< HEAD
import com.ethiomentor.util.PasswordUtil;

public class AuthService {

	  private UserDAO userDAO = new UserDAO();

	  public User login(String email, String password, String role) {
		  
	    return userDAO.authenticate(email, password, role);
	  }
	}
=======

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String identifier, String password) {
        return userDAO.authenticate(identifier, password);
    }
}
>>>>>>> 8fb5b46 (finilized)

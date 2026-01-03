package com.ethiomentor.service;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.PasswordUtil;

public class AuthService {

	  private UserDAO userDAO = new UserDAO();

	  public User login(String email, String password, String role) {
		  
	    return userDAO.authenticate(email, password, role);
	  }
	}

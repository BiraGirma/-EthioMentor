 package com.ethiomentor.controller;

import com.ethiomentor.config.DBInit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns = "/StartupServlet", loadOnStartup = 1)
public class StartupServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
	    super.init();
	    System.out.println("StartupServlet INIT called!");
	    DBInit.init();
	    System.out.println("Database initialization completed at startup.");
	}

}

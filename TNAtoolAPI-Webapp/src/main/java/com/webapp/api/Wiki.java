package com.webapp.api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/TNAtoolAPI-Webapp/wiki")
public class Wiki extends HttpServlet {
	// GET
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/resources/wiki/index.html").forward(request, response);
		
	}
	
	// POST
}

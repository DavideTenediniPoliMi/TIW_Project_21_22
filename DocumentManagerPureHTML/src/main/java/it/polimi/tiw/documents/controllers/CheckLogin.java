package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/CheckLogin")
public class CheckLogin extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		
		String username = escape(request.getParameter("username"));
		String password = escape(request.getParameter("password"));

		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			forward(request, response, Paths.LOGIN_PAGE, new HttpVariable("errorText", "Fill every field!"));
			return;
		}

		byte[] passwordDigest;
		
		try {
			passwordDigest = digestPassword(password);
		} catch (NoSuchAlgorithmException e) {
			errorHandler.sendInternalError("Encryption failed");
			return;
		}
		
		User user = null;

		try {
			user = getDAOHandler().checkCredentials(username, passwordDigest);
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		if (user == null) {
			forward(request, response, Paths.LOGIN_PAGE, new HttpVariable("errorText", "Wrong username and/or password"));
			return;
		}

		request.getSession().setAttribute("user", user);
		response.sendRedirect(Paths.TO_HOME_PAGE);
	}

}

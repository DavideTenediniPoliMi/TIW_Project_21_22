package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.EmailValidator;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;

@WebServlet("/RegisterUser")
@MultipartConfig
public class RegisterUser extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();

		String username = escape(request.getParameter("username"));
		String email = escape(request.getParameter("email"));
		String password = escape(request.getParameter("password"));
		String passwordDuplicate = escape(request.getParameter("passwordDuplicate"));

		if (username == null || username.isEmpty() || email == null|| email.isEmpty()
				|| password == null || password.isEmpty() 
				|| passwordDuplicate == null || passwordDuplicate.isEmpty()) {
			errorHandler.sendMissingParamsError();
			return;
		}
		
		if (username.length() > 30 || !EmailValidator.isValid(email) 
				|| password.contains(" ") || !password.equals(passwordDuplicate)) {
			errorHandler.sendBadParamsError("Some field wasn't filled properly!");
			return;
		}

		byte[] passwordDigest;

		try {
			passwordDigest = digestPassword(password);
		} catch (NoSuchAlgorithmException e) {
			errorHandler.sendInternalError("Encryption failed");
			return;
		}

		User user = new User();

		try {
			if (daoHandler.areSignUpInfoTaken(username, email)) {
				errorHandler.sendBadParamsError("Username and/or email already taken");
				return;
			}

			user.setUsername(username).setEmail(email).setPassword(passwordDigest);

			daoHandler.registerUser(user);
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		request.getSession().setAttribute("user", user);
		send(response, new HttpVariable("username", new Gson().toJson(username)));
	}
}

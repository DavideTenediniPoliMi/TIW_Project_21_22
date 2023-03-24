package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.EmailValidator;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/RegisterUser")
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

		if (username == null || username.isBlank() || email == null|| email.isBlank()
				|| password == null || password.isBlank() 
				|| passwordDuplicate == null || passwordDuplicate.isBlank()) {
			forward(request, response, Paths.LOGIN_PAGE, new HttpVariable("errorText", "Fill every field!"),
					new HttpVariable("isRegister", true));
			return;
		}
		
		if (username.length() > 30 || !EmailValidator.isValid(email) 
				|| password.contains(" ") || !password.equals(passwordDuplicate)) {
			forward(request, response, Paths.LOGIN_PAGE,
					new HttpVariable("errorText", "Some field wasn't filled properly!"),
					new HttpVariable("isRegister", true));
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
				forward(request, response, Paths.LOGIN_PAGE,
						new HttpVariable("errorText", "Username and/or email already taken"),
						new HttpVariable("isRegister", true));
				return;
			}

			user.setUsername(username).setEmail(email).setPassword(passwordDigest);

			daoHandler.registerUser(user);
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		request.getSession().setAttribute("user", user);
		response.sendRedirect(Paths.TO_HOME_PAGE);
	}
}

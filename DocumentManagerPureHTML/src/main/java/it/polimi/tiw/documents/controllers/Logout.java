package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/Logout")
public class Logout extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		resetSession(request);
		
		response.sendRedirect(Paths.TO_LOGIN_PAGE);
	}
}

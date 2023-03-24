package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.ConnectionHandler;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.TemplateHandler;

public abstract class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	@Override
	public void init() throws UnavailableException {
		ServletContext context = getServletContext();
		connection = ConnectionHandler.getConnection(context);
		templateEngine = TemplateHandler.getEngine(context);
	}

	public User getLoggedUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}

	public DAOHandler getDAOHandler() {
		return new DAOHandler(connection);
	}

	public void forward(HttpServletRequest request, HttpServletResponse response, String path,
			HttpVariable... variables) throws IOException {
		WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());

		for (HttpVariable variable : variables) {
			if (!variable.getName().isEmpty()) {
				webContext.setVariable(variable.getName(), variable.getValue());
			}
		}

		templateEngine.process(path, webContext, response.getWriter());
	}

	public void dispatchTo(HttpServletRequest request, HttpServletResponse response, String path,
			HttpVariable... variables) throws ServletException, IOException {
		for (HttpVariable variable : variables) {
			if (!variable.getName().isEmpty()) {
				request.setAttribute(variable.getName(), variable.getValue());
			}
		}
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
		requestDispatcher.forward(request, response);
	}
	
	public String escape(String param) {
		if (param == null) return null;
		
		return StringEscapeUtils.escapeJava(param.strip());
	}
	
	public byte[] digestPassword(String password) throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
	}

	public void resetSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}
	}

	@Override
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}
}

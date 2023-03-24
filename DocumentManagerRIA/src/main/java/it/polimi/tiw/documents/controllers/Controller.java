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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.ConnectionHandler;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.HttpVariable;

public abstract class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	@Override
	public void init() throws UnavailableException {
		ServletContext context = getServletContext();
		connection = ConnectionHandler.getConnection(context);
	}

	public User getLoggedUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}

	public DAOHandler getDAOHandler() {
		return new DAOHandler(connection);
	}

	public void send(HttpServletResponse response, HttpVariable... variables) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		JsonObject message = new JsonObject();

		for (HttpVariable variable : variables) {
			if (!variable.getName().isEmpty()) {
				message.add(variable.getName(), JsonParser.parseString(variable.getValue()));
			}
		}

		response.getWriter().println(message);
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
	
	public String escape(String params) {
		if (params == null) return null;
		
		return StringEscapeUtils.escapeJava(params.strip());
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

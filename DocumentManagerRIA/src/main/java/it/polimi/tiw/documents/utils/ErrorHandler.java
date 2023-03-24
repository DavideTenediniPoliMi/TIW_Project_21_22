package it.polimi.tiw.documents.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ErrorHandler {
	private HttpServletResponse response;
	
	public ErrorHandler(HttpServletResponse response) {
		this.response = response;
	}
	
	public void sendError(int statusCode, String errorMessage) throws IOException {
		response.setStatus(statusCode);
		response.getWriter().println(errorMessage);
	}
	
	public void sendInternalError(String errorMessage) throws IOException {
		sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
	}
	
	public void sendMissingParamsError() throws IOException {
		sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
	}
	
	public void sendBadParamsError(String errorMessage) throws IOException {
		sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
	}
	
	public void sendBadParamsError() throws IOException {
		sendBadParamsError("Bad parameters");
	}
	
	public void sendDBError() throws IOException {
		sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database interaction failed");
	}
	
	public void sendForbiddenError() throws IOException {
		sendError(HttpServletResponse.SC_FORBIDDEN, "You are not the owner of this resource");
	}
	
	public void sendNotFoundError() throws IOException {
		sendError(HttpServletResponse.SC_NOT_FOUND, "No such resource");
	}
	
	public void sendUnauthorizedError() throws IOException {
		sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong username and/or password");
	}
}

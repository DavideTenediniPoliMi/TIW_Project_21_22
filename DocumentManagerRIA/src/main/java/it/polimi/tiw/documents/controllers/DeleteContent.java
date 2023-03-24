package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Content;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;

@WebServlet("/DeleteContent")
public class DeleteContent extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();
		
		User user = getLoggedUser(request);
		
		String contentType = escape(request.getParameter("contentType"));
		String idParam = escape(request.getParameter("id"));
		
		if (contentType == null || contentType.isBlank() || idParam == null || idParam.isBlank()) {
			errorHandler.sendMissingParamsError();
			return;
		}
		
		int contentId;
		
		try {
			contentId = Integer.parseInt(idParam);
		} catch (NumberFormatException e) {
			errorHandler.sendBadParamsError();
			return;
		}
		
		try {
			Content target = daoHandler.getContentById(contentType, contentId);
			
			if (target == null) {
				errorHandler.sendBadParamsError();
				return;
			}
			
			if (target.getOwner().getId() != user.getId()) {
				errorHandler.sendForbiddenError();
				return;
			}
			
			switch(contentType) {
				case "FOLDER":
					daoHandler.deleteFolder(contentId);
					break;
				case "SUBFOLDER":
					daoHandler.deleteSubfolder(contentId);
					break;
				case "DOCUMENT":
					daoHandler.deleteDocument(contentId);
					break;
				default:
					errorHandler.sendBadParamsError();
					return;
			}
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}
		
		send(response);
	}
}

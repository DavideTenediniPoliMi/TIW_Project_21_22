package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;

@WebServlet("/MoveDocument")
public class MoveDocument extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();

		User user = getLoggedUser(request);

		String documentIdParam = escape(request.getParameter("documentId"));
		String subfolderIdParam = escape(request.getParameter("subfolderId"));

		if (documentIdParam == null || documentIdParam.isEmpty() || subfolderIdParam == null
				|| subfolderIdParam.isEmpty()) {
			errorHandler.sendMissingParamsError();
			return;
		}

		int documentId;
		int subfolderId;

		try {
			documentId = Integer.parseInt(documentIdParam);
			subfolderId = Integer.parseInt(subfolderIdParam);
		} catch (NumberFormatException e) {
			errorHandler.sendBadParamsError();
			return;
		}

		Subfolder subfolder;
		Document document;

		try {
			subfolder = daoHandler.getSubfolderById(subfolderId);
			document = daoHandler.getDocumentById(documentId);

			if (subfolder == null || document == null) {
				errorHandler.sendBadParamsError();
				return;
			}

			if (!subfolder.getOwner().equals(user) || !document.getOwner().equals(user)) {
				errorHandler.sendForbiddenError();
				return;
			}

			if (subfolder.equals(document.getParentFolder())) {
				errorHandler.sendBadParamsError();
				return;
			}

			daoHandler.moveDocumentToSubfolder(document, subfolder);
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		send(response);
	}
}

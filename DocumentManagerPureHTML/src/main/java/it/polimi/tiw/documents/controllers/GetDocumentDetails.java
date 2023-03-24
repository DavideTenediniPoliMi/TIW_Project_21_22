package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/GetDocumentDetails")
public class GetDocumentDetails extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler erroreHandler = new ErrorHandler(response);
		
		User user = getLoggedUser(request);

		String documentIdParam = escape(request.getParameter("documentId"));

		if (documentIdParam == null || documentIdParam.isBlank()) {
			erroreHandler.sendMissingParamsError();
			return;
		}

		int documentId;

		try {
			documentId = Integer.parseInt(documentIdParam);
		} catch (NumberFormatException e) {
			erroreHandler.sendBadParamsError();
			return;
		}

		Document document;

		try {
			document = getDAOHandler().getDocumentById(documentId);
		} catch (SQLException e) {
			erroreHandler.sendDBError();
			return;
		}

		if (document == null) {
			erroreHandler.sendNotFoundError();
			return;
		}

		if (!user.equals(document.getOwner())) {
			erroreHandler.sendForbiddenError();
			return;
		}

		forward(request, response, Paths.DOCUMENT_PAGE, new HttpVariable("document", document));
	}
}

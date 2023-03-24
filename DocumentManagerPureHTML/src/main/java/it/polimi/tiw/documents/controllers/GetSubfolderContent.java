package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/GetSubfolderContent")
public class GetSubfolderContent extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler erroreHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();
		
		User user = getLoggedUser(request);

		String subfolderIdParam = escape(request.getParameter("subfolderId"));

		if (subfolderIdParam == null || subfolderIdParam.isBlank()) {
			erroreHandler.sendMissingParamsError();
			return;
		}

		int subfolderId;

		try {
			subfolderId = Integer.parseInt(subfolderIdParam);
		} catch (NumberFormatException e) {
			erroreHandler.sendBadParamsError();
			return;
		}

		List<Document> documents;
		Subfolder subfolder;

		try {
			subfolder = daoHandler.getSubfolderById(subfolderId);

			if (subfolder == null) {
				erroreHandler.sendNotFoundError();
				return;
			}

			if (!user.equals(subfolder.getOwner())) {
				erroreHandler.sendForbiddenError();
				return;
			}

			documents = daoHandler.getDocumentsInsideSubfolder(subfolder);
		} catch (SQLException e) {
			erroreHandler.sendDBError();
			return;
		}

		forward(request, response, Paths.SUBFOLDER_PAGE, new HttpVariable("subfolder", subfolder),
				new HttpVariable("documents", documents));
	}
}

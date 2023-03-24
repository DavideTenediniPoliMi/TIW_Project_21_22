package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/GoToHomePage")
public class GoToHomePage extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();

		User user = getLoggedUser(request);

		String documentIdParam = escape(request.getParameter("documentId"));

		Document document = null;

		if (documentIdParam != null) {
			int documentId;

			try {
				documentId = Integer.parseInt(documentIdParam);
			} catch (NumberFormatException e) {
				errorHandler.sendBadParamsError();
				return;
			}

			try {
				document = daoHandler.getDocumentById(documentId);
			} catch (SQLException e) {
				errorHandler.sendDBError();
				return;
			}

			if (document == null) {
				errorHandler.sendNotFoundError();
				return;
			}

			if (!user.equals(document.getOwner())) {
				errorHandler.sendForbiddenError();
				return;
			}
		}

		Map<Folder, List<Subfolder>> folderTree = new LinkedHashMap<Folder, List<Subfolder>>();

		try {
			List<Folder> folders = daoHandler.getFoldersOfUser(user);

			for (Folder folder : folders) {
				folderTree.put(folder, daoHandler.getSubfoldersInsideFolder(folder));
			}
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		HttpVariable optionalVariable = (documentIdParam != null) ? new HttpVariable("document", document)
				: new HttpVariable("", "");

		forward(request, response, Paths.HOME_PAGE, new HttpVariable("folderTree", folderTree), optionalVariable);
	}
}

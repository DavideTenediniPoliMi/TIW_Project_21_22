package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;
import it.polimi.tiw.documents.utils.Paths;

@WebServlet("/GoToCreateContentPage")
public class GoToCreateContentPage extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();
		
		User user = getLoggedUser(request);

		List<Folder> folders;
		List<Subfolder> subfolders = new ArrayList<>();

		try {
			folders = daoHandler.getFoldersOfUser(user);

			for (Folder folder : folders) {
				subfolders.addAll(daoHandler.getSubfoldersInsideFolder(folder));
			}
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		forward(request, response, Paths.CREATE_CONTENT_PAGE, new HttpVariable("folders", folders),
				new HttpVariable("subfolders", subfolders));
	}
}

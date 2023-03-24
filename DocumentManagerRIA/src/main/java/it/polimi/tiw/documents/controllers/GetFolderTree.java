package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;
import it.polimi.tiw.documents.utils.HttpVariable;

@WebServlet("/GetFolderTree")
public class GetFolderTree extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();

		User user = getLoggedUser(request);

		JsonArray foldersJson = new JsonArray();

		try {
			List<Folder> folders = daoHandler.getFoldersOfUser(user);

			for (Folder folder : folders) {
				JsonObject folderJson = folder.serialize();
				List<Subfolder> subfolders = daoHandler.getSubfoldersInsideFolder(folder);
				JsonArray subfoldersJson = new JsonArray();
				
				for (Subfolder subfolder : subfolders) {
					JsonObject subfolderJson = subfolder.serialize();
					List<Document> documents = daoHandler.getDocumentsInsideSubfolder(subfolder);
					JsonArray documentsJson = new JsonArray();
					
					for (Document document : documents) {
						documentsJson.add(document.serialize());
					}
					
					subfolderJson.add("documents", documentsJson);
					subfoldersJson.add(subfolderJson);
				}
				
				folderJson.add("subfolders", subfoldersJson);
				foldersJson.add(folderJson);
			}
		} catch (SQLException e) {
			errorHandler.sendDBError();
			return;
		}

		send(response, new HttpVariable("folderTree", new Gson().toJson(foldersJson)));
	}
}

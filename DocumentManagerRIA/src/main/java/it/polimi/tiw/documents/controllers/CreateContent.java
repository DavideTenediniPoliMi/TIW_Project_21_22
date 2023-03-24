package it.polimi.tiw.documents.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.utils.DAOHandler;
import it.polimi.tiw.documents.utils.ErrorHandler;

@WebServlet("/CreateContent")
@MultipartConfig
public class CreateContent extends Controller {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorHandler errorHandler = new ErrorHandler(response);
		DAOHandler daoHandler = getDAOHandler();
		
		User user = getLoggedUser(request);
		
		String contentType = escape(request.getParameter("contentType"));
		String name = escape(request.getParameter("name"));
		String parentFolderIdParam = escape(request.getParameter("parentFolderId"));
		String parentSubfolderIdParam = escape(request.getParameter("parentSubfolderId"));
		String summary = escape(request.getParameter("summary"));
		String type = escape(request.getParameter("type"));

		if (contentType == null || contentType.isBlank() || name == null || name.isBlank()) {
			errorHandler.sendMissingParamsError();
			return;
		}
		
		if (name.length() > 30) {
			errorHandler.sendBadParamsError();
			return;
		}

		switch (contentType) {
			case "FOLDER":
				if ((parentFolderIdParam != null && !parentFolderIdParam.isEmpty()) 
						|| (parentSubfolderIdParam != null && !parentSubfolderIdParam.isEmpty()) 
						|| (summary != null && !summary.isEmpty()) || (type != null && !type.isEmpty())) {
					errorHandler.sendBadParamsError();
					return;
				}
				
				Folder newFolder = new Folder();
				
				newFolder.setOwner(user)
					.setName(name)
					.setCreationDate(LocalDate.now());
	
				try {
					daoHandler.createFolder(newFolder);
				} catch (SQLException e) {
					errorHandler.sendDBError();
					return;
				}
	
				break;
			case "SUBFOLDER":
				if (parentFolderIdParam == null || parentFolderIdParam.isBlank()) {
					errorHandler.sendMissingParamsError();
					return;
				}
				
				if ((parentSubfolderIdParam != null && !parentSubfolderIdParam.isEmpty()) 
						|| (summary != null && !summary.isEmpty()) || (type != null && !type.isEmpty())) {
					errorHandler.sendBadParamsError();
					return;
				}
	
				int parentFolderId;
	
				try {
					parentFolderId = Integer.parseInt(parentFolderIdParam);
				} catch (NumberFormatException e) {
					errorHandler.sendBadParamsError();
					return;
				}
	
				Folder folder;
				Subfolder newSubfolder = new Subfolder();
	
				try {
					folder = daoHandler.getFolderById(parentFolderId);
	
					if (folder == null) {
						errorHandler.sendBadParamsError();
						return;
					}
					
					if (!folder.getOwner().equals(user)) {
						errorHandler.sendForbiddenError();
						return;
					}
	
					newSubfolder.setParentFolder(folder)
						.setName(name)
						.setCreationDate(LocalDate.now());
	
					daoHandler.createSubfolder(newSubfolder);
				} catch (SQLException e) {
					errorHandler.sendDBError();
					return;
				}
	
				break;
			case "DOCUMENT":
				if (parentSubfolderIdParam == null || parentSubfolderIdParam.isBlank() || summary == null || 
						type == null || type.isBlank()) {
					errorHandler.sendMissingParamsError();
					return;
				}
				
				if (parentFolderIdParam != null && !parentFolderIdParam.isEmpty() || type.length() > 3) {
					errorHandler.sendBadParamsError();
					return;
				}
	
				int parentSubfolderId;
	
				try {
					parentSubfolderId = Integer.parseInt(parentSubfolderIdParam);
				} catch (NumberFormatException e) {
					errorHandler.sendBadParamsError();
					return;
				}
	
				Subfolder subfolder;
				Document newDocument = new Document();
	
				try {
					subfolder = daoHandler.getSubfolderById(parentSubfolderId);
	
					if (subfolder == null) {
						errorHandler.sendMissingParamsError();
						return;
					}
					
					if (!subfolder.getOwner().equals(user)) {
						errorHandler.sendForbiddenError();
						return;
					}
	
					newDocument.setParentFolder(subfolder)
						.setSummary(summary)
						.setType(type)
						.setName(name)
						.setCreationDate(LocalDate.now());
	
					daoHandler.createDocument(newDocument);
				} catch (SQLException e) {
					errorHandler.sendDBError();
					return;
				}
	
				break;
			default:
				errorHandler.sendBadParamsError();
				return;
		}

		send(response);
	}
}

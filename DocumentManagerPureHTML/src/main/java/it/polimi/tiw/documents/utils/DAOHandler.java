package it.polimi.tiw.documents.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.dao.DocumentDAO;
import it.polimi.tiw.documents.dao.FolderDAO;
import it.polimi.tiw.documents.dao.SubfolderDAO;
import it.polimi.tiw.documents.dao.UserDAO;

public class DAOHandler {
	private UserDAO userDAO;
	private FolderDAO folderDAO;
	private SubfolderDAO subfolderDAO;
	private DocumentDAO documentDAO;
	
	public DAOHandler(Connection connection) {
		userDAO = new UserDAO(connection);
		folderDAO = new FolderDAO(connection);
		subfolderDAO = new SubfolderDAO(connection);
		documentDAO = new DocumentDAO(connection);
	}
	
	public User checkCredentials(String username, byte[] password) throws SQLException {
		return userDAO.checkCredentials(username, password);
	}
	
	public void registerUser(User user) throws SQLException {
		userDAO.registerUser(user);
	}
	
	public boolean areSignUpInfoTaken(String username, String email) throws SQLException {
		return userDAO.areSignUpInfoTaken(username, email);
	}
	
	public List<Folder> getFoldersOfUser(User user) throws SQLException {
		return folderDAO.getFoldersOfUser(user);
	}
	
	public Folder getFolderById(int folderId) throws SQLException {
		return folderDAO.getFolderById(folderId);
	}
	
	public void createFolder(Folder folder) throws SQLException {
		folderDAO.createFolder(folder);
	}

	public List<Subfolder> getSubfoldersInsideFolder(Folder folder) throws SQLException {
		return subfolderDAO.getSubfoldersInsideFolder(folder);
	}
	
	public Subfolder getSubfolderById(int subfolderId) throws SQLException {
		return subfolderDAO.getSubfolderById(subfolderId);
	}
	
	public void createSubfolder(Subfolder subfolder) throws SQLException {
		subfolderDAO.createSubfolder(subfolder);
	}
	
	public List<Document> getDocumentsInsideSubfolder(Subfolder subfolder) throws SQLException {
		return documentDAO.getDocumentsInsideSubfolder(subfolder);
	}
	
	public Document getDocumentById(int documentId) throws SQLException {
		return documentDAO.getDocumentById(documentId);
	}
	
	public void createDocument(Document document) throws SQLException {
		documentDAO.createDocument(document);
	}
	
	public void moveDocumentToSubfolder(Document document, Subfolder subfolder) throws SQLException {
		documentDAO.moveDocumentToSubfolder(document, subfolder);
	}
}

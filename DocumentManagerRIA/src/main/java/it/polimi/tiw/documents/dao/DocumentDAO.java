package it.polimi.tiw.documents.dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;
import it.polimi.tiw.documents.beans.Document;
import it.polimi.tiw.documents.beans.Folder;

public class DocumentDAO extends DAO {
	public DocumentDAO(Connection connection) {
		super(connection);
	}

	public List<Document> getDocumentsInsideSubfolder(Subfolder subfolder) throws SQLException {
		List<Document> documents = new ArrayList<Document>();

		String query = "SELECT id, name, creation_date FROM document WHERE parent_folder_id = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, subfolder.getId());

			try (ResultSet result = prepStatement.executeQuery();) {
				while (result.next()) {
					Document document = new Document();

					document.setParentFolder(subfolder)
						.setName(result.getString("name"))
						.setCreationDate(result.getObject("creation_date", LocalDate.class))
						.setId(result.getInt("id"));

					documents.add(document);
				}
			}
		}

		return documents;
	}

	public Document getDocumentById(int documentId) throws SQLException {
		String query = "SELECT d.name, d.creation_date, d.summary, d.type, "
				+ "s.id, s.name, s.creation_date , f.id, f.name, f.creation_date, "
				+ "u.id, u.username, u.email, u.password "
				+ "FROM (((document AS d INNER JOIN subfolder AS s ON d.parent_folder_id = s.id) "
				+ "INNER JOIN folder AS f ON s.parent_folder_id = f.id) "
				+ "INNER JOIN user AS u ON f.owner_id = u.id) WHERE d.id = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, documentId);

			try (ResultSet result = prepStatement.executeQuery();) {
				if (!result.next()) return null;

				User user = new User();
				Folder folder = new Folder();
				Subfolder subfolder = new Subfolder();
				Document document = new Document();
				
				user.setUsername(result.getString(12))
					.setEmail(result.getString(13))
					.setPassword(result.getBytes(14))
					.setId(result.getInt(11));
				
				folder.setOwner(user)
					.setName(result.getString(9))
					.setCreationDate(result.getObject(10, LocalDate.class))
					.setId(result.getInt(8));
				
				subfolder.setParentFolder(folder)
					.setName(result.getString(6))
					.setCreationDate(result.getObject(7, LocalDate.class))
					.setId(result.getInt(5));

				document.setParentFolder(subfolder)
					.setSummary(result.getString(3))
					.setType(result.getString(4))
					.setName(result.getString(1))
					.setCreationDate(result.getObject(2, LocalDate.class))
					.setId(documentId);
				
				return document;
			}
		}
	}

	public void createDocument(Document document) throws SQLException {
		String query = "INSERT into document (name, creation_date, parent_folder_id, summary, type) "
				+ "VALUES(?, ?, ?, ?, ?)";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setString(1, document.getName());
			prepStatement.setObject(2, document.getCreationDate());
			prepStatement.setInt(3, document.getParentFolder().getId());
			prepStatement.setString(4, document.getSummary());
			prepStatement.setString(5, document.getType());

			prepStatement.executeUpdate();
		}
	}

	public void moveDocumentToSubfolder(Document document, Subfolder subfolder) throws SQLException {
		String query = "UPDATE document SET parent_folder_id = ? WHERE id = ? ";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, subfolder.getId());
			prepStatement.setInt(2, document.getId());

			prepStatement.executeUpdate();
		}
	}
	
	public void deleteDocument(int documentId) throws SQLException {
		String query = "DELETE FROM document WHERE id = ?";
		
		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, documentId);
			
			prepStatement.executeUpdate();
		}
	}
}

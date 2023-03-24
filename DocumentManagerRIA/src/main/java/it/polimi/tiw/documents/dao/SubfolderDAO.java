package it.polimi.tiw.documents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.Subfolder;
import it.polimi.tiw.documents.beans.User;

public class SubfolderDAO extends DAO {
	public SubfolderDAO(Connection connection) {
		super(connection);
	}

	public List<Subfolder> getSubfoldersInsideFolder(Folder folder) throws SQLException {
		List<Subfolder> subfolders = new ArrayList<Subfolder>();

		String query = "SELECT id, name, creation_date FROM subfolder WHERE parent_folder_id = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, folder.getId());

			try (ResultSet result = prepStatement.executeQuery();) {
				while (result.next()) {
					Subfolder subfolder = new Subfolder();

					subfolder.setParentFolder(folder)
						.setName(result.getString("name"))
						.setCreationDate(result.getObject("creation_date", LocalDate.class))
						.setId(result.getInt("id"));

					subfolders.add(subfolder);
				}
			}
		}

		return subfolders;
	}

	public Subfolder getSubfolderById(int subfolderId) throws SQLException {
		String query = "SELECT s.name, s.creation_date , f.id, f.name, f.creation_date, "
				+ "u.id, u.username, u.email, u.password " 
				+ "FROM ((subfolder AS s INNER JOIN folder AS f ON s.parent_folder_id = f.id) "
				+ "INNER JOIN user AS u ON f.owner_id = u.id ) WHERE s.id = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, subfolderId);

			try (ResultSet result = prepStatement.executeQuery();) {
				if (!result.next()) return null;
				
				User user = new User();
				Folder folder = new Folder();
				Subfolder subfolder = new Subfolder();
				
				user.setUsername(result.getString(7))
					.setEmail(result.getString(8))
					.setPassword(result.getBytes(9))
					.setId(result.getInt(6));
				
				folder.setOwner(user)
					.setName(result.getString(4))
					.setCreationDate(result.getObject(5, LocalDate.class))
					.setId(result.getInt(3));
				
				subfolder.setParentFolder(folder)
					.setName(result.getString(1))
					.setCreationDate(result.getObject(2, LocalDate.class))
					.setId(subfolderId);

				return subfolder;
			}
		}
	}

	public void createSubfolder(Subfolder subfolder) throws SQLException {
		String query = "INSERT into subfolder (name, creation_date, parent_folder_id) VALUES(?, ?, ?)";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setString(1, subfolder.getName());
			prepStatement.setObject(2, subfolder.getCreationDate());
			prepStatement.setInt(3, subfolder.getParentFolder().getId());

			prepStatement.executeUpdate();
		}
	}
	
	public void deleteSubfolder(int subfolderId) throws SQLException {
		String query = "DELETE FROM subfolder WHERE id = ?";
		
		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, subfolderId);
			
			prepStatement.executeUpdate();
		}
	}
}

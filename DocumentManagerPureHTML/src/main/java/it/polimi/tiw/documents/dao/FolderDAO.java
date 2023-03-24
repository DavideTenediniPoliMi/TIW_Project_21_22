package it.polimi.tiw.documents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.documents.beans.Folder;
import it.polimi.tiw.documents.beans.User;

public class FolderDAO extends DAO {
	public FolderDAO(Connection connection) {
		super(connection);
	}

	public List<Folder> getFoldersOfUser(User user) throws SQLException {
		List<Folder> folders = new ArrayList<Folder>();

		String query = "SELECT id, name, creation_date FROM folder WHERE owner_id = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, user.getId());

			try (ResultSet result = prepStatement.executeQuery();) {
				while (result.next()) {
					Folder folder = new Folder();

					folder.setOwner(user)
						.setName(result.getString("name"))
						.setCreationDate(result.getObject("creation_date", LocalDate.class))
						.setId(result.getInt("id"));

					folders.add(folder);
				}
			}
		}

		return folders;
	}

	public Folder getFolderById(int folderId) throws SQLException {
		String query = "SELECT f.name, f.creation_date, u.id, u.username, u.email, u.password " 
				+ "FROM folder AS f INNER JOIN user AS u ON f.owner_id = u.id WHERE f.id = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setInt(1, folderId);

			try (ResultSet result = prepStatement.executeQuery();) {
				if (!result.next()) return null;
				
				User user = new User();
				Folder folder = new Folder();
				
				user.setUsername(result.getString(4))
					.setEmail(result.getString(5))
					.setPassword(result.getBytes(6))
					.setId(result.getInt(3));

				folder.setOwner(user)
					.setName(result.getString(1))
					.setCreationDate(result.getObject(2, LocalDate.class))
					.setId(folderId);

				return folder;
			}
		}
	}

	public void createFolder(Folder folder) throws SQLException {
		String query = "INSERT into folder (name, creation_date, owner_id) VALUES(?, ?, ?)";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setString(1, folder.getName());
			prepStatement.setObject(2, folder.getCreationDate());
			prepStatement.setInt(3, folder.getOwner().getId());

			prepStatement.executeUpdate();
		}
	}
}

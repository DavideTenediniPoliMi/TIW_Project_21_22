package it.polimi.tiw.documents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.documents.beans.User;

public class UserDAO extends DAO {
	public UserDAO(Connection connection) {
		super(connection);
	}

	public User checkCredentials(String username, byte[] password) throws SQLException {
		String query = "SELECT id, email FROM user WHERE username = ? AND password = ?";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setString(1, username);
			prepStatement.setBytes(2, password);

			try (ResultSet result = prepStatement.executeQuery();) {
				if (!result.next()) return null;
				
				User user = new User();

				user.setUsername(username)
					.setEmail(result.getString("email"))
					.setPassword(password)
					.setId(result.getInt("id"));

				return user;
			} 
		}
	}

	public void registerUser(User user) throws SQLException {
		String query = "INSERT into user (username, email, password) VALUES(?, ?, ?)";

		try (PreparedStatement prepStatement = prepareStatementWithReturnKeys(query);) {
			prepStatement.setString(1, user.getUsername());
			prepStatement.setString(2, user.getEmail());
			prepStatement.setBytes(3, user.getPassword());

			prepStatement.executeUpdate();
			
			ResultSet result = prepStatement.getGeneratedKeys();
			result.next();
			
			user.setId(result.getInt(1));
		}
	}

	public boolean areSignUpInfoTaken(String username, String email) throws SQLException {
		String query = "SELECT * FROM user WHERE (username = ? OR email = ?)";

		try (PreparedStatement prepStatement = prepareStatement(query);) {
			prepStatement.setString(1, username);
			prepStatement.setString(2, email);

			try (ResultSet result = prepStatement.executeQuery();) {
				return result.next();
			}
		}
	}
}

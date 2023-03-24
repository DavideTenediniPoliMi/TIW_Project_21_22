package it.polimi.tiw.documents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DAO {
	private Connection connection;
	
	public DAO(Connection connection) {
		this.connection = connection;
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}
	
	public PreparedStatement prepareStatementWithReturnKeys(String query) throws SQLException {
		return connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}
}

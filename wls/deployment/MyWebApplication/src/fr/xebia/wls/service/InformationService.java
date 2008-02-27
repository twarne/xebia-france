package fr.xebia.wls.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class InformationService {

	private String jndiName;

	private DataSource ds;
	private Connection connection;

	public InformationService(String jndiName) {
		super();
		this.jndiName = jndiName;
	}

	public DataSource getDataSource() throws Exception {
		ds = (DataSource) new InitialContext().lookup(jndiName);
		return ds;
	}

	public Connection getConnection() throws Exception {
		connection = ds.getConnection();
		return connection;
	}

	public String getURL() throws Exception {
		return connection.getMetaData().getURL();
	}

	public String getUserName() throws Exception {
		return connection.getMetaData().getUserName();
	}

	public void close() {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}

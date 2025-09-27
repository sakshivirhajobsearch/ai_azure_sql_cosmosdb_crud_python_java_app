package com.ai.azure.sql.cosmosdb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AzureSQLRepository {

	private String url, user, password;

	public AzureSQLRepository(Properties config) {
		this.url = config.getProperty("azure_sql_url");
		this.user = config.getProperty("azure_sql_user");
		this.password = config.getProperty("azure_sql_password");
	}

	public List<String> fetchUsers() {
		List<String> users = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT name FROM Users");
			while (rs.next()) {
				users.add(rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	// Add insert/update/delete methods similarly
}

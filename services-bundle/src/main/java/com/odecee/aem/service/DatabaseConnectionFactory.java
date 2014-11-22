package com.odecee.aem.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;

public interface DatabaseConnectionFactory {
	
	Connection getConnection() throws SQLException;
	
	void closeConnection(Connection con);
	
	<T> List<T> runQuery(String query, final String path, final ResourceResolver resourceResolver, final String resourceType, Object... args);
	
	<T> List<T> runQuery(String sql, RowMapper<T> rowMapper, Object...args);
	
	public static interface RowMapper<T> {
		T mapRow(ResultSet rs) throws SQLException;
	}
	
}

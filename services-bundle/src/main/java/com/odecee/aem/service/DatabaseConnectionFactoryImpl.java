package com.odecee.aem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Deactivate;

import com.odecee.aem.DatabaseResourceConfiguration;

@Component(metatype = false)
@Service
public class DatabaseConnectionFactoryImpl implements DatabaseConnectionFactory {
	
	private final static Logger log = LoggerFactory.getLogger(DatabaseConnectionFactoryImpl.class);
	
	@Reference
	private DatabaseResourceConfiguration config;
	
	private PGConnectionPoolDataSource ds;
	
	@Activate
	public void activate() {
		this.ds = new PGConnectionPoolDataSource();
		this.ds.setServerName(config.getHost());
		this.ds.setPortNumber(Integer.parseInt(config.getPort()));
		this.ds.setDatabaseName(config.getDatabaseName());
		this.ds.setUser(config.getUsername());
		this.ds.setPassword(config.getPassword());
	}
	
	@Deactivate
	public void deactivate() {
	}

	@Override
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	
	@Override
	public void closeConnection(Connection con) {
		if(con != null) {
			try { con.close(); } catch (SQLException e) {}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> runQuery(String query, final String path, final ResourceResolver resourceResolver, final String resourceType, Object... args) {
		return (List<T>) runQuery(query, new RowMapper<Resource>() {
			public Resource mapRow(ResultSet rs) throws SQLException {
				
		    	ResultSetMetaData rsmd = rs.getMetaData();
		    	ResourceMetadata resourceMetaData = new ResourceMetadata();
		    	for(int i=1;i<=rsmd.getColumnCount();i++) {
		    		log.info("Column Name: " + rsmd.getColumnName(i));
		    		log.info("Value: " + rs.getObject(i));
		    		resourceMetaData.put(rsmd.getColumnName(i).toLowerCase(), rs.getObject(i));
		    	}
		    	
		    	resourceMetaData.setResolutionPath(path);
			    Resource resource = new SyntheticResource(resourceResolver, resourceMetaData, resourceType); 
				
				return resource;
			}
		}, args);
	}

	@Override
	public <T> List<T> runQuery(String sql, RowMapper<T> rowMapper, Object...args) {
		try {
			Connection conn = ds.getConnection();

			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				for(int i=0; i<args.length; i++) {
					log.info("Argument no."+i+": "+args[i]);
					ps.setObject(i+1, args[i]);					
				}
				java.sql.ResultSet rs = ps.executeQuery();

				List<T> retVal = new ArrayList<T>();
				while(rs.next()) {
					retVal.add(rowMapper.mapRow(rs));
				}

				rs.close();
				ps.close();

				return retVal;
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("Error getting resource for SQL: " + sql, e);
			throw new RuntimeException("Error getting resource", e);
		}			
	}

}
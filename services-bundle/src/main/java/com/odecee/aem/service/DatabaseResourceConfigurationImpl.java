package com.odecee.aem.service;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.odecee.aem.DatabaseResourceConfiguration;

@Component(immediate = true, metatype = true)
@Service
public class DatabaseResourceConfigurationImpl implements DatabaseResourceConfiguration {
	
	public static final String PROP_HOST_DEFAULT = "localhost";
	public static final String PROP_PORT_DEFAULT = "5432";
	public static final String PROP_DATABASE_TYPE_DEFAULT = "postgres";
	public static final String PROP_DATABASE_NAME_DEFAULT = "postgres";
	public static final String PROP_USERNAME_DEFAULT = "zorannikdovski";
	public static final String PROP_PASSWORD_DEFAULT = "odecee";	
	
	@Property(value = PROP_HOST_DEFAULT, label = "Database Host")
	public static final String PROP_HOST = "database.host";
	
	@Property(value = PROP_PORT_DEFAULT, label = "Database Post")
	public static final String PROP_PORT = "database.port";
	
	@Property(value = PROP_DATABASE_TYPE_DEFAULT, label = "Database Type")
	public static final String PROP_DATABASE_TYPE = "database.type";
	
	@Property(value = PROP_DATABASE_NAME_DEFAULT, label = "Database Name")
	public static final String PROP_DATABASE_NAME = "database.name";
	
	@Property(value = PROP_USERNAME_DEFAULT, label = "Database Username")
	public static final String PROP_USERNAME = "database.username";
	
	@Property(value = PROP_PASSWORD_DEFAULT, label = "Database Password")
	public static final String PROP_PASSWORD = "databse.password";
	
	private String host;
	private String port;
	private String databaseType;
	private String databaseName;
	private String userName;
	private String password;

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getPort() {
		return port;
	}

	@Override
	public String getDatabaseType() {
		return databaseType;
	}

	@Override
	public String getDatabaseName() {
		return databaseName;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getConnectionString() {
		return "jdbc:" + getDatabaseType() + "://" + getHost() + ":" + getPort() + "/" + getDatabaseName();
	}
	
	@Activate
	protected void activate(final Map<String, Object> props) {
		host = PropertiesUtil.toString(props.get(PROP_HOST), PROP_HOST_DEFAULT);
		port = PropertiesUtil.toString(props.get(PROP_PORT), PROP_PORT_DEFAULT);
		databaseType = PropertiesUtil.toString(props.get(PROP_DATABASE_TYPE), PROP_DATABASE_TYPE_DEFAULT);
		databaseName = PropertiesUtil.toString(props.get(PROP_DATABASE_NAME), PROP_DATABASE_NAME_DEFAULT);
		userName = PropertiesUtil.toString(props.get(PROP_USERNAME), PROP_USERNAME_DEFAULT);
		password = PropertiesUtil.toString(props.get(PROP_PASSWORD), PROP_PASSWORD_DEFAULT);
	}

}

package com.odecee.aem;

public interface DatabaseResourceConfiguration {

	String getHost();
	String getPort();
	String getDatabaseType();
	String getDatabaseName();
	String getUsername();
	String getPassword();
	String getConnectionString();
}


/**
 * 
 */
package com.ejie.uda.utils;

/**
 *
 */
public class ConnectionData {

	
	private String sid;
	private String host;
	private String portNumber;
	private String schema;
	private String catalog;
	private String userName;
	private String password;
	private String url;
	
	

	/**
	 * 
	 */
	public ConnectionData() {
		
	}

	public ConnectionData(String sid, String host, String portNumber, String schema, String catalog,
			String userName, String password, String url) {
		this.sid = sid;
		this.host = host;
		this.portNumber = portNumber;
		this.schema = schema;
		this.catalog = catalog;
		this.userName = userName;
		this.password = password;
		this.url = url;
	}
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

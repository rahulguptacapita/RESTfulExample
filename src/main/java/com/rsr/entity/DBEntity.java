package com.rsr.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

abstract public class DBEntity {

    public enum DatabaseType { ORACLE, SQLSERVER }
   
    private String host = "localhost";
    private String port = "1433";  //
    private String sid = "DEV";   // SID
    private String username = "dev"; // DATABASE USERNAME
    private String password = "Integra100";  // DATABASE PASSWORD
    public DatabaseType databseType = DatabaseType.SQLSERVER;

    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    
    public void setPort(String port) {
        this.port = port;
    }

    
    public String getSid() {
        return sid;
    }

    
    public void setSid(String sid) {
        this.sid = sid;
    }

    
    public String getUsername() {
        return username;
    }
    
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    

    public void setPassword(String password) {
        this.password = password;
    }

    
    public DatabaseType getDatabseType() {
        return databseType;
    }
    
    public void setDatabseType(DatabaseType databseType) {
        this.databseType = databseType;
    }

    public  Connection getConnection() {

        Connection conn = null;

        try {
            switch (getDatabseType()) {
            case ORACLE:
                Class.forName("oracle.jdbc.driver.OracleDriver");
                break;
            case SQLSERVER:
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                break;
            }
            conn = DriverManager.getConnection(buildURL(), getUsername(), getPassword());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return conn;
    }

    private String buildURL() {

        String oracleURL = "jdbc:oracle:thin:@<HOST>:<PORT>:<SID>";
        String sqlURL = "jdbc:sqlserver://<HOST>:<PORT>;database=<SID>";

        String replacedURL = null;

        switch (getDatabseType()) {

        case ORACLE:
            replacedURL = oracleURL;
            break;
        case SQLSERVER:
            replacedURL = sqlURL;
            break;
        }

        replacedURL = replacedURL.replaceFirst("<HOST>", getHost());
        replacedURL = replacedURL.replaceFirst("<PORT>", getPort());
        replacedURL = replacedURL.replaceFirst("<SID>", getSid());

        return replacedURL;
    }

    protected void closeConn(Connection conn, Statement stat, ResultSet rs) {
    	if (rs != null) {
		    try { rs.close(); } catch (Exception e) { /**/ }
		}
		if (stat != null) {
		    try { stat.close(); } catch (Exception e) { /**/ }
		}
		if (conn != null) {
		    try { conn.close(); } catch (Exception e) { /**/ }
		}
	}
    
    public abstract void validateEntity(JSONObject entity, String primaryKey) throws JSONException;
    
    public abstract JSONArray getEntities(String tableName) throws JSONException;
	public abstract void postEntity(String tableName, String primaryKey, JSONObject entity) throws JSONException;
	public abstract JSONObject deleteEntity(String tableName, String id) throws JSONException;
	public abstract JSONObject getEntity(String tableName, String id) throws JSONException;

	public abstract void putEntity(String tableName, String primaryKey, JSONObject entity, String id) throws JSONException;
	
}

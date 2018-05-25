package com.rsr.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rsr.entity.DBEntity.DatabaseType;

abstract public class DBEntity {

    public enum DatabaseType { ORACLE, SQLSERVER }
//
//    private String host = "";
//    private String port = "";  //
//    private String sid = "";   // SID
//    private String username = ""; // DATABASE USERNAME
//    private String password = "";  // DATABASE PASSWORD
//    public DatabaseType databseType = DatabaseType.ORACLE;
   
    private String host = "localhost";
    private String port = "1433";  //
    private String sid = "DEV";   // SID
    private String username = "dev"; // DATABASE USERNAME
    private String password = "Integra100";  // DATABASE PASSWORD
    public DatabaseType databseType = DatabaseType.SQLSERVER;
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }
    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }
    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }
    /**
     * @param sid the sid to set
     */
    public void setSid(String sid) {
        this.sid = sid;
    }
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the databseType
     */
    public DatabaseType getDatabseType() {
        return databseType;
    }
    /**
     * @param databseType the databseType to set
     */
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
    
	public abstract JSONArray getEntity(String tableName) throws JSONException;
	public abstract void postEntity(String tableName, String primaryKey, JSONObject entity) throws JSONException;
	public abstract JSONArray deleteEntity(String tableName, String id);	

}

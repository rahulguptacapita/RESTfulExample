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
	
	
	
	public static int getNextNo(Connection conn, String tableName, String columnName, String columnCaption) {

        String lastNoAsString = getLastNo(conn, tableName, columnName);

        int lastNoInDb = 0;
        if (lastNoAsString == null) {
            addLastNoSetting(conn, tableName, columnName, columnCaption);
        } else if (lastNoAsString.length() > 0) {
            lastNoInDb = Integer.parseInt(lastNoAsString);
        }

        for (int i = 1; i <= 100; i++) {

            int nextNo = lastNoInDb + 1;

            if (noHasBeenUsed(conn, tableName, columnName, nextNo)) {
                nextNo = getMaxNo(conn, tableName, columnName) + 1;
            }

            if (setLastNo(conn, tableName, columnName, lastNoInDb, nextNo)) {
                return nextNo;
    		} else {
    			lastNoInDb = Integer.parseInt(getLastNo(conn, tableName, columnName));
            }

        }
        throw new RuntimeException("Failed to get next number after 100 attempts");
    }
	
	   private static boolean setLastNo(Connection conn, String tableName, String columnName, int lastNo, int nextNo) {

	        Statement stat = null;
	        try {
	            String sql = "update cau_settings set setting_value = '" + nextNo + "' where section = 'LAST_USED_NOS' and setting = '"
	                    + tableName + "." + columnName + "' and setting_value = '" + lastNo + "'";
	            stat = conn.createStatement();
	            if (stat.executeUpdate(sql) == 1) {
	                return true;
	            }
	        } catch (SQLException e) {
	            throw new RuntimeException(e.getMessage(), e);
	        } finally {
	        }
	        return false;
	    }
	
    private static int getMaxNo(Connection conn, String tableName, String columnName) {

        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = conn.createStatement();
            rs = stat.executeQuery("select max(" + columnName + ") from " + tableName);
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
        }

    }

    private static boolean noHasBeenUsed(Connection conn, String tableName, String columnName, int nextNo) {
        boolean noHasBeenUsed = false;
        Statement stat = null;
        ResultSet rs = null;
        try {
            String sql = "select count(*) from " + tableName + " where " + columnName + " = " + nextNo;
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);
            rs.next();
            if (rs.getInt(1) > 0) {
                noHasBeenUsed = true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
        }
        return noHasBeenUsed;
    }

    private static void addLastNoSetting(Connection conn, String tableName, String columnName, String columnCaption) {
        Statement stat = null;

        try {
            String sql = "insert into cau_settings (section, setting, setting_seq, setting_value, setting_prompts, setting_descr, setting_format, application_id) "
                    + "values ('LAST_USED_NOS','"
                    + tableName
                    + "."
                    + columnName
                    + "',0,'0','"
                    + columnCaption
                    + "','Last Used Number for "
                    + columnCaption + "','', '" + tableName.substring(0, 3) + "')";
            stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
        	//closeConn(Connection conn, Statement stat, ResultSet rs)
        }
    }
	
	   private static String getLastNo(Connection conn, String tableName, String columnName) {
	        String lastNo = null;
	        Statement stat = null;
	        ResultSet rs = null;
	        try {
	            String sql = "select setting_value from cau_settings where section = 'LAST_USED_NOS' and setting = '" + tableName + "."
	                    + columnName + "'";
	            stat = conn.createStatement();
	            rs = stat.executeQuery(sql);
	            if (rs.next()) {
	                lastNo = rs.getString("setting_value");
	                if (lastNo == null) {
	                    lastNo = "";
	                }
	            }
	        } catch (SQLException e) {
	            throw new RuntimeException(e.getMessage(), e);
	        } finally {
	        }
	        return lastNo;
	    }

	
}

package com.rsr.entity;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rsr.entity.DBEntity.DatabaseType;

public class TstClaimHeaderEntity extends DBEntity {

	public String claimId;
	public String empId;
	public String claimDateForm;
	public String costCentre;
	public String whoSetup;
	public String dateSetup;
	public String whoAmended;
	public String dateAmended;

	public JSONArray getEntity(String tableName) throws JSONException {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		JSONArray array = new JSONArray();

		try {
			conn = getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from " + tableName);

			while (rs.next()) {
				JSONObject object = new JSONObject();
				for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {

					if (rs.getObject(entityColumn.name()) != null) {
						object.put(entityColumn.name(), getObjectForColumn(rs, entityColumn));
					} else {
						object.put(entityColumn.name(), "");
					}
				}
				array.put(object);
			}

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		} finally {
			closeConn(conn, stat, rs);
		}
		return array;
	}

	private Object getObjectForColumn(ResultSet rs, TstClaimHeaderEntityColumn entityColumn) throws SQLException {

		int columnType = entityColumn.getColumnType();
		switch (columnType) {
		case Types.VARCHAR:
		case Types.LONGNVARCHAR:
		case Types.CHAR:
		case Types.NVARCHAR:
		case Types.NCHAR:
			return rs.getString(entityColumn.name());
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return rs.getDate(entityColumn.name());
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.NUMERIC:
			return rs.getInt(entityColumn.name());
		case Types.DOUBLE:
			return rs.getDouble(entityColumn.name());
		case Types.FLOAT:
			return rs.getFloat(entityColumn.name());
		case Types.BLOB:
			return rs.getBlob(entityColumn.name());
		default:
			return rs.getObject(entityColumn.name());
		}
	}

	@Override
	public void postEntity(String tableName, String primaryKey, JSONObject entity) throws JSONException {

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			
			entity.remove(primaryKey);
			entity.put(primaryKey, getRandomPrimaryKey());

			
			String sql = prepareQuery(tableName, primaryKey, entity);
			System.out.println(sql);
			// Statement stmt = conn.createStatement();
			// stmt.executeUpdate(sql);

			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			//int executeUpdate = pstmt.executeUpdate();
			//doAnything();
			
			
			 int index = 1;
			 
			 for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {
				 //setValues(pstmt, index, entityColumn.name(), entity.get(entityColumn.name()));
			 	 pstmt.setObject(index,  entity.get(entityColumn.name()));
				 index++;
			 }
			 
//			 while (keys.hasNext()) {
//				 String key = (String) keys.next();
//				 
//				// pstmt.setObject(index, x);
//			 	 setValues(pstmt, index, key, entity.get(key));
//			 	 index++;
//			 }
		 
			pstmt.execute();
			
			
			System.out.println(sql);

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		} finally {
			closeConn(conn, stat, rs);
		}
	}

	private void setValues(PreparedStatement pstmt, int index, String key, Object value) throws SQLException {

		TstClaimHeaderEntityColumn column = TstClaimHeaderEntityColumn.valueOf(key);
		
		switch (column.getColumnType()) {
		case Types.VARCHAR:
		case Types.LONGNVARCHAR:
		case Types.CHAR:
		case Types.NVARCHAR:
		case Types.NCHAR:
			pstmt.setString(index, (String) value);
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			
			pstmt.setDate(index, new Date(0l));
			break;
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.NUMERIC:
			pstmt.setInt(index, (int) value);
			break;
		case Types.DOUBLE:
			pstmt.setDouble(index, (Double) value);
			
			
			break;
		case Types.FLOAT:
			pstmt.setFloat(index, (Float) value);
			break;
		default:
			pstmt.setObject(index, value);
		}
	}

	private String prepareQuery(String tableName, String primaryKey, JSONObject entity)
			throws SQLException, JSONException {
		String sql;
		String columns = "(";
		String values = "(";

		for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {

	//		String val = "";

//			if (primaryKey.equalsIgnoreCase(entityColumn.name())) {
//				continue;
//			}

//			if (entity.get(entityColumn.name()) != null) {
//				String tempString = (String) entity.get(entityColumn.name());
//				if (tempString.isEmpty()) {
//					continue;
//				}
//			} else {
//				continue;
//			}
//
//			if (entityColumn.getColumnType() == Types.INTEGER) {
//				val = entity.get(entityColumn.name()) + "";
//			} else {
//				val = "'" + entity.get(entityColumn.name()) + "'";
//			}

			columns = columns + entityColumn.name();
			//values = values + val;
			 values = values + "?";
			columns = columns + ",";
			values = values + ",";
		}

		columns = columns.substring(0, columns.length() - 1);
		values = values.substring(0, values.length() - 1);

		columns = columns + ")";
		values = values + " )";

		sql = "INSERT INTO " + tableName + columns + " VALUES" + values;
		return sql;
	}

	private String getRandomPrimaryKey() {
		Random rnd = new Random();
		String n = 100000 + rnd.nextInt(900000) + "";
		return n;
	}

	@Override
	public JSONArray deleteEntity(String tableName, String id) {
		
		
		return null;
	}

	// @Override
	// public String getPrimaryKey(String tableName) {
	// Connection conn = null;
	// Statement stat = null;
	// ResultSet rs = null;
	//
	// try {
	// conn = getConnection();
	// DatabaseMetaData metaData = conn.getMetaData();
	// rs = metaData.getPrimaryKeys(null, null, tableName);
	//
	// if(rs.next()) {
	// return rs.getString(1);
	// }
	// return null;
	// } catch (SQLException sqle) {
	// throw new RuntimeException(sqle.getMessage(), sqle);
	// } finally {
	// closeConn(conn, stat, rs);
	// }
	// }

}

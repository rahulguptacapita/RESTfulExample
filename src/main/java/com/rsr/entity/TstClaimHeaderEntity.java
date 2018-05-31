package com.rsr.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Random;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TstClaimHeaderEntity extends DBEntity {

	public JSONArray getEntities(String tableName) throws JSONException {
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
			entity.put(primaryKey, getNextNo(conn, tableName, primaryKey, primaryKey));
			String sql = prepareQuery(tableName, primaryKey, entity);
			System.out.println(sql);
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			int index = 1;
			for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {
				pstmt.setObject(index, entity.get(entityColumn.name()));
				index++;
			}

			pstmt.execute();
			System.out.println(sql);

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		} finally {
			closeConn(conn, stat, rs);
		}
	}

	private String prepareQuery(String tableName, String primaryKey, JSONObject entity)
			throws SQLException, JSONException {

		String sql;
		String columns = "(";
		String values = "(";

		for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {
			columns = columns + entityColumn.name();
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
	public JSONObject deleteEntity(String tableName, String id) throws JSONException {

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		JSONObject result = new JSONObject();

		try {
			conn = getConnection();
			stat = conn.createStatement();
			String query = "delete from  " + tableName + " where claim_id like ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			int executeUpdate = pstmt.executeUpdate();

			if (executeUpdate == 0) {
				result.put("message", "No record found");
				return result;
			}

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		} finally {
			closeConn(conn, stat, rs);
		}
		result.put("message", "deleted successfully");
		return result;
	}

	@Override
	public JSONObject getEntity(String tableName, String id) throws JSONException {

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		JSONObject object = new JSONObject();
		try {
			conn = getConnection();
			stat = conn.createStatement();

			String query = "select * from " + tableName + " where claim_id like ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {
					if (rs.getObject(entityColumn.name()) != null) {
						object.put(entityColumn.name(), getObjectForColumn(rs, entityColumn));
					} else {
						object.put(entityColumn.name(), "");
					}
				}
				return object;
			}

			object.put("message", "no recored found");
			return object;

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		} finally {
			closeConn(conn, stat, rs);
		}
	}

	@Override
	public void putEntity(String tableName, String primaryKey, JSONObject entity, String id) throws JSONException {

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			entity.remove(primaryKey);
			entity.put(primaryKey, id);

			String sql = prepareUpdateQuery(tableName, primaryKey, entity, id);
			System.out.println(sql);

			PreparedStatement pstmt = conn.prepareStatement(sql);
			int index = 1;

			for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {
				if (entityColumn.name().equalsIgnoreCase(primaryKey)) {
					continue;
				}
				pstmt.setObject(index, entity.get(entityColumn.name()));
				index++;
			}

			pstmt.setObject(index, id);
			pstmt.execute();

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		} finally {
			closeConn(conn, stat, rs);
		}
	}

	private String prepareUpdateQuery(String tableName, String primaryKey, JSONObject entity, String id)
			throws JSONException {

		String sql = "";

		for (TstClaimHeaderEntityColumn entityColumn : TstClaimHeaderEntityColumn.values()) {
			if (entityColumn.name().equalsIgnoreCase(primaryKey)) {
				continue;
			}
			sql = sql + entityColumn.name() + " = " + "?,";
		}
		sql = sql.substring(0, sql.length() - 1);

		String whereClause = " where " + primaryKey + " = " + "?";
		String updateQuery = "UPDATE " + tableName + " SET " + sql + whereClause;
		return updateQuery;
	}

	public void validateEntity(JSONObject entity, String primaryKey) throws JSONException {

		for (TstClaimHeaderEntityColumn column : TstClaimHeaderEntityColumn.values()) {
			if (primaryKey.equalsIgnoreCase(column.name())) {
				continue;
			}
			Object columnValue = entity.get(column.name());
			if (columnValue == null) {
				throw new RuntimeException("cannot find " + column.name());
			}
		}
		if (TstClaimHeaderEntityColumn.values().length != entity.length()
				&& TstClaimHeaderEntityColumn.values().length - 1 == entity.length()) {
			throw new RuntimeException("More then enough parameter found");
		}
	}

}

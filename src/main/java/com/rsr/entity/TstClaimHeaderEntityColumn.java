package com.rsr.entity;

import java.sql.Types;

public enum TstClaimHeaderEntityColumn {
	
	CLAIM_ID (Types.VARCHAR),
	 EMP_ID (Types.VARCHAR),
	 CLAIM_DATE_FROM (Types.VARCHAR),
	 COST_CENTRE (Types.VARCHAR),
	 WHO_SETUP (Types.VARCHAR),
	 DATE_SETUP (Types.DATE),
	WHO_AMENDED (Types.VARCHAR),
	DATE_AMENDED (Types.DATE);
	
	private int columnType;
	
	 TstClaimHeaderEntityColumn(int columnType) {
		this.columnType = columnType;	
	 }
	
	 public int getColumnType() {
			return columnType;
	 }
	
}

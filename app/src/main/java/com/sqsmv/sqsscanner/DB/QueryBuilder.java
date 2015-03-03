package com.sqsmv.sqsscanner.DB;

public class QueryBuilder {

	
	private StringBuilder sb;
	
	public QueryBuilder(){
		
		
		
	}
	
	/**
	 * @param tableName
	 * @param tableColumns
	 * @return
	 */
	public String buildInsertQuery(String tableName, String[] tableColumns){
		
		this.sb = new StringBuilder();


		sb.append("INSERT OR REPLACE INTO ");
		sb.append(tableName);
		sb.append(" (");
		
		for (String col : tableColumns){
			
			sb.append(col);
			sb.append(" ,");
			
		}
		
		int len = sb.length();
		sb.delete(len -2, len);
		sb.append(")");
		
		sb.append(" VALUES(");
		
		for(int n = 0; n < tableColumns.length; n++){
			
			sb.append(" ?,");
			
		}
		len = sb.length();
		sb.delete(len -1, len);
		sb.append(")");
		
		
		return sb.toString();
		
	}
	
	/**
	 * @param tableName
	 * @param selCols
	 * @param whereCols
	 * @return
	 */
	public String buildSelectQuery(String tableName, String[] selCols, String whereCols[]){
		
		this.sb = new StringBuilder();
		
		sb.append("SELECT ");
		
		buildColList(selCols);
		sb.append(" FROM ");
		sb.append(tableName);
		
		if(whereCols.length > 0){
		
		sb.append(" WHERE ");
			buildWhereClause(whereCols);
		}
		return sb.toString();
	}

	/**
	 * @param whereCols
	 */
	private void buildWhereClause(String[] whereCols) {
		
		for( String whereCol : whereCols){
			
			sb.append(whereCol);
			sb.append(" = ?, ");

		}
		int len = sb.length();
		sb.delete(len-2, len);
		
	}

	/**
	 * @param columns
	 */
	private void buildColList(String[] columns) {

		
		for (String col : columns){
			
			sb.append(col);
			sb.append(", ");


		}
		int len = sb.length();
		sb.delete(len-2, len);

	}
		
}

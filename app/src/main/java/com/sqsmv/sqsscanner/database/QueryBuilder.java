package com.sqsmv.sqsscanner.database;

public class QueryBuilder
{
	public static String buildInsertQuery(String tableName, String[] tableColumns)
    {
        StringBuilder sb = new StringBuilder();

		sb.append("INSERT OR REPLACE INTO ");
		sb.append(tableName);
		sb.append("(");
		
		for(String col : tableColumns)
        {
			sb.append(col);
			sb.append(", ");
		}
		
		int len = sb.length();
		sb.delete(len - 2, len);
		sb.append(")");
		
		sb.append(" VALUES(");
		
		for(int n = 0; n < tableColumns.length; n++)
        {
			sb.append(" ?,");
		}
		len = sb.length();
		sb.delete(len - 1, len);
		sb.append(")");

		return sb.toString();
	}
	
	public static String buildSelectQuery(String tableName, String[] selCols, String whereCols[])
    {
        StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT ");
		
		buildColList(sb, selCols);
		sb.append(" FROM ");
		sb.append(tableName);
		
		if(whereCols.length > 0)
        {
            sb.append(" WHERE ");
			buildWhereClause(sb, whereCols);
		}
		return sb.toString();
	}

	private static void buildWhereClause(StringBuilder sb, String[] whereCols)
    {
		for(String whereCol : whereCols)
        {
			sb.append(whereCol);
			sb.append(" = ?, ");
		}
		int len = sb.length();
		sb.delete(len - 2, len);
	}

	private static void buildColList(StringBuilder sb, String[] columns)
    {
		for(String col : columns)
        {
			sb.append(col);
			sb.append(", ");
		}
		int len = sb.length();
		sb.delete(len - 2, len);
	}
}

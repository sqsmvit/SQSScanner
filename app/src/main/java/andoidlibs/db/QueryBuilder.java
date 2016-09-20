package andoidlibs.db;

/**
 * The QueryBuilder class is a class containing static methods for building basic insert and select queries for SQL.
 */
public class QueryBuilder
{
    /**
     * Builds an insert query String for the specified table and column names.
     * @param tableName       The array of name of the table to insert into.
     * @param tableColumns    The array of column names of the table to insert into.
     * @return The insert query String that was built.
     */
    public static String buildInsertQuery(String tableName, String[] tableColumns)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT OR REPLACE INTO ");
        sb.append(tableName);
        sb.append("(");

        sb.append(buildColumnList(tableColumns));
        
        sb.append(")");
        
        sb.append(" VALUES(");
        
        for(int count = 0; count < tableColumns.length; count++)
        {
            sb.append("?");
            if(count != tableColumns.length - 1)
            {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * Builds a basic select query String for the specified table, allowing for selection of specific column names and the option of a where clause.
     * @param tableName        The name of the table to select from.
     * @param selectColumns    The array of column names to select.
     * @param whereColumns     The array of column names for the where clause. NULL can be passed in to specify no columns.
     * @return The select query String that was built.
     */
    public static String buildSelectQuery(String tableName, String[] selectColumns, String[] whereColumns)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ");

        sb.append(buildColumnList(selectColumns));
        sb.append(" FROM ");
        sb.append(tableName);
        sb.append(buildWhereClause(whereColumns));
        return sb.toString();
    }

    /**
     * Builds a comma separated list String of column names.
     * @param columns    The array of column names to create the list from.
     * @return The comma separated list String that was built.
     */
    private static String buildColumnList(String[] columns)
    {
        StringBuilder sb = new StringBuilder();
        for(int count = 0; count < columns.length; count++)
        {
            sb.append(columns[count]);
            if(count != columns.length - 1)
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Builds a where clause String from an array of columns.
     * @param whereColumns    The array of column names for the where clause. NULL can be passed in to specify no columns.
     * @return The where clause String that was built, or an empty String if none were specified or the array was NULL.
     */
    private static String buildWhereClause(String[] whereColumns)
    {
        StringBuilder sb = new StringBuilder();
        if(whereColumns != null && whereColumns.length > 0)
        {
            sb.append(" WHERE ");
            for(int count = 0; count < whereColumns.length; count++)
            {
                sb.append(whereColumns[count]);
                sb.append(" = ?");
                if(count != whereColumns.length - 1)
                {
                    sb.append(" AND ");
                }
            }
        }
        return sb.toString();
    }
}

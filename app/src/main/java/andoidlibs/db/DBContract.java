package andoidlibs.db;

/**
 * Interface used primarily for providing information related to a specific table to DBAccess and DBRecord objects.
 */
public interface DBContract
{
    /**
     * Gets the name of the table associated with the DBContract.
     * @return The name of the table.
     */
    String getTableName();

    /**
     * Gets the create statement for the table associated with the DBContract.
     * @return The create statement for the table.
     */
    String getTableCreateString();

    /**
     * Gets the drop statement for the table associated with the DBContract.
     * @return The drop statement for the table.
     */
    String getTableDropString();

    /**
     * Gets the name of the primary key for the table associated with the DBContract.
     * @return The name of the primary key.
     */
    String getPrimaryKeyName();

    /**
     * Gets an array containing the names of all columns for the table associated with the DBContract.
     * @return The array containing the names of all the columns for the table.
     */
    String[] getColumnNames();
}

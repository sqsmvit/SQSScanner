package androidlibs.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * The DBAccess abstract class is used to control access between the user and the database. Ideally each DBAccess object controls access to a single
 * table at a time, as this provides the best interaction between it and the DBRecord class associated with the same table.
 */
public abstract class DBAccess
{
    public static final String NULL_STRING = "__NULL__";
    private SQLiteDatabase db;
    private SQLiteOpenHelper sqliteOpenHelper;
    private DBContract dbContract;

    /**
     * Constructor.
     * @param sqliteOpenHelper    The SQLiteOpenHelper that provides access to the database.
     * @param dbContract          The DBContract subclass containing information on the table to access.
     */
    public DBAccess(SQLiteOpenHelper sqliteOpenHelper, DBContract dbContract)
    {
        this.sqliteOpenHelper = sqliteOpenHelper;
        this.dbContract = dbContract;
    }

    /**
     * Opens writeable access to the database for the DBAccess object.
     */
    public void open()
    {
        db = sqliteOpenHelper.getWritableDatabase();
    }

    /**
     * Resets the table by dropping and recreating it.
     */
    public void reset()
    {
        getDB().execSQL(dbContract.getTableDropString());
        getDB().execSQL(dbContract.getTableCreateString());
    }

    /**
     * Selects all records in the table, requesting all of the columns for that table.
     * @return A Cursor containing all the the request information.
     */
    public Cursor selectAll()
    {
        String[] selectColumns = new String[] {"*"};
        return getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, null), null);
    }

    /**
     * Selects a specific record in the table matched by the unique primary key value, requesting all of the columns for that table.
     * @param pKey    The the unique primary key value to match.
     * @return A Cursor containing all the the request information.
     */
    public Cursor selectByPk(String pKey)
    {
        String[] selectColumns = new String[] {"*"};
        String[] whereColumns = new String[] {getPrimaryKeyName()};
        String[] args = new String[] {pKey};
        return getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns), args);
    }

    /**
     * Inserts a single record into the table.
     * @param record    The DBRecord containing information to insert.
     */
    public void insertRecord(DBRecord record)
    {
        String insertQuery = QueryBuilder.buildInsertQuery(getTableName(), getTableColumns());
        SQLiteStatement query = getDB().compileStatement(insertQuery);
        getDB().beginTransaction();
        for(int count = 0; count < getTableColumns().length; count++)
        {
            String insertValue = record.getTableInsertData()[count];
            if(insertValue.equals(NULL_STRING))
            {
                query.bindNull(count + 1);
            }
            else
            {
                query.bindString(count + 1, insertValue);
            }
        }
        query.executeInsert();
        query.close();
        getDB().setTransactionSuccessful();
        getDB().endTransaction();
    }

    /**
     * Deletes a single record from the table matched by the unique primary key value.
     * @param pKey    The the unique primary key value to match.
     */
    public void deleteByPk(String pKey)
    {
        getDB().delete(dbContract.getTableName(), dbContract.getPrimaryKeyName() + " = ?", new String[]{pKey});
    }

    /**
     * Deletes all the records in the table.
     */
    public void deleteAll()
    {
        getDB().delete(dbContract.getTableName(), null, null);
    }

    /**
     * Gets the name of the table the DBAccess object is associated with.
     * @return The name of the table the DBAccess object is associated with.
     */
    public String getTableName()
    {
        return dbContract.getTableName();
    }

    /**
     * Gets the name of the primary key for the table the DBAccess object is associated with.
     * @return The name of the primary key for the table the DBAccess object is associated with.
     */
    public String getPrimaryKeyName()
    {
        return dbContract.getPrimaryKeyName();
    }

    /**
     * Gets an array containing the names of all columns in the table.
     * @return The array containing the names of all columns in the table.
     */
    public String[] getTableColumns()
    {
        return dbContract.getColumnNames();
    }

    /**
     * Gets the SQLiteDatabase object for access to the database.
     * @return The SQLiteDatabase object for access to the database.
     *
     */
    protected SQLiteDatabase getDB()
    {
        return db;
    }
}

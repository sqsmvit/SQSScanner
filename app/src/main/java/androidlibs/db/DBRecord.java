package androidlibs.db;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

/**
 * The DBRecord abstract class is used for storing and handling information for database records for a single table. It also contains methods that
 * need to be implemented for interaction with the DBAccess class.
 */
public abstract class DBRecord
{
    private DBContract dbContract;

    /**
     * Constructor.
     * @param dbContract    The DBContract subclass containing information on the table the record belongs to..
     */
    public DBRecord(DBContract dbContract)
    {
        this.dbContract = dbContract;
    }

    /**
     * Constructor. Populates the initial data with a Cursor. It is assumed that the Cursor is already pointed at an active row of data.
     * @param dbContract    The DBContract subclass containing information on the table to the record belongs to.
     * @param dbCursor      The Cursor used to populate the data in the DBRecord.
     */
    public DBRecord(DBContract dbContract, Cursor dbCursor)
    {
        this.dbContract = dbContract;
        initRecord();
        if(!dbCursor.isBeforeFirst() && !dbCursor.isAfterLast())
        {
            buildWithCursor(dbCursor);
        }
    }

    /**
     * Sets the DBRecord's fields with default values.
     */
    public abstract void initRecord();

    /**
     * Gets a String array of values for all fields stored. The order of the values should match the one from getColumnNames() in the DBContract class
     * associated with the table this record is for.
     * @return The String array of values for all fields.
     */
    public abstract String[] getTableInsertData();

    /**
     * Populates the data stored with a Cursor. It is assumed that the Cursor is already pointed at an active row of data.
     * @param dbCursor    The Cursor used to populate the data in the DBRecord.
     * @return true if any values were successfully populated, otherwise false.
     */
    public boolean buildWithCursor(Cursor dbCursor)
    {
        boolean success = false;
        List<String> columnList = Arrays.asList(dbContract.getColumnNames());

        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            String columnName = dbCursor.getColumnName(count);
            if(columnList.contains(columnName))
            {
                success = true;
            }
            setByColumnName(columnName, dbCursor.getString(count));
        }
        return success;
    }

    /**
     * Sets the value of a field inside the DBRecord by matching it against the name of the corresponding column in the database.
     * @param columnName    Name of the column to match against.
     * @param value         Value to set the field to.
     */
    protected abstract void setByColumnName(String columnName, String value);
}

package androidlibs.db.xml;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import androidlibs.db.DBRecord;

/**
 * The XMLDBRecord abstract class used for storing and handling information for database records for a single table. It also contains methods that
 * need to be implemented for interaction with the XMLDBAccess class.
 */
public abstract class XMLDBRecord extends DBRecord
{
    private XMLDBContract xmlDBContract;
    private String sha;

    /**
     * Constructor.
     * @param xmlDBContract    The XMLDBContract subclass containing information on the table the record belongs to..
     */
    public XMLDBRecord(XMLDBContract xmlDBContract)
    {
        super(xmlDBContract);
        this.xmlDBContract = xmlDBContract;
    }

    /**
     * Constructor. Populates the initial data with a Cursor. It is assumed that the Cursor is already pointed at an active row of data.
     * @param xmlDBContract    The DBContract subclass containing information on the table to the record belongs to..
     * @param dbCursor         The Cursor used to populate the data in the XMLDBRecord.
     */
    public XMLDBRecord(XMLDBContract xmlDBContract, Cursor dbCursor)
    {
        super(xmlDBContract);
        this.xmlDBContract = xmlDBContract;
        initRecord();
        if(!dbCursor.isBeforeFirst() && !dbCursor.isAfterLast())
        {
            buildWithCursor(dbCursor);
        }
    }

    /**
     * Gets the sha value.
     * @return The sha value.
     */
    public String getSha()
    {
        return sha;
    }

    /**
     * Sets the sha value.
     * @param sha    The sha value to set.
     */
    public void setSha(String sha)
    {
        this.sha = sha;
    }

    @Override
    public boolean buildWithCursor(Cursor dbCursor)
    {
        boolean success = false;
        List<String> columnList = Arrays.asList(xmlDBContract.getColumnNames());

        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            String columnName = dbCursor.getColumnName(count);
            if(columnList.contains(columnName) && !columnName.equals(XMLDBContract.COLUMN_NAME_SHA))
            {
                success = true;
            }
            setByColumnName(columnName, dbCursor.getString(count));
        }
        return success;
    }
}

package andoidlibs.db.xml;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;

import andoidlibs.db.DBAccess;
import andoidlibs.db.QueryBuilder;

/**
 * Abstract class used to control access between the user and the database for tables that require an import from an XML file. All records stored in
 * the XML file are assumed to have a SHA value that are used to figure out when a change has been made to the record.
 */
public abstract class XMLDBAccess extends DBAccess
{
    private XMLDBContract xmlDBContract;

    /**
     * Constructor.
     * @param sqliteOpenHelper    The SQLiteOpenHelper that provides access to the database.
     * @param xmlDBContract       The XMLDBContract subclass containing information on the table to access.
     */
    public XMLDBAccess(SQLiteOpenHelper sqliteOpenHelper, XMLDBContract xmlDBContract)
    {
        super(sqliteOpenHelper, xmlDBContract);
        this.xmlDBContract = xmlDBContract;
    }

    /**
     * Inserts a batch of records stored as an ArrayList<String>.
     * @param batch    Batch of ArrayList<String> records to insert.
     */
    public void insertBatch(ArrayList<ArrayList<String>> batch)
    {
        String insertQuery = new QueryBuilder().buildInsertQuery(getTableName(), getTableColumns());
        SQLiteStatement query = getDB().compileStatement(insertQuery);
        getDB().beginTransaction();

        query.clearBindings();

        for(ArrayList<String> record : batch)
        {
            query.bindAllArgsAsStrings(record.toArray(new String[record.size()]));
            query.executeInsert();
        }
        query.close();
        getDB().setTransactionSuccessful();
        getDB().endTransaction();
    }

    /**
     * Gets a HashMap of sha values mapped to their primary key values from the table associated with the XMLDBAccess object.
     * @return The hashmap of sha values mapped to their primary key values.
     */
    public HashMap<String, String> getSha()
    {
        HashMap<String, String> mapIds= new HashMap<String, String>();

        String tempPK;
        String tempSha;

        String[] queryColumns = {xmlDBContract.getPrimaryKeyName(), XMLDBContract.COLUMN_NAME_SHA};

        Cursor dbCursor = getDB().query(xmlDBContract.getTableName(), queryColumns, null, null, null, null, null);

        if(dbCursor.moveToFirst())
        {
            int pkCol = dbCursor.getColumnIndex(xmlDBContract.getPrimaryKeyName());
            int shaCol = dbCursor.getColumnIndex(XMLDBContract.COLUMN_NAME_SHA);
            do
            {
                tempPK = dbCursor.getString(pkCol);
                tempSha = dbCursor.getString(shaCol);

                mapIds.put(tempPK, tempSha);
            }
            while(dbCursor.moveToNext());
        }
        dbCursor.close();
        return mapIds;
    }

    /**
     * Gets the name of the XML import file used to populate the table associated with the XMLDBAccess object.
     * @return The name of the XML import file.
     */
    public String getXMLFileName()
    {
        return xmlDBContract.getXMLFileName();
    }
}

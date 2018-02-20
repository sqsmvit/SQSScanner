package androidlibs.db.xml;

import androidlibs.db.DBContract;

/**
 * The XMLDBContract interface is used for providing information related to a specific table and the import file for that table to XMLDBAccess
 * and XMLDBRecord objects.
 */
public interface XMLDBContract extends DBContract
{
    String COLUMN_NAME_SHA = "sha";

    /**
     * Gets the name of the XML import file used to populate the table associated with the XMLDBContract.
     * @return The name of the XML import file.
     */
    String getXMLFileName();
}

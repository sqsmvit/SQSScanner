package com.sqsmv.sqsscanner.database.lens;

import androidlibs.db.xml.XMLDBContract;

public class LensContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "Lens";
    protected static final String XML_FILE_NAME = "lens.xml";
    protected static final String COLUMN_NAME_LENSID = "lensid";
    protected static final String COLUMN_NAME_NAME = "name";
    protected static final String COLUMN_NAME_DESCRIPTION = "description";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                COLUMN_NAME_LENSID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_NAME + " TEXT, " +
                COLUMN_NAME_DESCRIPTION + " TEXT, " +
                COLUMN_NAME_SHA + " TEXT);";
    }
    
    @Override
    public String getTableDropString()
    {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    @Override
    public String getPrimaryKeyName()
    {
        return COLUMN_NAME_LENSID;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_LENSID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_DESCRIPTION,
                COLUMN_NAME_SHA
        };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}

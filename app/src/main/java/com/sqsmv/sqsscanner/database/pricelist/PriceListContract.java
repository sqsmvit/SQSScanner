package com.sqsmv.sqsscanner.database.pricelist;

import andoidlibs.db.xml.XMLDBContract;

public class PriceListContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "PriceList";
    protected static final String XML_FILE_NAME = "pricelist.xml";
    protected static final String COLUMN_NAME_PRICELISTID = "pricelistid";
    protected static final String COLUMN_NAME_PRICELIST = "pricelist";
    protected static final String COLUMN_NAME_ACTIVE = "active";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                COLUMN_NAME_PRICELISTID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_PRICELIST + " TEXT, " +
                COLUMN_NAME_ACTIVE + " INTEGER, " +
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
        return COLUMN_NAME_PRICELISTID;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_PRICELISTID,
                COLUMN_NAME_PRICELIST,
                COLUMN_NAME_ACTIVE,
                COLUMN_NAME_SHA
        };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}

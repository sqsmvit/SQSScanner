package com.sqsmv.sqsscanner.database.productlens;

import com.sqsmv.sqsscanner.database.XMLDBContract;

public class ProductLensContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "ProductLens";
    protected static final String XML_FILE_NAME = "productlens.xml";
    protected static final String COLUMN_NAME_PRODUCTLENSID = "productlensid";
    protected static final String COLUMN_NAME_MASNUM = "masnum";
    protected static final String COLUMN_NAME_LENSID = "lensid";
    protected static final String COLUMN_NAME_PRICELISTID = "pricelistid";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE " + getTableName() + " (" +
                COLUMN_NAME_PRODUCTLENSID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_MASNUM + " TEXT, " +
                COLUMN_NAME_LENSID + " TEXT, " +
                COLUMN_NAME_PRICELISTID + " TEXT, " +
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
        return COLUMN_NAME_PRODUCTLENSID;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_PRODUCTLENSID,
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_LENSID,
                COLUMN_NAME_PRICELISTID,
                COLUMN_NAME_SHA
        };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}

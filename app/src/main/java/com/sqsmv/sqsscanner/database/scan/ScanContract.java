package com.sqsmv.sqsscanner.database.scan;

import android.provider.BaseColumns;

import androidlibs.db.DBContract;

public class ScanContract implements DBContract, BaseColumns
{
    protected static final String TABLE_NAME = "Scan";
    protected static final String COLUMN_NAME_MASNUM = "masNum";
    protected static final String COLUMN_NAME_QUANTITY = "quantity";
    protected static final String COLUMN_NAME_FKPULLID = "fkPullId";
    protected static final String COLUMN_NAME_SCANDATE = "scanDate";
    protected static final String COLUMN_NAME_TITLE = "title";
    protected static final String COLUMN_NAME_PRICELIST = "priceList";
    protected static final String COLUMN_NAME_RATING = "rating";
    protected static final String COLUMN_NAME_LOCATION = "location";
    protected static final String COLUMN_NAME_NUMBOXES = "numBoxes";
    protected static final String COLUMN_NAME_INITIALS = "initials";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_MASNUM + " TEXT, " +
                COLUMN_NAME_QUANTITY + " TEXT, " +
                COLUMN_NAME_FKPULLID + " TEXT, " +
                COLUMN_NAME_SCANDATE + " TEXT NOT NULL, " +
                COLUMN_NAME_TITLE + " TEXT, " +
                COLUMN_NAME_PRICELIST + " TEXT, " +
                COLUMN_NAME_RATING + " TEXT, " +
                COLUMN_NAME_LOCATION + " TEXT, " +
                COLUMN_NAME_NUMBOXES + " TEXT, " +
                COLUMN_NAME_INITIALS + " TEXT);";
    }

    @Override
    public String getTableDropString()
    {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    @Override
    public String getPrimaryKeyName()
    {
        return _ID;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                _ID,
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_QUANTITY,
                COLUMN_NAME_FKPULLID,
                COLUMN_NAME_SCANDATE,
                COLUMN_NAME_TITLE,
                COLUMN_NAME_PRICELIST,
                COLUMN_NAME_RATING,
                COLUMN_NAME_LOCATION,
                COLUMN_NAME_NUMBOXES,
                COLUMN_NAME_INITIALS
        };
    }
}

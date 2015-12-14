package com.sqsmv.sqsscanner.database;

public interface DBContract
{
    String getTableName();

    String getTableCreateString();

    String getTableDropString();

    String getPrimaryKeyName();

    String[] getColumnNames();
}

package com.sqsmv.sqsscanner.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

//Data Access controls all actual interaction with the database
public abstract class DBAccess
{
    private SQLiteDatabase db;
    private DBAdapter dbAdapter;
    private DBContract dbContract;

    public DBAccess(DBAdapter dbAdapter, DBContract dbContract)
    {
        this.dbAdapter = dbAdapter;
        this.dbContract = dbContract;
    }

    public void open()
    {
        db = dbAdapter.getWritableDatabase();
    }

    public void reset()
    {
        getDB().execSQL(dbContract.getTableDropString());
        getDB().execSQL(dbContract.getTableCreateString());
    }

    public Cursor selectAll()
    {
        String[] selectColumns = new String[]{"*"};
        return getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, null), null);
    }

    public Cursor selectByPk(String pKey)
    {
        String[] selectColumns = new String[]{"*"};
        String[] whereColumns = new String[]{getPrimaryKeyName()};
        String[] args = new String[]{pKey};
        return getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns), args);
    }

    public void insertRecord(DBRecord record)
    {
        String insertQuery = QueryBuilder.buildInsertQuery(getTableName(), getTableColumns());
        SQLiteStatement query = getDB().compileStatement(insertQuery);
        getDB().beginTransaction();
        for(int count = 0; count < getTableColumns().length; count++)
        {
            String insertValue = record.getTableInsertData()[count];
            if(insertValue.equals("null"))
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

    public void deleteByPk(String pKey)
    {
        getDB().delete(dbContract.getTableName(), dbContract.getPrimaryKeyName() + " = ?", new String[]{pKey});
    }

    public void deleteAll()
    {
        getDB().delete(dbContract.getTableName(), null, null);
    }

    public String getTableName()
    {
        return dbContract.getTableName();
    }

    public String getPrimaryKeyName()
    {
        return dbContract.getPrimaryKeyName();
    }

    public String[] getTableColumns()
    {
        return dbContract.getColumnNames();
    }

    protected SQLiteDatabase getDB()
    {
        return db;
    }
}

package com.sqsmv.sqsscanner.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sqsmv.sqsscanner.DB.LensContract.LensTable;

import java.util.ArrayList;
import java.util.HashMap;

public class LensDataSource implements DataSource
{
    private SQLiteDatabase db;
    private DBAdapter dbAdapter;
    private static String DB_TABLE = LensTable.TABLE_NAME;

    public static String[] lensCols =
    {
            LensTable.COLUMN_NAME_PK_LENSID,
            LensTable.COLUMN_NAME_NAME,
            LensTable.COLUMN_NAME_DESCRIPTION,
            LensTable.COLUMN_NAME_SHA
    };

    private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, lensCols);

    private static final String LENSID_QUERY = "Select " + LensTable.COLUMN_NAME_PK_LENSID + " FROM " + DB_TABLE + " WHERE " + LensTable.COLUMN_NAME_NAME + " = ?";

    private static final String LENSNAME_QUERY = "Select " + LensTable.COLUMN_NAME_NAME + " FROM " + DB_TABLE;

    public LensDataSource(Context ctx)
    {
        dbAdapter = new DBAdapter(ctx);
    }

    @Override
    public void open() throws SQLException
    {
        db = dbAdapter.getWritableDatabase();
    }

    public void read() throws SQLException
    {
        db = dbAdapter.getReadableDatabase();
    }

    @Override
    public void close()
    {
        dbAdapter.close();
    }

    @Override
    public HashMap<String, String> getSha()
    {
        HashMap<String, String> mapIds= new HashMap<String, String>();

        String tempPK;
        String tempSha;

        Cursor c = this.db.query(LensDataSource.DB_TABLE, new String[]{LensTable.COLUMN_NAME_PK_LENSID, LensTable.COLUMN_NAME_SHA}, null, null, null,null, null);

        if(c.moveToFirst())
        {
            int pkCol = c.getColumnIndex(LensTable.COLUMN_NAME_PK_LENSID);
            int shaCol = c.getColumnIndex(LensTable.COLUMN_NAME_SHA);
            do
            {
                tempPK = c.getString(pkCol);
                tempSha = c.getString(shaCol);

                mapIds.put(tempPK, tempSha);
            }while(c.moveToNext());
        }
        c.close();
        return mapIds;
    }

    @Override
    public void insertBatch(ArrayList<ArrayList<String>> batch)
    {
        final SQLiteStatement query = this.db.compileStatement(INSERT_QUERY);
        db.beginTransaction();
        try
        {
            query.clearBindings();

            for(ArrayList<String> line : batch)
            {
                for(int i = 0; i < lensCols.length; i++)
                {
                    query.bindString(i+1, line.get(i));
                }
                query.executeInsert();
            }
        }
        finally
        {
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
        }
    }

    public String getLensId(String lensName)
    {
        String[] args = {lensName};
        String lensId = "";

        Cursor cur = db.rawQuery(LENSID_QUERY, args);
        if(cur.moveToFirst())
            lensId = cur.getString(0);

        return lensId;
    }

    public ArrayList<String> getAllLensNames()
    {
        ArrayList<String> lensNames = new ArrayList<String>();
        String[] args = {};

        try
        {
            Cursor cur = db.rawQuery(LENSNAME_QUERY, args);
            if (cur.moveToFirst())
                lensNames.add(cur.getString(0));

            while (cur.moveToNext())
            {
                lensNames.add(cur.getString(0));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return lensNames;
    }

    public void resetDB()
    {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        db.execSQL(DBAdapter.CREATE_TABLE_LENS);
    }
}

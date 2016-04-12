package com.sqsmv.sqsscanner.database.lens;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.DBAdapter;

import java.util.ArrayList;

import andoidlibs.db.QueryBuilder;
import andoidlibs.db.xml.XMLDBAccess;

public class LensAccess extends XMLDBAccess
{
    public LensAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new LensContract());
    }

    public String getLensId(String lensName)
    {
        String[] selectColumns = new String[]{LensContract.COLUMN_NAME_LENSID};
        String[] whereColumns = new String[]{LensContract.COLUMN_NAME_NAME};
        String[] args = {lensName};
        String lensId = "";

        Cursor cur = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns), args);
        if(cur.moveToFirst())
        {
            lensId = cur.getString(0);
        }

        return lensId;
    }

    public ArrayList<String> getAllLensNames()
    {
        ArrayList<String> lensNames = new ArrayList<String>();
        String[] selectColumns = new String[]{LensContract.COLUMN_NAME_NAME};
        String[] whereColumns = new String[]{};

        Cursor cur = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, null), null);
        if (cur.moveToFirst())
        {
            lensNames.add(cur.getString(0));
            while (cur.moveToNext())
            {
                lensNames.add(cur.getString(0));
            }
        }

        return lensNames;
    }
}

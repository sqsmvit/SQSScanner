package com.sqsmv.sqsscanner.database.lens;

import android.content.Context;
import android.database.Cursor;

import com.sqsmv.sqsscanner.DB.LensContract.LensTable;
import com.sqsmv.sqsscanner.database.XMLDBAccess;

import java.util.ArrayList;

public class LensAccess extends XMLDBAccess
{
    public LensAccess(Context context)
    {
        super(context, new LensContract());
    }

    public String getLensId(String lensName)
    {
        String[] args = {lensName};
        String lensId = "";
        String lensIdQuery = "SELECT " + LensTable.COLUMN_NAME_PK_LENSID + " FROM " + LensContract.TABLE_NAME + " WHERE " + LensTable.COLUMN_NAME_NAME + " = ?";

        Cursor cur = getDB().rawQuery(lensIdQuery, args);
        if(cur.moveToFirst())
        {
            lensId = cur.getString(0);
        }

        return lensId;
    }

    public ArrayList<String> getAllLensNames()
    {
        ArrayList<String> lensNames = new ArrayList<String>();
        String[] args = {};
        String lensNameQuery = "SELECT " + LensTable.COLUMN_NAME_NAME + " FROM " + LensContract.TABLE_NAME;
        Cursor cur = getDB().rawQuery(lensNameQuery, args);
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

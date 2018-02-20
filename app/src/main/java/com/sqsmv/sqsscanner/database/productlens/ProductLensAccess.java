package com.sqsmv.sqsscanner.database.productlens;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.DBAdapter;
import androidlibs.db.QueryBuilder;
import androidlibs.db.xml.XMLDBAccess;

public class ProductLensAccess extends XMLDBAccess
{
    public ProductLensAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new ProductLensContract());
    }

    public Cursor selectByMasNumLensId(String masNum, String lensId)
    {
        String[] selectColumns = new String[]{"*"};
        String[] whereColumns = new String[]{ProductLensContract.COLUMN_NAME_MASNUM, ProductLensContract.COLUMN_NAME_LENSID};
        String[] args = new String[]{masNum, lensId};
        return getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns), args);
    }
}

package com.example.sqsscanner.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductLensDataSource implements DataSource
{
    private SQLiteDatabase db;
    private DBAdapter dbAdapter;
    private static String DB_TABLE = ProductLensContract.ProductLensTable.TABLE_NAME;

    public static String[] productLensCols =
            {
                    ProductLensContract.ProductLensTable.COLUMN_NAME_FK_MASNUM,
                    ProductLensContract.ProductLensTable.COLUMN_NAME_FK_LENSID,
                    ProductLensContract.ProductLensTable.COLUMN_NAME_FK_PRICELISTID,
                    ProductLensContract.ProductLensTable.COLUMN_NAME_SHA
            };

    private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, productLensCols);

    public ProductLensDataSource(Context ctx)
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

        String tempMasnum;
        String tempSha;

        Cursor c = this.db.query(ProductLensDataSource.DB_TABLE, new String[]{ProductLensContract.ProductLensTable.COLUMN_NAME_FK_MASNUM, ProductLensContract.ProductLensTable.COLUMN_NAME_SHA}, null, null, null,null, null);

        if(c.moveToFirst())
        {
            int masnumCol = c.getColumnIndex(ProductLensContract.ProductLensTable.COLUMN_NAME_FK_MASNUM);
            int shaCol = c.getColumnIndex(ProductLensContract.ProductLensTable.COLUMN_NAME_SHA);
            do
            {
                tempMasnum = c.getString(masnumCol);
                tempSha = c.getString(shaCol);

                mapIds.put(tempMasnum, tempSha);
            }while(c.moveToNext());
        }

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
                for(int i = 0; i < productLensCols.length; i++)
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

    public void resetDB()
    {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        db.execSQL(DBAdapter.CREATE_TABLE_PRODUCT_LENS);
    }
}

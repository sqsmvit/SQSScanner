package com.sqsmv.sqsscanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sqsmv.sqsscanner.database.lens.LensContract;
import com.sqsmv.sqsscanner.database.pricelist.PriceListContract;
import com.sqsmv.sqsscanner.database.product.ProductContract;
import com.sqsmv.sqsscanner.database.productlens.ProductLensContract;
import com.sqsmv.sqsscanner.database.scan.ScanContract;
import com.sqsmv.sqsscanner.database.upc.UPCContract;


public class DBAdapter extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "PullDB";

    private static final int DATABASE_VERSION = 4;

    private static final XMLDBContract[] xmlContracts = {new ProductContract(), new UPCContract(), new PriceListContract(), new LensContract(), new ProductLensContract()};
    private static final DBContract[] scanContracts = {new ScanContract()};

    public DBAdapter(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        createTables(db, xmlContracts);
        createTables(db, scanContracts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        resetTables(db, xmlContracts);
    }

    public void resetImportData()
    {
        SQLiteDatabase db = getWritableDatabase();
        resetTables(db, xmlContracts);
        db.close();
    }

    private void createTables(SQLiteDatabase db, DBContract[] dbContracts)
    {
        for(DBContract dbContract : dbContracts)
        {
            db.execSQL(dbContract.getTableCreateString());
        }
    }

    private void resetTables(SQLiteDatabase db, DBContract[] dbContracts)
    {
        for(DBContract dbContract : dbContracts)
        {
            db.execSQL(dbContract.getTableDropString());
            db.execSQL(dbContract.getTableCreateString());
        }
    }
}

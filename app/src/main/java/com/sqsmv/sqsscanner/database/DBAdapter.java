package com.sqsmv.sqsscanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sqsmv.sqsscanner.database.lens.LensContract;
import com.sqsmv.sqsscanner.database.pricelist.PriceListContract;
import com.sqsmv.sqsscanner.database.prodloc.ProdLocContract;
import com.sqsmv.sqsscanner.database.product.ProductContract;
import com.sqsmv.sqsscanner.database.productlens.ProductLensContract;
import com.sqsmv.sqsscanner.database.scan.ScanContract;
import com.sqsmv.sqsscanner.database.upc.UPCContract;

import androidlibs.db.DBContract;
import androidlibs.db.xml.XMLDBContract;

/**
 * DBAdapter is a SQLiteOpenHelper subclass specific to this app.
 */
public class DBAdapter extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "PullDB";

    private static final int DATABASE_VERSION = 6;

    private static final XMLDBContract[] xmlContracts = {new ProductContract(), new UPCContract(), new PriceListContract(),
                                                         new LensContract(), new ProdLocContract(), new ProductLensContract()};
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

    /**
     * Drops and recreates all tables with import data.
     */
    public void resetImportData()
    {
        SQLiteDatabase db = getWritableDatabase();
        resetTables(db, xmlContracts);
        db.close();
    }

    /**
     * Creates the tables specified by an array of DBContracts.
     * @param db             The SQLiteDabase to create the tables on.
     * @param dbContracts    The array of DBContracts specifying which tables to create.
     */
    private void createTables(SQLiteDatabase db, DBContract[] dbContracts)
    {
        for(DBContract dbContract : dbContracts)
        {
            db.execSQL(dbContract.getTableCreateString());
        }
    }

    /**
     * Drops and recreates tables specified by an array of DBContracts.
     * @param db             The SQLiteDabase to create the tables on.
     * @param dbContracts    The array of DBContracts specifying which tables to drop and recreate.
     */
    private void resetTables(SQLiteDatabase db, DBContract[] dbContracts)
    {
        for(DBContract dbContract : dbContracts)
        {
            db.execSQL(dbContract.getTableDropString());
            db.execSQL(dbContract.getTableCreateString());
        }
    }
}

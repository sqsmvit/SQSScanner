package com.example.sqsscanner.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sqsscanner.DB.PriceListContract.PriceListTable;
import com.example.sqsscanner.DB.ProductContract.ProductTable;
import com.example.sqsscanner.DB.PullLinesContract.PullLinesTable;
import com.example.sqsscanner.DB.PullListContract.PullListTable;
import com.example.sqsscanner.DB.ScanContract.ScanTable;
import com.example.sqsscanner.DB.UPCContract.UPCTable;
import com.example.sqsscanner.DB.ProductLensContract.ProductLensTable;
import com.example.sqsscanner.DB.LensContract.LensTable;

public class DBAdapter extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "PullDB";
	
	public static final int DATABASE_VERSION = 3;
	
	public static final String NOT_NULL = "NOT NULL";
	public static final String COMMA_SEP = ", ";
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	
	private static final String CREATE_TABLE_PRODUCT = 
			"CREATE TABLE " +
			ProductTable.TABLE_NAME + " (" +
			ProductTable.COLUMN_NAME_PK_MAS_ID + " TEXT PRIMARY KEY" + COMMA_SEP +
			ProductTable.COLUMN_NAME_TITLE_NAME + " TEXT " + NOT_NULL + COMMA_SEP +
			ProductTable.COLUMN_NAME_CATEGORY + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_RATING + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_STREET_DATE + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_TITLE_FILM + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_NO_COVER + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_FK_PRICE_LIST + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_NEW + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_BOXSET + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_MULTIPACK + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_MEDIA_FORMAT + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_PRICE_FILTERS + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_SPECIAL_FIELDS + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_STUDIO + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_SEASON + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_NUMBER_OF_DISCS + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_THEATER_DATE + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_STUDIO_NAME + " TEXT" + COMMA_SEP +
			ProductTable.COLUMN_NAME_SHA + " TEXT " +
			//"REFERENCES " + PriceListTable.TABLE_NAME +
			//	"(" + PriceListTable.COLUMN_NAME_PK_PRICE_LIST +") " +
			");";
			
	private static final String CREATE_TABLE_UPC= 
			"CREATE TABLE " +
			UPCTable.TABLE_NAME + "(" +
			UPCTable.COLUMN_NAME_PK_UPC_ID + " TEXT PRIMARY KEY" + COMMA_SEP +
			UPCTable.COLUMN_NAME_SHA + " TEXT " + COMMA_SEP +
			UPCTable.COLUMN_NAME_FK_MAS_ID + " TEXT " + 
			//"REFERENCES " + ProductTable.TABLE_NAME + "(" + ProductTable.COLUMN_NAME_PK_MAS_ID + ")" +
			");";
	
	private static final String CREATE_TABLE_PRICE_LIST = 
			"CREATE TABLE " +
			PriceListTable.TABLE_NAME + "(" +
            PriceListTable.COLUMN_NAME_PK_PRICE_LIST + " TEXT PRIMARY KEY" + COMMA_SEP +
            PriceListTable.COLUMN_NAME_PRICELISTNAME + " TEXT " + COMMA_SEP +
			PriceListTable.COLUMN_NAME_ACTIVE + " INTEGER" + COMMA_SEP +
            PriceListTable.COLUMN_NAME_SHA + " TEXT " +
			");";
	
	private static final String CREATE_TABLE_PULL_LIST = 
			"CREATE TABLE " +
			PullListTable.TABLE_NAME + "(" +
			PullListTable.COLUMN_NAME_PK_PULL_ID + " STRING PRIMARY KEY" + COMMA_SEP +
			PullListTable.COLUMN_NAME_NAME + " TEXT" + COMMA_SEP +
			PullListTable.COLUMN_NAME_PULLED_FOR + " TEXT" + COMMA_SEP +
			PullListTable.COLUMN_NAME_JOB_NUMBER + " INTEGER" + COMMA_SEP +
			PullListTable.COLUMN_NAME_SCHEDULED_DATE + " TEXT" + COMMA_SEP +
			PullListTable.COLUMN_NAME_MANUAL_QTY + " INTEGER" + COMMA_SEP +
			PullListTable.COLUMN_NAME_PULL_QTY + " INTEGER" +  COMMA_SEP +
            PullListTable.COLUMN_NAME_FKLENS + " TEXT" + COMMA_SEP +
            PullListTable.COLUMN_NAME_FKPRICELIST + " TEXT" + COMMA_SEP +
			PullListTable.COLUMN_NAME_SHA + " TEXT " +
			");";
	
	private static final String CREATE_TABLE_PULL_LINES = 
			"CREATE TABLE " +
			PullLinesTable.TABLE_NAME + "(" +
			PullLinesTable.COLUMN_NAME_FK_PULL_ID + " INTEGER " + 
				"REFERENCES " + PullListTable.TABLE_NAME  + "(" + PullListTable.COLUMN_NAME_PK_PULL_ID + ")" +
			COMMA_SEP +
			PullLinesTable.COLUMN_NAME_FK_MASNUM + " TEXT " +
				"REFERENCES " + ProductTable.TABLE_NAME + "(" + ProductTable.COLUMN_NAME_PK_MAS_ID + ")" +
			COMMA_SEP +
			PullLinesTable.COLUMN_NAME_SCHEDULED_QTY + " INTEGER NOT NULL" + COMMA_SEP +
			PullLinesTable.COLUMN_NAME_SHA + " TEXT " + COMMA_SEP +
			" PRIMARY KEY (" + PullLinesTable.COLUMN_NAME_FK_PULL_ID + COMMA_SEP + PullLinesTable.COLUMN_NAME_FK_MASNUM + ")" +
			")";

    public static final String CREATE_TABLE_PRODUCT_LENS =
            "CREATE TABLE " +
            ProductLensTable.TABLE_NAME + "(" +
            ProductLensTable.COLUMN_NAME_PK_PRODUCTLENS + " TEXT PRIMARY KEY " + COMMA_SEP +
            ProductLensTable.COLUMN_NAME_FK_MASNUM + " TEXT " + COMMA_SEP +
            ProductLensTable.COLUMN_NAME_FK_LENSID + " TEXT " + COMMA_SEP +
            ProductLensTable.COLUMN_NAME_FK_PRICELISTID + " TEXT" + COMMA_SEP +
            ProductLensTable.COLUMN_NAME_SHA + " TEXT" + ")";

    public static final String CREATE_TABLE_LENS =
            "CREATE TABLE " +
            LensTable.TABLE_NAME + "(" +
            LensTable.COLUMN_NAME_PK_LENSID + " TEXT PRIMARY KEY " + COMMA_SEP +
            LensTable.COLUMN_NAME_NAME + " TEXT " + COMMA_SEP +
            LensTable.COLUMN_NAME_DESCRIPTION + " TEXT" + COMMA_SEP +
            LensTable.COLUMN_NAME_SHA + " TEXT" +
            ")";
	
	private static final String CREATE_TABLE_SCANS =
            "CREATE TABLE "+
			ScanTable.TABLE_NAME + " (" + ScanTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			ScanTable.COLUMN_NAME_SCAN_ENTRY + " TEXT" + " NOT NULL" + COMMA_SEP +
			ScanTable.COLUMN_NAME_FK_PULL_ID + " TEXT" + " REFERENCES " + PullListTable.TABLE_NAME + "(" + PullListTable.COLUMN_NAME_PK_PULL_ID + ")"
			+ COMMA_SEP +
			ScanTable.COLUMN_NAME_QUANTITY + " TEXT" + COMMA_SEP +
			ScanTable.COLUMN_NAME_DATE + " TEXT" + " NOT NULL" +  COMMA_SEP +
			ScanTable.COLUMN_NAME_MARK_ID + " TEXT" + COMMA_SEP + 
			ScanTable.COLUMN_NAME_TITLE + " TEXT " + COMMA_SEP + 
			ScanTable.COLUMN_NAME_PRICE_LIST + " TEXT " + COMMA_SEP + 
			ScanTable.COLUMN_NAME_MASNUM + " TEXT " + COMMA_SEP + 
			ScanTable.COLUMN_NAME_PRICEFILTERS + " TEXT " + COMMA_SEP + 
			ScanTable.COLUMN_NAME_RATING + " TEXT " + COMMA_SEP +
            ScanTable.COLUMN_NAME_LOCATION + " TEXT " +
            ")";
	
	public DBAdapter(Context ctx)
    {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
    {
		db.execSQL(CREATE_TABLE_PRODUCT);
		db.execSQL(CREATE_TABLE_UPC);
		db.execSQL(CREATE_TABLE_PRICE_LIST);
		db.execSQL(CREATE_TABLE_LENS);
        db.execSQL(CREATE_TABLE_PRODUCT_LENS);
        //db.execSQL(CREATE_TABLE_PULL_LIST);
        //db.execSQL(CREATE_TABLE_PULL_LINES);
        db.execSQL(CREATE_TABLE_SCANS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
		// TODO Auto-generated method stub
		
		db.execSQL(DROP_TABLE + ProductTable.TABLE_NAME);
		db.execSQL(DROP_TABLE + UPCTable.TABLE_NAME);
		db.execSQL(DROP_TABLE + PriceListTable.TABLE_NAME);
        db.execSQL(DROP_TABLE + LensTable.TABLE_NAME);
        db.execSQL(DROP_TABLE + ProductLensTable.TABLE_NAME);
        //db.execSQL(DROP_TABLE + PullListTable.TABLE_NAME);
		//db.execSQL(DROP_TABLE + PullLinesTable.TABLE_NAME);

        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_UPC);
        db.execSQL(CREATE_TABLE_PRICE_LIST);
        db.execSQL(CREATE_TABLE_LENS);
        db.execSQL(CREATE_TABLE_PRODUCT_LENS);
        //db.execSQL(CREATE_TABLE_PULL_LIST);
        //db.execSQL(CREATE_TABLE_PULL_LINES);
	}

    public void resetAll()
    {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(DROP_TABLE + ProductTable.TABLE_NAME);
        db.execSQL(DROP_TABLE + UPCTable.TABLE_NAME);
        db.execSQL(DROP_TABLE + PriceListTable.TABLE_NAME);
        db.execSQL(DROP_TABLE + LensTable.TABLE_NAME);
        db.execSQL(DROP_TABLE + ProductLensTable.TABLE_NAME);
        //db.execSQL(DROP_TABLE + PullListTable.TABLE_NAME);
        //db.execSQL(DROP_TABLE + PullLinesTable.TABLE_NAME);

        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_UPC);
        db.execSQL(CREATE_TABLE_PRICE_LIST);
        db.execSQL(CREATE_TABLE_LENS);
        db.execSQL(CREATE_TABLE_PRODUCT_LENS);
        //db.execSQL(CREATE_TABLE_PULL_LIST);
        //db.execSQL(CREATE_TABLE_PULL_LINES);
    }
}

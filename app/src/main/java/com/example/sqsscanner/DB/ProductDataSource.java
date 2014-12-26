package com.example.sqsscanner.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.sqsscanner.DB.ProductContract.ProductTable;
import com.example.sqsscanner.DB.ProductLensContract.ProductLensTable;
import com.example.sqsscanner.DB.PriceListContract.PriceListTable;
import com.example.sqsscanner.DB.LensContract.LensTable;
import com.example.sqsscanner.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDataSource implements DataSource
{

	private SQLiteDatabase db;
	private DBAdapter dbAdapter;
	private static String DB_TABLE = ProductTable.TABLE_NAME;
		
	private static final String[] prodCols = {
			ProductTable.COLUMN_NAME_PK_MAS_ID,
			ProductTable.COLUMN_NAME_TITLE_NAME,
			ProductTable.COLUMN_NAME_CATEGORY,
			ProductTable.COLUMN_NAME_RATING,
			ProductTable.COLUMN_NAME_STREET_DATE,
			ProductTable.COLUMN_NAME_TITLE_FILM,
			ProductTable.COLUMN_NAME_NO_COVER,
			ProductTable.COLUMN_NAME_FK_PRICE_LIST,
			ProductTable.COLUMN_NAME_NEW,
			ProductTable.COLUMN_NAME_BOXSET,
			ProductTable.COLUMN_NAME_MULTIPACK,
			ProductTable.COLUMN_NAME_MEDIA_FORMAT,
			ProductTable.COLUMN_NAME_PRICE_FILTERS,
			ProductTable.COLUMN_NAME_SPECIAL_FIELDS,
			ProductTable.COLUMN_NAME_STUDIO,
			ProductTable.COLUMN_NAME_SEASON,
			ProductTable.COLUMN_NAME_NUMBER_OF_DISCS,
			ProductTable.COLUMN_NAME_THEATER_DATE,
			ProductTable.COLUMN_NAME_STUDIO_NAME,
			ProductTable.COLUMN_NAME_SHA
	};
	
	private static final String DB_PRODUCT_QUERY = "Select * FROM " + ProductTable.TABLE_NAME + " WHERE " + ProductTable.COLUMN_NAME_PK_MAS_ID + "= ?" ;

    //private static final String DB_PRODUCT_JOIN_QUERY = "Select p.*, COALESCE(prl." + PriceListTable.COLUMN_NAME_PRICELISTNAME + ",'N/A') FROM " + ProductTable.TABLE_NAME + " p " +
    private static final String DB_PRODUCT_JOIN_QUERY = "Select p.*, prl." + PriceListTable.COLUMN_NAME_PRICELISTNAME + " FROM " + ProductTable.TABLE_NAME + " p " +
            "LEFT JOIN " + ProductLensTable.TABLE_NAME + " pl ON p." + ProductTable.COLUMN_NAME_PK_MAS_ID + " = pl." + ProductLensTable.COLUMN_NAME_FK_MASNUM + " AND pl." + ProductLensTable.COLUMN_NAME_FK_LENSID + " = ? " +
            "LEFT JOIN " + PriceListTable.TABLE_NAME + " prl ON pl." + ProductLensTable.COLUMN_NAME_FK_PRICELISTID + " = prl." + PriceListTable.COLUMN_NAME_PK_PRICE_LIST +
            " WHERE p." + ProductTable.COLUMN_NAME_PK_MAS_ID + "= ?" ;

    private static final String DB_PRODUCT_LENS_PRICELIST_QUERY = "Select p.*, COALESCE(prl." + PriceListTable.COLUMN_NAME_PRICELISTNAME + ",'N/A'), COALESCE(l." + LensTable.COLUMN_NAME_NAME + ",'N/A') FROM " + ProductTable.TABLE_NAME + " p " +
            "LEFT JOIN " + ProductLensTable.TABLE_NAME + " pl ON p." + ProductTable.COLUMN_NAME_PK_MAS_ID + " = pl." + ProductLensTable.COLUMN_NAME_FK_MASNUM + " AND pl." + ProductLensTable.COLUMN_NAME_FK_LENSID + " = ? " +
            "LEFT JOIN " + PriceListTable.TABLE_NAME + " prl ON pl." + ProductLensTable.COLUMN_NAME_FK_PRICELISTID + " = prl." + PriceListTable.COLUMN_NAME_PK_PRICE_LIST +
            "LEFT JOIN " + LensTable.TABLE_NAME + " l ON pl." + ProductLensTable.COLUMN_NAME_FK_LENSID + " = l." + LensTable.COLUMN_NAME_PK_LENSID +
            " WHERE p." + ProductTable.COLUMN_NAME_PK_MAS_ID + "= ?" ;
	
	private static final String DB_SHA_QUERY = "SELECT " + ProductTable.COLUMN_NAME_PK_MAS_ID + ", " + ProductTable.COLUMN_NAME_SHA + " FROM " + ProductTable.TABLE_NAME;	
	
	//private static final String DB_SHA_QUERY = new QueryBuilder().buildSelectQuery(DB_TABLE, new String[]{ProductTable.COLUMN_NAME_PK_MAS_ID, ProductTable.COLUMN_NAME_SHA}, new String[]{});
	
	private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, prodCols);
	
	/**
	 * @param ctx
	 */
	public ProductDataSource(Context ctx)
    {
		dbAdapter = new DBAdapter(ctx);
	}
	
	/* (non-Javadoc)
	 * @see com.example.sqsscanner.DB.DataSource#open()
	 */
	public void open() throws SQLException
    {
		db = dbAdapter.getWritableDatabase();
	}
	
	public void read() throws SQLException{
		
		db = dbAdapter.getReadableDatabase();
	}
	
	/* (non-Javadoc)
	 * @see com.example.sqsscanner.DB.DataSource#close()
	 */
	public void close()
    {
		dbAdapter.close();
	}
	
	/* (non-Javadoc)
	 * @see com.example.sqsscanner.DB.DataSource#insertBatch(java.util.ArrayList)
	 */
	@Override
	public void insertBatch(ArrayList<ArrayList<String>> batch)
    {
		final SQLiteStatement query = this.db.compileStatement(INSERT_QUERY);
		db.beginTransaction();
		try
        {
			query.clearBindings();
			
			for(ArrayList<String> prod : batch)
            {
				for(int i = 0; i < prodCols.length; i++)
                {
					query.bindString(i+1, prod.get(i));
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
	
	/**
	 * @param masNum
	 * @return
	 */
	public boolean deleteProduct(int masNum) {
		
		return this.db.delete(DB_TABLE, prodCols[0] + " = " + Integer.toString(masNum), null) > 0;
	}
	
	/**
	 * @param product
	 * @return
	 */
	public boolean updateProduct(Product product){
		
		ContentValues val = null;

		return this.db.update(DB_TABLE, val, prodCols[0] + " = " + Integer.toString(product.getMasNum()), null) > 0;
		
	}
	  
	/**
	 * @param id
	 * @return
	 */
	public Product getProduct(String id)
    {
		String[] args = {id};
		
		Cursor cur = this.db.rawQuery(DB_PRODUCT_QUERY, args);
		
		return new Product(cur);
	}

    public Product getJoinProduct(String lensId, String masnum)
    {
        String[] args = {lensId, masnum};

        Cursor cur = this.db.rawQuery(DB_PRODUCT_JOIN_QUERY, args);

        return new Product(cur);
    }
	
	/* (non-Javadoc)
	 * @see com.example.sqsscanner.DB.DataSource#getSha()
	 */
	public HashMap<String, String> getSha(){
		
		HashMap<String, String> mapIds= new HashMap<String, String>();
		String tempUPC;
		String tempSha;
		
		Cursor c = getAllMas();

		if(c.moveToFirst()){
   			int upcCol = c.getColumnIndex(ProductTable.COLUMN_NAME_PK_MAS_ID);
			int shaCol = c.getColumnIndex(ProductTable.COLUMN_NAME_SHA);
			do{
			
				tempUPC = c.getString(upcCol);
				tempSha = c.getString(shaCol);
				
				mapIds.put(tempUPC, tempSha);
				
				
			}while(c.moveToNext());
			
			
		}
		
		return mapIds;
		
	}
	
	/**
	 * @return
	 */
	private Cursor getAllMas()
    {
		Cursor dbCursor = this.db.rawQuery(DB_SHA_QUERY, null);
		dbCursor.getCount();
		return dbCursor;
	}
}

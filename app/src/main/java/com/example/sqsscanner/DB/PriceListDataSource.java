package com.example.sqsscanner.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.sqsscanner.DB.PriceListContract.PriceListTable;



public class PriceListDataSource implements DataSource {

	
	private SQLiteDatabase db;
	private DBAdapter dbAdapter;
	private static String DB_TABLE = PriceListTable.TABLE_NAME;
	
	public static String[] priceListCols = {
			PriceListTable.COLUMN_NAME_PK_PRICE_LIST,
			PriceListTable.COLUMN_NAME_ACTIVE,
			PriceListTable.COLUMN_NAME_SHA
	};
	
	private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, priceListCols);
	
	private static final String DB_QUERY = "SELECT " + PriceListTable.COLUMN_NAME_ACTIVE + " FROM " + PriceListTable.TABLE_NAME + 
			" WHERE " + PriceListTable.COLUMN_NAME_PK_PRICE_LIST + "= ?"; 
	
	/**
	 * @param ctx
	 */
	public PriceListDataSource(Context ctx){
		
		dbAdapter = new DBAdapter(ctx);
		
	}
	
	public void open() throws SQLException{
		
		db = dbAdapter.getWritableDatabase();
		
	}
	
	public void read() throws SQLException{
		
		db = dbAdapter.getReadableDatabase();
	}
	
	public void close() {
		
		dbAdapter.close();
		
	}
	
	/**
	 * @param priceList
	 * @param active
	 * @return
	 */
	public long createPriceList(String priceList, int active){
		
		ContentValues vals = new ContentValues();
		
		vals.put(priceListCols[0], priceList);
		vals.put(priceListCols[1], active);
		
		return db.insert(DB_TABLE, null , vals);
		
	}
	
	/**
	 * @param priceList
	 * @return
	 */
	public int getIsActive(String priceList){
		
		String[] args = {priceList};
		
		Cursor cur = db.rawQuery(DB_QUERY, args);
		
		cur.moveToLast();
		
		return cur.getInt(cur.getPosition());
		
	}

	@Override
	public HashMap<String, String> getSha() {
		
		HashMap<String, String> mapIds= new HashMap<String, String>();
		
		String tempPK;
		String tempSha;
		
		Cursor c = this.db.query(PriceListDataSource.DB_TABLE,new String[]{PriceListTable.COLUMN_NAME_PK_PRICE_LIST, PriceListTable.COLUMN_NAME_SHA}, null, null, null,null, null);

		if(c.moveToFirst()){
   			int pkCol = c.getColumnIndex(PriceListTable.COLUMN_NAME_PK_PRICE_LIST);
			int shaCol = c.getColumnIndex(PriceListTable.COLUMN_NAME_SHA);
			do{
			
				tempPK = c.getString(pkCol);
				tempSha = c.getString(shaCol);
				
				mapIds.put(tempPK, tempSha);
				
				
			}while(c.moveToNext());
			
			
		}
		
		return mapIds;
	}

	@Override
	public void insertBatch(ArrayList<ArrayList<String>> batch) {
		
		final SQLiteStatement query = this.db.compileStatement(INSERT_QUERY);
		db.beginTransaction();
		try{
			
			query.clearBindings();
			
			for(ArrayList<String> line : batch){
		
				for(int i = 0; i < priceListCols.length; i++){
					query.bindString(i+1, line.get(i));
					
				}
				
				query.executeInsert();
			
			}
		}finally{
			this.db.setTransactionSuccessful();
			this.db.endTransaction();
			
		}	
		
	}
		
	
}

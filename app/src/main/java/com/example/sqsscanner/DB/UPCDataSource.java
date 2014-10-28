package com.example.sqsscanner.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.sqsscanner.DB.UPCContract.UPCTable;



public class UPCDataSource implements DataSource {
	
	private SQLiteDatabase db;
	private DBAdapter dbAdapter; 
	private static String DB_TABLE = UPCTable.TABLE_NAME;
	
	private static final String[] upcCols = {
		UPCTable.COLUMN_NAME_PK_UPC_ID,
		UPCTable.COLUMN_NAME_SHA,
		UPCTable.COLUMN_NAME_FK_MAS_ID

	};
	
	private static final String DB_MAS_QUERY = "SELECT * FROM " + UPCTable.TABLE_NAME + " WHERE " + UPCTable.COLUMN_NAME_PK_UPC_ID + "= ?" ; 
	private static final String DB_UPCS_QUERY = "SELECT " + UPCTable.COLUMN_NAME_PK_UPC_ID + ", " + UPCTable.COLUMN_NAME_SHA + " FROM " + UPCTable.TABLE_NAME;	
	private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, upcCols);
	
	public UPCDataSource(Context ctx){
		
		dbAdapter = new DBAdapter(ctx);
		
	}
			
	public void open() throws SQLException {
		
		db = dbAdapter.getWritableDatabase();
		
	}
	
	public void read() throws SQLException {
		
		db = dbAdapter.getReadableDatabase();
		
	}
		
	public void close() {
		
		dbAdapter.close();
		
	}
	
	public boolean deleteUPC(String upc){
		
		return this.db.delete(DB_TABLE, UPCTable.COLUMN_NAME_PK_UPC_ID + "=" + upc , null) > 0;
		
	}
	
	public boolean updateUPC(String upc, String sha, int masnum){
		ContentValues vals = new ContentValues();
		
		vals.put(UPCTable.COLUMN_NAME_PK_UPC_ID, upc);
		vals.put(UPCTable.COLUMN_NAME_SHA, sha);
		vals.put(UPCTable.COLUMN_NAME_FK_MAS_ID, masnum);
		
		db.beginTransaction();
		
		int i = this.db.update(DB_TABLE, vals,  UPCTable.COLUMN_NAME_PK_UPC_ID + "= ?" , new String[]{upc});
		
		db.setTransactionSuccessful();
		db.endTransaction();
		return i > 0;
		
		
	}
	
	public String getMasNumFromUPC(String upc){
		
		String[] args = {upc};
		
		Cursor dbCur = this.db.rawQuery(DB_MAS_QUERY, args);
		dbCur.moveToFirst();
		
		if(dbCur.getCount() <= 0){
			
			return "";
		}
		
		return Integer.toString(dbCur.getInt(dbCur.getColumnIndex(UPCTable.COLUMN_NAME_FK_MAS_ID)));
		
	}
	
	public Cursor getAllUPC(){
		
		Cursor dbCur = this.db.rawQuery(DB_UPCS_QUERY, null);
		
		dbCur.getCount();
		
		return dbCur;		
				
		
	}
	
	public HashMap<String, String> getSha(){
		
		HashMap<String, String> mapIds= new HashMap<String, String>();
		String tempUPC;
		String tempSha;
		
		Cursor c = db.query(UPCTable.TABLE_NAME, new String[]{"*"}, null , null, null, null, null, null);

		if(c.moveToFirst()){
			int upcCol = c.getColumnIndex(UPCTable.COLUMN_NAME_PK_UPC_ID);
			int shaCol = c.getColumnIndex(UPCTable.COLUMN_NAME_SHA);
			do{
			
				tempUPC = c.getString(upcCol);
				tempSha = c.getString(shaCol);
				
				mapIds.put(tempUPC, tempSha);
				
				
			}while(c.moveToNext());
			

		}
		
		return mapIds;
		
		
	}

	@Override
	public void insertBatch(ArrayList<ArrayList<String>> batch) {
		final SQLiteStatement query = this.db.compileStatement(INSERT_QUERY);
		db.beginTransaction();
		try{
			
			for(ArrayList<String> prod : batch){
				query.clearBindings();
							
				query.bindString(1, prod.get(0));
				System.out.println(prod.get(0));
				query.bindString(2, prod.get(2));
				System.out.println(prod.get(2));
				query.bindString(3, prod.get(1));
				System.out.println(prod.get(1));
						
				query.executeInsert();
			
			}
		}finally{
			this.db.setTransactionSuccessful();
			this.db.endTransaction();
			
		}	
		
	}
  	
}

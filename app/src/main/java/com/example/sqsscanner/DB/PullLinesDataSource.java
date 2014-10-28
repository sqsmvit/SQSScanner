package com.example.sqsscanner.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.sqsscanner.DB.PullLinesContract.PullLinesTable;
import com.example.sqsscanner.Unimplemented.PullLines;


public class PullLinesDataSource implements DataSource {

	private SQLiteDatabase db;
	private DBAdapter dbAdapter;
	private static String DB_TABLE = PullLinesTable.TABLE_NAME;
	
	private static final String[] lineCols = {
		PullLinesTable.COLUMN_NAME_FK_PULL_ID,
		PullLinesTable.COLUMN_NAME_FK_MASNUM,
		PullLinesTable.COLUMN_NAME_SCHEDULED_QTY,
		PullLinesTable.COLUMN_NAME_SHA
	};
	
	private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, lineCols);
	
	private static final String DB_QUERY = "Select * FROM " + PullLinesTable.TABLE_NAME + "WHERE " + PullLinesTable.COLUMN_NAME_FK_PULL_ID + "= ?" +
			" AND " + PullLinesTable.COLUMN_NAME_FK_MASNUM + "= ?"; 
	
	/**
	 * @param ctx
	 */
	public PullLinesDataSource(Context ctx){
		
		dbAdapter = new DBAdapter(ctx);
		
	}
	
	/**
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		
		db = dbAdapter.getWritableDatabase();
		
	}
	
	public void read() throws SQLException{
		
		db = dbAdapter.getReadableDatabase();
	}
	
	/**
	 * 
	 */
	public void close() {
		
		dbAdapter.close();
		
	}
	
	/**
	 * @param pullLine
	 * @return
	 */
	private ContentValues createVals(PullLines pullLine){
		
		ContentValues vals = new ContentValues();
		
		for (int i = 0; i < lineCols.length; i++){
			
			switch (i){
			case 0:
				vals.put(lineCols[i], pullLine.getPullNumber());
				break;
			case 1:
				vals.put(lineCols[i], pullLine.getMasNum());
				break;
			case 2:
				vals.put(lineCols[i], pullLine.getScheduledQty());
				break;

			}
		}
		return vals;
	}
	
	/**
	 * @param pullLine
	 * @return
	 */
	public long createPullLine(PullLines pullLine){
		
		ContentValues vals = createVals(pullLine);
		
		return db.insert(DB_TABLE, null, vals);
		
	}
	
	/**
	 * @param pullLine
	 * @return
	 */
	public boolean deletePullLine(PullLines pullLine){
		
		String where = PullLinesTable.COLUMN_NAME_FK_PULL_ID + "=" + Integer.toString(pullLine.getPullNumber()) + 
				"AND" + PullLinesTable.COLUMN_NAME_FK_MASNUM + "=" + Integer.toString(pullLine.getMasNum());
		
		return this.db.delete(DB_TABLE, where, null) > 0;
		
	}
	
	/**
	 * @param pullLine
	 * @return
	 */
	public boolean updatePullLine(PullLines pullLine){
		
		ContentValues vals = createVals(pullLine);
		
		String where = PullLinesTable.COLUMN_NAME_FK_PULL_ID + "=" + Integer.toString(pullLine.getPullNumber()) + 
				"AND" + PullLinesTable.COLUMN_NAME_FK_MASNUM + "=" + Integer.toString(pullLine.getMasNum());
		
		return this.db.update(DB_TABLE, vals, where, null) > 0;
		
	}
	
	/**
	 * @param pullNum
	 * @param masNum
	 * @return
	 */
	public PullLines getPullLine(int pullNum, int masNum){
		
		String[] args = {Integer.toString(pullNum), Integer.toString(masNum)};
		
		return new PullLines(this.db.rawQuery(DB_QUERY, args));
		
		
	}

	@Override
	public HashMap<String, String> getSha() {
		HashMap<String, String> mapIds= new HashMap<String, String>();
		
		String tempPK;
		String tempSha;
		
		Cursor c = this.db.query(PullLinesTable.TABLE_NAME,new String[]{PullLinesTable.COLUMN_NAME_FK_PULL_ID, PullLinesTable.COLUMN_NAME_SHA}, null, null, null,null, null);

		if(c.moveToFirst()){
   			int pkCol = c.getColumnIndex(PullLinesTable.COLUMN_NAME_FK_PULL_ID);
			int shaCol = c.getColumnIndex(PullLinesTable.COLUMN_NAME_SHA);
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
		
				for(int i = 0; i < lineCols.length; i++){
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

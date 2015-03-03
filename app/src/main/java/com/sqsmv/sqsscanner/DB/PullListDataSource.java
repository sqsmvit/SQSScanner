package com.sqsmv.sqsscanner.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sqsmv.sqsscanner.DB.PullListContract.PullListTable;
import com.sqsmv.sqsscanner.PullList;

import java.util.ArrayList;
import java.util.HashMap;


public class PullListDataSource implements DataSource
{
	private SQLiteDatabase db;
	private DBAdapter dbAdapter;
	private static String DB_TABLE = PullListTable.TABLE_NAME;
	
	private static final String[] dbCols = {
			PullListTable.COLUMN_NAME_PK_PULL_ID,
			PullListTable.COLUMN_NAME_NAME,
			PullListTable.COLUMN_NAME_PULLED_FOR,
			PullListTable.COLUMN_NAME_JOB_NUMBER,
			PullListTable.COLUMN_NAME_SCHEDULED_DATE,
			PullListTable.COLUMN_NAME_MANUAL_QTY,
			PullListTable.COLUMN_NAME_PULL_QTY,
            PullListTable.COLUMN_NAME_FKLENS,
            PullListTable.COLUMN_NAME_FKPRICELIST,
			PullListTable.COLUMN_NAME_SHA
	};
	private static final String INSERT_QUERY = new QueryBuilder().buildInsertQuery(DB_TABLE, dbCols);
	private static final String DB_QUERY = "Select * FROM " + PullListTable.TABLE_NAME + " WHERE " + PullListTable.COLUMN_NAME_PK_PULL_ID + "= ?" ;
	
	/**
	 * @param ctx
	 */
	public PullListDataSource(Context ctx){
		
		dbAdapter = new DBAdapter(ctx);
		
	}
	
	/* (non-Javadoc)
	 * @see com.sqsmv.sqsscanner.DB.DataSource#open()
	 */
	public void open() throws SQLException{
		
		db = dbAdapter.getWritableDatabase();
		
	}
	
	public void read() throws SQLException{
		
		db = dbAdapter.getReadableDatabase();
	}
	
	/* (non-Javadoc)
	 * @see com.sqsmv.sqsscanner.DB.DataSource#close()
	 */
	public void close() {
		
		dbAdapter.close();
		
	}
	
	/**
	 * @param pullList
	 * @return
	 */
	private ContentValues createValues(PullList pullList){
		
		ContentValues vals = new ContentValues();
		
		for (int i = 0; i < dbCols.length; i++){
			
			switch (i){
			case 0:
				vals.put(dbCols[i], pullList.getPullNumber());
				break;
			case 1:
				vals.put(dbCols[i], pullList.getName());
				break;
			case 2:
				vals.put(dbCols[i], pullList.getPulledFor());
				break;
			case 3:
				vals.put(dbCols[i], pullList.getJob());
				break;
			case 4:
				vals.put(dbCols[i], pullList.getScheduledDate());
				break;
			case 5:
				vals.put(dbCols[i], pullList.getManQty());
				break;
			case 6:
				vals.put(dbCols[i], pullList.getPullQty());
				break;

			}
		
		}
		
		return vals;
		
		
	}
		
	/**
	 * @param pullList
	 * @return
	 */
	public long createPullList(PullList pullList){
		
		ContentValues vals = createValues(pullList);
		
		return this.db.insert(DB_TABLE, null, vals);
		
	}
	
	/**
	 * @param pullList
	 * @return
	 */
	public boolean updatePullList(PullList pullList){
		
		ContentValues vals = createValues(pullList);
		
		return this.db.update(DB_TABLE, vals, PullListTable.COLUMN_NAME_PK_PULL_ID + "=" + Integer.toString(pullList.getPullNumber()), null) > 0;
		
	}
	
	/**
	 * @param pullNum
	 * @return
	 */
	public boolean deletePullList(int pullNum)
    {
		return this.db.delete(DB_TABLE, PullListTable.COLUMN_NAME_PK_PULL_ID + "=" + Integer.toString(pullNum), null) > 0;
	}
	
	/**
	 * @param pullNum
	 * @return
	 */
	public PullList getPullList(int pullNum){
		
		String[] args = {Integer.toString(pullNum)};
		
		return new PullList(this.db.rawQuery(DB_QUERY, args));
		
	}

	/* (non-Javadoc)
	 * @see com.sqsmv.sqsscanner.DB.DataSource#getSha()
	 */
	@Override
	public HashMap<String, String> getSha() {
		HashMap<String, String> mapIds= new HashMap<String, String>();
		
		String tempPK;
		String tempSha;
		
		Cursor c = this.db.query(PullListTable.TABLE_NAME,new String[]{PullListTable.COLUMN_NAME_PK_PULL_ID, PullListTable.COLUMN_NAME_SHA}, null, null, null,null, null);

		if(c.moveToFirst()){
   			int pkCol = c.getColumnIndex(PullListTable.COLUMN_NAME_PK_PULL_ID);
			int shaCol = c.getColumnIndex(PullListTable.COLUMN_NAME_SHA);
			do{
			
				tempPK = c.getString(pkCol);
				tempSha = c.getString(shaCol);
				
				mapIds.put(tempPK, tempSha);
				
				
			}while(c.moveToNext());
			
			
		}
		
		return mapIds;
	}

	/* (non-Javadoc)
	 * @see com.sqsmv.sqsscanner.DB.DataSource#insertBatch(java.util.ArrayList)
	 */
	@Override
	public void insertBatch(ArrayList<ArrayList<String>> batch) {
		
		final SQLiteStatement query = this.db.compileStatement(INSERT_QUERY);
		db.beginTransaction();
		try{
			
			query.clearBindings();
			
			for(ArrayList<String> line : batch){
		
				for(int i = 0; i < dbCols.length; i++){
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
}

  package com.example.sqsscanner.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.sqsscanner.DB.ScanContract.ScanTable;
import com.example.sqsscanner.ScanRecord;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanDataSource implements DataSource
{
	private SQLiteDatabase db;
	private DBAdapter dbAdapter;
	private static String DB_TABLE = ScanTable.TABLE_NAME;
	
	private static String SCAN_FROM_PULL_QUERY = "SELECT * FROM " + ScanTable.TABLE_NAME + " WHERE " + ScanTable.COLUMN_NAME_FK_PULL_ID + " = ?";
	
	public ScanDataSource(Context ctx)
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
	public void close() {
	
		dbAdapter.close();
	}

	public void insertScan(ScanRecord rec){
		
		ContentValues values = new ContentValues();
		values.put(ScanTable.COLUMN_NAME_SCAN_ENTRY, rec.getScanEntry());
		values.put(ScanTable.COLUMN_NAME_FK_PULL_ID, rec.getPullNumber());
		values.put(ScanTable.COLUMN_NAME_QUANTITY, rec.getQuantity());
		values.put(ScanTable.COLUMN_NAME_DATE, rec.getScanDate());
		values.put(ScanTable.COLUMN_NAME_MARK_ID,  rec.getMark());
		values.put(ScanTable.COLUMN_NAME_TITLE, rec.getTitle());
		values.put(ScanTable.COLUMN_NAME_PRICE_LIST, rec.getPriceList());
		values.put(ScanTable.COLUMN_NAME_MASNUM, rec.getMasNum());
		values.put(ScanTable.COLUMN_NAME_PRICEFILTERS, rec.getPriceFilters());
		values.put(ScanTable.COLUMN_NAME_RATING, rec.getRating());
		
		long i = db.insert(ScanTable.TABLE_NAME, null, values);
		System.out.println(i);
		
	}
	
	public Cursor getAllScans(){
		
		return this.db.query(ScanTable.TABLE_NAME, null, null, null, null, null, null);
		
	}
	
	public Cursor getScansForPrint(boolean compactMode)
	{
		if(compactMode)
		{
			String[] cols = new String[]{ScanTable.COLUMN_NAME_MASNUM, ScanTable.COLUMN_NAME_SCAN_ENTRY, "SUM("+ScanTable.COLUMN_NAME_QUANTITY+") As Total", ScanTable.COLUMN_NAME_FK_PULL_ID, ScanTable.COLUMN_NAME_DATE, ScanTable.COLUMN_NAME_TITLE, ScanTable.COLUMN_NAME_PRICE_LIST, ScanTable.COLUMN_NAME_PRICEFILTERS, ScanTable.COLUMN_NAME_RATING};
			return this.db.query(ScanTable.TABLE_NAME, cols, null, null, ScanTable.COLUMN_NAME_SCAN_ENTRY+ ", " + ScanTable.COLUMN_NAME_FK_PULL_ID, null, ScanTable.COLUMN_NAME_TITLE);
		}
		else
		{
			String[] cols = new String[]{ScanTable.COLUMN_NAME_MASNUM, ScanTable.COLUMN_NAME_SCAN_ENTRY, ScanTable.COLUMN_NAME_QUANTITY, ScanTable.COLUMN_NAME_FK_PULL_ID, ScanTable.COLUMN_NAME_DATE, ScanTable.COLUMN_NAME_TITLE};//, ScanTable.COLUMN_NAME_PRICE_LIST};
			return this.db.query(ScanTable.TABLE_NAME, cols, null, null, null, null, null);
		}
	}
	
	public void delAllScans(){
		
		this.db.delete(ScanTable.TABLE_NAME, null, null);
	}
	
	//get all scans under a pull id
	public Cursor getScansByPullId(String pullId){
		
		String[] args = {pullId};
		
		Cursor dbCur = this.db.rawQuery(SCAN_FROM_PULL_QUERY, args);		

		return dbCur;
		
	}
	
	public Cursor getMarkValues(){
		String query = "SELECT "+ ScanTable._ID + ", " + ScanTable.COLUMN_NAME_MARK_ID + ", COUNT(*) AS Lines, SUM(" + ScanTable.COLUMN_NAME_QUANTITY + ") AS Total FROM " + ScanTable.TABLE_NAME + " WHERE "  + ScanTable.COLUMN_NAME_MARK_ID + " IS NOT NULL AND "+ ScanTable.COLUMN_NAME_MARK_ID +" !='' " +" GROUP BY " + ScanTable.COLUMN_NAME_MARK_ID;
		Cursor dbCur = this.db.rawQuery(query, null);
		

		return dbCur;
		
	}
	
	public String[] getMarkValuesById(String markId){
		String query = "SELECT "+ ScanTable.COLUMN_NAME_MARK_ID + ", COUNT(*) AS Lines, SUM(" + ScanTable.COLUMN_NAME_QUANTITY + ") AS Total FROM " + ScanTable.TABLE_NAME + " WHERE " + ScanTable.COLUMN_NAME_MARK_ID + "= ? GROUP BY " + ScanTable.COLUMN_NAME_MARK_ID;
		Cursor dbCur = this.db.rawQuery(query, new String[]{markId});
		
		String[] vals = new String[2];
		
		if(dbCur.moveToFirst()){
			
			do{
				String lines = Integer.toString(dbCur.getInt(dbCur.getColumnIndex("Lines")));
				String quantity = Integer.toString(dbCur.getInt(dbCur.getColumnIndex("Total")));
				
				vals[0] = lines;
				vals[1] = quantity;
				
			}while(dbCur.moveToNext());
			
			
		}
		
		return vals;
		
		
	}
	
	//count of a scan of in a specific pull
	public int[] getScanTotalCounts(String pullNum, String scanEntry){
		
		//Cursor cur = this.db.query(ScanTable.TABLE_NAME, null,ScanTable.COLUMN_NAME_SCAN_ENTRY + "=? AND " + ScanTable.COLUMN_NAME_FK_PULL_ID +"=? ", new String[]{scanEntry, pullNum}, null, null, null, null );
	    String query = "SELECT COUNT(*) AS Lines, SUM("+ ScanTable.COLUMN_NAME_QUANTITY + ") AS Total FROM " + ScanTable.TABLE_NAME + " WHERE "+ ScanTable.COLUMN_NAME_SCAN_ENTRY + "=? AND " + ScanTable.COLUMN_NAME_FK_PULL_ID +"=? ";
		Cursor cur = this.db.rawQuery(query, new String[]{scanEntry, pullNum});
	    int[] counts = new int[2];
		if(cur.moveToFirst()){	
			counts[0] = cur.getInt(cur.getColumnIndex("Lines"));
			counts[1] = cur.getInt(cur.getColumnIndex("Total"));
		}
		
		return counts;
		
	}
	
	public int delScansByMark(String mark){
		
		String[] args = {mark};
		
		return this.db.delete(ScanTable.TABLE_NAME, ScanTable.COLUMN_NAME_MARK_ID + " = ?", args);
		
	}
	
	public int delScansById(String id){
		
		String[] args = {id};
		
		return this.db.delete(ScanTable.TABLE_NAME, ScanTable._ID + " = ?", args);
	}
	
	public int getTotalByPull(String pullId){
		
		Cursor cur = this.db.query(ScanTable.TABLE_NAME, new String[]{"SUM ("+ScanTable.COLUMN_NAME_QUANTITY +") As Total"} ,ScanTable.COLUMN_NAME_FK_PULL_ID + "=?", new String[]{pullId}, null, null, null, null );
		int total = 0;
		if(cur.moveToNext()){
			
			total = cur.getInt(cur.getColumnIndex("Total"));			
		}
		return total;
		
	}
	
	public String[] getPullNums(){
		
		
		int i = 0;
		Cursor cur = this.db.query(true, ScanTable.TABLE_NAME, new String[]{ScanTable.COLUMN_NAME_FK_PULL_ID}, null, null, null, null, null, null);
		String[] pulls = new String[cur.getCount()];
		if(cur.moveToFirst()){
			do
            {
				pulls[i] = cur.getString(0);
				i++;
			}while(cur.moveToNext());
		}
        return pulls;
		
	}
		
	@Override
	public HashMap<String, String> getSha() {
		return null;
	}

	@Override
	public void insertBatch(ArrayList<ArrayList<String>> batch) {
		
	}

	public Cursor getValueByID(int id)
    {
		String query = new QueryBuilder().buildSelectQuery(DB_TABLE, new String[]{"*"}, new String[]{ScanTable._ID});
		
		return this.db.rawQuery(query, new String[]{Integer.toString(id)});
		
		//return this.db.rawQuery(query, new String[]{Integer.toString(id)});
	}
	
	public Cursor getValueByMarkID(int id, String[] selCols){
		String query = new QueryBuilder().buildSelectQuery(DB_TABLE, selCols, new String[]{ScanTable.COLUMN_NAME_MARK_ID});
		
		return this.db.rawQuery(query, new String[]{Integer.toString(id)});	
		
	}

    public int updateRecordByID(int id, ScanRecord editRecord)
    {
        ContentValues values = new ContentValues();
        values.put(ScanTable.COLUMN_NAME_FK_PULL_ID, editRecord.getPullNumber());
        values.put(ScanTable.COLUMN_NAME_QUANTITY, editRecord.getQuantity());

        return db.update(DB_TABLE, values, ScanTable._ID + " = ?", new String[]{String.valueOf(id)});
    }
}

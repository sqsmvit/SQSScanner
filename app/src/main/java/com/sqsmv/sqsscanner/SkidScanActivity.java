package com.sqsmv.sqsscanner;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;

import com.sqsmv.sqsscanner.DB.ScanContract.ScanTable;
import com.sqsmv.sqsscanner.DB.ScanDataSource;

/**
 * Activity for managing and tracking skids scanned by the user.
 * Each skid contains a subgroup of individual scans.  Skids are 
 * presented as an expandable list with each scan as a subgroup
 * of that skid.  Users can edit any skid or delete any scan.
 * 
 * @author ChrisS
 *
 */
public class SkidScanActivity extends ExpandableListActivity {
	
	/**
	 * @author ChrisS
	 *
	 */
	private class MarkCursorTreeAdapter extends SimpleCursorTreeAdapter {

		public MarkCursorTreeAdapter(Context context, Cursor cursor,
				int groupLayout, String[] groupFrom, int[] groupTo,
				int childLayout, String[] childFrom, int[] childTo) 
		{
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
					childTo);
		}

		/* (non-Javadoc)
		 * @see android.widget.CursorTreeAdapter#getChildrenCursor(android.database.Cursor)
		 */
		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) 
		{
			int id = groupCursor.getInt(groupCursor.getColumnIndex(ScanTable.COLUMN_NAME_MARK_ID));
			Cursor childCursor =  scanDataSource.getValueByMarkID(id, new String[]{ScanTable._ID, ScanTable.COLUMN_NAME_TITLE, ScanTable.COLUMN_NAME_QUANTITY});
			childCursor.moveToFirst();
			return childCursor;
			
		}

	}

	private ScanDataSource scanDataSource;
	private MarkCursorTreeAdapter markAdapter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mark_scan);
		
		scanDataSource = new ScanDataSource(this);
		scanDataSource.read();
		
		Cursor dbCur = scanDataSource.getMarkValues();
		
		/*
		 * Creates the adapter for the Expandable List view
		 * 
		 * Displays each skid as the parent of the subgroup of the scans on that skid
		 * as an expanable list.  Clicking on any skid will open the subgroup underneath it.
		 * 
		 */
		markAdapter = new MarkCursorTreeAdapter(this.getBaseContext(), dbCur, 
				R.layout.skid_row, new String[]{ScanTable.COLUMN_NAME_MARK_ID, 
				"Lines", "Total"}, new int[] {R.id.mark_id, R.id.instruction_lines, R.id.mark_count},
				R.layout.scan_row, new String[]{ScanTable.COLUMN_NAME_TITLE, ScanTable.COLUMN_NAME_QUANTITY}, 
				new int[] {R.id.scanTitle, R.id.Scan_qty});
						
		ExpandableListView listView = getExpandableListView();
		
		View header = getLayoutInflater().inflate(R.layout.skid_header, null);
		
		listView.addHeaderView(header);
		
		getExpandableListView().setGroupIndicator(null);
		
		this.setListAdapter(markAdapter);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume(){
		scanDataSource.open();
		super.onResume();

		
	}
	
	/**
	 * @param v
	 */
	public void clickGoToScan(View v){

		//get position of item in the expandable list
		int rawPos = getExpandableListView().getPositionForView((LinearLayout) v.getParent());
		long expPos = getExpandableListView().getExpandableListPosition(rawPos);
		int pos = ExpandableListView.getPackedPositionGroup(expPos);
		
		//gets the cursor for the adapter and moves it to the correct position
		Cursor tempCursor = markAdapter.getCursor();
		tempCursor.moveToPosition(pos);
		//extract the skid id from the row of the cursor
		String mark = tempCursor.getString(tempCursor.getColumnIndex(ScanTable.COLUMN_NAME_MARK_ID));
		
		//creates the Intent for going to the ScanHome Activity
		Intent intent = new Intent(this, ScanHomeActivity.class);
		
		//sends the selected skid to the Scan Activity
		Bundle returnData = new Bundle();
		returnData.putString("MARK_ID", mark);
		intent.putExtras(returnData);
		startActivity(intent);
		
				
		
	}
	
	/**
	 * @param v
	 * @return
	 */
	private int getGroupPos(View v){
		int rawPos = getExpandableListView().getPositionForView((LinearLayout) v.getParent());
		long expPos = getExpandableListView().getExpandableListPosition(rawPos);
		int pos = ExpandableListView.getPackedPositionGroup(expPos);
		
		return pos;
	}
	
	/**
	 * @param v
	 * @return
	 */
	private int getChildPos(View v){
		
		int rawPos = getExpandableListView().getPositionForView((LinearLayout) v.getParent());
		long expPos = getExpandableListView().getExpandableListPosition(rawPos);
		int pos = ExpandableListView.getPackedPositionChild(expPos);
		return pos;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){ 
		    	 
		    	 Bundle b = data.getExtras();
		    	 ScanRecord editScan = b.getParcelable("EDIT_RECORD");
		    	 scanDataSource.open();
		    	 scanDataSource.insertScan(editScan);
				 markAdapter.changeCursor(scanDataSource.getMarkValues());
				 markAdapter.notifyDataSetChanged();
	
		     }
		  }
	}
	
	/**
	 * @param v
	 */
	public void clickGoToEdit(View v){
		
		int childPos = getChildPos(v);
		
		Cursor tempCursor = markAdapter.getChild(getGroupPos(v), childPos);
		tempCursor.moveToPosition(childPos);
		ScanRecord editScan = new ScanRecord(tempCursor);
		this.scanDataSource.delScansById(Integer.toString(tempCursor.getInt(tempCursor.getColumnIndex(ScanTable._ID))));
		
		Intent intent = new Intent(this, EditRecordActivity.class);
		Bundle b = new Bundle();
		b.putParcelable("EDIT_SCAN", editScan);
		intent.putExtras(b);
		startActivityForResult(intent, 1);
		
	}
		
	/**
	 * @param v
	 */
	public void delRow(View v){
		
		final View v1 = v;
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
			// set title
			alertDialogBuilder.setTitle("Delete Scan");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Delete Scan?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						int childPos = getChildPos(v1);
						
						Cursor tempCursor = markAdapter.getChild(getGroupPos(v1), getChildPos(v1));
						tempCursor.moveToPosition(childPos);

						scanDataSource.delScansById(Integer.toString(tempCursor.getInt(tempCursor.getColumnIndex(ScanTable._ID))));
						markAdapter.changeCursor(scanDataSource.getMarkValues());
						markAdapter.notifyDataSetChanged();
						

					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
	
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause(){
		super.onPause();
		this.scanDataSource.close();
		
	
	}
	
	/**
	 * @param v
	 */
	public void onClickBack(View v){
		
		onBackPressed();
		
	}

}

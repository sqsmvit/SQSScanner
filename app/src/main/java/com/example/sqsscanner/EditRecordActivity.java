package com.example.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sqsscanner.DB.ScanDataSource;

public class EditRecordActivity extends Activity {

	private String editScanId;
	private String editOldQty;
	private String title;
	String pullNum;
	private Spinner spPullNumbers;

	private ScanRecord editRecord;
	private Intent intent;
	private EditText scanTitle;
	private EditText qty;
	private ArrayAdapter<String> spinAdp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_record);
		
		final Context context = this;
		
		ScanDataSource scanDataSource = new ScanDataSource(this);
		scanDataSource.open();
		String[] pullNumbers = scanDataSource.getPullNums();
		scanDataSource.close();
		
		spinAdp = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, pullNumbers);
		
		spinAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		intent = getIntent();
		
		editRecord = intent.getExtras().getParcelable("EDIT_SCAN");
		
		pullNum = editRecord.getPullNumber();
		editScanId = editRecord.getPullNumber();
		editOldQty = editRecord.getQuantity();
		title = editRecord.getTitle();
		
		spPullNumbers = (Spinner) findViewById(R.id.spinPullNumbers);
		scanTitle = (EditText) findViewById(R.id.editScanId);
		qty = (EditText) findViewById(R.id.editQtyNum);
		
		if(editScanId.contains("SQS")){
			
			qty.setEnabled(true);
		}
		
		
		spPullNumbers.setAdapter(spinAdp);
		setSpinner(pullNum);
		
		scanTitle.setText(title);
		qty.setText(editOldQty);
		
		scanTitle.setFocusable(false);
		scanTitle.setEnabled(false);
		
		
	}
	
	public void onClickDone(View v){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle("Confirm Edit");

		// set dialog message
		alertDialogBuilder
			.setMessage("Confirm Edit?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					
					finish();
												
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
			
			if ((qty.getText().toString().equals(editOldQty))|| (pullNum.equals(spPullNumbers.getSelectedItem().toString()))){
					
				finish();
	
			}
			
			else{
				
			alertDialog.show();
			
			}
			// show it			
		
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_record, menu);
		return true;
	}

	/**
	 * @param key
	 */
	public void setSpinner(String key){
		
		int i = 0;
		
		while(i < spinAdp.getCount()){
		
			if (key.equals(spPullNumbers.getItemAtPosition(i).toString())){
				
				spPullNumbers.setSelection(i);
				
			}
			
			i++;
			
		}
	}
		
	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish(){
		
		String newQty = qty.getText().toString();
		
		editRecord.setPullNumber(spPullNumbers.getSelectedItem().toString());
		editRecord.setQuantity(newQty);
		
	    Bundle bundle = new Bundle();
	    bundle.putParcelable("EDIT_RECORD", editRecord);    
	    Intent returnData = new Intent();
	    returnData.putExtras(bundle);
	    setResult(RESULT_OK, returnData);
				
		super.finish();
	}
	
}

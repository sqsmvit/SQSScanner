package com.sqsmv.sqsscanner;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sqsmv.sqsscanner.DB.ScanDataSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class PullReviewActivity extends ListActivity
{
    public static boolean FILE_EXPORTED;
	private int exportModeChoice;
	private int invAdjChoice;
	
	private File exportFile;
	
	private SimpleAdapter pullAdapter;
	private ScanDataSource scanDS;
	private ArrayList<HashMap<String, String>>pullNumberList = new ArrayList<HashMap<String, String>>();

	TextView commitModeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_review);
		
		setConfig();
		
		ListView listView = getListView();
		View header = getLayoutInflater().inflate(R.layout.pullrow_header, null);
		listView.addHeaderView(header);
		
		commitModeView = (TextView) findViewById(R.id.commitMode);
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
			{
                int editPos = position - 1;
				String pullNum = pullNumberList.get(editPos).get("pullNum");
				String pullRecords = pullNumberList.get(editPos).get("pullLines");
				String pullPieces = pullNumberList.get(editPos).get("pullCount");
				goToScans(view, pullNum, pullRecords, pullPieces, editPos);
			}				
		});
		
		displayMode();
	}

	/**
	 * @param v
	 */
	public void onClickBack(View v)
	{
		onBackPressed();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		createAdapter();
		getListView().setAdapter(pullAdapter);
		pullAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		this.pullNumberList.clear();
		pullAdapter.notifyDataSetInvalidated();
	}

	public void writeFromDB()
	{
		ScanWriter scanWriter = new ScanWriter(this, exportModeChoice, invAdjChoice);

		try {
			scanWriter.writeToFile();
			scanWriter.writeBackup();
			scanWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
			
		this.exportFile = scanWriter.getFile(); 

		
	}
	
	/**
	 * @param v
	 */
	public void onClickDelete(View v){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle("Confirm Mass Delete");

		// set dialog message
		alertDialogBuilder
			.setMessage("Delete ALL Scans ?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					
					performMassDelete();
												
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
		alertDialog.show();
		
	}
	
	public void performMassDelete()
	{
		scanDS = new ScanDataSource(this);
		scanDS.open();
		scanDS.delAllScans();
		pullNumberList.clear();
		pullAdapter.notifyDataSetChanged();
	}

	/**
	 * @param v
	 */
	public void onClickCommit(View v)
	{
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(!(pullNumberList.isEmpty()))
		{
			if (wifi.isConnected())
			{
				writeFromDB();
				
				try
				{
					ScanExporter scanExporter = new ScanExporter(this, exportFile, exportModeChoice, true);
					scanExporter.exportScan();
				}
                catch (IOException e)
                {
					e.printStackTrace();
				}
				this.pullNumberList.clear();
				pullAdapter.notifyDataSetChanged();
				FILE_EXPORTED = true;
			}
			else
			{
				Toast.makeText(this, "Not Connected to WIFI - cannot commit scan", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			Toast.makeText(this, "No Scan to Commit", Toast.LENGTH_LONG).show();
		}
	} 
		
	private void createAdapter()
	{
		createAdapterDataset();
		pullAdapter = new SimpleAdapter(this, pullNumberList, R.layout.pull_row,
				new String[] {"pullNum","pullLines","pullCount"}, 
				new int[]{R.id.Pull_Num, R.id.Pull_Lines, R.id.Pull_Count});
	}

	/**
	 * @param v
	 * @param key
	 * @param records
	 * @param pieces
	 * @param pos
	 */
	public void goToScans(View v, String key, String records, String pieces, int pos){
		
		Intent intent = new Intent(this, ScanReviewActivity.class);
		
		intent.putExtra("PULL_NUM",key);
		startActivity(intent);
		
	}
	
	/**
	 * 
	 */
	private void createAdapterDataset()
	{
		HashMap<String, String> pullEntry; 

		ScanDataSource scanDataSource = new ScanDataSource(this);
		scanDataSource.open();
		String[] pulls = scanDataSource.getPullNums();
		
		for (String pull : pulls)
		{
			pullEntry = new HashMap<String, String>();
			pullEntry.put("pullNum", pull);
			pullEntry.put("pullLines", Integer.toString(scanDataSource.getScansByPullId(pull).getCount()));
			pullEntry.put("pullCount", Integer.toString(scanDataSource.getTotalByPull(pull)));
			this.pullNumberList.add(pullEntry);
		}
		scanDataSource.close();
	}
		
	/**
	 * 
	 */
	public void resetScans()
	{
		pullAdapter.notifyDataSetInvalidated();
	}
	
	/**
	 * 
	 */
	private void setConfig()
	{
		DroidConfigManager appConfig = new DroidConfigManager(this);
		//default is DBX
		exportModeChoice = appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, null, 1);
        invAdjChoice = appConfig.accessInt(DroidConfigManager.INVENTORY_MODE_CHOICE, null, 1);
    }
	
	private void displayMode()
	{
        if(exportModeChoice == 1)
        {
            //Normal Mode
            commitModeView.setText("Normal Pull Mode");
        }
        else if(exportModeChoice == 2)
        {
            //Consolidated Mode
            commitModeView.setText("Consolidated Pull Mode");
        }
        else if(exportModeChoice == 3)
        {
            //BillB Mode
            commitModeView.setText("Bill B. Mode");
        }
        else if(exportModeChoice == 4)
        {
            //Drew mode
            commitModeView.setText("Drew Mode");
        }
        else if(exportModeChoice == 5)
        {
            //RI Mode
            commitModeView.setText("RI Mode");
        }
		else
        {
            commitModeView.setText("Error");
        }
	}
}

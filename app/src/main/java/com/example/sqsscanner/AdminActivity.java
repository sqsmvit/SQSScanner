package com.example.sqsscanner;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.sqsscanner.DB.DBAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author ChrisS
 *
 *Admin Activity
 *
 *This Activity is used primarily by the admin for the device.
 *This Activity can be accessed by scanning the Admin bar-code
 *or by enter '1' as the value for the Pull Number.
 *
 *
 *In this Activity the admin can:
 *
 *Review a list of the previous pull files.
 *
 *Select a Pull File and send it to the internal device storage
 *
 *Set the export location for the device for the Commit Activity
 */
public class AdminActivity extends ListActivity
{
	private ArrayList<HashMap<String, String>>backupList = new ArrayList<HashMap<String, String>>();
	private Context context;
	//private RadioGroup exportGrp;
	private SharedPreferences scanConfig;
	private SimpleAdapter backupAdapter;
	private File root;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_admin);
		
		context = this;
		
		scanConfig = getSharedPreferences("scanConfig", 0);
		View header = getLayoutInflater().inflate(R.layout.admin_header, null);
		//exportGrp = (RadioGroup) header.findViewById(R.id.exportGrp);
		//exportGrp.check(scanConfig.getInt("exportSel", 1));
		

		//File root = new File(getFilesDir().getAbsolutePath());
		//root = new File(getDir(getString(R.string.BACKUP_DIR, Context.MODE_PRIVATE), 0).getAbsolutePath());
		root = new File(Environment.getExternalStorageDirectory().toString() + "/backups");
		root.mkdir();
		createAdapterDataset(root);
		//ListDir(root);
		
		backupAdapter = new SimpleAdapter(this, backupList, R.layout.admin_file_row,
				new String[] {"fileName","fileDate","fileSize"}, 
				new int[]{R.id.adminFileName, R.id.adminFileDate, R.id.adminFileSize});
		
/*		ArrayAdapter<String> dirList = new ArrayAdapter<String>(this,
				R.layout.activity_admin, 
				backupList);*/
		
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		listView.addHeaderView(header);
		listView.setAdapter(backupAdapter);

	}
	
	/**
	 * onClickSort will sort the column represented by the header
	 * view slected in ascending order.
	 * 
	 * @param v - the view that triggered the event
	 */
	public void onClickSort(View v){
		
		String key;
		
		if(v.getId() == R.id.headBackupName){key = "fileName";}
		else if (v.getId() == R.id.headBackupDate){key = "fileDate";}
		else {key = "fileSize";}
		
		sortList(backupList, key);
		backupAdapter.notifyDataSetChanged();
		
	}
	
	public void onClickUpdate(View v)
	{
		
		Intent popIntent = new Intent(this, PopDatabaseService.class);
		String[] xmlFiles = this.getResources().getStringArray(R.array.fmDumpFiles);
		int[] xmlSchemas = getSchemaResIds();
		
   		popIntent.putExtra("XML_FILES", xmlFiles);
   		popIntent.putExtra("XML_SCHEMAS", xmlSchemas);
   		popIntent.putExtra("FORCE_UPDATE", 1);
		
   		this.startService(popIntent);
		
   		Toast.makeText(this, "DB Update starting...", Toast.LENGTH_LONG).show();
   		
	}

    public void onClickResetDB(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Reset DB");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to reset the database?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int id)
                    {
                        DBAdapter dbAdapter = new DBAdapter(context);
                        dbAdapter.resetAll();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
	
	private int[] getSchemaResIds(){
		
		TypedArray xmlArrays = this.getResources().obtainTypedArray(R.array.xml_list);
		
		int[] xmlSchemas = new int[xmlArrays.length()];
		
		for(int i = 0; i < xmlArrays.length(); i++){
			
			xmlSchemas[i] = xmlArrays.getResourceId(i, 0);	
		}
		
		xmlArrays.recycle();
		return xmlSchemas;
	}
	/**
	 * sortList sorts the list of hashmaps by the key chosen in the onClickSort
	 * event
	 * 
	 * 
	 * @param list - the list to be sorted
	 * @param key - the key to sort on
	 */
	private void sortList(ArrayList<HashMap<String, String>> list,
			final String key) {
		
		Comparator<HashMap<String, String>> comp = new Comparator<HashMap<String, String>>(){

			@Override
			public int compare(HashMap<String, String> lhs,
					HashMap<String, String> rhs) {
				
				String val1 = lhs.get(key);
				String val2 = rhs.get(key);
				
				return val1.compareTo(val2);
			}
			
			
		};
		
		Collections.sort(list, comp);
	}

	/**
	 * Creates the data set for the list view.  This dataset
	 * consists of the filename, the date the file was created,
	 * and the size of the file.
	 * 
	 * @param root - the root file path to where the backups are stored
	 */
	private void createAdapterDataset(File root){
		
		SimpleDateFormat fileFmt = new SimpleDateFormat(" MM-dd-yy", Locale.US);
		HashMap<String, String> backupEntry; 
		File[] files = root.listFiles();
		//String[] pulls = scanDataSource.getPullNums();
		
		for (File file : files){
			backupEntry = new HashMap<String, String>();
			backupEntry.put("fileName", file.getName());
			backupEntry.put("fileDate", fileFmt.format(new Date(file.lastModified())));
			backupEntry.put("fileSize", String.format("%.2f",(file.length()/1024.0)) + "kb");
			this.backupList.add(backupEntry);
			
		}
		
	}
	
	/* (non-Javadoc)
	 * 
	 * Selects which file to export.
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id){
		
		final File selected = new File(root, backupList.get(pos-1).get("fileName"));
		final String fileName = selected.getName();

			
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			 
		// set title
		alertDialogBuilder.setTitle("Export Backup");
 
		// set dialog message
		alertDialogBuilder
			.setMessage("Send Backup to DropBox?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        exportBackup(selected, fileName);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            })
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * 
	 * Send the user back to the ScanHomeActivity.
	 * 
	 * @param v - the view that triggered the event
	 */
	public void onClickBack(View v)
    {
		super.onBackPressed();
		//writePref();
	}
	
	
	/**
	 * Writes the export location to the shared preferences file.
	 * 
	 * 
	 */
    /*
	private void writePref(){
		
		SharedPreferences.Editor scanState = scanConfig.edit();
		scanState.putInt("exportSel", exportGrp.getCheckedRadioButtonId());
		scanState.putInt("exportMethod", setExport());
		scanState.commit();
		
	}
	*/
	/**
	 * 
	 * Exports a backup to the internal storage on the device
	 * 
	 * @param src - where the file is coming from
	 * @param fName - where the file is going to
	 * @throws IOException
	 */
	private void exportBackup(File src, String fName) throws IOException
	{
		//Export to SD Card
		//ScanExporter scanExporter = new ScanExporter(this, src, true, false);
		//scanExporter.exportScan();
		//Export to DropBox 
		ScanExporter scanExporter = new ScanExporter(this, src, false, false);
		scanExporter.exportScan();
		/*
		File root = new File(Environment.getExternalStorageDirectory().toString() + "/Scans");
		root.mkdirs();
		File expScan = new File(root.getAbsolutePath(), fName);
		copyFile(src, expScan);	
		*/
	}
	
	
	
	/**
	 * 
	 * Copies the file to the filesystem
	 * 
	 * @param src - where the file is coming from
	 * @param dest - where the file is going to
	 * @throws IOException
	 */
	public void copyFile(File src, File dest) throws IOException{
		
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dest);

	    byte[] buf = new byte[1024];
	    int len;
	    
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    
	    in.close();
	    out.close();
		
	}
	
	/**
	 * 
	 * Looks at which radiobutton is selected and sets:
	 * 
	 * 0 - sdcard export
	 * 1 - dropbox export
	 * 
	 * @return integer bool value for exporting to dropbox
	 */
    /*
	public int setExport(){
		
		int checkId = exportGrp.getCheckedRadioButtonId();
		
		switch(checkId){
		
			case(R.id.SD): return getResources().getInteger(R.integer.EXPORT_SD);
		
			case(R.id.DBX): return getResources().getInteger(R.integer.EXPORT_DBX);

		default: return 1;
		
		}
		
	}
    */
}

package com.example.sqsscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.sqsscanner.DB.ProductLensDataSource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoadActivity extends Activity {
	 
	 private static final String TAG = "Load_Activity";
	 
	//String[] xmlFiles;
	//int[] xmlSchemas;
	//private static ProgressDialog progressDialog; 
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String message = String.format("in onCreate and starting the LoadActivity!");
		Log.d(TAG, message);
		setContentView(R.layout.activity_load);
	}

	protected void onResume(){
		super.onResume();
		String message = String.format("in onResume and for the LoadActivity!");
		Log.d(TAG, message);
		//xmlFiles = LoadActivity.this.getResources().getStringArray(R.array.fmDumpFiles);
		//xmlSchemas = getSchemaResIds();
	}
	
	/**
	 * Gets the resource Ids for the schemas of the xml files.
	 * 
	 * Located in xml_input.xml
	 * 
	 * 
	 * @return
	 */
	public int[] getSchemaResIds(){
		
		TypedArray xmlArrays = this.getResources().obtainTypedArray(R.array.xml_list);
		
		int[] xmlSchemas = new int[xmlArrays.length()];
		
		for(int i = 0; i < xmlArrays.length(); i++){
			
			xmlSchemas[i] = xmlArrays.getResourceId(i, 0);	
		}
		
		xmlArrays.recycle();
		return xmlSchemas;
	}
	

	public void startScan(View v){
		
		String message = String.format("in startScan and for the LoadActivity!");
		Log.d(TAG, message);
		System.out.println(message);

		String buildDate = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());
		String message2 = String.format("today's buildDate = %s versus the system stored buildDate = %s",
				buildDate, getSharedPreferences("scanConfig", 0).getString("buildDate", ""));
		Log.d(TAG, message2);
		if(!(buildDate.equals(getSharedPreferences("scanConfig", 0).getString("buildDate", ""))))
		{
			try
			{
				Intent popIntent = new Intent(this, PopDatabaseService.class);
				String[] xmlFiles = this.getResources().getStringArray(R.array.fmDumpFiles);
				int[] xmlSchemas = getSchemaResIds();
		
				popIntent.putExtra("XML_FILES", xmlFiles);
				popIntent.putExtra("XML_SCHEMAS", xmlSchemas);
				popIntent.putExtra("FORCE_UPDATE", 1);
		
				this.startService(popIntent);
				Utilities.cleanFolder(new File(Environment.getExternalStorageDirectory().toString() + "/backups"), 180);
				getSharedPreferences("scanConfig", 0).edit().putString("buildDate", buildDate).apply();

                long productLensResetMilli = Long.parseLong(getSharedPreferences("scanConfig", 0).getString("ProductLensResetMilli", "0"));

                if(((System.currentTimeMillis() - productLensResetMilli)/86400000) >= 7)
                {
                    ProductLensDataSource plds = new ProductLensDataSource(this);
                    plds.open();
                    plds.resetDB();
                    plds.close();
                }
                getSharedPreferences("scanConfig", 0).edit().putString("ProductLensResetMilli", Long.toString(System.currentTimeMillis())).apply();

				Intent intent = new Intent(this, ScanHomeActivity.class);
				pauseDialog(intent);
			}
            catch (NotFoundException e)
			{
				Log.i(TAG, "Crash during db pop"  ,e);
				e.printStackTrace();
			}
		}
		else
		{
			Intent intent = new Intent(this, ScanHomeActivity.class);
			startActivity(intent);
		}
	}
	
	private void pauseDialog( final Intent intent){
		String message = String.format("in pauseDialog and for the LoadActivity!");
		Log.d(TAG, message);
		
		final ProgressDialog pausingDialog = ProgressDialog.show(LoadActivity.this, "", "Loading Please Stay in Wifi Range...", true);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(10000);
					startActivity(intent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // The length to 'pause' for				
				pausingDialog.dismiss();
			}
		}.start();
	}
}


package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.sqsmv.sqsscanner.DB.LensDataSource;
import com.sqsmv.sqsscanner.DB.ProductLensDataSource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoadActivity extends Activity
{
	 
	private static final String TAG = "Load_Activity";

    SharedPreferences config;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		String message = String.format("in onCreate and starting the LoadActivity!");
		Log.d(TAG, message);
		setContentView(R.layout.activity_load);

        config = getSharedPreferences("scanConfig", 0);
        setTitle(getTitle() + " v" + getVersion());
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
				buildDate, config.getString("buildDate", ""));
		Log.d(TAG, message2);
		if(!(buildDate.equals(config.getString("buildDate", ""))))
		{
            if(checkNeedUpdate())
                startUpdate();
            else
            {
                try
                {
                    config.edit().putString("priorVersion", "").apply();
                    Intent popIntent = new Intent(this, PopDatabaseService.class);
                    String[] xmlFiles = this.getResources().getStringArray(R.array.fmDumpFiles);
                    int[] xmlSchemas = getSchemaResIds();

                    popIntent.putExtra("XML_FILES", xmlFiles);
                    popIntent.putExtra("XML_SCHEMAS", xmlSchemas);
                    popIntent.putExtra("FORCE_UPDATE", 1);

                    this.startService(popIntent);
                    Utilities.cleanFolder(new File(Environment.getExternalStorageDirectory().toString() + "/backups"), 180);
                    config.edit().putString("buildDate", buildDate).apply();

                    long productLensResetMilli = Long.parseLong(config.getString("ProductLensResetMilli", "0"));

                    LensDataSource lds = new LensDataSource(this);
                    lds.open();
                    lds.resetDB();
                    lds.close();

                    if(((System.currentTimeMillis() - productLensResetMilli) / 86400000) >= 7)
                    {
                        ProductLensDataSource plds = new ProductLensDataSource(this);
                        plds.open();
                        plds.resetDB();
                        plds.close();
                    }
                    config.edit().putString("ProductLensResetMilli", Long.toString(System.currentTimeMillis())).apply();

                    pauseDialog();
                }
                catch(NotFoundException e)
                {
                    Log.i(TAG, "Crash during db pop", e);
                    e.printStackTrace();
                }
            }
		}
		else
            startScanHomeActivity();
	}
	
	private void pauseDialog()
    {
		String message = String.format("in pauseDialog and for the LoadActivity!");
		Log.d(TAG, message);
		
		final ProgressDialog pausingDialog = ProgressDialog.show(LoadActivity.this, "Updating", "Please Stay in Wifi Range...", true);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(10000);
                    startScanHomeActivity();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // The length to 'pause' for				
				pausingDialog.dismiss();
			}
		}.start();
	}

    private void startScanHomeActivity()
    {
        Intent intent = new Intent(this, ScanHomeActivity.class);
        startActivity(intent);
    }

    /**
     * Gets the device version from the Android Manifest
     *
     * @return the version of the application
     */
    private String getVersion()
    {
        PackageManager man = getPackageManager();

        PackageInfo info;
        try
        {
            info = man.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return "";
        }
    }


    private boolean checkNeedUpdate()
    {
        boolean needUpdate = false;
        DBXManager dbxManager = new DBXManager(this);
        String apkFileName = getString(R.string.apk_file_name);
        String lastUpdated = config.getString("lastAppUpdate", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.US);
        dbxManager.getDbxFileDate("/out/" + apkFileName);
        Date fileModifiedDate = dbxManager.getDbxFileDate("/out/" + apkFileName);

        if(lastUpdated.isEmpty())
            config.edit().putString("lastAppUpdate", dateFormat.format(fileModifiedDate)).apply();
        else if(!lastUpdated.equals(dateFormat.format(fileModifiedDate)) || config.getString("priorVersion", "").equals(getVersion()))
            needUpdate = true;

        return needUpdate;
    }

    private void startUpdate()
    {
        config.edit().putString("priorVersion", getVersion()).apply();
        ProgressDialog.show(this, "Updating Application", "Please Stay in Wifi Range...", true);
        Intent appUpdateIntent = new Intent(this, AppUpdateService.class);
        startService(appUpdateIntent);
    }
}


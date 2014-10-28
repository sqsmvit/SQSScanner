package com.example.sqsscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import com.dropbox.sync.android.DbxAccountManager;


/**
 * @author ChrisS
 *
 *Startup Activity makes sure that the application is linked to Dropbox
 *If the device is not linked it makes a request using the DropBox Sync API 
 *to form the link through the default browser.  Also sets the app title.
 *
 */
public class CheckDBXActivity extends Activity {
		
	private static final String TAG = "CheckDBXActivity";
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * ****** THIS IS THE ENTRY POINT ***********
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String message = String.format("in onCreate and starting the PROGRAM for real!");
		Log.d(TAG, message);
		System.out.println(message);
		setContentView(R.layout.activity_check_dbx);
				
		checkDbxAcct();		   
		setAppTitle();
		 	   
		
	}

	/**
	 * Sets the title of the application
	 */
	private void setAppTitle() {
	
		this.setTitle("Scanner" + " v" + getVersion());
	}   

	/**
	 * Gets the device version from the Android Manifest
	 * 
	 * @return the version of the application
	 */
	private String getVersion(){
		PackageManager man = this.getPackageManager();
		
		PackageInfo info;
		try {
			info = man.getPackageInfo(this.getPackageName(), 0);
			return info.versionName;
		
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
			return "";
		}

		
	}
	
	
	/**
	 * Check is the device is connected to wifi.
	 * 
	 * @return boolean for the wifi connection
	 */
	private boolean checkWifi(){
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		String message = String.format("in checkWifi and is connected is %b", wifi.isConnected());
		Log.d(TAG, message);
		return wifi.isConnected();
	}
	
	
	/**
	 * Starts the LoadActivity
	 * 
	 * 
	 */
	private void startLoadActivity(){
		
 		Intent intent = new Intent(this, LoadActivity.class);
    	this.startActivity(intent);
		
	}
	
	/**
	 * 
	 * Displays a Toast error message to the user.
	 * 
	 * @param msg - message to display
	 */
	private void displayErrMessage(String msg){
		
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Check to make sure the app is linked to a DropBox Account.
	 * 
	 * 
	 */
	public void checkDbxAcct(){
		
		
		if(checkWifi()){
			DbxAccountManager mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), getString(R.string.DBX_APP_KEY), getString(R.string.DBX_SECRET_KEY));
			if(!(mDbxAcctMgr.hasLinkedAccount())){
				mDbxAcctMgr.startLink((Activity)this, 0);
			}
			else{
				String message = String.format("in checkDbxAcct and the DbxAcctManager is convinced we have a linked Account.");
				Log.d(TAG, message);
				startLoadActivity();
				
			}
		}
		else{
			
			displayErrMessage(this.getString(R.string.ERR_WIFI));

			
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String message = String.format("in onActivityResult and we got a requestCode of %d and resultCode of %d.", requestCode, resultCode);
		Log.d(TAG, message);
		
		  if (requestCode == 0) {

			     if(resultCode == RESULT_OK){ 
			    	 
			    	 startLoadActivity();  
			    	 
			     }
			     
			     else{
			    	 
			    	 displayErrMessage(this.getString(R.string.ERR_DROPBOX));
			    	 
			     }
		  }
	}

}

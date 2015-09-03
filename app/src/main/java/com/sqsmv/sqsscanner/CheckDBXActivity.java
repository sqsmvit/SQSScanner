package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * @author ChrisS
 *
 *Startup Activity makes sure that the application is linked to Dropbox
 *If the device is not linked it makes a request using the DropBox Sync API 
 *to form the link through the default browser.  Also sets the app title.
 *
 */
public class CheckDBXActivity extends Activity
{
    private static final String TAG = "CheckDBXActivity";

	private DroidConfigManager appConfig;
	private DropboxManager dropboxManager;

    /* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * ****** THIS IS THE ENTRY POINT ***********
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		String message = String.format("in onCreate and starting the PROGRAM for real!");
		Log.d(TAG, message);
		setContentView(R.layout.activity_check_dbx);

		appConfig = new DroidConfigManager(this);
		dropboxManager = new DropboxManager(this);

	}

	@Override
	protected void onResume()
	{
		super.onResume();
        checkDbxAcct();

		if(dropboxManager.finishAuthentication())
		{
			String accessToken = dropboxManager.getOAuth2AccessToken();
			appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, accessToken, "");
			dropboxManager.setStaticOAuth2AccessToken(accessToken);
		}
		if(dropboxManager.hasLinkedAccount())
        {
            startLoadActivity();
        }
	}

	/**
	 * Check if the device is connected to wifi.
	 * 
	 * @return boolean for the wifi connection
	 */
	private boolean checkWifi()
	{
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		String message = String.format("in checkWifi and is connected is %b", wifi.isConnected());
		Log.d(TAG, message);
		return wifi.isConnected();
	}

	/**
	 * Starts the LoadActivity
	 */
	private void startLoadActivity()
    {
 		Intent intent = new Intent(this, LoadActivity.class);
    	this.startActivity(intent);
	}
	
	/**
	 * Displays a Toast error message to the user.
	 * @param msg - message to display
	 */
	private void displayErrMessage(String msg)
    {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Check to make sure the app is linked to a DropBox Account.
	 */
	public void checkDbxAcct()
    {
		if(checkWifi())
        {
			String accessToken = appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, null, "");
			if(!accessToken.isEmpty())
			{
				dropboxManager.setStaticOAuth2AccessToken(accessToken);
			}
			else
			{
				dropboxManager.linkDropboxAccount();
			}
		}
		else
        {
			displayErrMessage(this.getString(R.string.ERR_WIFI));
            finish();
		}
	}
}

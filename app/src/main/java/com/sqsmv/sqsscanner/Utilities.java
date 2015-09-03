package com.sqsmv.sqsscanner;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class Utilities
{
	private static final String TAG = "Utilities";
	
	public static void cleanFolder(File root, long days)
	{
		Log.d(TAG, "In cleanFolder()");
		if (root.exists())
		{
			File[] fileList = root.listFiles();
			
			long eligibleForDeletion = System.currentTimeMillis()
					- (days * 24 * 60 * 60 * 1000L);

			for (File listFile : fileList)
			{
				if(listFile.lastModified() < eligibleForDeletion)
				{
					if (!listFile.delete())
						Log.w(TAG, "Unable to Delete File: " + listFile.getName());
				}
			}
		}
	}
	
	public static void makeToast(Context callingContext, String message)
	{
		Log.d(TAG, "In makeToast()");
		Toast.makeText(callingContext, message, Toast.LENGTH_SHORT).show();
	}

    public static void makeLongToast(Context callingContext, String message)
    {
        Log.d(TAG, "In makeToast()");
        Toast.makeText(callingContext, message, Toast.LENGTH_LONG).show();
    }

	public static String getDefaultGateway(Context callingContext)
	{
		Log.d(TAG, "In getDefaultGateway()");
		WifiManager wifi = (WifiManager)callingContext.getSystemService(callingContext.WIFI_SERVICE);
		DhcpInfo d = wifi.getDhcpInfo();
		String s_gateway = intToIp(d.gateway);
		return s_gateway;
	}
	
	private static String intToIp(int i)
	{
		Log.d(TAG, "In intToIp()");
		return ((i >> 24 ) & 0xFF ) + "." +
				((i >> 16 ) & 0xFF) + "." +
				((i >> 8 ) & 0xFF) + "." +
				( i & 0xFF);
	}

	public static String getVersion(Context callingContext)
	{
		String version = "";

		try
		{
			PackageInfo packageInfo = callingContext.getPackageManager().getPackageInfo(callingContext.getPackageName(), 0);
			version = packageInfo.versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return version;
	}
}

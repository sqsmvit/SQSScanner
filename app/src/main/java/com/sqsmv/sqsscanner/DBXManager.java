package com.sqsmv.sqsscanner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

/**Manages interactions through the DropBox Sync API
 * 
 * 
 * @author ChrisS
 *
 */
public class DBXManager {

	private DbxAccountManager mDbxAcctMgr;
	private DbxFileSystem dbxFs;
	private Context context;
	static final private String appKey = "nau5nnk7gnhddc0";
	static final private String appSecretKey = "mwkurdz74x9pang";
	
	/**
	 * @param c  Context of the calling activity
	 */
	public DBXManager(Context c)
	{
		this.context = c;
		this.init();
		this.mDbxAcctMgr = DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecretKey);
		this.getFileSystem();
		clearCache();
	}
	

	/**Initializes the manager.  Makes sure wifi is accesible and creates the manager.
	 * 
	 */
	public void init()
	{
		ConnectivityManager connManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(wifi.isConnected())
		{
			DbxAccountManager mDbxAcctMgr = DbxAccountManager.getInstance(this.context.getApplicationContext(), DBXManager.appKey, DBXManager.appSecretKey);
			if(!(mDbxAcctMgr.hasLinkedAccount()))
			{
				mDbxAcctMgr.startLink((Activity)this.context, 0);
			}
		}
		else
		{
			Toast.makeText(this.context, "Must be connected to WiFi to start scan!", Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * gets the DropBox file system
	 * 
	 */
	private void getFileSystem(){
		
		try
		{
			this.dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
		}
		catch (Unauthorized e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param thisFile
	 * @throws DbxException
	 */
	private void checkFileSyncStatus(DbxFile thisFile) throws DbxException
	{
		DbxFileStatus status = thisFile.getSyncStatus();
		Log.d("DBXManager", status.toString());
		if(!status.isCached)
			thisFile.update();
		/*
		while(!status.isLatest)
		{
			thisFile.update();
			status = thisFile.getSyncStatus();
			//Log.d("DBXManager", status.toString());
			
			if(status.isLatest)
				Log.d("DBXManager", "Latest");
			else
				Log.d("DBXManager", "Not Latest");
		}
		*/
	}
	
	/**
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public DbxFile openFile(String filePath) throws IOException
	{
		DbxPath dbxPath = new DbxPath(filePath);
		DbxFile thisFile = this.dbxFs.open(dbxPath);
		checkFileSyncStatus(thisFile);
		return thisFile;
	}
	
	/**
	 * @param dbxFile
	 * @param path
	 * @throws IOException
	 */
	public void writeToStorage(FileInputStream dbxFile, String path) throws IOException
	{
		FileOutputStream out = new FileOutputStream(path);
		
		byte[] buff = new byte[1024];
		int len;
		
		while((len = dbxFile.read(buff)) > 0)
		{
			out.write(buff, 0, len);
		}
		dbxFile.close();
		out.close();
	}
	
	/**
	 * @param filePath
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public boolean writeFile(String filePath, InputStream in) throws IOException{
		
		DbxPath dbxPath = new DbxPath(filePath);
		
		DbxFile thisFile = this.dbxFs.open(dbxPath);
		
		FileOutputStream out = thisFile.getWriteStream();
		
	    byte[] buf = new byte[1024];
	    int len;
	    
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    
	    in.close();
	    out.close();
		
		return true;
		
	}
	
	/**
	 * 
	 */
	public void checkAccount()
	{
		if(!(mDbxAcctMgr.hasLinkedAccount()))
		{
			mDbxAcctMgr.startLink((Activity) this.context, 0);
		}
	}
	
	private void clearCache()
	{
		try
		{
			dbxFs.setMaxFileCacheSize(0);
		}
		catch(DbxException e)
		{
			Log.d("DBXManager", "clearCache Exception Caught");
			e.printStackTrace();
		}
	}
}

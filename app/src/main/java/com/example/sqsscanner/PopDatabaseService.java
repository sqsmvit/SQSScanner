package com.example.sqsscanner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dropbox.sync.android.DbxFile;
import com.example.sqsscanner.DB.DataSource;
import com.example.sqsscanner.DB.LensDataSource;
import com.example.sqsscanner.DB.PriceListDataSource;
import com.example.sqsscanner.DB.ProductDataSource;
import com.example.sqsscanner.DB.ProductLensDataSource;
import com.example.sqsscanner.DB.UPCDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/* special note ... this is a service ... woot woot! */
public class PopDatabaseService extends IntentService
{
	private static final String TAG = "PopDatabaseService";
	private Boolean forceDBUpdate;

    String zipFileName = "files.zip";
	
	public PopDatabaseService() {
		super(TAG);
		forceDBUpdate = false;
	}

	@Override
	protected void onHandleIntent(Intent intent)
    {
		
		Log.i(TAG, "Received an Intent: " + intent);
		String[] xmlFiles = intent.getStringArrayExtra("XML_FILES");
		int[] xmlSchemas = intent.getIntArrayExtra("XML_SCHEMAS");
		forceDBUpdate = intent.getIntExtra("FORCE_UPDATE", 0) == 1 ? true : false;
		
		//TODO: Uncomment when pull list info is being used
		//DataSource[] dataSources = new DataSource[] {new ProductsDataSource(this), new UPCDataSource(this), new PriceListDataSource(this), new PullListDataSource(this), new PullLinesDataSource(this)};
		
		DataSource[] dataSources = new DataSource[] {new LensDataSource(this), new ProductDataSource(this), new UPCDataSource(this), new PriceListDataSource(this), new ProductLensDataSource(this)};//, new PullListDataSource(this)};
		makeNotification("Dropbox Download Started", false);

        //Download files.zip from DropBox
        downloadDBXZip();
        File zipFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + zipFileName);
        unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + zipFileName);
        zipFile.delete();

        makeNotification("Database Update Started", false);

        int i = 0;
		
		for(DataSource dataSource : dataSources)
		{
			FMDumpHandler xmlHandler = new FMDumpHandler(this, xmlFiles[i], dataSource, this.getResources().getStringArray(xmlSchemas[i]), forceDBUpdate);
			xmlHandler.run();
			i += 1;
		}

        makeNotification("Database Update Finished", true);
	}

    private void downloadDBXZip()
    {
        String message = String.format("in copyDBXFile");
        Log.d(TAG, message);
        DBXManager dbxMan = new DBXManager(this);

        try
        {
            DbxFile dbxXml = dbxMan.openFile("/out/lens.xml");
            dbxMan.writeToStorage(dbxXml.getReadStream(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/lens.xml");
            dbxXml.close();
            //DbxFile
            dbxXml = dbxMan.openFile("/out/" + zipFileName);
            dbxMan.writeToStorage(dbxXml.getReadStream(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + zipFileName);
            dbxXml.close();
            Log.i(this.toString(), "It worked!");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(this.toString(), "It did NOT Work! DropBox ERROR", e);
        }
    }

    private void unzip(String zipFile)
    {
        byte[] buffer = new byte[1024];

        try
        {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null)
            {
                String fileName = ze.getName();
                File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

	private void makeNotification(String message, boolean finished)
	{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setContentTitle("SQSScanner")
		        .setContentText(message)
		        .setTicker(message);
		long[] pattern = {0, 1000, 500, 1000};
		if(finished)
		{
	        mBuilder.setSmallIcon(R.drawable.ic_launcher);
			mBuilder.setVibrate(pattern);
		}
		else
			mBuilder.setSmallIcon(R.drawable.ic_pen);
		// Creates an explicit intent for an Activity in your app
		Intent emptyIntent = new Intent();

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
}

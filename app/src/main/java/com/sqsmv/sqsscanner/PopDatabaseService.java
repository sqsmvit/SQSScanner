package com.sqsmv.sqsscanner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sqsmv.sqsscanner.DB.DataSource;
import com.sqsmv.sqsscanner.DB.LensDataSource;
import com.sqsmv.sqsscanner.DB.PriceListDataSource;
import com.sqsmv.sqsscanner.DB.ProductDataSource;
import com.sqsmv.sqsscanner.DB.ProductLensDataSource;
import com.sqsmv.sqsscanner.DB.UPCDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/* special note ... this is a service ... woot woot! */
public class PopDatabaseService extends IntentService
{
	private static final String TAG = "PopDatabaseService";

    private String zipFileName = "files.zip";
	
	public PopDatabaseService()
    {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent)
    {
		Log.i(TAG, "Received an Intent: " + intent);
		String[] xmlFiles = intent.getStringArrayExtra("XML_FILES");
		int[] xmlSchemas = intent.getIntArrayExtra("XML_SCHEMAS");

		DataSource[] dataSources = new DataSource[] {new LensDataSource(this), new ProductDataSource(this), new UPCDataSource(this),
                                                     new PriceListDataSource(this), new ProductLensDataSource(this)};
		makeNotification("Dropbox Download Started", false);

        //Needed for X and X2
        //resetDBs();

        //Download files.zip from DropBox
        downloadDBXZip();
        File zipFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + zipFileName);
        unzip(zipFile);
        zipFile.delete();

        makeNotification("Database Update Started", false);

        int i = 0;
        ArrayList<Thread> updateThreads = new ArrayList<Thread>();
		for(DataSource dataSource : dataSources)
		{
			FMDumpHandler xmlHandler = new FMDumpHandler(xmlFiles[i], dataSource, getResources().getStringArray(xmlSchemas[i]));
			//xmlHandler.run();
            Thread updateThread = new Thread(xmlHandler, xmlFiles[i]);
            updateThreads.add(updateThread);
            //updateThread.start();
			i += 1;
		}

        startUpdateThreads(updateThreads);

        makeNotification("Database Update Finished", true);
	}

    private void downloadDBXZip()
    {
        String message = String.format("in copyDBXFile");
        Log.d(TAG, message);
        DropboxManager dbxMan = new DropboxManager(this);

        dbxMan.writeToStorage("/out/" + zipFileName, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + zipFileName, false);
    }

    private void unzip(File zipFile)
    {
        byte[] buffer = new byte[1024];

        try
        {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            String fileDirectory = zipFile.getParent();

            while(ze!=null)
            {
                String fileName = ze.getName();
                File newFile = new File(fileDirectory + File.separator + fileName);
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

    private void startUpdateThreads(ArrayList<Thread> updateThreads)
    {
        int count = 0;
        while(!updateThreads.isEmpty())
        {
            Thread updateThread = updateThreads.get(count);
            Log.d(TAG, "Staring thread " + updateThread.getName());
            updateThread.start();
            count++;
            if((count % 2) == 0 || updateThreads.size() < 2)
            {
                for(int i = 0; i < count; i++)
                {
                    try
                    {
                        updateThreads.remove(0).join();
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                count = 0;
            }
        }
    }

    private void resetDBs()
    {
        LensDataSource lds = new LensDataSource(this);
        lds.open();
        lds.resetDB();
        lds.close();

        ProductLensDataSource plds = new ProductLensDataSource(this);
        plds.open();
        plds.resetDB();
        plds.close();
    }
}

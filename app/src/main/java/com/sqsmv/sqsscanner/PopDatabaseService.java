package com.sqsmv.sqsscanner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.lens.LensAccess;
import com.sqsmv.sqsscanner.database.pricelist.PriceListAccess;
import com.sqsmv.sqsscanner.database.prodloc.ProdLocAccess;
import com.sqsmv.sqsscanner.database.product.ProductAccess;
import com.sqsmv.sqsscanner.database.productlens.ProductLensAccess;
import com.sqsmv.sqsscanner.database.upc.UPCAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import andoidlibs.db.xml.XMLDBAccess;

/**
 * Service that coordinates the steps for updating the database using the export files from FileMaker.
 */
public class PopDatabaseService extends IntentService
{
	private static final String TAG = "PopDatabaseService";

    private String zipFileName = "files.zip";
    private String zipStorageLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + zipFileName;

    /**
     * Constructor.
     */
	public PopDatabaseService()
    {
		super(TAG);
	}

    @Override
	protected void onHandleIntent(Intent intent)
    {
        makeNotification("Dropbox Download Started", false);
        DBAdapter dbAdapter = new DBAdapter(this);
        Semaphore popDBSemaphore = new Semaphore(2);

        boolean isSlowUpdate = Utilities.totalDeviceMemory(this) <= 1024;

        //Download files.zip from Dropbox
        downloadDBXZip();
        File zipFile = new File(zipStorageLocation);
        try
        {
            unzip(zipFile);
            zipFile.delete();

            resetTables(dbAdapter);
            makeNotification("Database Update Started", false);

            XMLDBAccess[] xmlDBAccesses = new XMLDBAccess[]{new LensAccess(dbAdapter), new ProductAccess(dbAdapter),
                                                            new UPCAccess(dbAdapter), new PriceListAccess(dbAdapter),
                                                            new ProdLocAccess(dbAdapter), new ProductLensAccess(dbAdapter)};
            ArrayList<Thread> updateThreads = new ArrayList<Thread>();
            for(XMLDBAccess xmlDBAccess : xmlDBAccesses)
            {
                updateThreads.add(new FMDumpHandler(xmlDBAccess, isSlowUpdate, popDBSemaphore));
            }

            startUpdateThreads(updateThreads);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        dbAdapter.close();
        makeNotification("Database Update Finished", true);
	}

    /**
     * Downloads the .zip file that contains all of the exported FileMaker XML data.
     */
    private void downloadDBXZip()
    {
        Log.d(TAG, "in copyDBXFile");
        DropboxManager dbxMan = new DropboxManager(this);

        dbxMan.writeToStorage("/out/" + zipFileName, zipStorageLocation, false);
    }

    /**
     * Uncompresses a specified file in the directory it is currently in. The file is assumed to be a compressed zip file.
     * @param zipFile    The File to uncompress.
     * @throws IOException
     */
    private void unzip(File zipFile) throws IOException
    {
        byte[] buffer = new byte[1024];

        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();
        String fileDirectory = zipFile.getParent();

        while(ze!=null)
        {
            String fileName = ze.getName();
            File newFile = new File(fileDirectory + "/" + fileName);
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

    /**
     * Makes an Android notification that will alert the user to the current status the import is in.
     * @param message     Message to put on the notification.
     * @param finished    Whether the import is finished or not.
     */
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
        {
            mBuilder.setSmallIcon(R.drawable.ic_pen);
        }
		// Creates an explicit intent for an Activity in your app
		Intent emptyIntent = new Intent();

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}

    /**
     * Loops through an ArrayList of Threads and starts each thread, then joins each of them.
     * @param updateThreads    The ArrayList of Threads to loop through.
     */
    private void startUpdateThreads(ArrayList<Thread> updateThreads)
    {
        for(Thread updateThread : updateThreads)
        {
            updateThread.start();
        }
        for(Thread updateThread : updateThreads)
        {
            try
            {
                updateThread.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Controls the resetting of tables that need to be recreated to compensate for records that were deleted from the FileMaker database.
     * @param dbAdapter    The DBAdapter to use for access to the database.
     */
    private void resetTables(DBAdapter dbAdapter)
    {
        DroidConfigManager droidConfigManager = new DroidConfigManager(this);
        Date currentDate = new Date();

        LensAccess lensAccess = new LensAccess(dbAdapter);
        lensAccess.open();
        lensAccess.reset();

        ProdLocAccess prodLocAccess = new ProdLocAccess(dbAdapter);
        prodLocAccess.open();
        prodLocAccess.reset();

        try
        {
            Date lastDropDate = Utilities.parseYYMMDDString(droidConfigManager.accessString(DroidConfigManager.PRODUCTLENS_RESET_DATE, null, ""));

            if(((currentDate.getTime() - lastDropDate.getTime()) / 86400000) >= 7)
            {
                ProductLensAccess productLensAccess = new ProductLensAccess(dbAdapter);
                productLensAccess.open();
                productLensAccess.reset();
                droidConfigManager.accessString(DroidConfigManager.PRODUCTLENS_RESET_DATE, Utilities.formatYYMMDDDate(currentDate), "");
            }
        }
        catch(ParseException e)
        {
            droidConfigManager.accessString(DroidConfigManager.PRODUCTLENS_RESET_DATE, Utilities.formatYYMMDDDate(currentDate), "");
        }
    }
}

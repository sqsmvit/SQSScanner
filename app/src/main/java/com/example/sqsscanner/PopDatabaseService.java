package com.example.sqsscanner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sqsscanner.DB.DataSource;
import com.example.sqsscanner.DB.PriceListDataSource;
import com.example.sqsscanner.DB.ProductsDataSource;
import com.example.sqsscanner.DB.UPCDataSource;


/* special note ... this is a service ... woot woot! */
public class PopDatabaseService extends IntentService
{
	private static final String TAG = "PopDatabaseService";
	private Boolean forceDBUpdate;
	
	
	public PopDatabaseService() {
		super(TAG);
		forceDBUpdate = false;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.i(TAG, "Received an Intent: " + intent);
		String[] xmlFiles = intent.getStringArrayExtra("XML_FILES");
		int[] xmlSchemas = intent.getIntArrayExtra("XML_SCHEMAS");
		forceDBUpdate = intent.getIntExtra("FORCE_UPDATE", 0) == 1 ? true : false;
		
		//TODO: Uncomment when pull list info is being used
		//DataSource[] dataSources = new DataSource[] {new ProductsDataSource(this), new UPCDataSource(this), new PriceListDataSource(this), new PullListDataSource(this), new PullLinesDataSource(this)};
		
		DataSource[] dataSources = new DataSource[] {new ProductsDataSource(this), new UPCDataSource(this), new PriceListDataSource(this)};
		makeNotification("Database update started", false);
		
		int i = 0;
		
		for(DataSource dataSource : dataSources)
		{
			FMDumpHandler xmlHandler = new FMDumpHandler(this, xmlFiles[i], dataSource, this.getResources().getStringArray(xmlSchemas[i]), forceDBUpdate);
			xmlHandler.run();
			i += 1;
		}
		makeNotification("Database finished updating", true);
	}

	public void makeNotification(String message, boolean finished)
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

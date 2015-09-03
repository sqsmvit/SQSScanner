package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
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

    private DroidConfigManager appConfig;
    private UpdateLauncher updateLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		String message = String.format("in onCreate and starting the LoadActivity!");
		Log.d(TAG, message);
		setContentView(R.layout.activity_load);

        appConfig = new DroidConfigManager(this);
        updateLauncher = new UpdateLauncher(this);
        setTitle(getTitle() + " v" + Utilities.getVersion(this));
    }

	public void startScan(View v)
    {
		String message = String.format("in startScan and for the LoadActivity!");
		Log.d(TAG, message);
		System.out.println(message);

		String buildDate = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());

		if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
		{
            if(updateLauncher.checkNeedAppUpdate())
            {
                updateLauncher.startAppUpdate();
            }
            else
            {
                try
                {
                    appConfig.accessString(DroidConfigManager.PRIOR_VERSION, "", "");

                    Utilities.cleanFolder(new File(Environment.getExternalStorageDirectory().toString() + "/backups"), 180);
                    appConfig.accessString(DroidConfigManager.BUILD_DATE, buildDate, "");

                    long productLensResetMilli = Long.parseLong(appConfig.accessString(DroidConfigManager.PRODUCTLENS_RESET_MILLI, null, "0"));

                    LensDataSource lds = new LensDataSource(this);
                    lds.open();
                    lds.resetDB();
                    lds.close();
                    long currentTimeMIllis = System.currentTimeMillis();
                    if(((currentTimeMIllis - productLensResetMilli) / 86400000) >= 7)
                    {
                        ProductLensDataSource plds = new ProductLensDataSource(this);
                        plds.open();
                        plds.resetDB();
                        plds.close();
                    }

                    appConfig.accessString(DroidConfigManager.PRODUCTLENS_RESET_MILLI, Long.toString(currentTimeMIllis), "");

                    launchDBUpdate();
                }
                catch(NotFoundException e)
                {
                    Log.i(TAG, "Crash during db pop", e);
                    e.printStackTrace();
                }
            }
		}
        else
        {
            startScanHomeActivity();
        }
	}

    private void launchDBUpdate()
    {
        final Thread blockingThread = updateLauncher.startDBUpdate();
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    blockingThread.join();
                    startScanHomeActivity();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startScanHomeActivity()
    {
        Intent intent = new Intent(this, ScanHomeActivity.class);
        startActivity(intent);
    }
}


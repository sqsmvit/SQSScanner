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
    private DropboxManager dropboxManager;
    private UpdateLauncher updateLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
		Log.d(TAG, "in onCreate and starting the LoadActivity!");

        appConfig = new DroidConfigManager(this);
        dropboxManager = new DropboxManager(this);
        setTitle(getTitle() + " v" + Utilities.getVersion(this));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(Utilities.checkWifi(this))
        {
            if(dropboxManager.finishAuthentication())
            {
                String accessToken = dropboxManager.getOAuth2AccessToken();
                appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, accessToken, "");
            }
            linkDropboxAccount();
            updateLauncher = new UpdateLauncher(this);
        }
        else
        {
            Utilities.makeLongToast(this, getString(R.string.ERR_WIFI));
            finish();
        }
    }

    private void linkDropboxAccount()
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

    public void startScan(View v)
    {
		Log.d(TAG, "in startScan and for the LoadActivity!");

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
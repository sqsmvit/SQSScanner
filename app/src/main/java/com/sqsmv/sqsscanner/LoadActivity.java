package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.dropbox.core.android.Auth;

import java.io.File;
import java.util.Date;

/**
 * Entry Activity into SQSScanner. Dropbox linking is launched from this screen if it has not been done yet. Automatic app and database updates are
 * also launched from this screen.
 */
public class LoadActivity extends Activity
{
	private static final String TAG = "LoadActivity";

    private DroidConfigManager appConfig;
    private DropboxManager dropboxManager;
    private UpdateLauncher updateLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
		Log.d(TAG, "in onCreate and starting the LoadActivity!");

        setTitle(getTitle() + " v" + Utilities.getVersion(this));

        appConfig = new DroidConfigManager(this);
        dropboxManager = new DropboxManager(this);
        updateLauncher = new UpdateLauncher(this);

        findViewById(R.id.scanHomeButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startScan();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        String accessToken = appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, null, null);
        if (accessToken == null)
        {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null)
            {
                appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, accessToken, null);
                dropboxManager.initDbxClient(accessToken);
            }
            else
            {
                if(Utilities.checkWifi(this))
                {
                    dropboxManager.linkDropboxAccount();
                }
                else
                {
                    Utilities.makeLongToast(this, "Must be connected to WiFi to link to Dropbox!");
                    finish();
                }
            }
        }
        else
        {
            dropboxManager.initDbxClient(accessToken);
        }
    }

    /**
     * Checks the config file to see when the app was already updated on the current day. If not, it checks Dropbox for a new update to the app and
     * launches an update if there is one. If there isn't, then it launches the update for the database. ScanHomeActivity is launched if the update
     * has already been done.
     */
    public void startScan()
    {
		Log.d(TAG, "in startScan and for the LoadActivity!");

        String buildDate = Utilities.formatYYMMDDDate(new Date());
        if(!buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, "")) && Utilities.checkWifi(this))
        {
            if(updateLauncher.checkNeedAppUpdate())
            {
                updateLauncher.startAppUpdate();
            }
            else
            {
                appConfig.accessString(DroidConfigManager.PRIOR_VERSION, "", "");
                Utilities.cleanFolder(new File(Environment.getExternalStorageDirectory().toString() + "/backups"), 180);
                launchDBUpdate();
                appConfig.accessString(DroidConfigManager.BUILD_DATE, buildDate, "");
            }
        }
        else if(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, "")))
        {
            goToScanHomeActivity();
        }
        else
        {
            Utilities.makeToast(this, getString(R.string.ERR_WIFI));
        }
	}

    /**
     * Launches the database update, joining the blocking thread so ScanHomeActivity can be launched when the blocking thread finishes.
     */
    private void launchDBUpdate()
    {
        final Thread blockingThread = updateLauncher.startDBUpdate(true);
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    blockingThread.join();
                    goToScanHomeActivity();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Launches ScanHomeActivity.
     */
    private void goToScanHomeActivity()
    {
        Intent intent = new Intent(this, ScanHomeActivity.class);
        startActivity(intent);
    }
}
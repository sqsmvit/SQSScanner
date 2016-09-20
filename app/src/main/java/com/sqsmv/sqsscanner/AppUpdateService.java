package com.sqsmv.sqsscanner;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * The AppUpdateService is an IntentService that coordinates the steps for downloading and updating the app to a version stored on Dropbox.
 */
public class AppUpdateService extends IntentService
{
    private static final String TAG = "AppUpdateService";
    private String apkFileName, apkStorageLocation;
    private DropboxManager dropboxManager;

    /**
     * Constructor.
     */
    public AppUpdateService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        apkFileName = getString(R.string.apk_file_name);
        apkStorageLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + apkFileName;

        DroidConfigManager appConfig = new DroidConfigManager(this);
        dropboxManager = new DropboxManager(this);

        downloadAPK();
        String dbxFileRev = dropboxManager.getDbxFileRev("/out/" + apkFileName);
        appConfig.accessString(DroidConfigManager.CURRENT_APK_REV, dbxFileRev, "");
        updateApp();
    }

    /**
     * Downloads the new .apk file.
     */
    private void downloadAPK()
    {
        dropboxManager.writeToStorage("/out/" + apkFileName,
                apkStorageLocation, false);
    }

    /**
     * Updates the app using the downloaded .apk file.
     */
    private void updateApp()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkStorageLocation)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Without this flag android returns an intent error!
        startActivity(intent);
    }
}

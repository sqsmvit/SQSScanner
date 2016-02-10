package com.sqsmv.sqsscanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

/**
 * Utility class for starting updates for the app.
 */
public class UpdateLauncher
{
    private Context context;
    private DroidConfigManager appConfig;

    /**
     * Constructor.
     * @param context    The Context of the Activity or Service UpdateLauncher was instantiated for.
     */
    public UpdateLauncher(Context context)
    {
        this.context = context;
        appConfig = new DroidConfigManager(context);
    }

    /**
     * Starts the Service used to update the database for the app.
     * @param startBlockingThread    Whether the Thread for blocking user input needs to be started or not.
     * @return The Thread for blocking user input.
     */
    public Thread startDBUpdate(boolean startBlockingThread)
    {
        Intent popIntent = new Intent(context, PopDatabaseService.class);

        context.startService(popIntent);

        final ProgressDialog pausingDialog = new ProgressDialog(context);
        pausingDialog.setTitle("Updating Database");
        pausingDialog.setMessage("Please Stay in Wifi Range...");
        pausingDialog.setCancelable(true);

        Thread pausingDialogThread = new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(20000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                pausingDialog.dismiss();
            }
        };
        if(startBlockingThread)
        {
            pausingDialog.show();
            pausingDialogThread.start();
        }
        return pausingDialogThread;
    }

    /**
     * Checks Dropbox to see if a newer version of the apk has been uploaded.
     * @return true if an update is needed, otherwise false.
     */
    public boolean checkNeedAppUpdate()
    {
        DropboxManager dropboxManager = new DropboxManager(context);
        boolean needUpdate = false;

        String apkFileName = context.getString(R.string.apk_file_name);
        String currentRev = appConfig.accessString(DroidConfigManager.CURRENT_APK_REV, null, "");
        String dbxFileRev = dropboxManager.getDbxFileRev("/out/" + apkFileName);

        if(currentRev.isEmpty())
        {
            appConfig.accessString(DroidConfigManager.CURRENT_APK_REV, dbxFileRev, "");
        }
        else if(!currentRev.equals(dbxFileRev) || appConfig.accessString(DroidConfigManager.PRIOR_VERSION, null, "").equals(Utilities.getVersion(context)))
        {
            needUpdate = true;
        }

        return needUpdate;
    }

    /**
     * Starts the Service used to update the app.
     */
    public void startAppUpdate()
    {
        appConfig.accessString(DroidConfigManager.PRIOR_VERSION, Utilities.getVersion(context), "");
        ProgressDialog.show(context, "Updating Application", "Please Stay in Wifi Range...", true);
        Intent appUpdateIntent = new Intent(context, AppUpdateService.class);
        context.startService(appUpdateIntent);
    }
}

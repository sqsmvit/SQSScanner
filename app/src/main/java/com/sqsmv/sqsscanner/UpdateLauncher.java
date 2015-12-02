package com.sqsmv.sqsscanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;

public class UpdateLauncher
{
    private Context context;
    private DroidConfigManager appConfig;
    private DropboxManager dropboxManager;

    public UpdateLauncher(Context activityContext)
    {
        context = activityContext;
        appConfig = new DroidConfigManager(activityContext);
        dropboxManager = new DropboxManager(context);
    }

    public Thread startDBUpdate()
    {
        Intent popIntent = new Intent(context, PopDatabaseService.class);
        String[] xmlFiles = context.getResources().getStringArray(R.array.fmDumpFiles);
        int[] xmlSchemas = getSchemaResIds();

        popIntent.putExtra("XML_FILES", xmlFiles);
        popIntent.putExtra("XML_SCHEMAS", xmlSchemas);
        popIntent.putExtra("FORCE_UPDATE", 1);

        context.startService(popIntent);



        final ProgressDialog pausingDialog = ProgressDialog.show(context, "Updating Database", "Please Stay in Wifi Range...", true);
        Thread pausingDialogThread = new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                pausingDialog.dismiss();
            }
        };
        pausingDialogThread.start();
        return pausingDialogThread;
    }

    public boolean checkNeedAppUpdate()
    {
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

    public void startAppUpdate()
    {
        appConfig.accessString(DroidConfigManager.PRIOR_VERSION, Utilities.getVersion(context), "");
        ProgressDialog.show(context, "Updating Application", "Please Stay in Wifi Range...", true);
        Intent appUpdateIntent = new Intent(context, AppUpdateService.class);
        context.startService(appUpdateIntent);
    }

    /**
     * Gets the resource Ids for the schemas of the xml files.
     *
     * Located in xml_input.xml
     *
     * @return
     */
    private int[] getSchemaResIds()
    {
        TypedArray xmlArrays = context.getResources().obtainTypedArray(R.array.xml_list);
        int[] xmlSchemas = new int[xmlArrays.length()];

        for(int i = 0; i < xmlArrays.length(); i++)
        {
            xmlSchemas[i] = xmlArrays.getResourceId(i, 0);
        }

        xmlArrays.recycle();
        return xmlSchemas;
    }

}

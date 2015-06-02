package com.sqsmv.sqsscanner;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.dropbox.sync.android.DbxFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUpdateService extends IntentService
{
    private static final String TAG = "AppUpdateService";
    private String apkFileName;

    public AppUpdateService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        apkFileName = getString(R.string.apk_file_name);
        SharedPreferences appConfig = getSharedPreferences("scanConfig", 0);
        DBXManager dbxManager = new DBXManager(this);

        downloadAPK();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.US);
        Date fileModifiedDate = dbxManager.getDbxFileDate("/out/" + apkFileName);
        appConfig.edit().putString("lastAppUpdate", dateFormat.format(fileModifiedDate)).apply();

        updateApp();
    }

    private void downloadAPK()
    {
        DBXManager dbxMan = new DBXManager(this);

        try
        {
            DbxFile dbxXml = dbxMan.openFile("/out/lens.xml");
            dbxMan.writeToStorage(dbxXml.getReadStream(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/lens.xml");
            dbxXml.close();
            File updateFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "lens.xml");
            updateFile.delete();

            //DbxFile
            dbxXml = dbxMan.openFile("/out/" + apkFileName);
            dbxMan.writeToStorage(dbxXml.getReadStream(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + apkFileName);
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

    private void updateApp()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + apkFileName)),
                              "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
        startActivity(intent);
    }
}

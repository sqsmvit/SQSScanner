package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.scan.ScanAccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Activity that allows the user to review, commit, and mass delete their scans. Review information in this Activity is divided by pull number,
 * displaying how many records have been created for that pull and how many pieces have been scanned for product in that pull.
 */
public class PullReviewActivity extends Activity
{
    private static final String TAG = "PullReviewActivity";
    private DroidConfigManager appConfig;

    private DBAdapter dbAdapter;
    private ScanAccess scanAccess;

    private ListView pullListView;

    private SimpleAdapter pullAdapter;
    private ArrayList<HashMap<String, String>> pullNumberList;

    private int exportModeChoice, invAdjChoice;
    private boolean fileExported;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_review);

        appConfig = new DroidConfigManager(this);

        dbAdapter = new DBAdapter(this);
        scanAccess = new ScanAccess(dbAdapter);

        pullNumberList = new ArrayList<HashMap<String, String>>();

        setConfig();
        fileExported = false;

        pullListView = (ListView)findViewById(R.id.pullListView);

        pullNumberList = new ArrayList<HashMap<String, String>>();

        pullAdapter = new SimpleAdapter(this, pullNumberList, R.layout.pull_row,
                new String[] {"pullNum", "pullLines", "pullCount"},
                new int[]{R.id.Pull_Num, R.id.Pull_Lines, R.id.Pull_Count});

        pullListView.setAdapter(pullAdapter);

        setListeners();
        showExportMode();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        scanAccess.open();

        createAdapterDataset();
        pullAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        dbAdapter.close();
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("FILE_EXPORTED", fileExported);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    /**
     * Sets the listeners used for the current Activity's GUI elements.
     */
    private void setListeners()
    {
        findViewById(R.id.pullHeadBack).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        findViewById(R.id.massDelete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickDelete();
            }
        });

        findViewById(R.id.commitScan).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickCommit();
            }
        });

        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
            {
                String pullNum = pullNumberList.get(position).get("pullNum");
                goToScans(pullNum);
            }
        });
    }

    /**
     * Initializes the value of config related variables based off of the config file.
     */
    private void setConfig()
    {
        exportModeChoice = appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, null, 1);
        invAdjChoice = appConfig.accessInt(DroidConfigManager.INVENTORY_MODE_CHOICE, null, 1);
    }

    /**
     * Displays the export mode the app is currently in.
     */
    private void showExportMode()
    {
        ((TextView)findViewById(R.id.commitMode)).setText(ExportModeHandler.getExportMode(exportModeChoice));
    }

    /**
     * Creates the ArrayList of HashMaps of pull information for display.
     */
    private void createAdapterDataset()
    {
        pullNumberList.clear();
        HashMap<String, String> pullEntry;

        ArrayList<String> pulls = scanAccess.getPullNums();

        for (String pull : pulls)
        {
            pullEntry = new HashMap<String, String>();
            pullEntry.put("pullNum", pull);
            pullEntry.put("pullLines", Integer.toString(scanAccess.getTotalScansByPull(pull)));
            pullEntry.put("pullCount", Integer.toString(scanAccess.getTotalByPull(pull)));
            pullNumberList.add(pullEntry);
        }
    }

    /**
     * Prompts the user for mass deletion of all scan records.
     */
    private void onClickDelete()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Confirm Mass Delete")
                .setMessage("Delete ALL Scans?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        performMassDelete();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Deletes all records in the scan table.
     */
    private void performMassDelete()
    {
        scanAccess.deleteAll();
        pullNumberList.clear();
        pullAdapter.notifyDataSetChanged();
    }

    /**
     * Attempts to commit the scan to Dropbox, alerting the error if it is unsuccessful and clearing the scan table if successful.
     */
    private void onClickCommit()
    {
        if(!pullNumberList.isEmpty())
        {
            if(Utilities.checkWifi(this))
            {
                try
                {
                    File exportFile = writeFromDB();
                    if(ScanExporter.exportScan(this, exportFile, exportModeChoice))
                    {
                        if(exportModeChoice == 6)
                        {
                            appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, 1, 1);
                        }
                        ScanWriter.writeBackupFile(exportFile);
                        Utilities.makeToast(this, "File exported to DropBox");
                        performMassDelete();
                        fileExported = true;
                    }
                    else
                    {
                        Utilities.alertAlarm(this, 2000);
                        Utilities.alertVibrate(this, new long[] {0, 500, 500, 500, 500});
                        Utilities.makeToast(this, "Error exporting to DropBox");
                    }
                    exportFile.delete();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    Utilities.makeLongToast(this, "Error Writing Files.");
                }
            }
            else
            {
                Utilities.makeLongToast(this, "Not Connected to WiFi - Cannot Commit Scan.");
            }
        }
        else
        {
            Utilities.makeLongToast(this, "No Scan to Commit.");
        }
    }

    /**
     * Creates the export file from the Scan Table.
     * @return The created export file.
     * @throws IOException
     */
    private File writeFromDB() throws IOException
    {
        Cursor exportCursor = scanAccess.selectScansForPrint(exportModeChoice);
        File exportFile = ScanWriter.createExportFile(this, exportCursor, exportModeChoice, invAdjChoice);

        return exportFile;
    }

    /**
     * Launches ScanReviewActivity to review scans of the selected pull number.
     * @param pullNum    The pull number to review scans of.
     */
    private void goToScans(String pullNum)
    {
        Intent intent = new Intent(this, ScanReviewActivity.class);

        intent.putExtra("PULL_NUM", pullNum);
        startActivity(intent);
    }
}

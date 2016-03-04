package com.sqsmv.sqsscanner;

import android.content.Context;

import java.io.File;

/**
 * Utility class for exporting a file to Dropbox. This class contains the functionality determining which
 * directory the file should go into based off of settings used.
 */
public class ScanExporter
{
    /**
     * Exports a File to Dropbox, determining the appropriate sub directory for the File.
     * @param context       The Context of the Activity or Service making the method call.
     * @param exportFile    The File to export.
     * @param exportMode    The export mode being used.
     * @param fromCommit    Whether the call is being made from the initial commit or not.
     * @return true if the File was successfully exported, otherwise false.
     */
    public static boolean exportScan(Context context, File exportFile, int exportMode, boolean fromCommit)
    {
        return exportDBX(context, exportFile, ExportModeHandler.getExportDirectory(exportMode), fromCommit);
    }

    /**
     * Exports the File to the appropriate subdirectory on Dropbox.
     * @param context       The Context of the Activity or Service making the method call.
     * @param exportFile    The File to export.
     * @param exportPath    The path to the subdirectory to export to.
     * @param fromCommit    Whether the call is being made from the initial commit or not.
     * @return true if the File was successfully exported, otherwise false.
     */
    private static boolean exportDBX(Context context, File exportFile, String exportPath, boolean fromCommit)
    {
        DropboxManager dropboxManager = new DropboxManager(context);
        if(dropboxManager.hasLinkedAccount())
        {
            String scanPath = exportPath + exportFile.getName();
            dropboxManager.writeToDropbox(exportFile, scanPath, fromCommit, true);
            Utilities.makeToast(context, "File exported to DropBox");
            return true;
        }
        else
        {
            Utilities.makeToast(context, "Error exporting to DropBox");
            return false;
        }
    }
}

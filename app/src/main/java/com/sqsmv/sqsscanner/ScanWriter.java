package com.sqsmv.sqsscanner;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for translating data in the Scan table to a text file for export.
 */
public class ScanWriter
{
    /**
     * Creates an export File in the file storage area for the app.
     * @param context         The Context for the Activity or Service making the method call.
     * @param dbCursor        The Cursor containing the export information.
     * @param exportMode      The export mode being used.
     * @param invAdjChoice    The inventory adjustment choice, relevant if the export is in RI mode.
     * @return The File to be used for exporting.
     * @throws IOException
     */
    public static File createExportFile(Context context, Cursor dbCursor, int exportMode, int invAdjChoice) throws IOException
    {
        String fileName = buildFileName(exportMode);
        File exportFile = new File(context.getFilesDir() + "/" + fileName);
        FileOutputStream output = new FileOutputStream(exportFile, true);

        while(dbCursor.moveToNext())
        {
            String writeString = buildStringFromCursor(dbCursor);
            if(exportMode == 5)
            {
                if(invAdjChoice == 1)
                {
                    writeString = "add\t" + writeString;
                }
                else if(invAdjChoice == 2)
                {
                    writeString = "sub\t" + writeString;
                }
            }

            output.write(writeString.getBytes());
        }

        output.close();
        return exportFile;
    }

    /**
     * Traverses a row of a cursor to build a line of text for the export file.
     * @param dbCursor    The Cursor to traverse, already moved to the appropriate row.
     * @return The line of text for the export file.
     */
    private static String buildStringFromCursor(Cursor dbCursor)
    {
        String writeString = "";
        int columnCount = dbCursor.getColumnCount();

        for(int count = 0; count < columnCount; count++)
        {
            writeString += dbCursor.getString(count);
            if(count < columnCount - 1)
            {
                writeString += "\t";
            }
            else
            {
                writeString += "\n";
            }
        }
        return writeString;
    }

    /**
     * Builds the name of the export file.
     * @param exportMode    The export mode being used.
     * @return The name of the export file.
     */
    private static String buildFileName(int exportMode)
    {
        return ExportModeHandler.getFilePrefix(exportMode) + Utilities.getDeviceName() + "_" +
                Utilities.buildCurrentTimestamp() + ".txt";
    }

    /**
     * Writes an export file into the backup directory.
     * @param exportFile    The export file to write.
     * @throws IOException
     */
    public static void writeBackupFile(File exportFile) throws IOException
    {
        File root = new File(Environment.getExternalStorageDirectory().toString() + "/backups");
        root.mkdirs();
        File backupFile = new File(root.getAbsolutePath(), "B_" + exportFile.getName());
        Utilities.copyFile(exportFile, backupFile);
    }
}

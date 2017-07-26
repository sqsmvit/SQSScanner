package com.sqsmv.sqsscanner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for handling data related to export modes in the app.
 */
public class ExportModeHandler
{
    private final static ArrayList<String> exportModes =
            new ArrayList<String>(Arrays.asList(new String[] {"", "Normal Pull", "Consolidated Pull", "Bill B", "Drew",
                                                              "BInv", "Skid", "Reset", "Returns"}));
    private final static String[] filePrefixes = new String[] {"", "", "", "BB_", "DR_", "BI_", "S_", "IR_", "R_"};
    private final static String[] exportDirectories = new String[] {"/Default/", "/PullScan/", "/PullScan/", "/BB/", "/Drew/",
                                                                    "/BInv/", "/Skid/", "/Reset/", "/Returns/"};

    /**
     * Gets the ArrayList<String> listing all the export modes for the app.
     * @return The ArrayList<String> listing all the export modes.
     */
    public static ArrayList<String> getExportModesForSpinner()
    {
        ArrayList<String> exportModesForSpinner = new ArrayList<String>(exportModes);
        exportModesForSpinner.remove(0);
        return exportModesForSpinner;
    }

    /**
     * Gets the index of an export mode.
     * @param exportMode    The export mode.
     * @return The index in the ArrayList<String>
     */
    public static int getExportModeIndexFromSpinner(String exportMode)
    {
        return exportModes.indexOf(exportMode);
    }

    /**
     * Gets the export mode by index.
     * @param exportModeIndex    The index of the export mode.
     * @return The export mode.
     */
    public static String getExportMode(int exportModeIndex)
    {
        return exportModes.get(exportModeIndex);
    }

    /**
     * Gets the file prefix for export files by index.
     * @param exportModeIndex    The index of the export mode.
     * @return The file prefix.
     */
    public static String getFilePrefix(int exportModeIndex)
    {
        if(exportModeIndex < 0 || exportModeIndex >= exportDirectories.length)
        {
            exportModeIndex = 0;
        }
        return filePrefixes[exportModeIndex];
    }

    /**
     * Gets the export directory on Dropbox for an export by index.
     * @param exportModeIndex    The index of the export mode.
     * @return The export directory on Dropbox.
     */
    public static String getExportDirectory(int exportModeIndex)
    {
        if(exportModeIndex < 0 || exportModeIndex >= exportDirectories.length)
        {
            exportModeIndex = 0;
        }
        return exportDirectories[exportModeIndex];
    }
}

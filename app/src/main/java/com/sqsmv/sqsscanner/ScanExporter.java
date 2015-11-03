package com.sqsmv.sqsscanner;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**ScanExport class handles all exports from the device to 
 * either the DropBox or the SD Card.  First create the object
 * then make a call to exportScan() to export the file.
 * 
 * 
 * @author ChrisS
 *
 */
public class ScanExporter
{
	private File exportFile;
	private boolean fromCommit;
	private Context callingContext;
	private DropboxManager dropboxManager;
    private int exportMode;
	
	/**Creates a ScanExporter object which is used to export a
	 * scan to DropBox through the DropBox Sync API or to the External
	 * SD card.
	 * 
	 * @param ctx			context of calling application
	 * @param exportFile	the file to export
	 * @param exportMode	true - export to SD Card
	 * 						false - export to DropBox
	 */
	public ScanExporter(Context ctx, File exportFile, int exportMode, boolean fromCommit)
    {
		this.exportFile = exportFile;
		this.exportMode = exportMode;
		this.fromCommit = fromCommit;
		this.callingContext = ctx;
		this.dropboxManager = new DropboxManager(ctx);
	}
	
	public boolean exportScan() throws IOException
	{
        String exportPath = "/Default/";
        if(exportMode == 1 || exportMode == 2)
            exportPath = "/PullScan/";
        else if(exportMode == 3)
            exportPath = "/BB/";
        else if(exportMode == 4)
            exportPath = "/Drew/";
        else if(exportMode == 5)
            exportPath = "/RI/";
		else if(exportMode == 6)
			exportPath = "/Skid/";

        return exportDBX(exportPath);
	}
	
	/**
     * Exports the file to DropBox.
	 * @return if export is successful
	 * @throws IOException
	 */
	private boolean exportDBX(String exportPath) throws IOException
	{
		if(dropboxManager.hasLinkedAccount())
		{
			String scanPath = exportPath + this.exportFile.getName();
            dropboxManager.writeToDropbox(this.exportFile, scanPath, fromCommit, true);
			Toast.makeText(this.callingContext, "File exported to DropBox", Toast.LENGTH_SHORT).show();
			return true;
		}
		else
		{
			Toast.makeText(this.callingContext, "Error exporting to DropBox" , Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}

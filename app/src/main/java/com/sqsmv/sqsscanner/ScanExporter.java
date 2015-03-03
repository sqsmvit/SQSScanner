package com.sqsmv.sqsscanner;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

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
	private DbxAccountManager mDbxAcctMgr;
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
		this.mDbxAcctMgr = DbxAccountManager.getInstance(ctx.getApplicationContext(), ctx.getString(R.string.DBX_APP_KEY), ctx.getString(R.string.DBX_SECRET_KEY));
	}
	
	/**Process the export of the file to DropBox or the SD Card
	 * 
	 * 
	 * @return
	 * @throws IOException
	 */
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

        return exportDBX(exportPath);
        /*
		if(isSD)
		{
			return exportSD();
		}
		else
		{
			return exportDBX();	
		}*/
	}
	
	/**Exports the File to the SD card,
	 * Alerts the user that the File has been 
	 * exported.
	 * 
	 * @return whether the export was successful or not
	 * @throws IOException
	 */
	private boolean exportSD() throws IOException
	{
		if(checkSD())
		{
			File root = new File(Environment.getExternalStorageDirectory().toString() + "/Scans");
			root.mkdirs();
			File toSDFile = new File(root.getAbsolutePath(), this.exportFile.getName());
			new ScanWriter(this.callingContext, 0, this.exportFile.getName()).copyFile(exportFile, toSDFile);
			Toast.makeText(this.callingContext, "File exported to SD card" , Toast.LENGTH_SHORT).show();
			if(fromCommit)
				this.exportFile.delete();
			return true;
		}
		else
		{
			Toast.makeText(this.callingContext, "Error exporting to SD" , Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	/**Checks to make sure that the SD card 
	 * is available
	 * 
	 * 
	 * @return whether the sd card is available.
	 */
	private boolean checkSD()
	{
		String sdState = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(sdState) && !(Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdState)))
		{
			return true;
		}
		else
		{
			Toast.makeText(callingContext, "SD Card not available", Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**Exports the file to DropBox.
	 * 
	 * 
	 * @return if export is successful
	 * @throws IOException
	 */
	private boolean exportDBX(String exportPath) throws IOException
	{
		if(mDbxAcctMgr.hasLinkedAccount())
		{
			DbxPath scanPath = new DbxPath(exportPath + this.exportFile.getName());
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			DbxFile tempFile = dbxFs.create(scanPath);
			tempFile.getNewerStatus();
			tempFile.writeFromExistingFile(this.exportFile, fromCommit);
			Toast.makeText(this.callingContext, "File exported to DropBox" , Toast.LENGTH_SHORT).show();
			tempFile.close();
			return true;	
		}
		else
		{
			Toast.makeText(this.callingContext, "Error exporting to DropBox" , Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}

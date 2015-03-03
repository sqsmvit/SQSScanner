package com.sqsmv.sqsscanner;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.sqsmv.sqsscanner.DB.ScanDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanWriter
{
	private Cursor dbCur;
    private int exportMode;
	private boolean compactMode;
	private boolean invAdjMode;
	private int invAdjChoice;
	private String fileName;
	private Context callingContext;
	private String[] colNames;
	private ScanDataSource sds;
	
	/**
	 * @param ctx
	 * @param isCompact
	 */
	public ScanWriter(Context ctx, boolean isCompact, boolean invMode, int invModeChoice)
	{
		this.callingContext = ctx;
		this.sds = new ScanDataSource(callingContext);
		this.compactMode = isCompact;
		this.invAdjMode = invMode;
		this.invAdjChoice = invModeChoice;
		getCursor();
		setFileName();
	}

    public ScanWriter(Context ctx, int exportModeChoice, int invModeChoice)
    {
        this.callingContext = ctx;
        this.sds = new ScanDataSource(callingContext);
        this.exportMode = exportModeChoice;
        this.invAdjChoice = invModeChoice;
        getCursor();
        setFileName();
    }
	
	/**
	 * @param ctx
	 * @param exportModeChoice
	 * @param fileName
	 */
	public ScanWriter(Context ctx, int exportModeChoice, String fileName)
	{
		this.callingContext = ctx;
		this.sds = new ScanDataSource(callingContext);
		this.exportMode = exportModeChoice;
		getCursor();
		this.fileName = fileName;
	}
	
	
	/**
	 * 
	 */
	private void getCursor()
	{
		this.sds.open();			
		this.dbCur = sds.getScansForPrint(exportMode);
		this.colNames = dbCur.getColumnNames();
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public boolean writeToFile() throws IOException
    {
		this.dbCur.getColumnCount();
		String writeString;
		FileOutputStream output = this.callingContext.openFileOutput(this.fileName, Context.MODE_APPEND);

		while (dbCur.moveToNext())
		{
			writeString = buildString();
			output.write(writeString.getBytes());
		}
		
		output.close();	
		return true;
	}
	
	/**
	 * @return
	 */
	private String buildString()
	{
		String writeString = "";
		
		int i = 0;
		
		if(exportMode == 5)
		{
			if(invAdjChoice == 1)
				writeString = "add\t";
			else if(invAdjChoice == 2)
				writeString = "sub\t";
			else if(invAdjChoice == 3)
				writeString = "set\t";
		}
		
		//if there is no masNum write the scanEntry
		if(!dbCur.isNull(0))
		{
			writeString += dbCur.getString(0) + "\t";
		}
        i++;

		while(i < colNames.length)
		{
			if(i == colNames.length-1)
			{
				writeString += this.dbCur.getString(dbCur.getColumnIndex(colNames[i])) + "\n";
			}
			else
			{
				writeString += this.dbCur.getString(dbCur.getColumnIndex(colNames[i])) + "\t";
			}
			i++;
		}
		return writeString;
	}
	
	public void close()
	{
		this.sds.delAllScans();
		this.sds.close();
	}
	
	private void setFileName()
	{
	    String deviceId = getDeviceName();
	      
		Date today = new Date();
		SimpleDateFormat fileFmt = new SimpleDateFormat("yyMMdd_kkmm", Locale.US);
		fileName =  deviceId + "_" + fileFmt.format(today);
		if(exportMode == 3)
			fileName = "BB_"+ fileName;
		else if(exportMode == 4)
			fileName = "DR_"+ fileName;
        else if(exportMode == 5)
            fileName = "RI_"+ fileName;
		fileName += ".txt";
	}
		
	
	/**
	 * @return
	 */
	private String getDeviceName()
	{
		return BluetoothAdapter.getDefaultAdapter().getName();
	}
	
	/**
	 * @throws IOException
	 */
	public void writeBackup() throws IOException
	{
		//File root = callingContext.getDir(callingContext.getString(R.string.BACKUP_DIR), Context.MODE_PRIVATE);
		File root = new File(Environment.getExternalStorageDirectory().toString() + "/backups");
		root.mkdirs();
		File backupFile = new File(root.getAbsolutePath(), "B_" + this.fileName);
		//File backupFile = new File(root, "B_" + this.fileName);
		copyFile(backupFile);
	}
	
	/**
	 * @param dest
	 * @throws IOException
	 */
	private void copyFile(File dest) throws IOException
	{
	    InputStream in = new FileInputStream(this.getFile());
	    OutputStream out = new FileOutputStream(dest);

	    byte[] buf = new byte[1024];
	    int len;
	    
	    while ((len = in.read(buf)) > 0)
	    {
	        out.write(buf, 0, len);
	    }
	    
	    in.close();
	    out.close();
	}
	
	/**
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public void copyFile(File src, File dest) throws IOException{
		
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dest);

	    byte[] buf = new byte[1024];
	    int len;
	    
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    
	    in.close();
	    out.close();
		
	}
	
	/**
	 * @return
	 */
	public String getFileName(){
		
		return this.fileName;
	}
	
	/**
	 * @return
	 */
	public File getFile(){
		
		return new File(this.callingContext.getFilesDir() + "/" + this.fileName);
	}
}

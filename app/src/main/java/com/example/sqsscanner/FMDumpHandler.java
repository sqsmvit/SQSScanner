package com.example.sqsscanner;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.dropbox.sync.android.DbxFile;
import com.example.sqsscanner.DB.DataSource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class FMDumpHandler implements Runnable {

	private static final String TAG = "FMDumpHandler";
	private Context callingContext;
	private String file;
	private String[] xmlTags;
	private DataSource dataSource;
	private Boolean forceDBUpdate;
	
	public FMDumpHandler(Context ctx, String file, DataSource dataSource, String[] xmlTags)
    {
		this(ctx, file, dataSource, xmlTags, false);
	}
	
 	public FMDumpHandler(Context ctx, String file, DataSource dataSource, String[] xmlTags, Boolean forceUpdate)
    {
		String message = String.format("in constructor and starting %s!", TAG);
		Log.d(TAG, message);
		
		System.out.println(message);
		
		this.callingContext = ctx;
		this.file = file;
		this.dataSource = dataSource;
		this.xmlTags = xmlTags;
		this.forceDBUpdate = forceUpdate;
	}
	
	public boolean checkPreferences()
	{
		if (forceDBUpdate)
		{
			return false;
		}
		else
		{
			String buildDate = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());
			return buildDate.equals(callingContext.getSharedPreferences("scanConfig", 0).getString("buildDate", ""));
		}
	}

	private File copyDBXFile() throws Exception
	{
		String message = String.format("in copyDBXFile");
		Log.d(TAG, message);
		DBXManager dbxMan = new DBXManager(callingContext);
		
		try
		{
			DbxFile dbxXml = dbxMan.openFile("/out" + this.file);
			String message2 = String.format("dbxXml file is %s, public storage is %s", 
					dbxXml.getPath().toString(),
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + this.file);
			Log.d(TAG, message2);
			dbxMan.writeToStorage(dbxXml.getReadStream(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + this.file);
			message2 = String.format("It Worked!");
			dbxXml.close();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(this.toString(), "It did NOT Work! DropBox ERROR", e);
			throw e;
		}
		
		File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + this.file);
		
		return localFile;
	}
			
	private boolean buildTableFromXML() throws Exception
    {
		String message = String.format("in buildTableFromXML at 1");
		Log.d(TAG, message);

		XmlPullParser xpp = null;
		HashMap<String, String> dbItems = new HashMap<String, String>();
		dataSource.open();
		dbItems.putAll(this.dataSource.getSha());

		//File xmlFile = copyDBXFile();
        File xmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + this.file);
		
		InputStreamReader in = new InputStreamReader(new FileInputStream(xmlFile));
		
		message = String.format("in buildTableFromXML at 2");
		Log.d(TAG, message);
		
        try {
    		message = String.format("in buildTableFromXML at 3");
    		Log.d(TAG, message);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            
            xpp = factory.newPullParser();
    		message = String.format("in buildTableFromXML at 4");
    		Log.d(TAG, message);
    		
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.i(this.toString(), "XML ERROR", e);
			in.close();
			throw e;

		} 
		
		int eventType;

		try{
    		message = String.format("in buildTableFromXML at 5");
    		Log.d(TAG, message);
			xpp.setInput(in);
    		message = String.format("in buildTableFromXML at 6");
    		Log.d(TAG, message);
		}catch(Exception e){
			Log.i(this.toString(), "XML ERROR", e);
			return false;
		}
		
		//records
		eventType = xpp.getEventType();
		
		ArrayList<ArrayList<String>> batch = new ArrayList<ArrayList<String>>();
		String shaVal = "";
		int i = 0;
		while(eventType != XmlPullParser.END_DOCUMENT)
        {
			//record
			eventType = xpp.next();
			 //System.out.println(xpp.getName());
			switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    if(xpp.getName().equals("record"))
                    {
                        System.out.println(file + Integer.toString(i));
                        int recordEvent = xpp.getEventType();
                        ArrayList<String> objVars = new ArrayList<String>();
                        while(recordEvent != XmlPullParser.END_TAG && xpp.getName().equals("record"))
                        {
                            for(String tag : this.xmlTags)
                            {
                                //1st field
                                eventType = xpp.nextTag();
                                if(eventType == XmlPullParser.END_TAG)
                                {
                                    xpp.nextTag();
                                }

                                String value;
                                if(xpp.getName().equals(tag))
                                {
                                    value = xpp.nextText();
                                    if(tag.toUpperCase().equals("SHA"))
                                    {
                                        shaVal = value;
                                    }
							    }
							    else
                                {
                                    return false;
                                }

                                if(value.isEmpty() || value.toUpperCase().equals("N") || value.toUpperCase().equals("N1"))
                                {
                                    value = "0";
                                }

                                if(value.toUpperCase().equals("Y"))
                                {
                                    value = "1";
                                }
                                objVars.add(value);
                            }//end for
						
						if(!(dbItems.containsKey(objVars.get(0))))
                        {
							batch.add(objVars);
							i += 1;
						}
						else if(!(dbItems.get(objVars.get(0)).equals(shaVal)))
                        {
							batch.add(objVars);
							i += 1;
						}
						
						if(batch.size() == 500)
                        {
							dataSource.insertBatch(batch);
							batch.clear();
						}
					}//end while
				}//end if
			}//end switch
		}// end while
		message = String.format("in buildTableFromXML at 7");
		Log.d(TAG, message);
		if (!batch.isEmpty())
        {
			message = String.format("in buildTableFromXML at 8");
			Log.d(TAG, message);
			dataSource.insertBatch(batch);
			message = String.format("in buildTableFromXML at 9");
			Log.d(TAG, message);
		}
		dataSource.close();
		xmlFile.delete();
		return true;
	}

	@Override
	public void run() {
		String message = String.format("in run");
		Log.d(TAG, message);
		try {
			buildTableFromXML();
		} catch (XmlPullParserException e) {
			Log.i(this.toString(), "XML ERROR", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(this.toString(), "IO ERROR", e);
			e.printStackTrace();
		} catch (Exception e) {
			Log.i(this.toString(), "Build DB ERROR", e);
			e.printStackTrace();
		}
		
	}
	
}

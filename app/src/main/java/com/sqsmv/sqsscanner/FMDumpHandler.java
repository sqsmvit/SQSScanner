package com.sqsmv.sqsscanner;

import android.os.Environment;
import android.util.Log;

import com.sqsmv.sqsscanner.DB.DataSource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class FMDumpHandler implements Runnable {

	private static final String TAG = "FMDumpHandler";
	private String file;
	private String[] xmlTags;
	private DataSource dataSource;

	public FMDumpHandler(String file, DataSource dataSource, String[] xmlTags)
	{
		String message = String.format("in constructor %s!", TAG);
		Log.d(TAG, message);

		this.file = file;
		this.dataSource = dataSource;
		this.xmlTags = xmlTags;
	}

	private boolean buildTableFromXML() throws Exception
	{
		XmlPullParser xpp = null;
		HashMap<String, String> dbItems = new HashMap<String, String>();
		dataSource.open();
		dbItems.putAll(this.dataSource.getSha());

		File xmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + this.file);

		InputStreamReader in = new InputStreamReader(new FileInputStream(xmlFile));

		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);

			xpp = factory.newPullParser();

		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
			Log.i(this.toString(), "XML ERROR", e);
			in.close();
			throw e;
		}

		int eventType;

		try
		{
			xpp.setInput(in);
		}
		catch(Exception e)
		{
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
							System.out.println("Inserted 500");
							dataSource.insertBatch(batch);
							batch.clear();
						}
					}//end while
				}//end if
			}//end switch
		}// end while
		if (!batch.isEmpty())
		{
			System.out.println("Inserted " + batch.size());
			dataSource.insertBatch(batch);
		}
		dataSource.close();
		xmlFile.delete();
		return true;
	}

	@Override
	public void run()
	{
		String message = String.format("in run");
		Log.d(TAG, message);
		try
		{
			buildTableFromXML();
		}
		catch (XmlPullParserException e)
		{
			Log.i(this.toString(), "XML ERROR", e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.i(this.toString(), "IO ERROR", e);
			e.printStackTrace();
		}
		catch (Exception e)
		{
			Log.i(this.toString(), "Build DB ERROR", e);
			e.printStackTrace();
		}
	}
}

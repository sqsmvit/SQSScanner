package com.sqsmv.sqsscanner;

import android.os.Environment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import androidlibs.db.xml.XMLDBAccess;

/**
 * Utility class that takes the export files from FileMaker that have already been downloaded through Dropbox and imports the data into the app's
 * database. It is assumed that the files can be found in the Downloads directory on the phone. To optimize time, each record stored in the XML file
 * is assumed to match tha order of the column names retrieved from the getTableColumns() method in XMLDBAccess.
 */
public class FMDumpHandler extends Thread
{
    private static final String TAG = "FMDumpHandler";

    XMLDBAccess xmlDBAccess;
    private Semaphore popDBSemaphore;
    private boolean isSlowUpdate;

    /**
     * Constructor.
     * @param xmlDBAccess       The XMLDBAccess class for the table that is to be populated.
     * @param isSlowUpdate      Whether the FMDumpHandler needs to synchronize with other threads or not.
     * @param popDBSemaphore    The Semaphore to synchronize for synchronization with other threads.
     */
    FMDumpHandler(XMLDBAccess xmlDBAccess, boolean isSlowUpdate, Semaphore popDBSemaphore)
    {
        super(xmlDBAccess.getTableName());
        this.xmlDBAccess = xmlDBAccess;
        this.isSlowUpdate = isSlowUpdate;
        this.popDBSemaphore = popDBSemaphore;
    }

    /**
     * Overridden run method. Runs the import process for the table.
     */
    @Override
    public void run()
    {
        if(isSlowUpdate)
        {
            try
            {
                popDBSemaphore.acquire();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "run: start " + xmlDBAccess.getTableName());
        File xmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" +
                xmlDBAccess.getXMLFileName());
        try
        {
            InputStreamReader fileInput = new InputStreamReader(new FileInputStream(xmlFile));
            try
            {
                XmlPullParser xpp = createXmlPullParser(fileInput);
                importFromXML(xpp);
                xmlFile.delete();
            }
            catch(XmlPullParserException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                fileInput.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(isSlowUpdate)
            {
                popDBSemaphore.release();
            }
        }
        Log.d(TAG, "run: end " + xmlDBAccess.getTableName());
    }

    /**
     * Creates the XmlPullParser for parsing the FileMaker export file.
     * @param fileInput    The InputStreamReader containing information to the FileMaker export file.
     * @return The created XmlPullParser.
     * @throws XmlPullParserException
     */
    private XmlPullParser createXmlPullParser(InputStreamReader fileInput) throws XmlPullParserException
    {
        XmlPullParser xpp;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        xpp = factory.newPullParser();

        xpp.setInput(fileInput);

        return xpp;
    }

    /**
     * Imports the information from FileMaker export file into a table.
     * @param xpp    The XmlPullParser containing information for parsing the FileMaker export file.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void importFromXML(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        xmlDBAccess.open();
        HashMap<String, String> dbItems = xmlDBAccess.getSha();

        int eventType = xpp.getEventType();

        ArrayList<ArrayList<String>> batch = new ArrayList<ArrayList<String>>();
        String shaVal = "";
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            //record
            eventType = xpp.next();
            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    if(xpp.getName().equals("record"))
                    {
                        int recordEvent = xpp.getEventType();
                        ArrayList<String> objVars = new ArrayList<String>();
                        while(recordEvent != XmlPullParser.END_TAG && xpp.getName().equals("record"))
                        {
                            for(String tag : xmlDBAccess.getTableColumns())
                            {
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
                                    return;
                                }
                                objVars.add(value);
                            }

                            if(canInsert(objVars.get(0), shaVal, dbItems))
                            {
                                batch.add(objVars);
                            }

                            if(batch.size() >= 1000)
                            {
                                insertBatch(batch);
                            }
                        }//end while
                    }//end if
            }//end switch
        }// end while
        if(!batch.isEmpty())
        {
            insertBatch(batch);
        }
    }

    /**
     * Checks if a record needs to be inserted by seeing if the primary key and and sha match any records already in the database.
     * @param pKey       The primary key to use for the check.
     * @param shaVal     The sha value to use for the check.
     * @param dbItems    The HashMap containing the primary key mapped to a matching sha for the database.
     * @return true if the key and sha do not match any record already in the database, otherwise false.
     */
    private boolean canInsert(String pKey, String shaVal, HashMap<String, String> dbItems)
    {
        boolean canInsert = true;
        if(dbItems.containsKey(pKey) && dbItems.get(pKey).equals(shaVal))
        {
            canInsert = false;
        }

        return canInsert;
    }

    /**
     * Inserts a batch of records parsed from the FileMaker export file into the database.
     * @param batch    The ArrayList containing information for batch insertion.
     */
    private void insertBatch(ArrayList<ArrayList<String>> batch)
    {
        xmlDBAccess.insertBatch(batch);
        System.out.println(xmlDBAccess.getTableName() + ": Inserted " + Integer.toString(batch.size()) + " records");
        batch.clear();
    }
}

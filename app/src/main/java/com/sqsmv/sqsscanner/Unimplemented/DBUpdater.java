package com.sqsmv.sqsscanner.Unimplemented;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.sqsmv.sqsscanner.DB.DataSource;

public class DBUpdater {

	
	
	
	public DBUpdater(DataSource dataSource, String[] xmlTags, File xmlFile){
			
		
	}
	
	
	public boolean buildTableFromXML(DataSource dataSource, String[] xmlTags, File xmlFile) throws XmlPullParserException, IOException{

		XmlPullParser xpp = null;
		
		HashMap<String, String> dbItems = dataSource.getSha();
		
		InputStreamReader in = new InputStreamReader(new FileInputStream(xmlFile));
		
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            
            xpp = factory.newPullParser();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		int eventType;

		try{
			xpp.setInput(in);
		}catch(Exception e){
			
			return false;
		}
		
		//records
		eventType = xpp.getEventType();
		
		ArrayList<ArrayList<String>> batch = new ArrayList<ArrayList<String>>();
		int i = 0;
		while(eventType != XmlPullParser.END_DOCUMENT){

			//record
			eventType = xpp.next();
			 //System.out.println(xpp.getName());
			switch(eventType){
			
			case XmlPullParser.START_TAG:
				if(xpp.getName().equals("record")){
					i++;
					
					System.out.println(i);
					
					if(i == 31619){
						
						System.out.println("!!!!");
						
					}
					int recordEvent = xpp.getEventType();
					ArrayList<String> objVars = new ArrayList<String>();
					while(recordEvent != XmlPullParser.END_TAG && xpp.getName().equals("record")){

						for(String tag : xmlTags){
	
							//1st field
							eventType = xpp.nextTag();
							if(eventType == XmlPullParser.END_TAG){
								xpp.nextTag();
							}
							
							String value;
							if(xpp.getName().equals(tag)){
								value = xpp.nextText();
								//iterator positioned at field end tag
							}
							else{return false;}
							
							if(value.isEmpty() || value.toUpperCase().equals("N") || value.toUpperCase().equals("N1")){
								value = "0";
							}
							if(value.toUpperCase().equals("Y")){
								value = "1";
							}
							objVars.add(value);
						}//end for
						
						batch.add(objVars);
						

						
						if(batch.size() == 100){
							
							ArrayList<ArrayList<String>> delBatch = new ArrayList<ArrayList<String>>();
							
  							if(!dbItems.isEmpty()){
								
								for ( ArrayList<String> rec : batch){
									
									String key = rec.get(0);
									String sha = rec.get(rec.size()-1);
									
									if(dbItems.containsKey(key)){
										if(dbItems.get(key).equals(sha)){
											delBatch.add(rec);
											
										}
										
										dbItems.remove(key);	
									}
									
								}
							}
							
							batch.removeAll(delBatch);

							delBatch.clear();
							
							if(batch.size() > 0){
								createObj(dataSource, batch);
							}
							batch.clear();
						}
						
					}//end while

				}//end if
			
			}//end switch
		}// end while
		
		if (!batch.isEmpty()){
			createObj(dataSource, batch);
		}
		return eventType == XmlPullParser.END_DOCUMENT;
		
	}
	
	private void createObj(DataSource dataSource, ArrayList<ArrayList<String>> batch) {
		

		if(batch.get(0).size() > 3){

		}
		
		else{

		}	
	}
	
	
	
}

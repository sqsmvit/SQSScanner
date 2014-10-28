package com.example.sqsscanner.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.SQLException;

public interface DataSource {

	public void open() throws SQLException;
	
	public void close();
	
	public HashMap<String, String> getSha();
	
	public void insertBatch(ArrayList<ArrayList<String>> batch);
	
	
}

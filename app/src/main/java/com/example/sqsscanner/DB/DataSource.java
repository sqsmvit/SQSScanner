package com.example.sqsscanner.DB;

import android.database.SQLException;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataSource
{
	public void open() throws SQLException;
	
	public void close();
	
	public HashMap<String, String> getSha();
	
	public void insertBatch(ArrayList<ArrayList<String>> batch);
}

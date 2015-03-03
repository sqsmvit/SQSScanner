package com.sqsmv.sqsscanner;


import android.database.Cursor;

import java.util.ArrayList;

public class PullList {

	
	//order corresponds to db order of rows
	private int pullNumber; //0
	private String name; //1
	private String pulledFor; //2
	private int job; //3
	private String scheduledDate; //4
	private int manQty; //5
	private int pullQty; //6
    private String fkLens;
    private String fkPriceList;
	
	public PullList(){}
	
	/**
	 * @param pullNumber
	 * @param name
	 * @param pulledFor
	 * @param job
	 * @param scheduledDate
	 * @param manQty
	 * @param pullQty
	 */
	public PullList(int pullNumber, String name, String pulledFor, int job,
			String scheduledDate, int manQty, int pullQty) {
		super();
		this.pullNumber = pullNumber;
		this.name = name;
		this.pulledFor = pulledFor;
		this.job = job;
		this.scheduledDate = scheduledDate;
		this.manQty = manQty;
		this.pullQty = pullQty;
	}

	/**
	 * @param dbCur
	 */
	public PullList(Cursor dbCur){
		
		this.buildFromCursor(dbCur);
	}

	/**
	 * @param objVars
	 */
	public PullList(ArrayList<String> objVars) {
		
		int i = 0;
		
		for(String field : objVars){
			
			switch(i){
			
			case 0:
				this.pullNumber = Integer.parseInt(field);
				break;
			case 1:
				this.name = field;
				break;
			case 2:
				this.pulledFor = field;
				break;
			case 3:
				this.job = Integer.parseInt(field);
				break;
			case 4:
				this.scheduledDate = field;
				break;
			case 5:
				this.manQty = Integer.parseInt(field);
				break;
			case 6:
				this.pullQty = Integer.parseInt(field);
				break;

			}
			
			i++;
		}
	}

	/**
	 * @param dbCur
	 */
	private void buildFromCursor(Cursor dbCur) {
		
		dbCur.moveToFirst();
		
		int idx = dbCur.getPosition();
		
		do{
			
			switch(idx){
			case 0:
				this.pullNumber = dbCur.getInt(idx);
				break;
			case 1:
				this.name = dbCur.getString(idx);
				break;
			case 2:
				this.pulledFor = dbCur.getString(idx);
				break;
			case 3:
				this.job = dbCur.getInt(idx);
				break;
			case 4:
				this.scheduledDate = dbCur.getString(idx);
				break;
			case 5:
				this.manQty = dbCur.getInt(idx);
				break;
			case 6:
				this.pullQty = dbCur.getInt(idx);
				break;
			}
			
			idx++;
			
		}while (idx < 7);
		
		
	}

	/**
	 * @return
	 */
	public int getPullNumber() {
		return pullNumber;
	}

	/**
	 * @param pullNumber
	 */
	public void setPullNumber(int pullNumber) {
		this.pullNumber = pullNumber;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getPulledFor() {
		return pulledFor;
	}

	/**
	 * @param pulledFor
	 */
	public void setPulledFor(String pulledFor) {
		this.pulledFor = pulledFor;
	}

	/**
	 * @return
	 */
	public int getJob() {
		return job;
	}

	/**
	 * @param job
	 */
	public void setJob(int job) {
		this.job = job;
	}

	public String getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public int getManQty() {
		return manQty;
	}

	public void setManQty(int manQty) {
		this.manQty = manQty;
	}

	public int getPullQty() {
		return pullQty;
	}

	public void setPullQty(int pullQty) {
		this.pullQty = pullQty;
	}
	
}

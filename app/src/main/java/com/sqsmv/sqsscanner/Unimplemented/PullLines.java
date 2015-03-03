package com.sqsmv.sqsscanner.Unimplemented;

import java.util.ArrayList;

import android.database.Cursor;

public class PullLines {

	private int pullNumber;
	private int masNum;
	private int scheduledQty;
	
	public PullLines(){
		
		
	}


	public PullLines(int pullNumber, int masNum, int scheduledQty) {
		super();
		this.pullNumber = pullNumber;
		this.masNum = masNum;
		this.scheduledQty = scheduledQty;
	}


	public PullLines(Cursor cur){
		
		buildFromCursor(cur);
			
	}


	public PullLines(ArrayList<String> objVars) {
		
		this.pullNumber = Integer.parseInt(objVars.get(0));
		this.masNum = Integer.parseInt(objVars.get(1));
		this.scheduledQty = Integer.parseInt(objVars.get(2));
		
	}


	private void buildFromCursor(Cursor cur) {
		
		cur.moveToFirst();
		
		do{
			
			int idx = cur.getPosition();
			
			switch(idx){
			
			case 0:
				this.pullNumber = cur.getInt(cur.getPosition());
				break;
				
			case 1:
				this.pullNumber = cur.getInt(cur.getPosition());
				break;
				
			case 2:
				this.pullNumber = cur.getInt(cur.getPosition());
				break;
			
			}
			
			
		}while(cur.moveToNext());
		
	}


	public int getPullNumber() {
		return pullNumber;
	}




	public void setPullNumber(int pullNumber) {
		this.pullNumber = pullNumber;
	}




	public int getMasNum() {
		return masNum;
	}




	public void setMasNum(int masNum) {
		this.masNum = masNum;
	}




	public int getScheduledQty() {
		return scheduledQty;
	}




	public void setScheduledQty(int scheduledQty) {
		this.scheduledQty = scheduledQty;
	}
	
	
	
	
}

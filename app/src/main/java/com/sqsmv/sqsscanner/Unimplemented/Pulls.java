  package com.sqsmv.sqsscanner.Unimplemented;

import com.sqsmv.sqsscanner.ScanRecord;

import java.util.ArrayList;

public class Pulls implements PullInterface {

	private int pullId;
	private int pullPieceCount;
	private int pullLineCount;
	private ArrayList<ScanRecord> pullRecords;
	
	public Pulls(String pullId)  {
		
		this.pullId = Integer.parseInt(pullId);
		this.pullRecords = new ArrayList<ScanRecord>();
		this.pullPieceCount = 0;
		this.pullLineCount = 0;
	}


	/**
	 * @return the pullRecords
	 */
	public ArrayList<ScanRecord> getPullRecords() {
		return pullRecords;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pullId;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pulls))
			return false;
		Pulls other = (Pulls) obj;
		if (pullId != other.pullId)
			return false;
		return true;
	}


	/**
	 * @return the pullId
	 */
	public int getPullId() {
		return pullId;
	}


	/**
	 * @return the pullPieceCount
	 */
	public int getPullPieceCount() {
		return pullPieceCount;
	}


	/**
	 * @return the pullLineCount
	 */
	public int getPullLineCount() {
		return pullLineCount;
	}


	/**
	 * @param pullPieceCount the pullPieceCount to set
	 */
	public void setPullPieceCount(int pullPieceCount) {
		this.pullPieceCount += pullPieceCount;
	}


	/**
	 * @param pullLineCount the pullLineCount to set
	 */
	public void setPullLineCount(int pullLineCount) {
		this.pullLineCount += pullLineCount;
	}
	
	
	public boolean contains(ScanRecord rec){
		
		for(ScanRecord scan : this.pullRecords){
			
			if(scan.equals(rec)){
				
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(String scanId, int qty){
		
		
		return false;
		
	}
	
	@Override
	public boolean addScanRecord(ScanRecord rec) {
		
		return this.pullRecords.add(rec);		
		
	}
	
	@Override
	public boolean removeScanRecord(ScanRecord rec) {

		return this.pullRecords.remove(rec);
	}
	
}

package com.example.sqsscanner;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author ChrisS
 *
 */
public class ScanRecord implements Parcelable
{
	public String scanEntry;
	public String quantity;
	public String pullNumber;
	public String scanDate;
	public String mark;
	public String title;
	public String masNum;
	public String priceList;
	public String priceFilters;
	public String rating;
    public String location;
	
	/**
	 * 
	 */
	public ScanRecord(){}
	
	/**
	 * @param scanEntry
	 * @param quantity
	 * @param pullNumber
	 * @param mark
	 * @param title
	 * @param priceList
	 * @param masNum
	 */
	public ScanRecord(String scanEntry, String quantity, String pullNumber, String mark, String title, String priceList, String masNum, String priceFilters, String rating, String location)
	{
		super();
		if(quantity.isEmpty())
		{
			if(scanEntry.contains("SQS"))
			{
				quantity = scanEntry.substring(scanEntry.length()-3);
			}
		}
		
		this.scanEntry = scanEntry;	
		this.quantity = quantity;
		this.pullNumber = pullNumber;
		this.scanDate = setDate();
		this.mark = mark;
		this.title = title;
		this.priceList = priceList;
		this.masNum = masNum;
		this.priceFilters = priceFilters;
		this.rating = rating;
        this.location = location;
	}
	

	/**
	 * @param cur
	 */
	public ScanRecord(Cursor cur)
    {
		if(cur.getPosition() == -1){
			cur.moveToFirst();
		}	
		for(int i = 1; i < cur.getColumnCount(); i++){
				
			switch(i)
            {
                case 1: this.scanEntry = cur.getString(i);
                    break;
                case 2:	this.pullNumber = cur.getString(i);
					break;
			    case 3:	this.quantity = cur.getString(i);
					break;
    			case 4:	this.scanDate = cur.getString(i);
					break;
    			case 5:	this.mark = cur.getString(i);
					break;
    			case 6:	this.title = cur.getString(i);
					break;
    			case 7:	this.priceList = cur.getString(i);
					break;
    			case 8: this.masNum = cur.getString(i);
					break;
                case 9: this.location = cur.getString(i);
			}
		}
	}
	
	/**
	 * @param in
	 */
	public ScanRecord(Parcel in)
    {
		ArrayList<String> data = new ArrayList<String>();

		in.readStringList(data);
		int i = 1;
		for(String datum : data)
        {
			switch(i)
            {
    			case 1: this.scanEntry = datum;
					break;
	    		case 2:	this.pullNumber = datum;
					break;
		    	case 3:	this.quantity = datum;
					break;
    			case 4:	this.scanDate = datum;
					break;
	    		case 5:	this.mark = datum;
					break;
		    	case 6:	this.title = datum;
					break;
			    case 7:	this.priceList = datum;
					break;
			    case 8: this.masNum = datum;
					break;
                case 9: this.location = datum;
			}
			i++;
		}
	}
	
	/**
	 * @return
	 */
	public String setDate()
    {
		Date today = new Date();
		SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yy", Locale.US);
		return dateFmt.format(today);
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
    {
		return 0;
	}

	/**
	 * @return
	 */
	public String getScanEntry() {
		return scanEntry;
	}

	/**
	 * @param scanEntry
	 */
	public void setScanEntry(String scanEntry) {
		this.scanEntry = scanEntry;
	}

	/**
	 * @return
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return
	 */
	public String getPullNumber() {
		return pullNumber;
	}

	/**
	 * @param pullNumber
	 */
	public void setPullNumber(String pullNumber) {
		this.pullNumber = pullNumber;
	}

	/**
	 * @return
	 */
	public String getScanDate() {
		return scanDate;
	}

	/**
	 * @param scanDate
	 */
	public void setScanDate(String scanDate) {
		this.scanDate = scanDate;
	}

	/**
	 * @return
	 */
	public String getMark() {
		return mark;
	}

	/**
	 * @param mark
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getMasNum() {
		return masNum;
	}

	/**
	 * @param masNum
	 */
	public void setMasNum(String masNum) {
		this.masNum = masNum;
	}

	/**
	 * @return
	 */
	public String getPriceList() {
		return priceList;
	}

	/**
	 * @param priceList
	 */
	public void setPriceList(String priceList) {
		this.priceList = priceList;
	}
	
	public String getPriceFilters() {
		return priceFilters;
	}

	public void setPriceFilters(String priceFilters) {
		this.priceFilters = priceFilters;
	}
	
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags)
    {
		String[] list = new String[]  {this.scanEntry, this.pullNumber,
				this.quantity, this.scanDate, this.mark, 
				this.title, this.priceList, this.masNum, this.location};

		dest.writeStringArray(list);
	}
	
	/**
	 * 
	 */
	public static final Parcelable.Creator<ScanRecord> CREATOR = new Parcelable.Creator<ScanRecord>()
    {
		public ScanRecord createFromParcel(Parcel in){
			return new ScanRecord(in);
		}
			
		public ScanRecord[] newArray(int size)
        {
			return new ScanRecord[size];
		}
	};
}
	
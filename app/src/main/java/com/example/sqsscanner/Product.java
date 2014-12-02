package com.example.sqsscanner;

import android.database.Cursor;

import java.util.ArrayList;

public class Product {

	private int masNum;
	private String title;
	private String category;
	private String rating;
	private String streetDate;
	private int titleFilm;
	private int noCover;
	private String priceList;
	private int isNew;
	private int isBoxSet;
	private int multipack;
	private String mediaFormat;
	private String priceFilters;
	private String specialFields;
	private String studio;
	private String season;
	private int numberOfDiscs;
	private String theaterDate;
	private String studioName;
	private String sha;
	
	/**
	 * 
	 */
	public Product()
	{
	}
	
	/**
	 * @param masNum
	 * @param title
	 * @param category
	 * @param rating
	 * @param streetDate
	 * @param titleFilm
	 * @param noCover
	 * @param priceList
	 * @param sha
	 */
	public Product(int masNum, String title, String category, String rating,
			String streetDate, int titleFilm, int noCover, String priceList,
			int isNew, int isBoxSet, int multipack, String mediaFormat,
			String priceFilters, String specialFields, String studio, String season,
			int numberOfDiscs, String theaterDate, String studioName, String sha)
	{
		super();
		this.masNum = masNum;
		this.title = title;
		this.category = category;
		this.rating = rating;
		this.streetDate = streetDate;
		this.titleFilm = titleFilm;
		this.noCover = noCover;
		this.priceList = priceList;
		this.isNew = isNew;
		this.isBoxSet = isBoxSet;
		this.multipack = multipack;
		this.mediaFormat = mediaFormat;
		this.priceFilters = priceFilters;
		this.specialFields = specialFields;
		this.studio = studio;
		this.season = season;
		this.numberOfDiscs = numberOfDiscs;
		this.theaterDate = theaterDate;
		this.studioName = studioName;
		this.sha = sha;
	}
	
	/**
	 * @param dbCur
	 */
	public Product(Cursor dbCur)
	{
		this.buildFromCursor(dbCur);
	}
	
	/**
	 * @param objVars
	 */
	public Product(ArrayList<String> objVars)
	{
		int i = 0;
		
		for(String field : objVars)
		{
			switch(i)
			{
			case 0:
				this.masNum = Integer.parseInt(field);
				break;
			case 1:
				this.title = field;
				break;
			case 2:
				this.category = field;
				break;
			case 3:
				this.rating = field;
				break;
			case 4:
				this.streetDate = field;
				break;
			case 5:
				this.titleFilm = Integer.parseInt(field);
				break;
			case 6:
				this.noCover = Integer.parseInt(field);
				break;
			case 7:
				this.priceList = field;
				break;	
			case 8:
				this.isNew = Integer.parseInt(field);
				break;
			case 9:
				this.isBoxSet = Integer.parseInt(field);
				break;
			case 10:
				this.multipack = Integer.parseInt(field);
				break;
			case 11:
				this.mediaFormat = field;
				break;
			case 12:
				this.priceFilters = field;
				break;
			case 13:
				this.specialFields = field;
				break;
			case 14:
				this.studio = field;
				break;
			case 15:
				this.season = field;
				break;
			case 16:
				this.numberOfDiscs = Integer.parseInt(field);
				break;
			case 17:
				this.theaterDate = field;
				break;
			case 18:
				this.studioName = field;
				break;
			case 19:
				this.sha = field;
				break;
            case 20:
                this.priceList = field;
                break;
			}
			
			i++;
		}
		
		
	}

	/**
	 * @param dbCur
	 */
	private void buildFromCursor(Cursor dbCur)
	{
		if (dbCur.moveToFirst())
		{
			//dbCur.getColumnCount();
			int idx = dbCur.getPosition();
			do
			{
				switch(idx)
				{
				case 0:
					this.masNum = dbCur.getInt(idx);
					break;
				case 1:
					this.title = dbCur.getString(idx);
					break;
				case 2:
					this.category = dbCur.getString(idx);
					break;
				case 3:
					this.rating = dbCur.getString(idx);
					break;
				case 4:
					this.streetDate = dbCur.getString(idx);
					break;
				case 5:
					this.titleFilm = dbCur.getInt(idx);
					break;
				case 6:
					this.noCover = dbCur.getInt(idx);
					break;
				case 7:
					this.priceList = dbCur.getString(idx);
					break;
				case 8:
					this.isNew = dbCur.getInt(idx);
					break;
				case 9:
					this.isBoxSet = dbCur.getInt(idx);
					break;
				case 10:
					this.multipack = dbCur.getInt(idx);
					break;
				case 11:
					this.mediaFormat = dbCur.getString(idx);
					break;
				case 12:
					this.priceFilters = dbCur.getString(idx);
					break;
				case 13:
					this.specialFields = dbCur.getString(idx);
					break;
				case 14:
					this.studio = dbCur.getString(idx);
					break;
				case 15:
					this.season = dbCur.getString(idx);
					break;
				case 16:
					this.numberOfDiscs = dbCur.getInt(idx);
					break;
				case 17:
					this.theaterDate = dbCur.getString(idx);
					break;
				case 18:
					this.studioName = dbCur.getString(idx);
					break;
				case 19:
					this.sha = dbCur.getString(idx);
					break;
                case 20:
                    this.priceList = dbCur.getString(idx);
                    break;
				}
				idx++;
			}while (idx < dbCur.getColumnCount());
		}
	}

	/**
	 * @return
	 */
	public int getMasNum() {
		return this.masNum;
	}
	
	/**
	 * @param masNum
	 */
	public void setMasNum(int masNum) {
		this.masNum = masNum;
	}
	
	/**
	 * @return
	 */
	public String getTitle() {
		return this.title;
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
	public String getCategory() {
		return this.category;
	}
	
	/**
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * @return
	 */
	public String getRating() {
		return this.rating;
	}
	
	/**
	 * @param rating
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	
	/**
	 * @return
	 */
	public String getStreetDate() {
		return this.streetDate;
	}
	
	/**
	 * @param streetDate
	 */
	public void setStreetDate(String streetDate) {
		this.streetDate = streetDate;
	}
	
	/**
	 * @return
	 */
	public int getTitleFilm() {
		return this.titleFilm;
	}
	
	/**
	 * @param titleFilm
	 */
	public void setTitleFilm(int titleFilm) {
		this.titleFilm = titleFilm;
	}
	
	/**
	 * @return
	 */
	public int getNoCover() {
		return this.noCover;
	}
	
	/**
	 * @param noCover
	 */
	public void setNoCover(int noCover) {
		this.noCover = noCover;
	}
	
	/**
	 * @return
	 */
	public String getPriceList() {
		return this.priceList;
	}
	
	/**
	 * @param priceList
	 */
	public void setPriceList(String priceList) {
		this.priceList = priceList;
	}
	
	public int getIsNew()
	{
		return this.isNew;
	}
	
	public void setIsNew(int isNew)
	{
		this.isNew = isNew;
	}
	
	public int getIsBoxSet()
	{
		return this.isBoxSet;
	}
	
	public void setIsBoxSet(int isBoxSet)
	{
		this.isBoxSet = isBoxSet;
	}
	
	public int getMultipack()
	{
		return this.multipack;
	}
	
	public void setMultipack(int multipack)
	{
		this.multipack = multipack;
	}
	
	public String getMediaFormat()
	{
		return this.mediaFormat;
	}
	
	public void setMediaFormat(String mediaFormat)
	{
		this.mediaFormat = mediaFormat;
	}
	
	public String getPriceFilters()
	{
		return this.priceFilters;
	}
	
	public void setPriceFilters(String priceFilters)
	{
		this.priceFilters = priceFilters;
	}
	
	public String getSpecialFields()
	{
		return this.specialFields;
	}
	
	public void setSpecialFields(String specialFields)
	{
		this.specialFields = specialFields;
	}
	
	public String getStudio()
	{
		return this.studio;
	}
	
	public void setStudio(String studio)
	{
		this.studio = studio;
	}
	
	public String getSeason()
	{
		return this.season;
	}
	
	public void setSeason(String season)
	{
		this.season = season;
	}
	
	public int getNumberOfDiscs()
	{
		return this.numberOfDiscs;
	}
	
	public void setNumberOfDiscs(int numberOfDiscs)
	{
		this.numberOfDiscs = numberOfDiscs;
	}
	
	public String getTheaterDate()
	{
		return this.theaterDate;
	}
	
	public void setTheaterDate(String theaterDate)
	{
		this.theaterDate = theaterDate;
	}
	
	public String getStudioName()
	{
		return this.studioName;
	}

	public void setStudioName(String studioName)
	{
		this.studioName = studioName;
	}

	/**
	 * @return
	 */
	public String getSha() {
		return sha;
	}
	
}

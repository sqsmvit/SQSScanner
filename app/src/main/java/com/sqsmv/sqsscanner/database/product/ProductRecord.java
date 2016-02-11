package com.sqsmv.sqsscanner.database.product;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.XMLDBRecord;

public class ProductRecord extends XMLDBRecord
{
    private String masNum;
    private String name;
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

    public ProductRecord()
    {
        super(new ProductContract());
        initRecord();
    }

    public ProductRecord(String masNum, String name, String category, String rating,
                         String streetDate, int titleFilm, int noCover, String priceList,
                         int isNew, int isBoxSet, int multipack, String mediaFormat,
                         String priceFilters, String specialFields, String studio, String season,
                         int numberOfDiscs, String theaterDate, String studioName, String sha)
    {
        super(new ProductContract());
        setMasNum(masNum);
        setName(name);
        setCategory(category);
        setRating(rating);
        setStreetDate(streetDate);
        setTitleFilm(titleFilm);
        setNoCover(noCover);
        setPriceList(priceList);
        setIsNew(isNew);
        setIsBoxSet(isBoxSet);
        setMultipack(multipack);
        setMediaFormat(mediaFormat);
        setPriceFilters(priceFilters);
        setSpecialFields(specialFields);
        setStudio(studio);
        setSeason(season);
        setNumberOfDiscs(numberOfDiscs);
        setTheaterDate(theaterDate);
        setStudioName(studioName);
        setSha(sha);
    }

    public ProductRecord(Cursor dbCursor)
    {
        super(new ProductContract(), dbCursor);
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory()
    {
        return category;
    }

    public String getRating()
    {
        return rating;
    }

    public String getStreetDate()
    {
        return streetDate;
    }

    public int getTitleFilm()
    {
        return titleFilm;
    }

    public int getNoCover()
    {
        return noCover;
    }

    public String getPriceList()
    {
        return priceList;
    }

    public int getIsNew()
    {
        return isNew;
    }

    public int getIsBoxSet()
    {
        return isBoxSet;
    }

    public int getMultipack()
    {
        return multipack;
    }

    public String getMediaFormat()
    {
        return mediaFormat;
    }

    public String getPriceFilters()
    {
        return priceFilters;
    }

    public String getSpecialFields()
    {
        return specialFields;
    }

    public String getStudio()
    {
        return studio;
    }

    public String getSeason()
    {
        return season;
    }

    public int getNumberOfDiscs()
    {
        return numberOfDiscs;
    }

    public String getTheaterDate()
    {
        return theaterDate;
    }

    public String getStudioName()
    {
        return studioName;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public void setStreetDate(String streetDate)
    {
        this.streetDate = streetDate;
    }

    public void setTitleFilm(int titleFilm)
    {
        this.titleFilm = titleFilm;
    }

    public void setNoCover(int noCover)
    {
        this.noCover = noCover;
    }

    public void setPriceList(String priceList)
    {
        this.priceList = priceList;
    }

    public void setIsNew(int isNew)
    {
        this.isNew = isNew;
    }

    public void setIsBoxSet(int isBoxSet)
    {
        this.isBoxSet = isBoxSet;
    }

    public void setMultipack(int multipack)
    {
        this.multipack = multipack;
    }

    public void setMediaFormat(String mediaFormat)
    {
        this.mediaFormat = mediaFormat;
    }

    public void setPriceFilters(String priceFilters)
    {
        this.priceFilters = priceFilters;
    }

    public void setSpecialFields(String specialFields)
    {
        this.specialFields = specialFields;
    }

    public void setStudio(String studio)
    {
        this.studio = studio;
    }

    public void setSeason(String season)
    {
        this.season = season;
    }

    public void setNumberOfDiscs(int numberOfDiscs)
    {
        this.numberOfDiscs = numberOfDiscs;
    }

    public void setTheaterDate(String theaterDate)
    {
        this.theaterDate = theaterDate;
    }

    public void setStudioName(String studioName)
    {
        this.studioName = studioName;
    }

    public static ProductRecord buildNewProductRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        ProductRecord productRecord = new ProductRecord(dbCursor);
        dbCursor.close();
        return productRecord;
    }

    @Override
    public void initRecord()
    {
        setMasNum("");
        setName("***ERR: NOT FOUND***");
        setCategory("");
        setRating("");
        setStreetDate("");
        setTitleFilm(-1);
        setNoCover(-1);
        setPriceList("");
        setIsNew(-1);
        setIsBoxSet(-1);
        setMultipack(-1);
        setMediaFormat("");
        setPriceFilters("");
        setSpecialFields("");
        setStudio("");
        setSeason("");
        setNumberOfDiscs(-1);
        setTheaterDate("");
        setStudioName("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
            getMasNum(), getName(), getCategory(), getRating(), getStreetDate(), Integer.toString(getTitleFilm()),
            Integer.toString(getNoCover()), getPriceList(), Integer.toString(getIsNew()), Integer.toString(getIsBoxSet()),
            Integer.toString(getMultipack()), getMediaFormat(), getPriceFilters(), getSpecialFields(), getStudio(), getSeason(),
            Integer.toString(getNumberOfDiscs()), getTheaterDate(), getStudioName(), getSha()
        };
    }

    @Override
    public void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_NAME))
            {
                setName(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_CATEGORY))
            {
                setCategory(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_RATING))
            {
                setRating(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_STREETDATE))
            {
                setStreetDate(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_TITLEFILM))
            {
                setTitleFilm(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_NOCOVER))
            {
                setNoCover(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_PRICELIST))
            {
                setPriceList(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_ISNEW))
            {
                setIsNew(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_ISBOXSET))
            {
                setIsBoxSet(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_MULTIPACK))
            {
                setMultipack(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_MEDIAFORMAT))
            {
                setMediaFormat(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_PRICEFILTERS))
            {
                setPriceFilters(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_SPECIALFIELDS))
            {
                setSpecialFields(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_STUDIO))
            {
                setStudio(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_SEASON))
            {
                setSeason(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_NUMBEROFDISCS))
            {
                setNumberOfDiscs(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_THEATERDATE))
            {
                setTheaterDate(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_STUDIONAME))
            {
                setStudioName(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }
}

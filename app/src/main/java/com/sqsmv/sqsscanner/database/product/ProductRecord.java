package com.sqsmv.sqsscanner.database.product;

import android.database.Cursor;

import andoidlibs.db.xml.XMLDBRecord;

public class ProductRecord extends XMLDBRecord
{
    private String masNum;
    private String name;
    private String category;
    private String rating;
    private String streetDate;
    private String titleFilm;
    private String noCover;
    private String priceList;
    private String isNew;
    private String isBoxSet;
    private String multipack;
    private String mediaFormat;
    private String priceFilters;
    private String specialFields;
    private String studio;
    private String season;
    private String numberOfDiscs;
    private String theaterDate;
    private String studioName;

    public ProductRecord()
    {
        super(new ProductContract());
        initRecord();
    }

    public ProductRecord(String masNum, String name, String category, String rating,
                         String streetDate, String titleFilm, String noCover, String priceList,
                         String isNew, String isBoxSet, String multipack, String mediaFormat,
                         String priceFilters, String specialFields, String studio, String season,
                         String numberOfDiscs, String theaterDate, String studioName, String sha)
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

    public String getTitleFilm()
    {
        return titleFilm;
    }

    public String getNoCover()
    {
        return noCover;
    }

    public String getPriceList()
    {
        return priceList;
    }

    public String getIsNew()
    {
        return isNew;
    }

    public String getIsBoxSet()
    {
        return isBoxSet;
    }

    public String getMultipack()
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

    public String getNumberOfDiscs()
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

    public void setTitleFilm(String titleFilm)
    {
        this.titleFilm = titleFilm;
    }

    public void setNoCover(String noCover)
    {
        this.noCover = noCover;
    }

    public void setPriceList(String priceList)
    {
        this.priceList = priceList;
    }

    public void setIsNew(String isNew)
    {
        this.isNew = isNew;
    }

    public void setIsBoxSet(String isBoxSet)
    {
        this.isBoxSet = isBoxSet;
    }

    public void setMultipack(String multipack)
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

    public void setNumberOfDiscs(String numberOfDiscs)
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
        setTitleFilm("");
        setNoCover("");
        setPriceList("");
        setIsNew("");
        setIsBoxSet("");
        setMultipack("");
        setMediaFormat("");
        setPriceFilters("");
        setSpecialFields("");
        setStudio("");
        setSeason("");
        setNumberOfDiscs("");
        setTheaterDate("");
        setStudioName("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
            getMasNum(), getName(), getCategory(), getRating(), getStreetDate(), getTitleFilm(), getNoCover(), getPriceList(),
            getIsNew(), getIsBoxSet(), getMultipack(), getMediaFormat(), getPriceFilters(), getSpecialFields(), getStudio(),
            getSeason(), getNumberOfDiscs(), getTheaterDate(), getStudioName(), getSha()
        };
    }

    @Override
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(ProductContract.COLUMN_NAME_MASNUM))
        {
            setMasNum(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_NAME))
        {
            setName(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_CATEGORY))
        {
            setCategory(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_RATING))
        {
            setRating(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_STREETDATE))
        {
            setStreetDate(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_TITLEFILM))
        {
            setTitleFilm(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_NOCOVER))
        {
            setNoCover(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_PRICELIST))
        {
            setPriceList(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_ISNEW))
        {
            setIsNew(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_ISBOXSET))
        {
            setIsBoxSet(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_MULTIPACK))
        {
            setMultipack(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_MEDIAFORMAT))
        {
            setMediaFormat(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_PRICEFILTERS))
        {
            setPriceFilters(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_SPECIALFIELDS))
        {
            setSpecialFields(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_STUDIO))
        {
            setStudio(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_SEASON))
        {
            setSeason(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_NUMBEROFDISCS))
        {
            setNumberOfDiscs(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_THEATERDATE))
        {
            setTheaterDate(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_STUDIONAME))
        {
            setStudioName(value);
        }
        else if(columnName.equals(ProductContract.COLUMN_NAME_SHA))
        {
            setSha(value);
        }
    }
}

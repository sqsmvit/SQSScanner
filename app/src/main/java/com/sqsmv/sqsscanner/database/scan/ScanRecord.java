package com.sqsmv.sqsscanner.database.scan;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.DBRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author ChrisS
 *
 */
public class ScanRecord extends DBRecord
{
    public String id;
    public String masNum;
    public String quantity;
    public String fkPullId;
    public String scanDate;
    public String title;
    public String priceList;
    public String priceFilters;
    public String rating;
    public String location;

    public ScanRecord(String masNum, String quantity, String fkPullId, String title, String priceList, String priceFilters, String rating, String location)
    {
        super(new ScanContract());
        this.quantity = quantity;
        this.fkPullId = fkPullId;
        this.scanDate = initDate();
        this.title = title;
        this.priceList = priceList;
        this.masNum = masNum;
        this.priceFilters = priceFilters;
        this.rating = rating;
        this.location = location;
    }

    public ScanRecord(String fkPullId, String quantity)
    {
        super(new ScanContract());
        this.fkPullId = fkPullId;
        this.quantity = quantity;
        this.scanDate = initDate();
        this.title = "Skid";
        this.priceList = "";
        this.masNum = "";
        this.priceFilters = "";
        this.rating = "";
        this.location = "";
    }

    public ScanRecord(Cursor dbCursor)
    {
        super(new ScanContract());
        buildWithCursor(dbCursor);
    }

    public String initDate()
    {
        Date today = new Date();
        SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return dateFmt.format(today);
    }

    public String getId()
    {
        return id;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public String getFkPullId()
    {
        return fkPullId;
    }

    public String getScanDate()
    {
        return scanDate;
    }

    public String getTitle()
    {
        return title;
    }

    public String getPriceList()
    {
        return priceList;
    }

    public String getPriceFilters()
    {
        return priceFilters;
    }

    public String getRating()
    {
        return rating;
    }

    public String getLocation()
    {
        return location;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }

    public void setFkPullId(String fkPullId)
    {
        this.fkPullId = fkPullId;
    }

    public void setScanDate(String scanDate)
    {
        this.scanDate = scanDate;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setPriceList(String priceList)
    {
        this.priceList = priceList;
    }

    public void setPriceFilters(String priceFilters)
    {
        this.priceFilters = priceFilters;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[0];
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(ScanContract._ID))
            {
                setId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_QUANTITY))
            {
                setQuantity(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_FKPULLID))
            {
                setFkPullId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_SCANDATE))
            {
                setScanDate(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_TITLE))
            {
                setTitle(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_PRICELIST))
            {
                setPriceList(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_PRICEFILTERS))
            {
                setPriceFilters(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_RATING))
            {
                setRating(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_LOCATION))
            {
                setLocation(dbCursor.getString(count));
            }
        }
    }
}

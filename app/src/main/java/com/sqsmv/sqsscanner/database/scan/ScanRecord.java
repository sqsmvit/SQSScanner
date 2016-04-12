package com.sqsmv.sqsscanner.database.scan;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import andoidlibs.db.DBAccess;
import andoidlibs.db.DBRecord;

/**
 * @author ChrisS
 *
 */
public class ScanRecord extends DBRecord
{
    private String id;
    private String masNum;
    private String quantity;
    private String fkPullId;
    private String scanDate;
    private String title;
    private String priceList;
    private String rating;
    private String location;
    private String numBoxes;
    private String initials;

    public ScanRecord(String id, String masNum, String quantity, String fkPullId, String title, String priceList, String rating, String location, String numBoxes, String initials)
    {
        super(new ScanContract());
        setId(id);
        setMasNum(masNum);
        setQuantity(quantity);
        setFkPullId(fkPullId);
        setScanDate(initDate());
        setTitle(title);
        setPriceList(priceList);
        setRating(rating);
        setLocation(location);
        setNumBoxes(numBoxes);
        setInitials(initials);
    }

    public ScanRecord(String masNum, String quantity, String fkPullId, String title, String priceList, String rating, String location, String numBoxes, String initials)
    {
        this(DBAccess.NULL_STRING, masNum, quantity, fkPullId, title, priceList, rating, location, numBoxes, initials);
    }

    public ScanRecord(String fkPullId, String quantity, String initials)
    {
        this(DBAccess.NULL_STRING, "", quantity, fkPullId, "Skid", "", "", "", "", initials);
    }

    public ScanRecord(Cursor dbCursor)
    {
        super(new ScanContract(), dbCursor);
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

    public String getRating()
    {
        return rating;
    }

    public String getLocation()
    {
        return location;
    }

    public String getNumBoxes()
    {
        return numBoxes;
    }

    public String getInitials()
    {
        return initials;
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

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public void setNumBoxes(String numBoxes)
    {
        this.numBoxes = numBoxes;
    }

    public void setInitials(String initials)
    {
        this.initials = initials;
    }

    public static ScanRecord buildNewScanRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        ScanRecord scanRecord = new ScanRecord(dbCursor);
        dbCursor.close();
        return scanRecord;
    }

    @Override
    public void initRecord()
    {
        setId(DBAccess.NULL_STRING);
        setMasNum("");
        setQuantity("");
        setFkPullId("");
        setScanDate("");
        setTitle("");
        setPriceList("");
        setRating("");
        setLocation("");
        setNumBoxes("1");
        setInitials("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getId(), getMasNum(), getQuantity(), getFkPullId(), getScanDate(), getTitle(), getPriceList(), getRating(), getLocation(), getNumBoxes(), getInitials()
        };
    }

    @Override
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(ScanContract._ID))
        {
            setId(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_MASNUM))
        {
            setMasNum(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_QUANTITY))
        {
            setQuantity(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_FKPULLID))
        {
            setFkPullId(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_SCANDATE))
        {
            setScanDate(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_TITLE))
        {
            setTitle(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_PRICELIST))
        {
            setPriceList(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_RATING))
        {
            setRating(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_LOCATION))
        {
            setLocation(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_NUMBOXES))
        {
            setNumBoxes(value);
        }
        else if(columnName.equals(ScanContract.COLUMN_NAME_INITIALS))
        {
            setInitials(value);
        }
    }
}

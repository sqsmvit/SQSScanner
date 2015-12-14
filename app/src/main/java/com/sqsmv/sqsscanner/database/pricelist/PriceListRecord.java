package com.sqsmv.sqsscanner.database.pricelist;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.XMLDBRecord;

public class PriceListRecord extends XMLDBRecord
{
    private String priceListId;
    private String priceList;
    private int active;

    public PriceListRecord(String priceListId, String priceList, int active, String sha)
    {
        super(new PriceListContract());
        this.priceListId = priceListId;
        this.priceList = priceList;
        this.active = active;
        setSha(sha);
    }

    public PriceListRecord(Cursor dbCursor)
    {
        super(new PriceListContract());
        buildWithCursor(dbCursor);
    }

    public String getPriceListId()
    {
        return priceListId;
    }

    public String getPriceList()
    {
        return priceList;
    }

    public int getActive()
    {
        return active;
    }

    public void setPriceListId(String priceListId)
    {
        this.priceListId = priceListId;
    }

    public void setPriceList(String priceList)
    {
        this.priceList = priceList;
    }

    public void setActive(int active)
    {
        this.active = active;
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getPriceListId(), getPriceList(), Integer.toString(getActive()), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(PriceListContract.COLUMN_NAME_PRICELISTID))
            {
                setPriceListId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(PriceListContract.COLUMN_NAME_PRICELIST))
            {
                setPriceList(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(PriceListContract.COLUMN_NAME_ACTIVE))
            {
                setActive(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(PriceListContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }
}

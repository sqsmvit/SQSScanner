package com.sqsmv.sqsscanner.database.pricelist;

import android.database.Cursor;

import andoidlibs.db.xml.XMLDBRecord;

public class PriceListRecord extends XMLDBRecord
{
    private String priceListId;
    private String priceList;
    private String active;

    public PriceListRecord()
    {
        super(new PriceListContract());
        initRecord();
    }

    public PriceListRecord(String priceListId, String priceList, String active, String sha)
    {
        super(new PriceListContract());
        setPriceListId(priceListId);
        setPriceList(priceList);
        setActive(active);
        setSha(sha);
    }

    public PriceListRecord(Cursor dbCursor)
    {
        super(new PriceListContract(), dbCursor);
    }

    public String getPriceListId()
    {
        return priceListId;
    }

    public String getPriceList()
    {
        return priceList;
    }

    public String getActive()
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

    public void setActive(String active)
    {
        this.active = active;
    }

    public static PriceListRecord buildNewPriceListRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        PriceListRecord priceListRecord = new PriceListRecord(dbCursor);
        dbCursor.close();
        return priceListRecord;
    }

    @Override
    public void initRecord()
    {
        setPriceListId("");
        setPriceList("");
        setActive("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getPriceListId(), getPriceList(), getActive(), getSha()
        };
    }

    @Override
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(PriceListContract.COLUMN_NAME_PRICELISTID))
        {
            setPriceListId(value);
        }
        else if(columnName.equals(PriceListContract.COLUMN_NAME_PRICELIST))
        {
            setPriceList(value);
        }
        else if(columnName.equals(PriceListContract.COLUMN_NAME_ACTIVE))
        {
            setActive(value);
        }
        else if(columnName.equals(PriceListContract.COLUMN_NAME_SHA))
        {
            setSha(value);
        }
    }
}

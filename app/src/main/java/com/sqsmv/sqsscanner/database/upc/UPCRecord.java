package com.sqsmv.sqsscanner.database.upc;

import android.database.Cursor;

import andoidlibs.db.xml.XMLDBRecord;

public class UPCRecord extends XMLDBRecord
{
    private String upc;
    private String masNum;

    public UPCRecord(String upc, String masNum, String sha)
    {
        super(new UPCContract());
        setUPC(upc);
        setMasNum(masNum);
        setSha(sha);
    }

    public UPCRecord(Cursor dbCursor)
    {
        super(new UPCContract(), dbCursor);
    }

    public String getUPC()
    {
        return upc;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public void setUPC(String upc)
    {
        this.upc = upc;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public static UPCRecord buildNewUPCRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        UPCRecord upcRecord = new UPCRecord(dbCursor);
        dbCursor.close();
        return upcRecord;
    }

    @Override
    public void initRecord()
    {
        setUPC("");
        setMasNum("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getUPC(), getMasNum(), getSha()
        };
    }

    @Override
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(UPCContract.COLUMN_NAME_UPC))
        {
            setUPC(value);
        }
        else if(columnName.equals(UPCContract.COLUMN_NAME_MASNUM))
        {
            setMasNum(value);
        }
        else if(columnName.equals(UPCContract.COLUMN_NAME_SHA))
        {
            setSha(value);
        }
    }
}

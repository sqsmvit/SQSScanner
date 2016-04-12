package com.sqsmv.sqsscanner.database.prodloc;

import android.database.Cursor;

import andoidlibs.db.xml.XMLDBRecord;

public class ProdLocRecord extends XMLDBRecord
{
    private int pkLocScanLineId;
    private String buildingId;
    private String roomId;
    private String colId;
    private String rowId;
    private String scanStamp;
    private String masNum;
    private String locCode;
    private String name;

    public ProdLocRecord(Cursor dbCursor)
    {
        super(new ProdLocContract(), dbCursor);
    }

    public int getPKLocScanLineId()
    {
        return pkLocScanLineId;
    }

    public String getBuildingId()
    {
        return buildingId;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public String getColId()
    {
        return colId;
    }

    public String getRowId()
    {
        return rowId;
    }

    public String getScanStamp()
    {
        return scanStamp;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getLocCode()
    {
        return locCode;
    }

    public String getName()
    {
        return name;
    }

    public void setPkLocScanLineId(int pkLocScanLineId)
    {
        this.pkLocScanLineId = pkLocScanLineId;
    }

    public void setBuildingId(String buildingId)
    {
        this.buildingId = buildingId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public void setColId(String colId)
    {
        this.colId = colId;
    }

    public void setRowId(String rowId)
    {
        this.rowId = rowId;
    }

    public void setScanStamp(String scanStamp)
    {
        this.scanStamp = scanStamp;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setLocCode(String locCode)
    {
        if(locCode.isEmpty())
        {
            locCode = "NF";
        }
        this.locCode = locCode;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public void initRecord()
    {
        setPkLocScanLineId(-1);
        setBuildingId("");
        setRoomId("");
        setColId("");
        setRowId("");
        setScanStamp("");
        setMasNum("");
        setLocCode("");
        setName("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                Integer.toString(getPKLocScanLineId()), getBuildingId(), getRoomId(), getColId(), getRowId(), getScanStamp(),
                getMasNum(), getLocCode(), getName(), getSha()
        };
    }

    @Override
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(ProdLocContract.COLUMN_NAME_PKLOCSCANLINEID))
        {
            setPkLocScanLineId(Integer.parseInt(value));
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_BUILDINGID))
        {
            setBuildingId(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_ROOMID))
        {
            setRoomId(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_COLID))
        {
            setColId(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_ROWID))
        {
            setRowId(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_SCANSTAMP))
        {
            setScanStamp(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_MASNUM))
        {
            setMasNum(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_LOCCODE))
        {
            setLocCode(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_NAME))
        {
            setName(value);
        }
        else if(columnName.equals(ProdLocContract.COLUMN_NAME_SHA))
        {
            setSha(value);
        }
    }

    public static ProdLocRecord buildNewProdLocRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        ProdLocRecord prodLocRecord = new ProdLocRecord(dbCursor);
        dbCursor.close();
        return prodLocRecord;
    }
}

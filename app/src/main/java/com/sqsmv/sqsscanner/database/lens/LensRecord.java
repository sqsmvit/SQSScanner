package com.sqsmv.sqsscanner.database.lens;

import android.database.Cursor;

import androidlibs.db.xml.XMLDBRecord;

public class LensRecord extends XMLDBRecord
{
    private String lensId;
    private String name;
    private String description;

    public LensRecord(String lensId, String name, String description, String sha)
    {
        super(new LensContract());
        setLensId(lensId);
        setName(name);
        setDescription(description);
        setSha(sha);
    }

    public LensRecord(Cursor dbCursor)
    {
        super(new LensContract(), dbCursor);
    }

    public String getLensId()
    {
        return lensId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setLensId(String lensId)
    {
        this.lensId = lensId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public static LensRecord buildNewLensRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        LensRecord lensRecord = new LensRecord(dbCursor);
        dbCursor.close();
        return lensRecord;
    }

    @Override
    public void initRecord()
    {
        setLensId("");
        setName("");
        setDescription("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getLensId(), getName(), getDescription(), getSha()
        };
    }

    @Override
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(LensContract.COLUMN_NAME_LENSID))
        {
            setLensId(value);
        }
        else if(columnName.equals(LensContract.COLUMN_NAME_NAME))
        {
            setName(value);
        }
        else if(columnName.equals(LensContract.COLUMN_NAME_DESCRIPTION))
        {
            setDescription(value);
        }
        else if(columnName.equals(LensContract.COLUMN_NAME_SHA))
        {
            setSha(value);
        }
    }
}

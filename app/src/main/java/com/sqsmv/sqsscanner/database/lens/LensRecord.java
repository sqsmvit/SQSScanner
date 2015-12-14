package com.sqsmv.sqsscanner.database.lens;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.XMLDBRecord;

public class LensRecord extends XMLDBRecord
{
    private String lensId;
    private String name;
    private String description;

    public LensRecord(String lensId, String name, String description, String sha)
    {
        super(new LensContract());
        this.lensId = lensId;
        this.name = name;
        this.description = description;
        setSha(sha);
    }

    public LensRecord(Cursor dbCursor)
    {
        super(new LensContract());
        buildWithCursor(dbCursor);
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

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getLensId(), getName(), getDescription(), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(LensContract.COLUMN_NAME_LENSID))
            {
                setLensId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(LensContract.COLUMN_NAME_NAME))
            {
                setName(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(LensContract.COLUMN_NAME_DESCRIPTION))
            {
                setDescription(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(LensContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }
}

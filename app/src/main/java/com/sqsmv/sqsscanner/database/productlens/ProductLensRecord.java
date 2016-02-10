package com.sqsmv.sqsscanner.database.productlens;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.XMLDBRecord;

public class ProductLensRecord extends XMLDBRecord
{
    private String productLensId;
    private String masNum;
    private String lensId;
    private String priceListId;

    public ProductLensRecord(String productLensId, String masNum, String lensId, String priceListId, String sha)
    {
        super(new ProductLensContract());
        setProductLensId(productLensId);
        setMasNum(masNum);
        setLensId(lensId);
        setPriceListId(priceListId);
        setSha(sha);
    }

    public ProductLensRecord(Cursor dbCursor)
    {
        super(new ProductLensContract(), dbCursor);
    }

    public String getProductLensId()
    {
        return productLensId;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getLensId()
    {
        return lensId;
    }

    public String getPriceListId()
    {
        return priceListId;
    }

    public void setProductLensId(String productLensId)
    {
        this.productLensId = productLensId;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setLensId(String lensId)
    {
        this.lensId = lensId;
    }

    public void setPriceListId(String priceListId)
    {
        this.priceListId = priceListId;
    }

    public static ProductLensRecord buildNewProductLensRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        ProductLensRecord productLensRecord = new ProductLensRecord(dbCursor);
        dbCursor.close();
        return productLensRecord;
    }

    @Override
    public void initRecord()
    {
        setProductLensId("");
        setMasNum("");
        setLensId("");
        setPriceListId("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getProductLensId(), getMasNum(), getLensId(), getPriceListId(), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(ProductLensContract.COLUMN_NAME_PRODUCTLENSID))
            {
                setProductLensId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductLensContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductLensContract.COLUMN_NAME_LENSID))
            {
                setLensId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductLensContract.COLUMN_NAME_PRICELISTID))
            {
                setPriceListId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductLensContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }
}

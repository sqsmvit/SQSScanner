package com.sqsmv.sqsscanner.database.productlens;

import android.database.Cursor;

import andoidlibs.db.xml.XMLDBRecord;

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
    protected void setByColumnName(String columnName, String value)
    {
        if(columnName.equals(ProductLensContract.COLUMN_NAME_PRODUCTLENSID))
        {
            setProductLensId(value);
        }
        else if(columnName.equals(ProductLensContract.COLUMN_NAME_MASNUM))
        {
            setMasNum(value);
        }
        else if(columnName.equals(ProductLensContract.COLUMN_NAME_LENSID))
        {
            setLensId(value);
        }
        else if(columnName.equals(ProductLensContract.COLUMN_NAME_PRICELISTID))
        {
            setPriceListId(value);
        }
        else if(columnName.equals(ProductLensContract.COLUMN_NAME_SHA))
        {
            setSha(value);
        }
    }
}

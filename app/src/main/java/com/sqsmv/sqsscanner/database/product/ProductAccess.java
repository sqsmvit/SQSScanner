package com.sqsmv.sqsscanner.database.product;

import android.content.Context;
import android.database.Cursor;

import com.sqsmv.sqsscanner.DB.PriceListContract.PriceListTable;
import com.sqsmv.sqsscanner.DB.ProductContract.ProductTable;
import com.sqsmv.sqsscanner.DB.ProductLensContract.ProductLensTable;
import com.sqsmv.sqsscanner.database.XMLDBAccess;

public class ProductAccess extends XMLDBAccess
{
    private static final String DB_PRODUCT_JOIN_QUERY = "Select p.*, prl." + PriceListTable.COLUMN_NAME_PRICELISTNAME + " FROM " + ProductTable.TABLE_NAME + " p " +
            "LEFT JOIN " + ProductLensTable.TABLE_NAME + " pl ON p." + ProductTable.COLUMN_NAME_PK_MAS_ID + " = pl." + ProductLensTable.COLUMN_NAME_FK_MASNUM + " AND pl." + ProductLensTable.COLUMN_NAME_FK_LENSID + " = ? " +
            "LEFT JOIN " + PriceListTable.TABLE_NAME + " prl ON pl." + ProductLensTable.COLUMN_NAME_FK_PRICELISTID + " = prl." + PriceListTable.COLUMN_NAME_PK_PRICE_LIST +
            " WHERE p." + ProductTable.COLUMN_NAME_PK_MAS_ID + "= ?" ;

    public ProductAccess(Context context)
    {
        super(context, new ProductContract());
    }

    public ProductRecord getJoinProduct(String lensId, String masnum)
    {
        String[] args = {lensId, masnum};
        Cursor cursor = getDB().rawQuery(DB_PRODUCT_JOIN_QUERY, args);

        return new ProductRecord(cursor);
    }
}

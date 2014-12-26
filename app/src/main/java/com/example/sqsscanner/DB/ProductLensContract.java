package com.example.sqsscanner.DB;

import android.provider.BaseColumns;

public class ProductLensContract
{
    public ProductLensContract() {}

    public static abstract class ProductLensTable implements BaseColumns
    {
        public static final String TABLE_NAME = "ProductLens";
        public static final String COLUMN_NAME_PK_PRODUCTLENS = "pkProductLens";
        public static final String COLUMN_NAME_FK_MASNUM = "fkMasnum";
        public static final String COLUMN_NAME_FK_LENSID = "fkLensId";
        public static final String COLUMN_NAME_FK_PRICELISTID = "fkPriceListId";
        public static final String COLUMN_NAME_SHA = "SHA";
    }
}

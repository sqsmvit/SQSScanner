package com.example.sqsscanner.DB;

import android.provider.BaseColumns;

public final class LensContract
{
    public LensContract() {}

    public static abstract class LensTable implements BaseColumns
    {
        public static final String TABLE_NAME = "Lens";
        public static final String COLUMN_NAME_PK_LENSID = "pkLensId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_SHA = "SHA";
    }
}

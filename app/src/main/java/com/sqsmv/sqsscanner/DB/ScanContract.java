package com.sqsmv.sqsscanner.DB;

import android.provider.BaseColumns;

public final class ScanContract
{
	public ScanContract() {}
	
	public static abstract class ScanTable implements BaseColumns
	{
		public static final String TABLE_NAME = "Scan";
		public static final String COLUMN_NAME_SCAN_ENTRY = "scanEntry";
		public static final String COLUMN_NAME_QUANTITY = "qty";
		public static final String COLUMN_NAME_FK_PULL_ID = "pullId";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_MARK_ID = "markId";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_PRICE_LIST = "priceList";
		public static final String COLUMN_NAME_MASNUM = "masNum";
		public static final String COLUMN_NAME_PRICEFILTERS = "priceFilters";
		public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_LOCATION = "location";
	}
}

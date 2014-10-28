package com.example.sqsscanner.DB;

import android.provider.BaseColumns;

public final class PriceListContract {

	public PriceListContract() {}
	
	public static abstract class PriceListTable implements BaseColumns {
		
		public static final String TABLE_NAME = "PriceList";
		public static final String COLUMN_NAME_PK_PRICE_LIST = "pkPriceList";
		public static final String COLUMN_NAME_ACTIVE = "active";
		public static final String COLUMN_NAME_SHA = "sha";

		
	}
	
}

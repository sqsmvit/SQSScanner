package com.sqsmv.sqsscanner.DB;

import android.provider.BaseColumns;

public class PullLinesContract {
	
	public PullLinesContract() {}
	
	public static abstract class PullLinesTable implements BaseColumns {
		
		public static final String TABLE_NAME = "PullLines";
		public static final String COLUMN_NAME_FK_PULL_ID = "fkPull";
		public static final String COLUMN_NAME_FK_MASNUM = "fkMasnum";
		public static final String COLUMN_NAME_SCHEDULED_QTY = "scheduledQty";
		public static final String COLUMN_NAME_SHA = "SHA";
		
	}
}

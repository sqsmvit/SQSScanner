package com.example.sqsscanner.DB;

import android.provider.BaseColumns;

public class PullListContract {

	public PullListContract() {}
	
	public static abstract class PullListTable implements BaseColumns {
		
		public static final String TABLE_NAME = "PullList";
		public static final String COLUMN_NAME_PK_PULL_ID = "pkPull";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_PULLED_FOR="pulledFor";
		public static final String COLUMN_NAME_JOB_NUMBER = "jobNumber";
		public static final String COLUMN_NAME_SCHEDULED_DATE = "scheduledDate";
		public static final String COLUMN_NAME_MANUAL_QTY = "manualQty";
		public static final String COLUMN_NAME_PULL_QTY = "pullQty";
		public static final String COLUMN_NAME_SHA = "sha";

		
	}
	
}
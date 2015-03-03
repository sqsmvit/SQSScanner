package com.sqsmv.sqsscanner.DB;

import android.provider.BaseColumns;

public final class UPCContract {

	public UPCContract() {}
	
	public static abstract class UPCTable implements BaseColumns
    {
		public static final String TABLE_NAME = "UPC";
		public static final String COLUMN_NAME_PK_UPC_ID = "pkUpc";
		public static final String COLUMN_NAME_FK_MAS_ID = "fkMasnum";
		public static final String COLUMN_NAME_SHA = "SHA";
	}
}

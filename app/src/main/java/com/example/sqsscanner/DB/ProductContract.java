package com.example.sqsscanner.DB;

import android.provider.BaseColumns;

public class ProductContract
{
	public ProductContract() {}
	
	public static abstract class ProductTable implements BaseColumns
	{
		public static final String TABLE_NAME = "Product";
		public static final String COLUMN_NAME_PK_MAS_ID = "pkMasnum";
		public static final String COLUMN_NAME_TITLE_NAME = "name";
		public static final String COLUMN_NAME_CATEGORY = "category";
		public static final String COLUMN_NAME_RATING = "rating";
		public static final String COLUMN_NAME_STREET_DATE = "streetDate";
		public static final String COLUMN_NAME_TITLE_FILM = "titleFilm";
		public static final String COLUMN_NAME_NO_COVER = "noCover";
		public static final String COLUMN_NAME_FK_PRICE_LIST = "fkPriceList";
		public static final String COLUMN_NAME_NEW = "isNew";
		public static final String COLUMN_NAME_BOXSET = "isBoxSet";
		public static final String COLUMN_NAME_MULTIPACK = "multipack";
		public static final String COLUMN_NAME_MEDIA_FORMAT = "mediaFormat";
		public static final String COLUMN_NAME_PRICE_FILTERS = "priceFilters";
		public static final String COLUMN_NAME_SPECIAL_FIELDS = "specialFields";
		public static final String COLUMN_NAME_STUDIO = "studio";
		public static final String COLUMN_NAME_SEASON = "season";
		public static final String COLUMN_NAME_NUMBER_OF_DISCS = "numberOfDiscs";
		public static final String COLUMN_NAME_THEATER_DATE = "theaterDate";
		public static final String COLUMN_NAME_STUDIO_NAME = "studioName";
		public static final String COLUMN_NAME_SHA = "SHA";
	}
}

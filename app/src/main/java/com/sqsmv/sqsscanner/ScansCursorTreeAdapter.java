package com.sqsmv.sqsscanner;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorTreeAdapter;

public class ScansCursorTreeAdapter extends SimpleCursorTreeAdapter {



	public ScansCursorTreeAdapter(Context context, Cursor cursor,
			int groupLayout, String[] groupFrom, int[] groupTo,
			int childLayout, String[] childFrom, int[] childTo) 
	{
		super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
				childTo);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) 
	{
		// TODO Auto-generated method stub
		return null;
	}


}

package com.sqsmv.sqsscanner.database.pricelist;

import android.content.Context;

import com.sqsmv.sqsscanner.database.XMLDBAccess;


public class PriceListAccess extends XMLDBAccess
{
	public PriceListAccess(Context context)
	{
		super(context, new PriceListContract());
	}
}

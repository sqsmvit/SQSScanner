package com.sqsmv.sqsscanner.database.pricelist;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.XMLDBAccess;


public class PriceListAccess extends XMLDBAccess
{
	public PriceListAccess(DBAdapter dbAdapter)
	{
		super(dbAdapter, new PriceListContract());
	}
}

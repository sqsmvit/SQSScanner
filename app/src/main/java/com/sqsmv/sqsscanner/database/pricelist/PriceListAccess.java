package com.sqsmv.sqsscanner.database.pricelist;

import com.sqsmv.sqsscanner.database.DBAdapter;
import androidlibs.db.xml.XMLDBAccess;


public class PriceListAccess extends XMLDBAccess
{
	public PriceListAccess(DBAdapter dbAdapter)
	{
		super(dbAdapter, new PriceListContract());
	}
}

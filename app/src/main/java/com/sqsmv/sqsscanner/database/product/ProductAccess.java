package com.sqsmv.sqsscanner.database.product;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.XMLDBAccess;

public class ProductAccess extends XMLDBAccess
{
    public ProductAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new ProductContract());
    }
}

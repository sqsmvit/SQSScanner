package com.sqsmv.sqsscanner.database.upc;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.XMLDBAccess;


public class UPCAccess extends XMLDBAccess
{
    public UPCAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new UPCContract());
    }
}

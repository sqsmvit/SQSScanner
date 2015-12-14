package com.sqsmv.sqsscanner.database.upc;

import android.content.Context;

import com.sqsmv.sqsscanner.database.XMLDBAccess;


public class UPCAccess extends XMLDBAccess
{
    public UPCAccess(Context context)
    {
        super(context, new UPCContract());
    }
}

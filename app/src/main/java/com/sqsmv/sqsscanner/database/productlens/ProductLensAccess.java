package com.sqsmv.sqsscanner.database.productlens;

import android.content.Context;

import com.sqsmv.sqsscanner.database.XMLDBAccess;

public class ProductLensAccess extends XMLDBAccess
{
    public ProductLensAccess(Context context)
    {
        super(context, new ProductLensContract());
    }
}

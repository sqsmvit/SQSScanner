package com.sqsmv.sqsscanner;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.onbarcode.barcode.android.Code128;

/**
 * View subclass for use with the Barcode API.
 */
public class BarcodeView extends View
{
    Code128 barcode;

    /**
     * Constructor.
     * @param context    The Context for the BarcodeView to be displayed for.
     * @param attrs      The attributes to set to the BarcodeView
     */
	public BarcodeView(Context context, AttributeSet attrs)
    {
	    super(context, attrs);
	}

    /**
     * Overridden onDraw method for displaying the barcode.
     * @param canvas    The Canvas to display the barcode on.
     */
    @Override
	protected void onDraw(Canvas canvas)
    {
		super.onDraw(canvas);

        try
        {
            RectF bounds = new RectF(0, 0, 0, 0);
            barcode.drawBarcode(canvas, bounds);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

    /**
     * Sets the barcode to display, then triggers the onDraw method to display it.
     * @param barcode    The Code123 barcode to display
     */
    public void drawBarcode(Code128 barcode)
    {
        this.barcode = barcode;
        invalidate();
    }
}

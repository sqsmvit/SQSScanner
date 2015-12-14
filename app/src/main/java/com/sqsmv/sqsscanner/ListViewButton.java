package com.sqsmv.sqsscanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class ListViewButton extends ImageButton
{
    public ListViewButton(Context context) {
        super(context);
    }

    public ListViewButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ListViewButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed)
    {
        if(pressed && (getParent() instanceof View) &&(((View) getParent()).isPressed()))
        {
            return;
        }
        super.setPressed(pressed);
    }
}

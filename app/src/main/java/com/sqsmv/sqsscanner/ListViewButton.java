package com.sqsmv.sqsscanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class ListViewButton extends ImageButton {

	/**
	 * @param context
	 */
	public ListViewButton(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ListViewButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ListViewButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#setPressed(boolean)
	 */
	@Override
	public void setPressed(boolean pressed){
		if(pressed && (getParent() instanceof View) &&(((View) getParent()).isPressed())){
			return;
		}
		
		super.setPressed(pressed);
	}
}

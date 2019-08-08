package com.san.app.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

public class EditTextAirbnb_book extends EditText {

	public EditTextAirbnb_book(Context context) {
		super(context);
		setFont();
	}
	public EditTextAirbnb_book(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFont();
	}
	public EditTextAirbnb_book(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFont();
	}

	private void setFont() {
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/AirbnbCereal-Book.ttf");
		setTypeface(font, Typeface.NORMAL);
	}

}
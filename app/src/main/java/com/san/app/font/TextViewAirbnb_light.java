package com.san.app.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class TextViewAirbnb_light extends TextView {

		public TextViewAirbnb_light(Context context) {
			super(context);
			setFont();
		}
		public TextViewAirbnb_light(Context context, AttributeSet attrs) {
			super(context, attrs);
			setFont();
		}
		public TextViewAirbnb_light(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			setFont();
		}

		private void setFont() {
			Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/AirbnbCereal-Light.ttf");
			setTypeface(font, Typeface.NORMAL);
		}

}

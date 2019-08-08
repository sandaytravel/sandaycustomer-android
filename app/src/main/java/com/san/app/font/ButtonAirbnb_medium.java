package com.san.app.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonAirbnb_medium extends Button {

		public ButtonAirbnb_medium(Context context) {
			super(context);
			setFont();
		}
		public ButtonAirbnb_medium(Context context, AttributeSet attrs) {
			super(context, attrs);
			setFont();
		}
		public ButtonAirbnb_medium(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			setFont();
		}

		private void setFont() {
			Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/AirbnbCereal-Medium.ttf");
			setTypeface(font, Typeface.NORMAL);
		}

}

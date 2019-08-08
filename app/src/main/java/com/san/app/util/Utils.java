package com.san.app.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ptrstovka.calendarview2.CalendarDay;
import com.ptrstovka.calendarview2.DayViewDecorator;
import com.ptrstovka.calendarview2.DayViewFacade;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    public static ProgressDialog mProgressDialog;

    public static void showProgress(Context context) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissProgress();
        try {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.progressdialog);
            ImageView Img = mProgressDialog.findViewById(R.id.ImgV);
            Glide.with(context).asGif().load(R.raw.progress).into(Img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressNormal(Context context) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissProgress();
        try {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setContentView(R.layout.progressdialog);
            ProgressBar progressBar = mProgressDialog.findViewById(R.id.progressBar1);
            ImageView Img = mProgressDialog.findViewById(R.id.ImgV);
            progressBar.setVisibility(View.VISIBLE);
            Img.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void dismissProgress() {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setCartBadgeCount(Context mContext, TextView tvCount) {
        if (Pref.getValue(mContext, Constants.TAG_CART_BADGE_COUNT, 0) != 0) {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText("" + Pref.getValue(mContext, Constants.TAG_CART_BADGE_COUNT, 0));
            ((DashboardActivity) mContext).itemView.removeView(((DashboardActivity) mContext).cart_badge);
            ((DashboardActivity) mContext).itemView.addView(((DashboardActivity) mContext).cart_badge);
        } else {
            tvCount.setVisibility(View.GONE);
            ((DashboardActivity) mContext).itemView.removeView(((DashboardActivity) mContext).cart_badge);
        }
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public final static boolean isValidPassword(String hex) {
        //String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{6,}$";
        String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[#?!@$%^&*-]).{6,}$";
        return checkValidation(hex, PASSWORD_PATTERN);
    }

    public final static boolean isValidEmail(String hex) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return checkValidation(hex, EMAIL_PATTERN);
    }

    public final static boolean isValidPhone(String hex) {
        String PHONE_PATTERN = "^[+]?[0-9]{10,13}$";
        return checkValidation(hex, PHONE_PATTERN);
    }

    private static boolean checkValidation(String hex, String EMAIL_PATTERN) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }


    public static boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;


        return isKeyboardShown;
    }

    public static void getKeyboardOpenorNot(Context context, final View rootView, final View view) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

// r.bottom is the position above soft keypad or device button.
// if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
// keyboard is opened
                    view.setVisibility(View.GONE);
                } else {
// keyboard is closed
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void setHideSoftKeyboard(Context context, EditText mEditTextCommon) {
        InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(mEditTextCommon.getWindowToken(), 0);
    }

    public void setShowSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        if (activity != null) {

            if (view != null) {
                try {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }

        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static void activityenterAnim(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);

    }

    public static void activityexitAnim(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    /*String text = tv.getText().subSequence(0,
                            lineEndIndex - expandText.length() + 1)
                            + " " + expandText;*/
                    String text = tv.getText().subSequence(0, expandText.length()) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(tv.getText().toString(), tv, lineEndIndex, expandText, viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final String strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false);
                        tv.setTextColor(Color.BLACK);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 1, "View More", true);
                        tv.setTextColor(Color.BLACK);
                    }

                }
            }, strSpanned.indexOf(spanableText), strSpanned.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


    public static class BeforeAllDaysDisabledDecorator extends DayViewDecorator {


        @Override
        public boolean shouldDecorate(CalendarDay day) {
            CalendarDay date = CalendarDay.today();
            if ((day.isBefore(date))) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.areDaysDisabled();
        }
    }

    public static class AfterAllDaysDisabledDecorator extends DayViewDecorator {


        int year, month, day;

        public AfterAllDaysDisabledDecorator(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        public boolean shouldDecorate(CalendarDay calendarDay) {
            CalendarDay date = CalendarDay.from(year, month, day);
            if ((calendarDay.isAfter(date))) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.areDaysDisabled();
        }
    }

    public static class DaysOffDecorator extends DayViewDecorator {

        ArrayList<String> daylist;
        int sun = 0, mon = 0, tue = 0, wed = 0, thu = 0, fri = 0, sat = 0;

        public DaysOffDecorator(ArrayList<String> day) {
            daylist = day;

            for (int i = 0; i < daylist.size(); i++) {
                if (daylist.get(i).contains("sunday")) {
                    sun = 1;
                } else if (daylist.get(i).contains("monday")) {
                    mon = 1;
                } else if (daylist.get(i).contains("tuesday")) {
                    tue = 1;
                } else if (daylist.get(i).contains("wednesday")) {
                    wed = 1;
                } else if (daylist.get(i).contains("thursday")) {
                    thu = 1;
                } else if (daylist.get(i).contains("friday")) {
                    fri = 1;
                } else if (daylist.get(i).contains("saturday")) {
                    sat = 1;
                }
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            if (sun == 1 && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 1) {
                return true;
            } else if (mon == 1  && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 2) {
                return true;
            } else if (tue == 1  && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 3) {
                return true;
            } else if (wed == 1  && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 4) {
                return true;
            } else if (thu == 1  && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 5) {
                return true;
            } else if (fri == 1  && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 6) {
                return true;
            } else if (sat == 1  && day.getCalendar().get(Calendar.DAY_OF_WEEK) == 7) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.areDaysDisabled();
        }
    }

    public static class DateNotAvailableDecorator extends DayViewDecorator {
        HashSet<CalendarDay> dates;

        public DateNotAvailableDecorator(Collection<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.areDaysDisabled();
        }
    }


    public static File saveBitmap(Bitmap bitmap) {
        File file = null;
        String storePath = Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".jpg";
        if (bitmap != null) {
            file = new File(storePath);
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(storePath); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static SpannableString getRMConverter(float textSize, String givenAmount) {
        String s = "RM " + givenAmount;
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(textSize), 0, 2, 0);
        return ss1;
    }

    public static String getThousandsNotation(String givenAmount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        //formatter.applyPattern("#,###,###,###");
        String yourFormattedAmount = formatter.format(Double.parseDouble(givenAmount));
        return yourFormattedAmount;
    }

    public static String getThousandsNotationReview(int givenAmount) {
        int number = givenAmount;
        String numberString = "";
        if (Math.abs(number / 1000000) > 1) {
            numberString = (number / 1000000) + "m";
        } else if (Math.abs(number / 1000) > 1) {
            numberString = (number / 1000) + "k";
        } else {
            numberString = "" + givenAmount;
        }
        return numberString;
    }

    public static Boolean isActivityRunning(Context mCOntext, Class activityClass) {
        ActivityManager activityManager = (ActivityManager) mCOntext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    public static String monthName(int monthNumber) {
        String monthName = "";
        switch (monthNumber) {
            case 01:
                monthName = "Jan";
                break;
            case 02:
                monthName = "Feb";
                break;
            case 03:
                monthName = "Mar";
                break;
            case 04:
                monthName = "Apr";
                break;
            case 05:
                monthName = "May";
                break;
            case 06:
                monthName = "Jun";
                break;
            case 07:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
        }
        return monthName;

    }

    public static String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

   /* public static Context getMyContext(Context mContext, String lang){
        return MyContextWrapper.wrap(mContext,lang);
    }*/

    public static void setLocale(Context mContext, String lang, int from) {
        Configuration config = new Configuration();
        Log.e("TestValudLan","999  " + lang);
        if (lang.equals("en")) {
            config.locale = Locale.ENGLISH;
            Pref.setValue(mContext, Constants.APP_LANGUAGE, 1);
        } else if (lang.equals("ja")) {
            config.locale = Locale.JAPANESE;
            Pref.setValue(mContext, Constants.APP_LANGUAGE, 2);
        } else if (lang.equals("ko")) {
            config.locale = Locale.KOREAN;
            Pref.setValue(mContext, Constants.APP_LANGUAGE, 3);
        } else {
            config.locale = Locale.ENGLISH;
            Pref.setValue(mContext, Constants.APP_LANGUAGE, 1);
        }
        mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
        if (from == 0) {
            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_explore).setTitle(mContext.getString(R.string.explore));
            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_destination).setTitle(mContext.getString(R.string.destination));
            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_cart).setTitle(mContext.getString(R.string.cart));
            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_booking).setTitle(mContext.getString(R.string.booking));
            ((DashboardActivity) mContext).mBinding.moreMenuBottomNavigation.getMenu().findItem(R.id.action_account).setTitle(mContext.getString(R.string.account));
        }
    }
}

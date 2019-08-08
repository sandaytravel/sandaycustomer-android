package com.san.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.DashboardActivity;
import com.san.app.activity.MainActivity;
import com.san.app.adapter.AllReviewListAdapter;
import com.san.app.adapter.FaqActivitiesDetailsAdapter;
import com.san.app.adapter.PackageOptionFromActivitiesAdapter;
import com.san.app.databinding.FragmentDetailActivitiesBinding;
import com.san.app.font.TextViewAirbnb_medium;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.network.ApiClient;
import com.san.app.network.ApiInterface;
import com.san.app.util.Constants;
import com.san.app.util.FieldsValidator;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.util.Constants.APP_LANGUAGE;
import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;
import static com.san.app.util.Utils.setCartBadgeCount;
import static com.san.app.util.Utils.setLocale;


public class DetailActivitiesFragment extends BaseFragment {


    //class object declaration..
    FragmentDetailActivitiesBinding mBinding;
    View rootView;
    Context mContext;
    AllReviewListAdapter allReviewListAdapter;
    PackageOptionFromActivitiesAdapter packageOptionFromActivitiesAdapter;
    FaqActivitiesDetailsAdapter faqActivitiesDetailsAdapter;
    ArrayList<ViewActivityDetailModel.Packageoption> packageoptionArrayList = new ArrayList<>();
    ArrayList<ViewActivityDetailModel.Review> reviewsArrayList = new ArrayList<>();
    ArrayList<ViewActivityDetailModel.Faqdetail> faqListModelArrayList = new ArrayList<>();
    ViewActivityDetailModel viewActivityDetailModel;
    Animation animShake;

    //variable declaration.
    private String TAG = DetailActivitiesFragment.class.getSimpleName();
    private boolean isValid = true;
    private int activityId = 0;
    private boolean isWishlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_activities, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setLocale(mContext, Pref.getValue(mContext,Constants.APP_LANGUAGE,0)==0 || Pref.getValue(mContext,Constants.APP_LANGUAGE,1)==1 ? "en" : Pref.getValue(mContext,Constants.APP_LANGUAGE,2)==2 ? "ja" : Pref.getValue(mContext,Constants.APP_LANGUAGE,3)==3 ? "ko" : "en",0);
            setUp();
            setOnClickListener();
        } else {
            setCartBadgeCount(mContext, mBinding.cartBadge);

        }

        return rootView;
    }

    private void setUp() {
        mBinding.tvPackageOptions.setText(getString(R.string.package_options));
        mBinding.tvReadReviews.setText(getString(R.string.read_all_reviews));
        mBinding.tvActivityInfo.setText(getString(R.string.activity_information));
        mBinding.tvHowToUse.setText(getString(R.string.how_to_use));
        mBinding.tvCancelPolicy.setText(getString(R.string.cancelation_policy));
        mBinding.tvFAQPolicy.setText(getString(R.string.faqs));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        animShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);

       // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      //  StrictMode.setThreadPolicy(policy);

//faq list
        faqActivitiesDetailsAdapter = new FaqActivitiesDetailsAdapter(mContext, faqListModelArrayList);
        mBinding.rvFaqList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvFaqList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvFaqList.setAdapter(faqActivitiesDetailsAdapter);
        faqActivitiesDetailsAdapter.onClickPostion(onClickPosition);
        //mBinding.rvFaqList.setHasFixedSize(true);

        if (getArguments() != null) activityId = getArguments().getInt("activity_id");
        Log.e(TAG, "IDD " + activityId);
        changeToolbarView();

        setCartBadgeCount(mContext, mBinding.cartBadge);

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callActivityDetailList(); //get activity detail
            }
        }, 100);
    }

    private void setReviewiList() {
        //review  list
        allReviewListAdapter = new AllReviewListAdapter(mContext, reviewsArrayList, 0);
        mBinding.rvReviewsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvReviewsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvReviewsList.setAdapter(allReviewListAdapter);
    }


    private void setOnClickListener() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });


        mBinding.imgright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartListingFragment fragment = new CartListingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("badge_click", 1);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mBinding.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    Pref.setValue(mContext, "packageOptionList", new Gson().toJson(viewActivityDetailModel.getPayload().getPackageoptions()));
                    Pref.setValue(mContext,"isAddCartOrBookNow","addCart");
                    PackageOptionsListFragment fragment = new PackageOptionsListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("basicDetails", new Gson().toJson(viewActivityDetailModel.getPayload().getBasicdetails()));
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                } else {
                    startActivity(new Intent(mContext, MainActivity.class));
                    ((FragmentActivity) mContext).overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                }
            }
        });

        mBinding.tvBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    Pref.setValue(mContext, "packageOptionList", new Gson().toJson(viewActivityDetailModel.getPayload().getPackageoptions()));
                    Pref.setValue(mContext,"isAddCartOrBookNow","bookNow");
                    PackageOptionsListFragment fragment = new PackageOptionsListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("basicDetails", new Gson().toJson(viewActivityDetailModel.getPayload().getBasicdetails()));
                    fragment.setArguments(bundle);
                    changeFragment_back(fragment);
                } else {
                    startActivity(new Intent(mContext, MainActivity.class));
                    ((FragmentActivity) mContext).overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                }
            }
        });

        mBinding.imgWish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    mBinding.imgWish.startAnimation(animShake);
                    callActivityAddRemoveWishListAPI();
                } else {
                    openLoginView(mContext);
                }
            }
        });

        mBinding.tvReadReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllReviewListingFragment fragment = new AllReviewListingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", viewActivityDetailModel.getPayload().getBasicdetails().getActivityMain());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }


    private void callActivityDetailList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + activityId);
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ViewActivityDetailModel> call = apiService.viewactivityDetail(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ViewActivityDetailModel>() {
            @Override
            public void onResponse(Call<ViewActivityDetailModel> call, Response<ViewActivityDetailModel> response) {
                Log.e(TAG, "response " + response.toString());
                Utils.dismissProgress();
                //categoaryActivityModelList.clear();
                if (response.body() != null) {
                    viewActivityDetailModel = response.body();
                    setActivityDetailDataRes(viewActivityDetailModel.getPayload());
                    mBinding.activityMain.setVisibility(View.VISIBLE);
                    if (viewActivityDetailModel.getPayload().getPolicydetail().size() > 0)
                        setPolicyDetailView();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.optInt("code") == 401) {
                            new FieldsValidator(getActivity()).customToast(jsonObject.getString("message"), R.mipmap.cancel_toast_new);
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            errorBody(response.errorBody());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }

            @Override
            public void onFailure(Call<ViewActivityDetailModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
            }
        });
    }

    private void setActivityDetailDataRes(ViewActivityDetailModel.Payload payload) {
        Glide.with(mContext).load(payload.getBasicdetails().getImage()).apply(new RequestOptions().placeholder(R.color.login_btn_bg).error(R.color.login_btn_bg)).into(mBinding.imgMain);
        mBinding.tvActivityName.setText(payload.getBasicdetails().getTitle());
        mBinding.tvSubTitleActivity.setVisibility(!TextUtils.isEmpty(payload.getBasicdetails().getSubtitle()) ? View.VISIBLE : View.GONE);
        mBinding.tvSubTitleActivity.setText(payload.getBasicdetails().getSubtitle());
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payload.getBasicdetails().getDisplayPrice()) ? getRMConverter(0.6f, getThousandsNotation(payload.getBasicdetails().getDisplayPrice())) : getRMConverter(0.6f, getThousandsNotation(payload.getBasicdetails().getActualPrice())));
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(payload.getBasicdetails().getDisplayPrice()) ? getThousandsNotation(payload.getBasicdetails().getActualPrice()) : "");
        mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mBinding.tvTotalReview.setText("" + payload.getBasicdetails().getTotalReview() + " reviews");
        mBinding.ratingBar.setStepSize(Float.parseFloat("0.01"));
        mBinding.ratingBar.setRating(Float.parseFloat(String.valueOf(payload.getBasicdetails().getAverageReview())));
        mBinding.tvReviewAvg.setText(String.format("%.1f", payload.getBasicdetails().getAverageReview()));

        mBinding.lnDesc.setVisibility(!TextUtils.isEmpty(payload.getBasicdetails().getDescription().toString()) ? View.VISIBLE : View.GONE);
        mBinding.lnWhatMain.setVisibility(!TextUtils.isEmpty(payload.getWhatToExpect().getWhatToExpectDescription().toString()) ? View.VISIBLE : View.GONE);
        mBinding.lnActivityInfoMain.setVisibility(!TextUtils.isEmpty(payload.getActivityInformation().getActivityInformationDescription().toString()) ? View.VISIBLE : View.GONE);
        mBinding.lnHowToUseMain.setVisibility(!TextUtils.isEmpty(payload.getHowToUse().getHowToUseDescription().toString()) ? View.VISIBLE : View.GONE);
        mBinding.lnCancelPolicyMain.setVisibility(!TextUtils.isEmpty(payload.getCancellationPolicy().getCancellationPolicyDescription().toString()) ? View.VISIBLE : View.GONE);
        mBinding.lnReviewsMain.setVisibility(payload.getReviews().size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.lnFAQMain.setVisibility(payload.getFaqdetail().size() > 0 ? View.VISIBLE : View.GONE);

        setWebViewSetting(mBinding.webDesc, payload.getBasicdetails().getDescription());
        setWebViewSetting(mBinding.webWhatToExpect, payload.getWhatToExpect().getWhatToExpectDescription());
        setWebViewSetting(mBinding.webActivityInfo, payload.getActivityInformation().getActivityInformationDescription());
        setWebViewSetting(mBinding.webHowToUse, payload.getHowToUse().getHowToUseDescription());
        setWebViewSetting(mBinding.webCancelPolicy, payload.getCancellationPolicy().getCancellationPolicyDescription());


        mBinding.lnPackageOptionMain.setVisibility(payload.getPackageoptions().size() > 0 ? View.VISIBLE : View.GONE);
        isWishlist = payload.getBasicdetails().getWishlist().equals("1") ? true : false;
        mBinding.imgWish.setImageDrawable(isWishlist ? ContextCompat.getDrawable(mContext, R.mipmap.wish_selected_img) : ContextCompat.getDrawable(mContext, R.mipmap.wish_img));
        if (isWishlist)
            mBinding.imgWish.setColorFilter(ContextCompat.getColor(mContext, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        Log.e("PackageSelection", "0000   " + payload.getPackageoptions().size());
        //popular activities list
        packageoptionArrayList.addAll(payload.getPackageoptions());
        packageOptionFromActivitiesAdapter = new PackageOptionFromActivitiesAdapter(mContext, packageoptionArrayList, viewActivityDetailModel.getPayload().getBasicdetails(), 1);
        mBinding.rvPackageOptionsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPackageOptionsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPackageOptionsList.setAdapter(packageOptionFromActivitiesAdapter);


        //packageOptionFromActivitiesAdapter.notifyDataSetChanged();

        reviewsArrayList.addAll(payload.getReviews());
        setReviewiList();

        faqListModelArrayList.addAll(payload.getFaqdetail());
        faqActivitiesDetailsAdapter.notifyDataSetChanged();

        mBinding.lnBottomBtn.setVisibility(packageoptionArrayList.size() > 0 ? View.VISIBLE : View.GONE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mBinding.nestedMain.getLayoutParams();
        params.setMargins(0, 0, 0,120);
        mBinding.nestedMain.setLayoutParams(params);
    }

    private void setPolicyDetailView() {
        mBinding.lnPolicyDetail.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(5, 5, 5, 5);
        if (viewActivityDetailModel.getPayload().getPolicydetail().size() > 0) {
            for (int i = 0; i < viewActivityDetailModel.getPayload().getPolicydetail().size(); i++) {
                CircleImageView imageView = new CircleImageView(getActivity());
                /*Glide.with(mContext).load(viewActivityDetailModel.getPayload().getPolicydetail().get(i).getIcon_resized()).apply(new RequestOptions().placeholder(R.color.login_btn_bg).error(R.color.login_btn_bg))
                        .into(imageView);*/
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.circle_full_app_dark));
                TextViewAirbnb_medium textView = new TextViewAirbnb_medium(getActivity());
                textView.setPadding(25, 0, 0, 0);
                textView.setText(viewActivityDetailModel.getPayload().getPolicydetail().get(i).getName());
                textView.setTextSize(12f);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.black_color));
                LinearLayout ll = new LinearLayout(mContext);
                ll.setPadding(0, 10, 0, 10);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setGravity(Gravity.CENTER);
                ll.addView(imageView);
                ll.addView(textView);
                imageView.getLayoutParams().width = 16;
                imageView.getLayoutParams().height = 16;
                mBinding.lnPolicyDetail.addView(ll);
            }
        }

    }

    private void setWebViewSetting(final WebView webView, String webData) {
        //webView.loadData(webData, "text/html", "UTF-8");
        webView.loadDataWithBaseURL("http://localhost", webData, "text/html; video/mpeg", "UTF-8", "");
        webView.getSettings().setDefaultFontSize(38);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);

        /*webView.setWebChromeClient(new MyWebChromeClient(mContext));
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);*/

        /*webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setScrollbarFadingEnabled(true);
        webView.loadDataWithBaseURL("http://localhost",webData, "text/html; video/mpeg", "UTF-8","");*/
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }

    private void callActivityAddRemoveWishListAPI() {
        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + viewActivityDetailModel.getPayload().getBasicdetails().getActivityMain());
        data.put("activity_lang_id", "" + viewActivityDetailModel.getPayload().getBasicdetails().getActivityWhishlistId());
        data.put("language_id", ""+Pref.getValue(mContext,APP_LANGUAGE,0));
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.add_remove_whishlist(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e("TestData", "9999   " + jsonObject.toString());
                        mBinding.imgWish.setImageDrawable(isWishlist ? ContextCompat.getDrawable(mContext, R.mipmap.wish_img) : ContextCompat.getDrawable(mContext, R.mipmap.wish_selected_img));
                        mBinding.imgWish.setColorFilter(ContextCompat.getColor(mContext, isWishlist ? R.color.app_theme_dark : R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                        isWishlist = isWishlist ? false : true;
                        // wishListActivitiesAdapter.removeAt(2);
                        // wishListActivitiesAdapter.notifyDataSetChanged();
                    } else {
                        errorBody(response.errorBody());
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.dismissProgress();
            }
        });
    }


    private void changeToolbarView() {
        mBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 150) {
                    mBinding.tvTitle.setVisibility(View.VISIBLE);
                    mBinding.tvTitle.setText(mBinding.tvActivityName.getText());
                    mBinding.imgBackView.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                    mBinding.imgSearch.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                    mBinding.imgrightShare.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                    mBinding.imgright.setColorFilter(ContextCompat.getColor(mContext, R.color.black_color));
                    isShow = true;
                } else if (isShow) {
                    mBinding.tvTitle.setVisibility(View.GONE);
                    mBinding.imgBackView.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    mBinding.imgSearch.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    mBinding.imgrightShare.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    mBinding.imgright.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }

    OnClickPosition onClickPosition = new OnClickPosition() {
        @Override
        public void OnClickPosition(Integer position, String msg) {
            faqListModelArrayList.get(position).setOpen(faqListModelArrayList.get(position).isOpen() ? false : true);
            faqListModelArrayList.set(position, faqListModelArrayList.get(position));
            //faq list
            faqActivitiesDetailsAdapter = new FaqActivitiesDetailsAdapter(mContext, faqListModelArrayList);
            mBinding.rvFaqList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mBinding.rvFaqList.setItemAnimator(new DefaultItemAnimator());
            mBinding.rvFaqList.setAdapter(faqActivitiesDetailsAdapter);
            faqActivitiesDetailsAdapter.onClickPostion(onClickPosition);
            //faqActivitiesDetailsAdapter.notifyItemChanged(position);
        }
    };

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

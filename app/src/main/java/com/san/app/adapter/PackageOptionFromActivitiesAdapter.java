package com.san.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.activity.MainActivity;
import com.san.app.databinding.RowPackageOptionsDetailActivityBinding;
import com.san.app.fragment.BookingOptionPackagesFragment;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.util.Constants;
import com.san.app.util.Pref;

import java.util.List;

import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;

public class PackageOptionFromActivitiesAdapter extends RecyclerView.Adapter<PackageOptionFromActivitiesAdapter.MyViewHolder> {

    private Context mContext;
    private RowPackageOptionsDetailActivityBinding mBinding;
    private List<ViewActivityDetailModel.Packageoption> payloadList;
    private ViewActivityDetailModel.Basicdetails basicDetails;
    OnClickPosition onClickPosition;
    private int typeFrom = 0;

    public void onClickPosition(OnClickPosition onClickPosition) {
        this.onClickPosition = onClickPosition;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        TextView tvPkgTitle,tvDisplayPrice,tvActualPrice,tvViewMoreLess,tvDesc,tvSelect;
        CardView cardSelect;
        public MyViewHolder(View view) {
            super(view);
            tvPkgTitle=(TextView)view.findViewById(R.id.tvPkgTitle);
            tvDisplayPrice=(TextView)view.findViewById(R.id.tvDisplayPrice);
            tvActualPrice=(TextView)view.findViewById(R.id.tvActualPrice);
            tvViewMoreLess=(TextView)view.findViewById(R.id.tvViewMoreLess);
            tvDesc=(TextView)view.findViewById(R.id.tvDesc);
            tvSelect=(TextView)view.findViewById(R.id.tvSelect);
            cardSelect=(CardView)view.findViewById(R.id.cardSelect);
        }
    }


    public PackageOptionFromActivitiesAdapter(Context mContext, List<ViewActivityDetailModel.Packageoption> restaurantListModelArrayList, ViewActivityDetailModel.Basicdetails basiDetail, int typeFrom) {
        this.mContext = mContext;
        for(int i=0;i<restaurantListModelArrayList.size();i++){
            restaurantListModelArrayList.get(i).setExpand(false);
        }
        payloadList = restaurantListModelArrayList;
        basicDetails = basiDetail;
        this.typeFrom = typeFrom;
    }

    public PackageOptionFromActivitiesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_package_options_detail_activity, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(final MyViewHolder mBinding, final int position) {
        final ViewActivityDetailModel.Packageoption payloadModel = payloadList.get(position);
        mBinding.tvPkgTitle.setText(payloadModel.getPackageTitle());
        mBinding.cardSelect.setVisibility(typeFrom == 1 ? View.INVISIBLE : View.VISIBLE);
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? getRMConverter(0.6f, getThousandsNotation(payloadModel.getDisplayPrice())) : getRMConverter(0.6f, getThousandsNotation(payloadModel.getActualPrice())));
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? getThousandsNotation(payloadModel.getActualPrice()) : "");
        if (!TextUtils.isEmpty(payloadModel.getDisplayPrice()))
            mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (!TextUtils.isEmpty(payloadModel.getDescription())) {
            if (payloadModel.getDescription().length() > 18) {
                mBinding.tvViewMoreLess.setVisibility(View.VISIBLE);
                if (payloadList.get(position).isExpand()) {
                    payloadList.get(position).setExpand(false);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription()));
                    mBinding.tvViewMoreLess.setText("View Less");
                } else {
                    payloadList.get(position).setExpand(true);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription().substring(0, 18)));
                    mBinding.tvViewMoreLess.setText("View More");
                }
            } else {
                mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription()));
                mBinding.tvViewMoreLess.setVisibility(View.GONE);
            }
        }

        mBinding.tvViewMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payloadList.get(position).isExpand()) {
                    payloadList.get(position).setExpand(false);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription()));
                    mBinding.tvViewMoreLess.setText("View Less");
                } else {
                    payloadList.get(position).setExpand(true);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription().substring(0, 18)));
                    mBinding.tvViewMoreLess.setText("View More");
                }
            }
        });

        mBinding.tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""))) {
                    if (typeFrom == 0 || typeFrom == 1) {
                        BookingOptionPackagesFragment fragment = new BookingOptionPackagesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("package_details", new Gson().toJson(payloadModel));
                        bundle.putString("basicDetails", new Gson().toJson(basicDetails));
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        transaction.replace(R.id.frame, fragment, "fragment");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        onClickPosition.OnClickPosition(position, "");
                    }
                } else {
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                    ((FragmentActivity) mContext).overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                }

            }
        });


    }

    private void setWebViewSetting(WebView webView, String webData) {
        webView.loadData(webData, "text/html", "UTF-8");
        webView.getSettings().setDefaultFontSize(35);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
    }

    @Override
    public int getItemCount() {
        return payloadList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
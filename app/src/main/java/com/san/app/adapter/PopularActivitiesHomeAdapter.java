package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowPopularActivityHomeBinding;
import com.san.app.model.ViewCityDetailModel;

import java.util.List;

import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;
import static com.san.app.util.Utils.getThousandsNotationReview;

public class PopularActivitiesHomeAdapter extends RecyclerView.Adapter<PopularActivitiesHomeAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowPopularActivityHomeBinding mBinding;
    private List<ViewCityDetailModel.PopularActivity> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public PopularActivitiesHomeAdapter(Context mContext, List<ViewCityDetailModel.PopularActivity> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public PopularActivitiesHomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_popular_activity_home, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ViewCityDetailModel.PopularActivity payloadModel = payloadList.get(position);
        mBinding.tvTitle.setText(payloadModel.getTitle());
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? getRMConverter(0.6f, getThousandsNotation(payloadModel.getDisplayPrice())) : getRMConverter(0.6f, getThousandsNotation(payloadModel.getActualPrice())));
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? getThousandsNotation(payloadModel.getActualPrice()) : "");
        if (!TextUtils.isEmpty(payloadModel.getDisplayPrice()))
            mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mBinding.lnReview.setVisibility(payloadModel.getTotalReview() != 0 && payloadModel.getTotalReview() != null ? View.VISIBLE : View.GONE);
        mBinding.tvTotalReview.setText("" + payloadModel.getTotalReview() + " (Reviews)");
        mBinding.tvAvgReview.setText("" + String.format("%.1f", payloadModel.getAverageReview()));
        if (mBinding.lnReview.getVisibility() == View.VISIBLE)
            mBinding.tvTotalBooked.setPadding(10, 0, 0, 0);

        mBinding.tvTotalBooked.setVisibility(payloadModel.getTotalBooked() > 0 ? View.VISIBLE : View.GONE);
        mBinding.tvTotalBooked.setText("" + getThousandsNotationReview(payloadModel.getTotalBooked()) + " Booked");
        Glide.with(mContext)
                .load(payloadModel.getImage())
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))

                .into(mBinding.image);
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
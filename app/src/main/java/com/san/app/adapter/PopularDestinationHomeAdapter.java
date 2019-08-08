package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowPopularDestinationHomeBinding;
import com.san.app.model.CitiesListModel;

import java.util.List;

public class PopularDestinationHomeAdapter extends RecyclerView.Adapter<PopularDestinationHomeAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowPopularDestinationHomeBinding mBinding;
    private List<CitiesListModel> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public PopularDestinationHomeAdapter(Context mContext, List<CitiesListModel> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public PopularDestinationHomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_popular_destination_home, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CitiesListModel payloadModel = payloadList.get(position);

        mBinding.tvCityName.setVisibility(!TextUtils.isEmpty(payloadModel.city) ? View.VISIBLE : View.GONE);
        mBinding.tvCityName.setText(payloadModel.city);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((FragmentActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need three fix imageview in width
        int devicewidth = displaymetrics.widthPixels / 2;
        int deviceheight = displaymetrics.heightPixels / 4;

        mBinding.lnMain.getLayoutParams().width = devicewidth;
        Glide.with(mContext)
                .load(payloadModel.image_fullsize)
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                //.apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(mContext, 20, 0)))
                .into(mBinding.image);
    }

    @Override
    public int getItemCount() {
        return payloadList.size(); //payloadList.size() > 4 ? 4 : payloadList.size()
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
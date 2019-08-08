package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowPopularDestinationHomeBinding;
import com.san.app.model.CitiesListModel;

import java.util.ArrayList;

public class PopularDestinationAdapter extends RecyclerView.Adapter<PopularDestinationAdapter.MyViewHolder> {

    private Context mContext;
    private RowPopularDestinationHomeBinding mBinding;
    private ArrayList<CitiesListModel> payloadList;
    private int viewType = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public PopularDestinationAdapter(Context mContext, ArrayList<CitiesListModel> citiesListModelArrayList) {
        this.mContext = mContext;
        payloadList = citiesListModelArrayList;
    }

    public PopularDestinationAdapter(Context mContext, ArrayList<CitiesListModel> citiesListModelArrayList, int i) {
        this.mContext = mContext;
        payloadList = citiesListModelArrayList;
        viewType = i;
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
        Glide.with(mContext)
                .load(payloadModel.image_fullsize)
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                .into(mBinding.image);
    }

    @Override
    public int getItemCount() {
        if (payloadList.size() > 0) {
            if (viewType == 0) {
                return payloadList.size() > 4 ? 4 : payloadList.size();
            } else {
                return payloadList.size();
            }
        } else {
            return payloadList.size();
        }
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
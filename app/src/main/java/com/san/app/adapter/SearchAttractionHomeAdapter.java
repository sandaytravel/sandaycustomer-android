package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowSearchAttractionHomeBinding;
import com.san.app.model.SearchCityArractionListModel;

import java.util.List;

public class SearchAttractionHomeAdapter extends RecyclerView.Adapter<SearchAttractionHomeAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowSearchAttractionHomeBinding mBinding;
    private List<SearchCityArractionListModel.Activity> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public SearchAttractionHomeAdapter(Context mContext, List<SearchCityArractionListModel.Activity> cityModelArrayList) {
        this.mContext = mContext;
        payloadList = cityModelArrayList;
    }
   /*public AllDestinationChildAdapter(Context mContext) {
       this.mContext=mContext;
   }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_search_attraction_home, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SearchCityArractionListModel.Activity payloadModel = payloadList.get(position);

        mBinding.tvActivityName.setText(payloadModel.getActivityTitle());
        mBinding.tvCityName.setText(payloadModel.getCityName());
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
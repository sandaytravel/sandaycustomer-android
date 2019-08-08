package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowSearchDestinationChildBinding;
import com.san.app.model.CitiesSearchListModel;

import java.util.ArrayList;

public class SearchDestinationChildAdapter extends RecyclerView.Adapter<SearchDestinationChildAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowSearchDestinationChildBinding mBinding;
    private ArrayList<CitiesSearchListModel> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public SearchDestinationChildAdapter(Context mContext, ArrayList<CitiesSearchListModel> citiesListModelArrayList) {
        this.mContext = mContext;
        payloadList = citiesListModelArrayList;
    }
   /*public AllDestinationChildAdapter(Context mContext) {
       this.mContext=mContext;
   }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_search_destination_child, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CitiesSearchListModel payloadModel = payloadList.get(position);

        mBinding.tvCityName.setText(payloadModel.city);
       // mBinding.tvCountryName.setText(payloadModel.countryName);


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
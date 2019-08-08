package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowCountryListBinding;
import com.san.app.model.CountryListModel;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowCountryListBinding mBinding;
    private List<CountryListModel.Payload> payloadList;
    private String typeValue;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }

    public CountryListAdapter(Context mContext, ArrayList<CountryListModel.Payload> countryListModelArrayList, String typeValue) {
        this.mContext = mContext;
        payloadList = countryListModelArrayList;
        this.typeValue = typeValue;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_country_list, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CountryListModel.Payload payloadModel = payloadList.get(position);

        mBinding.tvCountryCode.setVisibility(typeValue.equals("country") ? View.GONE : View.VISIBLE);
        mBinding.tvCountryName.setText(payloadModel.getCountryName());
        mBinding.tvCountryCode.setText("+" + payloadModel.getCountryCode());
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
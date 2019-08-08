package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowMerchantNoteListBinding;
import com.san.app.model.MyOrderListModel;

import java.util.ArrayList;
import java.util.List;

public class MerchantNoteListAdapter extends RecyclerView.Adapter<MerchantNoteListAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowMerchantNoteListBinding mBinding;
    private List<MyOrderListModel.Note> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }

    public MerchantNoteListAdapter(Context mContext, ArrayList<MyOrderListModel.Note> countryListModelArrayList) {
        this.mContext = mContext;
        payloadList = countryListModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_merchant_note_list, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyOrderListModel.Note payloadModel = payloadList.get(position);
        mBinding.tvDesc.setVisibility(!TextUtils.isEmpty(payloadModel.getDescription()) ? View.VISIBLE : View.GONE);
        mBinding.tvDesc.setText(payloadModel.getDescription());
        mBinding.tvTime.setText(payloadModel.getCreatedAt());
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
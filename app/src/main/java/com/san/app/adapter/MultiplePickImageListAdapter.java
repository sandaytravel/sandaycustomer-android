package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.san.app.R;
import com.san.app.databinding.RowMultilpleImageListBinding;
import com.san.app.interfaces.OnClickPosition;

import java.util.ArrayList;
import java.util.List;

public class MultiplePickImageListAdapter extends RecyclerView.Adapter<MultiplePickImageListAdapter.MyViewHolder> {

    private Context mContext;
    private RowMultilpleImageListBinding mBinding;
    private List<String> payloadList;
    public OnClickPosition onClickPosition;

    //private ArrayList<RestaurantListModel> payloadList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }

    public void onclickPosition(OnClickPosition onClickPosition) {
        this.onClickPosition = onClickPosition;
    }

    public MultiplePickImageListAdapter(Context mContext, ArrayList<String> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public MultiplePickImageListAdapter(List<String> myList) {
        this.payloadList = myList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_multilple_image_list, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        mBinding.imgCancel.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        mBinding.imgSelected.setScaleType(position != 0 ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_XY);
        mBinding.cardImage.setCardElevation(position != 0 ? 5f : 0f);
        Glide.with(mContext)
                .load(payloadList.get(position))
                .into(mBinding.imgSelected);
        Log.e("TestImge","000   " + position + " img  " + payloadList.get(position));
        mBinding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TestImge","111   " + position + " img  " + payloadList.get(position));
                onClickPosition.OnClickPosition(position, "");
            }
        });

    }

    @Override
    public int getItemCount() {
        return payloadList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void removeAt(int position) {
        payloadList.remove(position);
        notifyItemRemoved(position);
        if (payloadList.size() > 0) notifyItemRangeChanged(position, payloadList.size());
    }
}
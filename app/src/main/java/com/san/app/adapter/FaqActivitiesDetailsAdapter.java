package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowFaqListBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.ViewActivityDetailModel;

import java.util.List;

public class FaqActivitiesDetailsAdapter extends RecyclerView.Adapter<FaqActivitiesDetailsAdapter.MyViewHolder> {

    private Context mContext;
    private RowFaqListBinding mBinding;
    private List<ViewActivityDetailModel.Faqdetail> payloadList;
    OnClickPosition onClickPosition;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }

    public void onClickPostion(OnClickPosition onClickPosition) {
        this.onClickPosition = onClickPosition;
    }

    public FaqActivitiesDetailsAdapter(Context mContext, List<ViewActivityDetailModel.Faqdetail> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public FaqActivitiesDetailsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_faq_list, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ViewActivityDetailModel.Faqdetail payloadModel = payloadList.get(position);

        mBinding.tvQuestion.setText("Q: " + payloadModel.getQuestion());
        mBinding.tvAnswer.setText("A: " + payloadModel.getAnswer());

        if (payloadModel.isOpen()) {
            payloadModel.setOpen(true);
            mBinding.tvAnswer.setVisibility(View.VISIBLE);
            mBinding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_up_grey));
        } else {
            payloadModel.setOpen(false);
            mBinding.tvAnswer.setVisibility(View.GONE);
            mBinding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_down_grey));
        }

        mBinding.lnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.imgUpDown.setTag(mBinding.imgUpDown.getTag().equals("down") ? "up" : "down");

                onClickPosition.OnClickPosition(position, "");
            }
        });
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
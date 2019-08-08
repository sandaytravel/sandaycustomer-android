package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowCategoryPopupBinding;
import com.san.app.model.ViewCityDetailModel;

import java.util.List;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.MyViewHolder> {

    private Context mContext;
    private RowCategoryPopupBinding mBinding;
    private List<ViewCityDetailModel.Category> payloadList;
    private int categoryId = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public CategoryFilterAdapter(Context mContext, List<ViewCityDetailModel.Category> categoryModelArrayList, Integer category_id) {
        this.mContext = mContext;
        payloadList = categoryModelArrayList;
        categoryId = category_id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_category_popup, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ViewCityDetailModel.Category payloadModel = payloadList.get(position);
        Glide.with(mContext)
                .load(payloadModel.getCategoryImage())
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                .into(mBinding.imgFirst);
        mBinding.imgTick.setVisibility(payloadModel.getCategoryId() == categoryId ? View.VISIBLE : View.GONE);
        mBinding.tvCateType.setText(payloadModel.getCategoryName());

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
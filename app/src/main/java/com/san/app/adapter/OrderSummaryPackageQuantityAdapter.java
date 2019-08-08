package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowPackageQuantityOptionsBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.MyOrderListModel;

import java.util.List;

import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;

public class OrderSummaryPackageQuantityAdapter extends RecyclerView.Adapter<OrderSummaryPackageQuantityAdapter.MyViewHolder> {

    private Context mContext;
    private RowPackageQuantityOptionsBinding mBinding;
    private List<MyOrderListModel.Packagequantity> payloadList;
    public OnClickPosition onClickPosition;

    public void onclickPosition(OnClickPosition onClickPosition) {
        this.onClickPosition = onClickPosition;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public OrderSummaryPackageQuantityAdapter(Context mContext, List<MyOrderListModel.Packagequantity> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public OrderSummaryPackageQuantityAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_package_quantity_options, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MyOrderListModel.Packagequantity payloadModel = payloadList.get(position);
        mBinding.tvName.setText(payloadModel.getQuantityName());
        mBinding.tvDisplayPrice.setText(getRMConverter(0.5f, getThousandsNotation(payloadModel.getQuantityPrice())));
        mBinding.imgMinus.setVisibility(View.GONE);
        mBinding.imgPlus.setVisibility(View.GONE);
        mBinding.tvCount.setBackground(null);


        double optionTotlaPrice = Double.parseDouble(payloadModel.getQuantityPrice()) * Double.parseDouble("" + payloadModel.getQuantity());
        mBinding.tvCountPrice.setText(getRMConverter(0.5f, getThousandsNotation(""+optionTotlaPrice)));
        mBinding.tvCount.setText("" + payloadModel.getQuantity());


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
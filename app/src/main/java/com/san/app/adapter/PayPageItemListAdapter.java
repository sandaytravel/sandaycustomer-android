package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowPayPageListItemDetailBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.CartViewListModel;
import com.san.app.util.Utils;

import java.util.ArrayList;

import static com.san.app.util.Utils.getThousandsNotation;

public class PayPageItemListAdapter extends RecyclerView.Adapter<PayPageItemListAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowPayPageListItemDetailBinding mBinding;

    private ArrayList<CartViewListModel.Payload> payloadList;
    OnClickPosition onClickPosition;


    public void onClickPosition(OnClickPosition onClickPosition) {
        this.onClickPosition = onClickPosition;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public PayPageItemListAdapter(Context mContext, ArrayList<CartViewListModel.Payload> cartViewListModelArrayList) {
        this.mContext = mContext;
        payloadList = cartViewListModelArrayList;
    }

    public PayPageItemListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_pay_page_list_item_detail, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CartViewListModel.Payload payloadModel = payloadList.get(position);

        mBinding.tvActivityName.setText(payloadModel.getActivityTitle());
        mBinding.tvPackageSelectedName.setText(payloadModel.getPackageTitle());
        mBinding.tvAmount.setText(Utils.getRMConverter(0.6f, getThousandsNotation(""+payloadModel.getTotalPrice())));
        mBinding.tvDate.setText("" + payloadModel.getBookingDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(payloadModel.getBookingDate().toString().split("-")[1])) + " " + payloadModel.getBookingDate().toString().split("-")[0]);

        if (payloadModel.getQuantity().size() == 1) {
            mBinding.tvSelectedGenderName.setText("" + payloadModel.getQuantity().get(0).getQuantity_name() + " - " + payloadModel.getQuantity().get(0).getQuantity());
        } else {
            StringBuilder commaSepValueBuilder = new StringBuilder();
            for (int i = 0; i < payloadModel.getQuantity().size(); i++) {
                if(payloadModel.getQuantity().get(i).getQuantity()>0) {
                    if (commaSepValueBuilder.length() > 0) commaSepValueBuilder.append(',');
                    commaSepValueBuilder.append("" + payloadModel.getQuantity().get(i).getQuantity_name() + " - " + payloadModel.getQuantity().get(i).getQuantity());
                }
            }
            mBinding.tvSelectedGenderName.setText(commaSepValueBuilder);
        }


    }

    @Override
    public int getItemCount() {
        return payloadList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
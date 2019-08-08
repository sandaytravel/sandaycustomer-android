package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.san.app.R;
import com.san.app.databinding.RowMyBookingMainListingBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.MyOrderListModel;
import com.san.app.util.Utils;

import java.util.List;

import static com.san.app.util.Utils.getThousandsNotation;

public class MyBookingMainListAdapter extends RecyclerView.Adapter<MyBookingMainListAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowMyBookingMainListingBinding mBinding;
    private List<MyOrderListModel.Payload> payloadList;
    MyBookingInnerListAdapter myBookingInnerListAdapter;
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


    public MyBookingMainListAdapter(Context mContext, List<MyOrderListModel.Payload> payloadList) {
        this.mContext = mContext;
        this.payloadList = payloadList;
    }

    public MyBookingMainListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_my_booking_main_listing, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MyOrderListModel.Payload payloadModel = payloadList.get(position);
        mBinding.tvTransactionNoValue.setText("" + payloadModel.getTransactionNumber());
        mBinding.tvBookingDateValue.setText("" + payloadModel.getTransactionDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(payloadModel.getTransactionDate().toString().split("-")[1])) + " " + payloadModel.getTransactionDate().toString().split("-")[0]);
        mBinding.cardProceedtopay.setVisibility(payloadModel.getPaymentStatus().equals("Pending") || payloadModel.getPaymentStatus().equals("Failed") ? View.VISIBLE : View.GONE);
        mBinding.tvPaymentStatus.setText(payloadModel.getPaymentStatus().equals("Pending") || payloadModel.getPaymentStatus().equals("Failed") ? mContext.getString(R.string.pending_payment) : mContext.getString(R.string.paid_status));
        mBinding.tvTotalAmountValue.setText(Utils.getRMConverter(0.6f, getThousandsNotation(String.format("%.2f", payloadModel.getTotalAmount()))));
        mBinding.tvPaymentStatus.setTextColor(ContextCompat.getColor(mContext,payloadModel.getPaymentStatus().equals("Pending") || payloadModel.getPaymentStatus().equals("Failed") ? R.color.gray : R.color.app_theme_dark ));
        mBinding.imgStatus.setColorFilter(ContextCompat.getColor(mContext, payloadModel.getPaymentStatus().equals("Pending") || payloadModel.getPaymentStatus().equals("Failed") ? R.color.gray : R.color.app_theme_dark), android.graphics.PorterDuff.Mode.SRC_IN);


        myBookingInnerListAdapter = new MyBookingInnerListAdapter(mContext, payloadModel.getOrders());
        mBinding.rvBookingInnerList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvBookingInnerList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvBookingInnerList.setAdapter(myBookingInnerListAdapter);

        mBinding.tvProceedPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPosition.OnClickPosition(position,"");
               /* PayPalWebPageFragment fragment = new PayPalWebPageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("transaction_id", "" + payloadModel.getTransactionId());
                bundle.putString("webviewurl", "" + payloadModel.getWebviewurl());
                bundle.putString("fromWhere", "booking");
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();*/
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
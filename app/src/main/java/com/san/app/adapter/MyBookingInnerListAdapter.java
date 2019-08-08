package com.san.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.databinding.RowMyBookingInnerListingBinding;
import com.san.app.fragment.BookingDetailPageFragment;
import com.san.app.fragment.WriteReviewFragment;
import com.san.app.model.MyOrderListModel;
import com.san.app.util.Utils;

import java.util.List;

import static com.san.app.util.Utils.getThousandsNotation;

public class MyBookingInnerListAdapter extends RecyclerView.Adapter<MyBookingInnerListAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowMyBookingInnerListingBinding mBinding;
    private List<MyOrderListModel.Order> payloadList;
    private Dialog qrDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public MyBookingInnerListAdapter(Context mContext, List<MyOrderListModel.Order> payloadList) {
        this.mContext = mContext;
        this.payloadList = payloadList;
    }

    public MyBookingInnerListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_my_booking_inner_listing, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MyOrderListModel.Order payloadModel = payloadList.get(position);
        mBinding.tvWriteReview.setVisibility(payloadModel.getIsReviewGiven().equals("1") ? View.GONE : View.VISIBLE);
        mBinding.tvBookingNo.setText("" + payloadModel.getOrderNumber());
        mBinding.tvbookingDate.setText("" + payloadModel.getBookingDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(payloadModel.getBookingDate().toString().split("-")[1])) + " " + payloadModel.getBookingDate().toString().split("-")[0]);
        mBinding.tvParticipateDate.setText("" + payloadModel.getParticipationDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(payloadModel.getParticipationDate().toString().split("-")[1])) + " " + payloadModel.getParticipationDate().toString().split("-")[0]);
        mBinding.tvActivityName.setText(payloadModel.getActivityName());
        mBinding.tvPackageSelectedName.setText(payloadModel.getPackageTitle());
        mBinding.tvPrice.setText(Utils.getRMConverter(0.6f, getThousandsNotation(payloadModel.getTotalPrice())));
        mBinding.cardVoucherCode.setVisibility(payloadModel.getOrderStatus() == 2 && payloadModel.getIsRedeem() == 0 ? View.VISIBLE : View.GONE);
        mBinding.lnGiftView.setVisibility(payloadModel.getIsRedeem() == 1 ? View.VISIBLE : View.GONE);
        mBinding.lnMain.setAlpha(payloadModel.getIsRedeem() == 1 ? 0.5f : 1f);
        if (payloadModel.getOrderStatus() == 0) {//0 = Pending ,1 = Canceled, 2 = Confirmed, 3 = Expired
            mBinding.imgStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.pending), android.graphics.PorterDuff.Mode.SRC_IN);
            mBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.pending));
            mBinding.tvStatus.setText(R.string.pending);
        } else if (payloadModel.getOrderStatus() == 1) {
            mBinding.imgStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.cancel), android.graphics.PorterDuff.Mode.SRC_IN);
            mBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.cancel));
            mBinding.tvStatus.setText(R.string.canceled);
        } else if (payloadModel.getOrderStatus() == 2) {
            mBinding.imgStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.confirm), android.graphics.PorterDuff.Mode.SRC_IN);
            mBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.confirm));
            mBinding.tvStatus.setText(R.string.confirmed);
        } else {
            mBinding.imgStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.expired), android.graphics.PorterDuff.Mode.SRC_IN);
            mBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.expired));
            mBinding.tvStatus.setText(R.string.expired);
        }

        mBinding.viewBottom.setVisibility(payloadList.size() == position + 1 ? View.GONE : View.VISIBLE);

        Glide.with(mContext)
                .load(payloadModel.getActivityImage())
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                .into(mBinding.imgPlace);

        if (payloadModel.getPackagequantity().size() == 1) {
            mBinding.tvQuantity.setText("" + payloadModel.getPackagequantity().get(0).getQuantityName() + " - " + payloadModel.getPackagequantity().get(0).getQuantity());
        } else {
            StringBuilder commaSepValueBuilder = new StringBuilder();
            for (int i = 0; i < payloadModel.getPackagequantity().size(); i++) {
                commaSepValueBuilder.append("" + payloadModel.getPackagequantity().get(i).getQuantityName() + " - " + payloadModel.getPackagequantity().get(i).getQuantity());
                if (i != payloadModel.getPackagequantity().size() - 1) {
                    commaSepValueBuilder.append("  ");
                }
            }
            mBinding.tvQuantity.setText(commaSepValueBuilder);
        }
        mBinding.lnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingDetailPageFragment fragment = new BookingDetailPageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("myOrderListModel", new Gson().toJson(payloadModel));
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        mBinding.tvWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteReviewFragment fragment = new WriteReviewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("detail", new Gson().toJson(payloadModel, MyOrderListModel.Payload.class));
                bundle.putInt("position", position);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mBinding.cardVoucherCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQRCodeDialog(mContext, payloadModel.getVoucherQrcode());
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


    private void openQRCodeDialog(Context mContext, final String img) {
        qrDialog = new Dialog(mContext);
        final LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        qrDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        qrDialog.setContentView(R.layout.dialog_view_qr_code);
        qrDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = qrDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(window.getAttributes());


        qrDialog.getWindow().setGravity(Gravity.CENTER);
        qrDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        final ImageView imgQR = (ImageView) qrDialog.findViewById(R.id.imgQR);
        TextView tvCancel = (TextView) qrDialog.findViewById(R.id.tvCancel);

        Glide.with(mContext)
                .load(Base64.decode(img, Base64.DEFAULT))
                .into(imgQR);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrDialog.dismiss();
            }
        });


        qrDialog.show();
    }

}
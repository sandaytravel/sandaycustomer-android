package com.san.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowCartAddedListingBinding;
import com.san.app.font.TextViewAirbnb_medium;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.CartViewListModel;
import com.san.app.util.Pref;
import com.san.app.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.san.app.util.Utils.getThousandsNotation;

public class CartListingAdapter extends RecyclerView.Adapter<CartListingAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowCartAddedListingBinding mBinding;

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


    public CartListingAdapter(Context mContext, ArrayList<CartViewListModel.Payload> cartViewListModelArrayList) {
        this.mContext = mContext;
        payloadList = cartViewListModelArrayList;
    }

    public CartListingAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_cart_added_listing, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CartViewListModel.Payload payloadModel = payloadList.get(position);


        /*if (payloadModel.getActivityStatus().equalsIgnoreCase("Delete")) {
            mBinding.imDelete.setEnabled(false);
            mBinding.imEdit.setEnabled(false);
            mBinding.tvItemAvailibility.setVisibility(View.VISIBLE);
            mBinding.tvItemAvailibility.setText("No Longer Available");
            mBinding.lnCartView.setAlpha(0.3f);
        } else {
            mBinding.imDelete.setEnabled(true);
            mBinding.imEdit.setEnabled(true);
            mBinding.tvItemAvailibility.setVisibility(payloadModel.getActivityStatus().equalsIgnoreCase("Expire") ? View.VISIBLE : View.GONE);
            mBinding.lnCartView.setAlpha(1f);
        }*/
       // mBinding.imEdit.setVisibility(payloadModel.getFreeVoucher() == 1 ? View.GONE : View.VISIBLE);
        //mBinding.tvFreeVoucher.setVisibility(payloadModel.getFreeVoucher() == 1 ? View.VISIBLE : View.GONE);
        mBinding.imDelete.setEnabled(true);
        mBinding.imEdit.setEnabled(true);
        // mBinding.tvItemAvailibility.setVisibility(payloadModel.getActivityStatus().equalsIgnoreCase("Expire") ? View.VISIBLE : View.GONE);
        mBinding.lnCartView.setAlpha(1f);


        mBinding.tvActivityName.setText(payloadModel.getActivityTitle());
        mBinding.tvPackageSelectedName.setText(payloadModel.getPackageTitle());
        mBinding.tvPrice.setText(Utils.getRMConverter(0.6f, getThousandsNotation("" + payloadModel.getTotalPrice())));
        mBinding.tvBookingDate.setText("" + payloadModel.getBookingDate().toString().split("-")[2] + " " + Utils.monthName(Integer.parseInt(payloadModel.getBookingDate().toString().split("-")[1])) + " " + payloadModel.getBookingDate().toString().split("-")[0]);

        Glide.with(mContext).load(payloadModel.getActivityImage()).apply(new RequestOptions().placeholder(R.color.login_btn_bg).error(R.color.login_btn_bg)).into(mBinding.imgPlace);

        if (payloadModel.getQuantity().size() == 1) {
            if (payloadModel.getQuantity().get(0).getQuantity() > 0)
                mBinding.tvSelectedGenderName.setText("" + payloadModel.getQuantity().get(0).getQuantity_name() + " - " + payloadModel.getQuantity().get(0).getQuantity());
        } else {
            StringBuilder commaSepValueBuilder = new StringBuilder();
            for (int i = 0; i < payloadModel.getQuantity().size(); i++) {
                if (payloadModel.getQuantity().get(i).getQuantity() > 0) {
                    if (commaSepValueBuilder.length() > 0) commaSepValueBuilder.append(',');
                    commaSepValueBuilder.append("" + payloadModel.getQuantity().get(i).getQuantity_name() + " - " + payloadModel.getQuantity().get(i).getQuantity());
                }
            }
            mBinding.tvSelectedGenderName.setText(commaSepValueBuilder);
        }

        mBinding.imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage(R.string.are_you_sure_delete_activity).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onClickPosition.OnClickPosition(position, "");
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });

        mBinding.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", payloadModel.getActivityId());
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();*/

                Pref.setValue(mContext, "from_edit", "1");
                Pref.setValue(mContext, "oldPackageId", "" + payloadModel.getPackageMainId());
                Pref.setValue(mContext, "oldBookingDate", payloadModel.getBookingDate());

                onClickPosition.OnClickPosition(position, "Edit");
            }
        });
        mBinding.imgError.setVisibility(payloadModel.getErrors().size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.imgError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorDialog(mContext, view, position, payloadModel.getErrors());
            }
        });
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

    private void errorDialog(Context mContext, View anchorView, int pos, List<String> errors) {

        View layout = ((FragmentActivity) mContext).getLayoutInflater().inflate(R.layout.popup_content, null);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // int measuredHeight = layout.getMeasuredHeight();
        final PopupWindow popup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
// Set content width and height
// Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
// Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
//popup.showAsDropDown(anchorView,-50,-12);
        popup.showAsDropDown(anchorView);
        TextViewAirbnb_medium mErrorDetailTv = (TextViewAirbnb_medium) layout.findViewById(R.id.tvErroDetail);
        mErrorDetailTv.setText("" + errors.get(0));

    }

    public void removeAt(int position) {
        payloadList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, payloadList.size());
    }
}
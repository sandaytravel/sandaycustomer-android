package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowPopularActivitiesBinding;
import com.san.app.fragment.DetailActivitiesFragment;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.WishListModel;
import com.san.app.util.Pref;

import java.util.List;

import static com.san.app.util.Utils.getThousandsNotationReview;

public class WishListActivitiesAdapter extends RecyclerView.Adapter<WishListActivitiesAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowPopularActivitiesBinding mBinding;
    private List<WishListModel.Payload> payloadList;
    public OnClickPosition onClickPosition;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public void onClickPosition(OnClickPosition onClickPosition) {
        this.onClickPosition = onClickPosition;
    }

    public WishListActivitiesAdapter(Context mContext, List<WishListModel.Payload> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public WishListActivitiesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_popular_activities, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final WishListModel.Payload payloadModel = payloadList.get(position);
        mBinding.tvTitle.setText(payloadModel.getTitle());
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? "RM " + payloadModel.getDisplayPrice() : "RM " + payloadModel.getActualPrice());
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? payloadModel.getActualPrice() : "");
        if (!TextUtils.isEmpty(payloadModel.getDisplayPrice()))
            mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mBinding.lnReview.setVisibility(payloadModel.getTotalReview() != 0 && payloadModel.getTotalReview() != null ? View.VISIBLE : View.GONE);
        mBinding.tvTotalReview.setText("(" + payloadModel.getTotalReview() + " Reviews)");
        mBinding.tvAvgReview.setText("" + String.format("%.1f", payloadModel.getAverageReview()));
        //if(mBinding.lnReview.getVisibility() == View.VISIBLE) mBinding.tvTotalBooked.setPadding(15,0,0,0);
        mBinding.tvTotalBooked.setVisibility(payloadModel.getTotalBooked() > 0 ? View.VISIBLE : View.GONE);
        mBinding.tvTotalBooked.setText("" + getThousandsNotationReview(payloadModel.getTotalBooked()) + " Booked");
        mBinding.imgWish.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.wish_selected_img));
        mBinding.imgWish.setColorFilter(ContextCompat.getColor(mContext, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        Glide.with(mContext)
                .load(payloadModel.getImage())
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                .into(mBinding.image);

        mBinding.lnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(mContext, "from_edit", ""); //for edit cart
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", payloadModel.getId());
                fragment.setArguments(bundle);
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right );
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mBinding.rlWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public void removeAt(int position) {
        payloadList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, payloadList.size());
    }
}
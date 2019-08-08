package com.san.app.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.R;
import com.san.app.databinding.RowReviewActivityListBinding;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.util.Utils;

import java.util.ArrayList;

public class AllReviewListAdapter extends RecyclerView.Adapter<AllReviewListAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowReviewActivityListBinding mBinding;
    private int type = 0;
    private ArrayList<ViewActivityDetailModel.Review> payloadList;
    private ArrayList<String> imageList=new ArrayList<>();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public AllReviewListAdapter(Context mContext, ArrayList<ViewActivityDetailModel.Review> restaurantListModelArrayList, int type) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
        this.type = type;
    }

    public AllReviewListAdapter(ArrayList<ViewActivityDetailModel.Review> myList) {
        payloadList = myList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_review_activity_list, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ViewActivityDetailModel.Review payloadModel = payloadList.get(position);
        mBinding.view.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
        mBinding.lnReviewImages.setVisibility(payloadModel.getReviewImages().size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.tvUserName.setText(payloadModel.getCustomerName());
        mBinding.tvDesc.setText(payloadModel.getReview());
        /*try {
            mBinding.tvDesc.setText(URLDecoder.decode(payloadModel.getReview(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        mBinding.ratingBar.setRating(Float.parseFloat("" + payloadModel.getRating()));
        mBinding.tvReviewDate.setText(""+payloadModel.getReviewDate().toString().split("-")[2]+" " + Utils.monthName(Integer.parseInt(payloadModel.getReviewDate().toString().split("-")[1]))+" "+payloadModel.getReviewDate().toString().split("-")[0]);

        Glide.with(mContext)
                .load(payloadModel.getProfilePic())
                .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                        .error(R.color.login_btn_bg))
                .into(mBinding.imgUser);


        if (payloadModel.getReviewImages().size() > 0) {
            mBinding.cardFirst.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(payloadModel.getReviewImages().get(0).getResizeImage())
                    .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                            .error(R.color.login_btn_bg))
                    .into(mBinding.imageFirst);

            if (payloadModel.getReviewImages().size() > 1) {
                mBinding.cardSecond.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(payloadModel.getReviewImages().get(1).getResizeImage())
                        .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                                .error(R.color.login_btn_bg))
                        .into(mBinding.imageSecond);
            }

            if (payloadModel.getReviewImages().size() > 2) {
                mBinding.cardFirstThird.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(payloadModel.getReviewImages().get(2).getResizeImage())
                        .apply(new RequestOptions().placeholder(R.color.login_btn_bg)
                                .error(R.color.login_btn_bg))
                        .into(mBinding.imageThird);
                mBinding.tvMoreImages.setVisibility(payloadModel.getReviewImages().size() > 3 ? View.VISIBLE : View.GONE);
                mBinding.tvMoreImages.setText("+" + String.valueOf(payloadModel.getReviewImages().size() - 3));
            }

            mBinding.imageFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageList.clear();
                    for(int i=0;i<payloadModel.getReviewImages().size();i++){
                        imageList.add(payloadModel.getReviewImages().get(i).getFullsizeImage());
                    }
                    ImagePopup((Activity) mContext,imageList,0);
                }
            });

            mBinding.imageSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageList.clear();
                    for(int i=0;i<payloadModel.getReviewImages().size();i++){
                        imageList.add(payloadModel.getReviewImages().get(i).getFullsizeImage());
                    }
                    ImagePopup((Activity) mContext,imageList,1);
                }
            });

            mBinding.imageThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageList.clear();
                    for(int i=0;i<payloadModel.getReviewImages().size();i++){
                        imageList.add(payloadModel.getReviewImages().get(i).getFullsizeImage());
                    }
                    ImagePopup((Activity) mContext,imageList,2);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return type == 0 ? 1 : payloadList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void ImagePopup(Activity activity, ArrayList<String> imgeselectedList, int i) {

        ArrayList<String> ImagesArray = imgeselectedList;

        ViewPager mPager;
        final int[] currentPage = {0};
        final Dialog dialogS = new Dialog(activity);
        dialogS.setContentView(R.layout.popup_image_show);
        dialogS.setCanceledOnTouchOutside(false);
        dialogS.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialogS.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogS.show();
        dialogS.getWindow().setAttributes(lp);

        dialogS.getWindow().setGravity(Gravity.CENTER);
        dialogS.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        mPager = dialogS.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(activity, ImagesArray));
        mPager.setCurrentItem(i);


        ImageView tv_g_cancel = dialogS.findViewById(R.id.tv_close);

        tv_g_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogS.dismiss();
            }
        });

        dialogS.show();
    }
}
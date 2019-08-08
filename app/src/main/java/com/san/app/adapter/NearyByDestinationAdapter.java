package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.san.app.R;
import com.san.app.databinding.RowNearbyDestinationBinding;

public class NearyByDestinationAdapter extends RecyclerView.Adapter<NearyByDestinationAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowNearbyDestinationBinding mBinding;
    //private ArrayList<RestaurantListModel> payloadList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public MyViewHolder(View view) {
            super(view);

        }
    }


    /*public PopularDestinationAdapter(Context mContext, ArrayList<RestaurantListModel> restaurantListModelArrayList) {
        this.mContext=mContext;
        payloadList=restaurantListModelArrayList;
    }*/
   public NearyByDestinationAdapter(Context mContext) {
       this.mContext=mContext;
   }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_nearby_destination, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       // RestaurantListModel payloadModel=payloadList.get(position);
        Glide.with(mContext)
                .load("http://www.destination-asia.com/media/upload/Landing_SIngapore_1600x565.jpg.1600x565_q85.jpg")
                //.placeholder(R.mipmap.rest_placeholder_img)
                //.error(R.mipmap.rest_placeholder_img)
                .into(mBinding.image);
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
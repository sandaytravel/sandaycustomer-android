package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.san.app.R;
import com.san.app.databinding.RowPackageQuantityOptionsBinding;
import com.san.app.interfaces.OnClickPosition;
import com.san.app.model.ViewActivityDetailModel;

import java.util.List;

import static com.san.app.util.Utils.getRMConverter;
import static com.san.app.util.Utils.getThousandsNotation;

public class PackageQuantityOptionsAdapter extends RecyclerView.Adapter<PackageQuantityOptionsAdapter.MyViewHolder> {

    private Context mContext;
    private RowPackageQuantityOptionsBinding mBinding;
    private List<ViewActivityDetailModel.PackageQuantity> payloadList;
    public OnClickPosition onClickPosition;

    public void onclickPosition(OnClickPosition onClickPosition){
        this.onClickPosition=onClickPosition;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public PackageQuantityOptionsAdapter(Context mContext, List<ViewActivityDetailModel.PackageQuantity> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public PackageQuantityOptionsAdapter(Context mContext) {
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
        final ViewActivityDetailModel.PackageQuantity payloadModel = payloadList.get(position);
        Log.e("TestValiddata","1111   " + payloadModel.getName());
        mBinding.tvName.setText(payloadModel.getName());
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? getRMConverter(0.5f,getThousandsNotation(payloadModel.getDisplayPrice())) : getRMConverter(0.5f,getThousandsNotation(payloadModel.getActualPrice())));

      //  mBinding.imgMinus.setImageTintList(payloadModel.getItem_count() != null && payloadModel.getItem_count() != 0 ? ContextCompat.getColorStateList(mContext, R.color.colorPrimary) : ContextCompat.getColorStateList(mContext, R.color.gray));
       // mBinding.imgPlus.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorPrimary));

        //if(payloadModel.getItem_count() !=0){
            //onClickPosition.OnClickPosition(position, "plus");
           // Log.e("TestMatch","5555 " + payloadModel.getItem_count());
        //}

        if(payloadModel.getItem_count() != null && payloadModel.getItem_count() != 0){
            mBinding.imgMinus.setEnabled(true);
        }else{
            mBinding.imgMinus.setEnabled(false);
        }
        if(payloadModel.getItem_count() == 0) mBinding.tvCountPrice.setText(getRMConverter(0.5f,"0"));
        String newPrice=!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? payloadModel.getDisplayPrice() : payloadModel.getActualPrice();
        double optionTotlaPrice=Double.parseDouble(newPrice)*Double.parseDouble(""+payloadModel.getItem_count());
        mBinding.tvCountPrice.setText(getRMConverter(0.5f,getThousandsNotation(""+optionTotlaPrice)));
        mBinding.tvCount.setText("" + payloadModel.getItem_count());

        mBinding.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(payloadModel.getMaximumQuantity())){
                    if(payloadModel.getItem_count() < Integer.parseInt(payloadModel.getMaximumQuantity())) {
                        onClickPosition.OnClickPosition(position, "plus");
                    }else{
                        Toast.makeText(mContext, mContext.getString(R.string.you_can_add_max_item)+" "+payloadModel.getMaximumQuantity()+" "+mContext.getString(R.string.qunatity_for_item), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    onClickPosition.OnClickPosition(position, "plus");
                }

            }
        });

        mBinding.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*if(payloadModel.getItem_count() > payloadModel.getMinimumQuantity()) {
                        onClickPosition.OnClickPosition(position, "minus");
                    }else{
                        Toast.makeText(mContext, mContext.getString(R.string.you_can_add_min_item)+" "+""+payloadModel.getMinimumQuantity()+" "+mContext.getString(R.string.qunatity_for_this_pkg)+" "+payloadModel.getName()+".", Toast.LENGTH_SHORT).show();
                    }*/

                onClickPosition.OnClickPosition(position,"minus");

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
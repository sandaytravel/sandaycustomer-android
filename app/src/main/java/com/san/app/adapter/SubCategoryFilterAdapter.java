package com.san.app.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.san.app.R;
import com.san.app.databinding.RowSubcategoryFilterBinding;
import com.san.app.model.ViewCityDetailModel;

import java.util.List;

public class SubCategoryFilterAdapter extends RecyclerView.Adapter<SubCategoryFilterAdapter.MyViewHolder> {

    private Context mContext;
    private RowSubcategoryFilterBinding mBinding;
    private List<ViewCityDetailModel.Category.Subcategory> payloadList;
    private int subCategoryId = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public SubCategoryFilterAdapter(Context mContext, List<ViewCityDetailModel.Category.Subcategory> categoryModelArrayList, Integer subcategory_id) {
        this.mContext = mContext;
        payloadList = categoryModelArrayList;
        subCategoryId = subcategory_id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_subcategory_filter, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ViewCityDetailModel.Category.Subcategory payloadModel = payloadList.get(position);
        mBinding.tvSUbName.setText("" + payloadModel.getSubcategoryName());


       // mBinding.cardSub.setCardBackgroundColor(payloadModel.getSubcategoryId() == subCategoryId ? ContextCompat.getColor(mContext, R.color.colorAccent) : ContextCompat.getColor(mContext, R.color.white));
        mBinding.tvSUbName.setTextColor(payloadModel.getSubcategoryId() == subCategoryId ? ContextCompat.getColor(mContext, R.color.app_theme_dark) : ContextCompat.getColor(mContext, R.color.black_color));

        /*mBinding.tagSubCategoryName.setTags(payloadList, new DataTransform<ViewCityDetailModel.Category.Subcategory>() {
            @NotNull
            @Override
            public String transfer(ViewCityDetailModel.Category.Subcategory item) {
                return item.getSubcategoryName();
            }
        });*/

       /* mBinding.tagActiviName.setClickListener(new TagView.TagClickListener<CitiesListModel>() {
            @SuppressLint("ResourceType")
            @Override
            public void onTagClick(CitiesListModel item) {
                ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("city_id", item.city_id);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });*/
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

    public void setSubCategoryId(int subCategoryId){
        this.subCategoryId=subCategoryId;
        notifyDataSetChanged();
    }
}
package com.san.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.tommykw.tagview.DataTransform;
import com.github.tommykw.tagview.TagView;
import com.san.app.R;
import com.san.app.databinding.RowSearchCityActivityBinding;
import com.san.app.fragment.ParentCountryDetailsFragment;
import com.san.app.model.CitiesListModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchPopularDestinationAdapter extends RecyclerView.Adapter<SearchPopularDestinationAdapter.MyViewHolder> {

    //private List<Movie> restauarntList;
    private Context mContext;
    private RowSearchCityActivityBinding mBinding;
    private List<CitiesListModel> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public SearchPopularDestinationAdapter(Context mContext, List<CitiesListModel> cityModelArrayList) {
        this.mContext = mContext;
        payloadList = cityModelArrayList;
    }
   /*public AllDestinationChildAdapter(Context mContext) {
       this.mContext=mContext;
   }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_search_city_activity, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CitiesListModel payloadModel = payloadList.get(position);
        ArrayList<CitiesListModel> titleList = new ArrayList<>();
        titleList.clear();
        titleList.add(payloadModel);
        mBinding.tagActiviName.setTags(titleList, new DataTransform<CitiesListModel>() {
            @NotNull
            @Override
            public String transfer(CitiesListModel item) {
                return item.city;
            }
        });

        mBinding.tagActiviName.setClickListener(new TagView.TagClickListener<CitiesListModel>() {
            @SuppressLint("ResourceType")
            @Override
            public void onTagClick(CitiesListModel item) {
                ParentCountryDetailsFragment fragment = new ParentCountryDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("city_id", payloadModel.city_id);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame, fragment, "fragment");
                transaction.addToBackStack(null);
                transaction.commit();
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
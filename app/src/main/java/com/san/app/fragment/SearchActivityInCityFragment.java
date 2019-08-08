package com.san.app.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.tommykw.tagview.DataTransform;
import com.github.tommykw.tagview.TagView;
import com.google.gson.Gson;
import com.san.app.R;
import com.san.app.databinding.FragmentSearchActivityInCityBinding;
import com.san.app.model.ViewCityDetailModel;
import com.san.app.util.Pref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SearchActivityInCityFragment extends BaseFragment {


    //class object declaration..
    FragmentSearchActivityInCityBinding mBinding;
    View rootView;
    Context mContext;
    ViewCityDetailModel viewCityDetailModel;
    List<ViewCityDetailModel.PopularActivity> popularActivityArrayList = new ArrayList<>();
    List<ViewCityDetailModel.PopularActivity> popularActivityArrayList_Temp = new ArrayList<>();
    //variable declaration.
    private String TAG = SearchActivityInCityFragment.class.getSimpleName();
    private boolean isValid = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_search_activity_in_city, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();
            setOnClickListener();
        }
        return rootView;
    }

    private void setOnClickListener() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                getFragmentManager().popBackStack();
            }
        });

        mBinding.tagSearchActivityList.setClickListener(new TagView.TagClickListener<ViewCityDetailModel.PopularActivity>() {
            @Override
            public void onTagClick(ViewCityDetailModel.PopularActivity item) {
                Pref.setValue(mContext, "from_edit", ""); //for edit cart
                DetailActivitiesFragment fragment = new DetailActivitiesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", item.getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
                hideSoftKeyboard();
            }
        });
    }


    private void setUp() {
        Glide.with(mContext)
                .load(ContextCompat.getDrawable(mContext,R.mipmap.search_page_bg))
                .apply(new RequestOptions().placeholder(R.color.bg_white)
                        .error(R.color.bg_white))
                .into(mBinding.imgBg);

        if (getArguments() != null) {
            Gson gson = new Gson();
            viewCityDetailModel = gson.fromJson(getArguments().getString("acitivityData").toString(), ViewCityDetailModel.class);
        }
        popularActivityArrayList = viewCityDetailModel.getPayload().getPopularActivity();
        mBinding.edtSearch.setHint(getString(R.string.search_activities_in) + viewCityDetailModel.getPayload().getCity().getCity());

       searchActivity(0);

        /*mBinding.rvSearchActivityList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        }));*/

        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) searchActivity(1);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBinding.imgCloseSearch.setVisibility(View.VISIBLE);
                } else {
                    searchActivity(0);
                    mBinding.imgCloseSearch.setVisibility(View.GONE);
                    mBinding.lnNoFilterData.setVisibility(View.GONE);
                    mBinding.tagSearchActivityList.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchActivity(1);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        mBinding.imgCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.edtSearch.setText("");
                mBinding.lnNoFilterData.setVisibility(View.GONE);
                mBinding.tagSearchActivityList.setVisibility(View.VISIBLE);
                searchActivity(0);
                hideSoftKeyboard();
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void searchActivity(int type) {
        mBinding.tagSearchActivityList.clear();
        if(type == 1) {
            popularActivityArrayList_Temp.clear();

            for (int i = 0; i < popularActivityArrayList.size(); i++) {
                if (popularActivityArrayList.get(i).getTitle().toLowerCase().contains(mBinding.edtSearch.getText().toString().toLowerCase())) {
                    popularActivityArrayList_Temp.add(popularActivityArrayList.get(i));
                }
            }
            mBinding.lnNoFilterData.setVisibility(popularActivityArrayList_Temp.size() > 0 ? View.GONE : View.VISIBLE);

            //popular activity list
            mBinding.tagSearchActivityList.setTags(popularActivityArrayList_Temp, new DataTransform<ViewCityDetailModel.PopularActivity>() {
                @NotNull
                @Override
                public String transfer(ViewCityDetailModel.PopularActivity item) {
                    return item.getTitle();
                }
            });
        }else{
            mBinding.tagSearchActivityList.setTags(popularActivityArrayList, new DataTransform<ViewCityDetailModel.PopularActivity>() {
                @NotNull
                @Override
                public String transfer(ViewCityDetailModel.PopularActivity item) {
                    return item.getTitle();
                }
            });
        }



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

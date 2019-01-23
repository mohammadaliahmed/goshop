package com.clicknshop.goshop.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clicknshop.goshop.Fragments.ProductListFragment;
import com.clicknshop.goshop.Models.ChildCategoryModel;

import java.util.ArrayList;

/**
 * Created by AliAh on 20/06/2018.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    Context context;
    ArrayList<ChildCategoryModel> categoryTitle;

    public FragmentAdapter(Context context, FragmentManager fm, ArrayList<ChildCategoryModel> categoryTitle) {
        super(fm);
        this.context = context;
        this.categoryTitle = categoryTitle;
    }

    @Override
    public Fragment getItem(int position) {

        return new ProductListFragment(categoryTitle.get(position).getChildCategory());
//
    }

    @Override
    public int getCount() {
        return categoryTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return categoryTitle.get(position).getChildCategory();

    }

}

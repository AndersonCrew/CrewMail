package com.dazone.crewemail.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dazone.crewemail.activities.new_refactor.FragmentCompanyChart;
import com.dazone.crewemail.activities.new_refactor.SearchMemberFragment;

public class AdapterOrganizationPager extends FragmentPagerAdapter {

    public AdapterOrganizationPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0) {
            return new FragmentCompanyChart();
        }

        return new SearchMemberFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}

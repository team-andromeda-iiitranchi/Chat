package chat.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import chat.chat.DeveloperFragments.DevFragment;

public class DeveloperPagerAdapter extends FragmentPagerAdapter {
    public DeveloperPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        DevFragment fragment=new DevFragment();
        int id=R.drawable.default_pic;
        String name,regNo;
        switch (position)
        {
            case 0:
                id=R.drawable.shubham;
                name="Shubham Kumar";
                regNo="2017UGCS001R";
                break;
            case 1:
                id=R.drawable.shubh;
                name="Shubham Kumar Singh";
                regNo="2017UGCS051R";
                break;
            case 2:
                id=R.drawable.pankaj;
                name="Pankaj Vaghela";
                regNo="2017UGCS032R";
                break;
            case 3:
                id=R.drawable.akshansh;
                name="Akshansh Kumar Singh";
                regNo="2017UGEC053R";
                break;
            default:
                name="";
                regNo="";

        }
        Bundle b=new Bundle();
        b.putInt("id",id);
        b.putString("name",name);
        b.putString("reg",regNo);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}

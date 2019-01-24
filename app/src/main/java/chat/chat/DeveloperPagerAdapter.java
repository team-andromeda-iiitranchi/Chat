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
        String name,regNo,credit,github;
        String linkedIn="https://linkedin.com/in/";
        switch (position)
        {
            case 0:
                id=R.drawable.shubham;
                name="Shubham Kumar";
                regNo="2017UGCS001R";
                credit="App Development and Co-ordination";
                github="https://github.com/shubhamkumar1739";
                linkedIn+="shubhamkumar1739";
                break;
            case 1:
                id=R.drawable.shubh;
                name="Shubham Kumar Singh";
                regNo="2017UGCS051R";
                github="https://github.com/denyshubh";
                credit="UI & Design";
                linkedIn+="denyshubh";
                break;
            case 2:
                id=R.drawable.pankaj;
                name="Pankaj Vaghela";
                regNo="2017UGCS032R";
                github="https://github.com/pankaj-dev";
                linkedIn+="vpankaj1998dev";
                credit="UI and Notifications";
                break;
            case 3:
                id=R.drawable.jha;
                name="Abhishek Jha";
                regNo="2017UGEC009R";
                github="https://github.com/abhishekjha1997";
                credit="Presentation";
                linkedIn+="abhishek-jha-iiitr";
                break;
            default:
                name="";
                regNo="";
                github="";
                credit="";
        }
        Bundle b=new Bundle();
        b.putInt("id",id);
        b.putString("name",name);
        b.putString("reg",regNo);
        b.putString("credit",credit);
        b.putString("github",github);
        b.putString("linkedIn",linkedIn);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}

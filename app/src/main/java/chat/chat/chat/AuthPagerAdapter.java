package chat.chat.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import chat.chat.ChatApp;

public class AuthPagerAdapter extends FragmentPagerAdapter {
    public AuthPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public FragmentAuthChat fragmentAuthChat;
    public FragmentAuthNotice fragmentAuthNotice;
    public FragmentStudentChat fragmentStudentChat;
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                 fragmentAuthChat=new FragmentAuthChat();
                return fragmentAuthChat;
            case 1:
                fragmentAuthNotice=new FragmentAuthNotice();
                return fragmentAuthNotice;
            case 2:
                fragmentStudentChat=new FragmentStudentChat();
                return fragmentStudentChat;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0: return "Faculty";
            case 1: return "Notices";
            case 2: return "Students";
        }
        return null;
    }
}

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
    public AuthPagerAdapter(FragmentManager fm,AuthNotice authNotice){
        super(fm);
        this.authNotice=authNotice;
    }
    private FragmentAuthChat fragmentAuthChat;
    private FragmentAuthNotice fragmentAuthNotice;
    private FragmentStudentChat fragmentStudentChat;
    private AuthNotice authNotice;
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                 fragmentAuthChat=new FragmentAuthChat();
                 authNotice.setFragmentAuthChat(fragmentAuthChat);
                return fragmentAuthChat;
            case 1:
                fragmentAuthNotice=new FragmentAuthNotice();
                return fragmentAuthNotice;
            case 2:
                fragmentStudentChat=new FragmentStudentChat();
                authNotice.setFragmentStudentChat(fragmentStudentChat);
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

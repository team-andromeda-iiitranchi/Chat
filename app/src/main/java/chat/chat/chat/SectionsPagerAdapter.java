package chat.chat.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                ChatFragment cf=new ChatFragment();
                return cf;
            case 1:
                NoticeFragment noticeFragment=new NoticeFragment();
                return noticeFragment;
            case 2:
                PollFragment pollFragment=new PollFragment();
                return pollFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
    return 3;
    }
}
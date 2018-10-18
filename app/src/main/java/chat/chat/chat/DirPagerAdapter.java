package chat.chat.chat;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DirPagerAdapter extends FragmentPagerAdapter {
    private final AuthNotice authNotice;
    private DirFacChat dirFacChat;
    private FragmentAuthNotice fragmentAuthNotice;


    public DirPagerAdapter(FragmentManager fm, AuthNotice authNotice) {
        super(fm);
        this.authNotice=authNotice;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 1:
                dirFacChat =new DirFacChat();
                authNotice.setDirFacChat(dirFacChat);
                return dirFacChat;
            case 0:fragmentAuthNotice=new FragmentAuthNotice();
                return fragmentAuthNotice;

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Notices";
            case 1:
                return "Faculty";
        }
        return null;
    }
}

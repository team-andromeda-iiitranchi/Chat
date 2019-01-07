package chat.chat;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DeveloperActivity extends AppCompatActivity {

    private DeveloperPagerAdapter adapter;
    private ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        pager= (ViewPager) findViewById(R.id.pager);
        adapter=new DeveloperPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }
}

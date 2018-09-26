package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

import static chat.chat.chat.ChatActivity.TEMP_PHOTO_JPG;

public class OptionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ProgressDialog mProgress=new ProgressDialog(this);

        mProgress.setTitle("Loading!");
        mProgress.setMessage("Getting User's Data");
        mProgress.setCanceledOnTouchOutside(false);
        initDrawer();

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser mUser=firebaseAuth.getCurrentUser();
                if(mUser==null)
                {

                    mProgress.dismiss();
                    startActivity(new Intent(OptionsActivity.this,MainActivity.class));
                    finish();
                }
                else
                {
                    final ViewPager mViewPager = (ViewPager) findViewById(R.id.tabPager);
                    tabLayout=(TabLayout)findViewById(R.id.tabLayout);
                    DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final List votedList=new ArrayList();
                    Query q=mRef.child("Users").orderByKey().equalTo(uid);
                    q.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Users users= dataSnapshot.getValue(Users.class);
                            ChatApp.user=users;
                            ChatApp.rollInfo=users.getUsername().substring(0,8);

                            if(!(users.getCR().equalsIgnoreCase("Faculty")||users.getCR().equalsIgnoreCase("Director"))) {
                                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                                mViewPager.setAdapter(mSectionsPagerAdapter);
                                mViewPager.setOffscreenPageLimit(3);
                                tabLayout.setupWithViewPager(mViewPager);
                                mViewPager.setCurrentItem(1);


                                Map map = users.getPolls();
                                if (map != null) {
                                    Iterator iterator = map.entrySet().iterator();
                                    while (iterator.hasNext()) {
                                        Map.Entry pair = (Map.Entry) iterator.next();
                                        votedList.add(pair.getKey());
                                    }
                                }
                            }
                            else
                            {
                                startActivity(new Intent(OptionsActivity.this,AuthNotice.class));
                                finish();
                            }
                            mProgress.dismiss();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options_activits,menu);
        return true;
    }
    public void initDrawer()
    {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(OptionsActivity.this,MainActivity.class));
        }
        if(item.getItemId()==R.id.account)
        {
            Intent intent=new Intent(OptionsActivity.this,AccountActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.notices)
        {

        }
        else if(id==R.id.library)
        {
            Intent intent=new Intent(OptionsActivity.this,LibraryActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public Toolbar getToolBar()
    {
        return toolbar;
    }
    private final int IMG=0;
    private final int DOC=1;
    private EditText mMessage;
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mMessage=ChatFragment.mMessage;
        UploadHelper uploadHelper = new UploadHelper(OptionsActivity.this, mMessage,"ChatFragment");
        if (requestCode == IMG) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(OptionsActivity.this, ImageTitleActivity.class);
                Uri photo = data.getData();
                intent.putExtra("image", photo);
                intent.putExtra("context","ChatFragment");
                intent.putExtra("receiver",ChatFragment.receiver);
                uploadHelper.makeTempAndUpload(intent, photo, TEMP_PHOTO_JPG);
            }
        } else if (requestCode == DOC) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(mMessage.getText())) {
                    Uri uri = data.getData();
                    uploadHelper.makeTempAndUpload(new Intent(), uri, "temp_doc.pdf");
                } else {
                    Toast.makeText(OptionsActivity.this, "Please add a message first!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}

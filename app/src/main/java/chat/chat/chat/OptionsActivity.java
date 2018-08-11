package chat.chat.chat;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;


public class OptionsActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser mUser=firebaseAuth.getCurrentUser();
                if(mUser==null)
                {
                    startActivity(new Intent(OptionsActivity.this,MainActivity.class));
                    finish();
                }
                else
                {
                    mViewPager=(ViewPager)findViewById(R.id.tabPager);
                    tabLayout=(TabLayout)findViewById(R.id.tabLayout);
                    DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final List votedList=new ArrayList();
                    Query q=mRef.child("Users").orderByKey().equalTo(uid);
                    q.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Users users= dataSnapshot.getValue(Users.class);
                            ChatApp.rollInfo=users.getUsername().substring(0,8);

                            mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
                            mViewPager.setAdapter(mSectionsPagerAdapter);
                            mViewPager.setOffscreenPageLimit(3);
                            tabLayout.setupWithViewPager(mViewPager);
                            mViewPager.setCurrentItem(1);


                            Map map=users.getPolls();
                            if(map!=null) {
                                Iterator iterator=map.entrySet().iterator();
                                while (iterator.hasNext())
                                {
                                    Map.Entry pair= (Map.Entry) iterator.next();
                                    votedList.add(pair.getKey());
                                }
                            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_options_activits,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
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
        return  true;
    }
    public Toolbar getToolBar()
    {
        return toolbar;
    }

}

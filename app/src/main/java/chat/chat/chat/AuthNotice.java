package chat.chat.chat;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import chat.chat.ChatApp;
import chat.chat.R;

public class AuthNotice extends AppCompatActivity {
    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter adapter;
    private List<Messages> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_notice);
        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info=cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if(isConnected) {
                    Intent intent = new Intent(AuthNotice.this, NoticeComposerActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(AuthNotice.this,"Not Connected to the Internet!",Toast.LENGTH_LONG).show();
                }
            }
        });
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mList=new ArrayList<>();
        recyclerView= (RecyclerView) findViewById(R.id.recAuth);
        adapter=new MessageAdapter(mList,AuthNotice.this);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
        if(ChatApp.user.getCR().equals("faculty")) {
            loadMessages(mRef.child("Faculty").child(ChatApp.user.getUsername()).child("Notices").child("An"));
        }
        else
        {
            loadMessages(mRef.child("Director").child("Notices").child("An"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.account)
        {
            Intent intent=new Intent(AuthNotice.this,AccountActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AuthNotice.this,MainActivity.class));
            finish();
        }
        else if(item.getItemId()==R.id.archives)
        {
            startActivity(new Intent(AuthNotice.this,LibraryActivity.class));
        }
        else if(item.getItemId()==R.id.categories)
        {
            startActivity(new Intent(AuthNotice.this,CategoryViewer.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public void loadMessages(DatabaseReference mRef)
    {
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                mList.add(messages);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mList.size()-1);
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

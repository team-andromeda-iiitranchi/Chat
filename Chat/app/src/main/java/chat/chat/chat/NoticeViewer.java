package chat.chat.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import chat.chat.R;

public class NoticeViewer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Messages> mList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_viewer);


        messageAdapter=new MessageAdapter(mList,NoticeViewer.this);
        recyclerView=(RecyclerView)findViewById(R.id.noticeViewerRecycler);
        linearLayoutManager =new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        String name= getIntent().getStringExtra("Name");
        String name1=name.toLowerCase();
        if(name1.length()>1) {
            name1 = Character.toUpperCase(name1.charAt(0)) + name1.substring(1);
        }
        else
        {
            name1=name1.toUpperCase();
        }
        getSupportActionBar().setTitle(name1);
        loadMessages(name);
    }
    public void loadMessages(String name)
    {
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
        mRef.child("message").child(name).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                mList.add(messages);
                messageAdapter.notifyDataSetChanged();
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

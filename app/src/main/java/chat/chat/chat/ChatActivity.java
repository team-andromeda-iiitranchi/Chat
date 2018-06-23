package chat.chat.chat;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import chat.chat.R;

public class ChatActivity extends AppCompatActivity {
    private ImageView mSendBtn;
    private EditText mMessage;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private RecyclerView mRecyclerView;
    private MessageAdapter messageAdapter;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSendBtn=(ImageView)findViewById(R.id.send);
        mMessage=(EditText)findViewById(R.id.message);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mMessage.getText())) {
                    sendMessage(mMessage.getText().toString(), mCurrentUser.getUid());
                    mMessage.setText("");
                }

            }
        });

        mRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getInstance().getCurrentUser();


        messageAdapter=new MessageAdapter(messagesList);
        mRecyclerView=(RecyclerView)findViewById(R.id.scrollView);
        mLinearLayout=new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayout);
        mRecyclerView.setAdapter(messageAdapter);
        loadMessages();


    }

    private void loadMessages() {
        mRef.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String message,final String uid) {
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("message").push();
        String key=mDatabase.getKey();

        Map map=new HashMap();
        map.put("seen","false");
        map.put("timestamp",ServerValue.TIMESTAMP);
        map.put("text",message);
        map.put("from",uid);
        mRef.child("message").child(key).setValue(map);


    }

}

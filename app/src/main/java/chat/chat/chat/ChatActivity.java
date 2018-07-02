package chat.chat.chat;

import android.app.DownloadManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.Query;
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
    private static final int ITEMS_PER_PAGE = 10;
    private static int NO_OF_PAGES = 1    ;
    private ImageView mSendBtn;
    private EditText mMessage;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private RecyclerView mRecyclerView;
    private MessageAdapter messageAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private int itemPos=0;
    private String lastKey="",prevToLast="";
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

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NO_OF_PAGES++;
                itemPos=0;
                loadMoreMessages();
            }
        });

    }

    private void loadMoreMessages() {
        DatabaseReference mRootRef=mRef.child("message");
        Query q=mRootRef.orderByKey().endAt(lastKey).limitToLast(ITEMS_PER_PAGE);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                if(!dataSnapshot.getKey().equals(prevToLast))
                {
                    messagesList.add(itemPos++,messages);
                }
                else {
                    prevToLast=lastKey;
                }
                Log.e("KEYS MORE","LAST KEY :"+lastKey+" PREV_TO_LAST :"+prevToLast+" CURRENT :"+dataSnapshot.getKey());
                if(itemPos==1)
                {
                    lastKey=dataSnapshot.getKey();

                }
                messageAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
                swipeRefreshLayout.setRefreshing(false);

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

    private void loadMessages() {
        DatabaseReference mRootRef=mRef.child("message");
        Query q=mRootRef.limitToLast(ITEMS_PER_PAGE*NO_OF_PAGES);
                q.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        itemPos++;
                        if(itemPos==1)
                        {
                            lastKey=dataSnapshot.getKey();
                            prevToLast=lastKey;
                        }
                        Log.e("KEYS","LAST KEY :"+lastKey+" PREV_TO_LAST :"+prevToLast+" CURRENT :"+dataSnapshot.getKey());
                        messageAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(messagesList.size() - 1);
                        swipeRefreshLayout.setRefreshing(false);
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

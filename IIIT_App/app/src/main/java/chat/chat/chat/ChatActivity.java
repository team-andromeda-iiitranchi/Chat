package chat.chat.chat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;
import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

public class ChatActivity extends AppCompatActivity {
    private static final int ITEMS_PER_PAGE = 10;
    public static final String TEMP_PHOTO_JPG = "temp_photo.jpg";
    private static int NO_OF_PAGES = 1    ;
    private ImageView mSendBtn;
    private EditText mMessage;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private RecyclerView mRecyclerView;
    private MessageAdapter messageAdapter;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private LinearLayout bar;
    private DatabaseReference mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //back option
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatActivity.this, OptionsActivity.class);
                startActivity(i);
                finish();
            }
        });

        mSendBtn = (ImageView) findViewById(R.id.send);
        mMessage = (EditText) findViewById(R.id.message);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    if (!TextUtils.isEmpty(mMessage.getText())) {
                        final Messages messages = new Messages();
                        messages.setText(mMessage.getText().toString());
                        sendMessage(messages, mCurrentUser.getUid());
                        mMessage.setText("");
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }

            }

        });
        mSendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    PickerDialogFragment pickerDialogFragment = new PickerDialogFragment();
                    pickerDialogFragment.show(getFragmentManager(), "picker");

                } else {
                    Toast.makeText(ChatActivity.this, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getInstance().getCurrentUser();


        //bar visibility changes
        bar = (LinearLayout) findViewById(R.id.bar);
        mRef.child("Users").child(mCurrentUser.getUid().toString()).child("CR").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String isCR = dataSnapshot.getValue().toString();
                if (!isCR.equals("false")) {
                    bar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //loading messages through adapter
        messageAdapter = new MessageAdapter(messagesList, ChatActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.scrollView);
        mLinearLayout = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayout);
        mRecyclerView.setAdapter(messageAdapter);
        if (ChatApp.rollInfo == null)
        {
            startActivity(new Intent(ChatActivity.this,OptionsActivity.class));
        }
            mDb=mRef.child(ChatApp.rollInfo).child("message").child("An");
        mDb.keepSynced(true);
        loadMessages(mDb);

        }

    private void loadMessages(DatabaseReference mRootRef) {
        mRootRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages=dataSnapshot.getValue(Messages.class);
                        if(messages!=null) {
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(messagesList.size() - 1);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        DataSnapshot dataSnapshot1 = dataSnapshot;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            dataSnapshot1 = data;
                        }
                        Messages messages = dataSnapshot1.getValue(Messages.class);
                        Messages messages1=messagesList.get(messagesList.size()-1);

                        if(!messages.isEqual(messages1)) {
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(messagesList.size()-1);
                        }
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


    private void sendMessage(final Messages message,final String uid) {

        List<String> categ;
        categ=message.getHashTag();
        long timestamp=System.currentTimeMillis();
        int count=0;//to check if message has been pushed
        for(int i=0;i<categ.size();i++) {
            String ctgry=categ.get(i);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("message").child(ctgry).push();
            String key = mDatabase.getKey();

            Map map = new HashMap();
            map.put("timestamp", timestamp);
            map.put("text", message.getText());
            map.put("from", uid);
            map.put("type","null");
            map.put("sender","Student");
            map.put("link","default");
            mRef.child(ChatApp.rollInfo).child("message").child(ctgry).child(key).setValue(map);
            count++;
        }
        //Message has not been sent due to improper hashtag
        if(count==0)
        {
            Toast.makeText(this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }

    }

    private final int IMG=0;
    private final int DOC=1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UploadHelper uploadHelper=new UploadHelper(ChatActivity.this,mMessage,"ChatActivity");
        if(requestCode==IMG)
        {
            if(resultCode==RESULT_OK)
            {
                Intent intent=new Intent(ChatActivity.this,ImageTitleActivity.class);
                Uri photo=(Uri)data.getData();
                intent.putExtra("image",photo);
                intent.putExtra("context","ChatActivity");
                uploadHelper.makeTempAndUpload(intent,photo,TEMP_PHOTO_JPG);
                finish();
            }
        }
        else if(requestCode==DOC)
        {
            if(resultCode==RESULT_OK)
            {
                if(!TextUtils.isEmpty(mMessage.getText())) {
                    Uri uri = data.getData();
                    uploadHelper.makeTempAndUpload(new Intent(), uri, "temp_doc.pdf");
                }
                else
                {
                    Toast.makeText(this, "Please add a message first!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        Intent i=new Intent(ChatActivity.this,OptionsActivity.class);
        startActivity(i);
        finish();
    }
    
}

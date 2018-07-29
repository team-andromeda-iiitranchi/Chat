package chat.chat.chat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
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
                Intent i=new Intent(ChatActivity.this,OptionsActivity.class);
                startActivity(i);
                finish();
            }
        });

        mSendBtn=(ImageView)findViewById(R.id.send);
        mMessage=(EditText)findViewById(R.id.message);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mMessage.getText())) {
                    final Messages messages=new Messages();
                    messages.setText(mMessage.getText().toString());
                    sendMessage(messages, mCurrentUser.getUid());
                    mMessage.setText("");
                }

            }

        });
        mSendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PickerDialogFragment pickerDialogFragment=new PickerDialogFragment();
                pickerDialogFragment.show(getFragmentManager(),"picker");
                return true;
            }
        });

        mRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getInstance().getCurrentUser();


        //bar visibility changes
        bar=(LinearLayout)findViewById(R.id.bar);
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
        messageAdapter=new MessageAdapter(messagesList,ChatActivity.this);
        mRecyclerView=(RecyclerView)findViewById(R.id.scrollView);
        mLinearLayout=new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayout);
        mRecyclerView.setAdapter(messageAdapter);
        loadMessages(mRef.child("message"));

        }

    private void loadMessages(DatabaseReference mRootRef) {
        mRootRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            Messages messages=dataSnapshot1.getValue(Messages.class);
                            addSort(messages);

                        }


                        /*Collections.sort(messagesList, new Comparator<Messages>() {
                            @Override
                            public int compare(Messages lhs, Messages rhs) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                return lhs.getTimestamp() > rhs.getTimestamp() ? 1 : (lhs.getTimestamp() < rhs.getTimestamp()) ? -1 : 0;
                            }
                        });*/
                        messageAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(messagesList.size()-1);
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

    public void addSort(Messages messages)
    {
        if(messagesList.isEmpty())
        {
            messagesList.add(messages);
        }
        else
        {
            int count=0;
            for(int i=0;i<messagesList.size();i++)
            {
                int comp=messages.compareTo(messagesList.get(i));
                if(comp>0)
                {
                    messagesList.add(i,messages);
                    count++;
                    break;
                }
                else if(comp==0)
                {
                    count++;
                    break;
                }
            }
            if(count==0)
            {
                messagesList.add(messages);
            }
        }
    }

    private void sendMessage(final Messages message,final String uid) {

        List<String> categ=new ArrayList<>();
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
            map.put("link","default");
            mRef.child("message").child(ctgry).child(key).setValue(map);
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
        if(requestCode==IMG)
        {
            if(resultCode==RESULT_OK)
            {
                Intent intent=new Intent(ChatActivity.this,ImageTitleActivity.class);
                Uri photo=(Uri)data.getData();
                intent.putExtra("image",photo);

                String state = Environment.getExternalStorageState();
                File mFileTemp;
                if(Environment.MEDIA_MOUNTED.equals(state)){
                    mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_JPG);

                }else{
                    mFileTemp = new File(getFilesDir(), TEMP_PHOTO_JPG);
                }
                if(!mFileTemp.exists()){
                    mFileTemp.mkdirs();
                }


                try {
                    InputStream io = getContentResolver().openInputStream(photo);
                    FileOutputStream fo = new FileOutputStream(mFileTemp);

                    copyStream(io,fo);

                    fo.close();
                    io.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                intent.putExtra("image_path",mFileTemp.getPath());
                startActivity(intent);
            }
        }
    }

    private void copyStream(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer))!= -1){
            output.write(buffer,0,bytesRead);
        }
    }

}

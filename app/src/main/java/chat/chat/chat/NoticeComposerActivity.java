package chat.chat.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

public class NoticeComposerActivity extends AppCompatActivity implements ChooserDialog.ChooserDialogListener{
    private EditText mMessage;
    private Button mSendBtn,mDocSend;
    static String mList[];
    String text;
    private List<String> selectedSections=new ArrayList<>(),selectedFaculty=new ArrayList<>();
    int facultyCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_composer);
        getSupportActionBar().setTitle("Notice");

        mMessage= (EditText) findViewById(R.id.notice);
        mSendBtn= (Button) findViewById(R.id.sendBtn);
        mDocSend=(Button) findViewById(R.id.docSendBtn);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initList();
            }
        });
        mDocSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        facultyCount=0;
    }
    public void initList()
    {
        final List<String> list=new ArrayList<>();
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("Sections");

        mRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                for(MutableData d:mutableData.getChildren())
                {
                    list.add(d.getKey());
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Faculty");
                    reference.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            for (MutableData d : mutableData.getChildren()) {
                                if(!d.getKey().equals(ChatApp.user.getUsername())) {
                                    list.add(d.getKey());
                                }
                            }

                            return Transaction.success(mutableData);
                        }


                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            mList = new String[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                mList[i] = list.get(i);
                            }
                            ChooserDialog c = new ChooserDialog();
                            c.show(getFragmentManager(), "ChooserDialog");
                        }
                    });

            }
        });


    }

    @Override
    public void onPositiveButtonClicked(String[] mList, List<Integer> selectedItems) {
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
        long timestamp=System.currentTimeMillis();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String sender=ChatApp.user.getCR();
        Map map=new HashMap();
        text=mMessage.getText().toString();
        mMessage.setText("");
        map.put("text",text);
        map.put("from",uid);
        map.put("sender",sender);
        map.put("link","default");
        map.put("type","null");
        map.put("timestamp",timestamp);
        Messages messages=new Messages(uid,text,sender,"default","null",timestamp);
        List<String> categ=messages.getHashTag();
        for(int i=0;i<selectedItems.size();i++)
        {
            String str=mList[selectedItems.get(i)];
            if(str.indexOf("fac")!=-1)
            {
                ifFaculty(mDatabase,str,categ,map);
            }
            else
            {
                ifSection(mDatabase,str,categ,map);
            }
        }
        if(ChatApp.user.getCR().equals("director"))
        {
            for(int i=0;i<categ.size();i++) {
                String ctgry=categ.get(i);
                String key=mDatabase.child("Director").child("Notices").child(ctgry).push().getKey();
                mDatabase.child("Director").child("Notices").child(ctgry).child(key).setValue(map);
            }
        }
        if(ChatApp.user.getCR().equals("faculty"))
        {
            for(int i=0;i<categ.size();i++) {
                String ctgry=categ.get(i);
                String key=mDatabase.child("Faculty").child(ChatApp.user.getUsername()).child("Notices").child(ctgry).push().getKey();
                mDatabase.child("Faculty").child(ChatApp.user.getUsername()).child("Notices").child(ctgry).child(key).setValue(map);
            }
        }
        startActivity(new Intent(NoticeComposerActivity.this,AuthNotice.class));
        finish();
    }
    public void ifFaculty(DatabaseReference mDatabase, String str, List<String> categ, Map map)
    {
        int count=0;
        for(int i=0;i<categ.size();i++)
        {
            String ctgry=categ.get(i);
            String key=mDatabase.child("Faculty").child(str).child("Notices").child(ctgry).push().getKey();
            mDatabase.child("Faculty").child(str).child("Notices").child(ctgry).child(key).setValue(map);
            count++;
        }
        if(count==0)
        {
            Toast.makeText(NoticeComposerActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }
    }
    public void ifSection(DatabaseReference mDatabase, String str, List<String> categ, Map map)
    {
        int count=0;
        for(int i=0;i<categ.size();i++)
        {
            String ctgry=categ.get(i);
            String key=mDatabase.child(str).child("message").child(ctgry).push().getKey();
            mDatabase.child(str).child("message").child(ctgry).child(key).setValue(map);
            count++;
        }
        if(count==0)
        {
            Toast.makeText(NoticeComposerActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }
    }
}

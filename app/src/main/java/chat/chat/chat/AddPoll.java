package chat.chat.chat;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import chat.chat.ChatApp;
import chat.chat.R;

public class AddPoll extends AppCompatActivity {
    private Button mAddBtn,mAddField;
    private EditText title,description;
    private String titleStr,descriptionStr;
    private List mList=new ArrayList();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FieldAdapter fieldAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poll);
        mAddBtn=(Button)findViewById(R.id.addBtn);
        title=(EditText)findViewById(R.id.title) ;
        description=(EditText)findViewById(R.id.description);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        recyclerView=(RecyclerView)findViewById(R.id.fieldRecycler);
        fieldAdapter=new FieldAdapter(mList,recyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fieldAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAddField=(Button)findViewById(R.id.addField);


        mAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.edt_text_layout,recyclerView,false);
                mList.add(mList.size(),relativeLayout);
                fieldAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mList.size()-1);
            }
        });



        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    mAddBtn.setOnClickListener(null);
                    List list = new ArrayList();
                    int count = 0;
                    for (int i = 0; i < mList.size(); i++) {
                        RelativeLayout relativeLayout = (RelativeLayout) mList.get(i);
                        EditText mEdit = (EditText) relativeLayout.findViewById(R.id.edtField);
                        if (TextUtils.isEmpty(mEdit.getText())) {
                            Toast.makeText(getApplicationContext(), "All fields must be filled!", Toast.LENGTH_LONG).show();
                            count++;
                            break;
                        } else {
                            list.add(mEdit.getText().toString());

                        }
                    }
                    titleStr = title.getText().toString();
                    descriptionStr = description.getText().toString();
                    if (!(mList.size() == 0 || mList.size() == 1 || count != 0) && !TextUtils.isEmpty(titleStr) && !TextUtils.isEmpty(descriptionStr)) {
                        setPoll(titleStr, descriptionStr, list);
                    } else if (mList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Add more fields!", Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(AddPoll.this, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void setPoll(final String titleStr, final String descriptionStr, final List list) {
        final long timestamp=System.currentTimeMillis();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        Query q=databaseReference.child("Sections").child(ChatApp.rollInfo);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long count =dataSnapshot.getValue(Long.class);
                    count++;
                Poll poll = new Poll(titleStr, descriptionStr, timestamp, count, list);
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mRootRef = mRef.child(ChatApp.rollInfo).child("Poll").push();
                final String key = mRootRef.getKey();
                mRef.child(ChatApp.rollInfo).child("Poll").child(key).setValue(poll);
                Toast.makeText(getApplicationContext(), "Successfully added the poll!", Toast.LENGTH_LONG).show();
                Intent i=new Intent(AddPoll.this, OptionsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
                addPerUser(key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void addPerUser(final String key)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key1=dataSnapshot.getKey();
                DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(key1).child("polls");
                Map map=new HashMap();
                map.put(key,"0");
                mDatabase.updateChildren(map);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

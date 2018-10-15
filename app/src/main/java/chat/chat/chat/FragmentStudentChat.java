package chat.chat.chat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStudentChat extends Fragment {

    static int state=1;
    public FragmentStudentChat() {

    }
    private View view;
    private LayoutInflater inflater;
    private RelativeLayout relativeLayout;
    private View inflatedView;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private DatabaseReference mRef;
    private String nameStr;
    private MessageAdapter messageAdapter;
    private List<Messages> mList;
    private LinearLayoutManager linearLayoutManager;
    int counter=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_student_chat, container, false);
        relativeLayout= (RelativeLayout) view.findViewById(R.id.relLayout);
        this.inflater=inflater;

        mRef= FirebaseDatabase.getInstance().getReference();

        listState();

        return view;
    }
    public void listState()
    {
        inflatedView=inflater.inflate(R.layout.sections_layout,null,false);
        relativeLayout.addView(inflatedView);
        linearLayout= (LinearLayout) inflatedView.findViewById(R.id.linearLayout);
        populateLinearLayout(mRef.child("Sections"));
    }
    public void populateLinearLayout(DatabaseReference mRef)
    {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dSnap:dataSnapshot.getChildren())
                {
                    String key=dSnap.getKey();
                    inflateSection(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void inflateSection(final String name)
    {
        View view=inflater.inflate(R.layout.each_section_layout,null,false);
        TextView textView= (TextView) view.findViewById(R.id.nameView);
        textView.setText(name);


        //setting onClickLstener

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameStr=name;
                toggle();
            }
        });


        linearLayout.addView(view);
    }
    public void toggle()
    {
        relativeLayout.removeAllViews();
        if(state==1)
        {
            state=2;
            backButtonEnable();
            chatState();
        }
        else
        {
            state=1;
            listState();
        }
    }

    private void backButtonEnable() {
        final AuthNotice authNotice= (AuthNotice) getActivity();
        authNotice.getSupportActionBar().setDisplayShowHomeEnabled(true);
        authNotice.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar=authNotice.getToolbar();

        //set on clickListener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                authNotice.getSupportActionBar().setDisplayShowHomeEnabled(false);
                authNotice.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });
    }

    public void chatState()
    {
        inflatedView=inflater.inflate(R.layout.other_chat_fragment,null,false);
        relativeLayout.addView(inflatedView);

        //Setting up recyclerView
        mList=new ArrayList<>();
        recyclerView= (RecyclerView) inflatedView.findViewById(R.id.recView2);
        messageAdapter=new MessageAdapter(mList,getActivity());
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        loadMessages(nameStr);

        //Declaring messageField and send button
        final EditText messageView= (EditText) inflatedView.findViewById(R.id.message);
        ImageView sendBtn=(ImageView) inflatedView.findViewById(R.id.send);


        //setting send button functionality
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageView);
            }
        });

    }
    public void sendMessage(TextView messageView)
    {
        if(!TextUtils.isEmpty(messageView.getText()))
        {
            Map map=new HashMap();
            map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("link","default");
            map.put("sender",ChatApp.user.getCR());
            map.put("text",messageView.getText().toString());
            map.put("type","null");
            map.put("timestamp",System.currentTimeMillis());

            messageView.setText("");

            String key=mRef.child(nameStr).child("CR").child(ChatApp.user.getUsername()).push().getKey();
            mRef.child(nameStr).child("CR").child(ChatApp.user.getUsername()).child(key).setValue(map);

            //if this is the first message
            //then set a listener at the child
            if(counter==0)
            {
                loadMessages(nameStr);
            }
        }
    }
    public void loadMessages(String name)
    {
        final String usrname=ChatApp.user.getUsername();
        final DatabaseReference mDatabase=mRef.child(name).child("CR");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(usrname))
                {
                    counter=1;
                    mDatabase.child(usrname).addChildEventListener(new ChildEventListener() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package chat.chat.chat;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

import chat.chat.ChatApp;
import chat.chat.R;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStudentChat extends Fragment {

    static int state=1;
    private EditText messageView;
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
    private AuthNotice authNotice;
    private List<Messages> mList;
    private String currentUid;
    private LinearLayoutManager linearLayoutManager;
    int counter=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_student_chat, container, false);
        relativeLayout= (RelativeLayout) view.findViewById(R.id.relLayout);
        this.inflater=inflater;

        authNotice=(AuthNotice)getActivity();

        currentUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
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
    public void populateLinearLayout(final DatabaseReference mRef)
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
        final TextView textView= (TextView) view.findViewById(R.id.nameView);
        textView.setText(name);
        //check for unseen messages
        mRef.child("lastSeen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUid))
                {
                    mRef.child("lastSeen").child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(name))
                            {
                                //if unseen messages from that section
                                if(dataSnapshot.child(name).child("unseen").getValue().toString().equals("1"))
                                {
                                    //change message tab colour
                                    textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                }
                            }
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
            backButtonDisable();
            state=1;
            listState();
        }
    }

    private void backButtonDisable() {
        authNotice.getSupportActionBar().setDisplayShowHomeEnabled(false);
        authNotice.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void backButtonEnable() {
        authNotice.getSupportActionBar().setDisplayShowHomeEnabled(true);
        authNotice.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar=authNotice.getToolbar();

        //set on clickListener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    public void chatState()
    {
        inflatedView=inflater.inflate(R.layout.other_chat_fragment,null,false);
        relativeLayout.addView(inflatedView);


        //set messages to seen
        mRef.child("lastSeen").child(currentUid).child(nameStr).child("unseen").setValue(0);

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
        messageView= (EditText) inflatedView.findViewById(R.id.message);
        ImageView sendBtn=(ImageView) inflatedView.findViewById(R.id.send);

        //setting send button functionality
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(messageView.getText())) {
                    final String text=messageView.getText().toString();
                    messageView.setText("");
                    sendMessage("default", "null", text, System.currentTimeMillis());
                }
            }
        });
        sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    PickerDialogFragment pickerDialogFragment = new PickerDialogFragment();
                    pickerDialogFragment.show(getActivity().getFragmentManager(), "picker");
                    authNotice.setChoice(AuthNotice.STUDENT);

                } else {
                    Toast.makeText(getActivity(), "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }
    public void sendMessage(String link, String type,String text,Long timestamp)
    {

            Map map=new HashMap();
            map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("link",link);
            map.put("sender",ChatApp.user.getCR());
            map.put("text",text);
            map.put("type",type);
            map.put("timestamp",timestamp);

            messageView.setText("");

            String key=mRef.child(nameStr).child("CR").child(ChatApp.user.getUsername()).push().getKey();
            mRef.child(nameStr).child("CR").child(ChatApp.user.getUsername()).child(key).setValue(map);

            //set seen status
            setUnseen();

            //if this is the first message
            //then set a listener at the child
            if(counter==0)
            {
                loadMessages(nameStr);
            }

    }

    private void setUnseen() {
        //set unseen node to 1
        Query q=mRef.child("Users");
        q.orderByChild("CR").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    String uid=d.getKey();
                    String name=d.child("username").getValue().toString();
                    //getting roll id of CRs and comparing them with target
                    if(name.substring(0,8).equals(nameStr))
                    {
                        mRef.child("lastSeen").child(uid).child(currentUid).child("unseen").setValue(1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadMessages(String name)
    {
        final String usrname=ChatApp.user.getUsername();
        final DatabaseReference mDatabase=mRef.child(name).child("CR");
        mRef.child("lastSeen").child(currentUid).child(name).child("unseen").setValue(0);
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
                            if(mList.size()==0||mList.get(mList.size()-1).getTimestamp()!=messages.getTimestamp()) {
                                mList.add(messages);
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(mList.size() - 1);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

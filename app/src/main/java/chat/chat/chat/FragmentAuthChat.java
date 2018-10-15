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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAuthChat extends Fragment {

    private View view;
    static int state=1;
    private DatabaseReference mRef;
    private String currentUser;
    private View inflatedLayout;
    private LayoutInflater inflater;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private String nameStr;
    private List<Messages> mList;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    int counter=0;

    public FragmentAuthChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_auth_chat, container, false);
        this.inflater=inflater;
        relativeLayout= (RelativeLayout) view.findViewById(R.id.authRelLayout);
        //init firebase database
        mRef= FirebaseDatabase.getInstance().getReference();
        currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

        listState();

        return view;
    }

    private void listState() {

        inflatedLayout=inflater.inflate(R.layout.auth_chat_layout,null,false);
        relativeLayout.addView(inflatedLayout);
        
        
        linearLayout = (LinearLayout) inflatedLayout.findViewById(R.id.linearLayout);

        Query q=mRef.child("Users");
        q.orderByChild("CR").equalTo("faculty").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.child("username").getValue().toString().equals(ChatApp.user.getUsername())) {
                    final View user = inflater.inflate(R.layout.all_users_layout, null, false);
                    linearLayout.addView(user);
                    TextView displ = (TextView) user.findViewById(R.id.displayName);
                    CircleImageView pic = (CircleImageView) user.findViewById(R.id.picture);
                    final String name=dataSnapshot.child("Name").getValue().toString();
                    String image=dataSnapshot.child("imageLink").getValue().toString();
                    final String username=dataSnapshot.child("username").getValue().toString();
                    setUserData(displ, pic,name,image);

                    user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nameStr=username;
                            toggle();
                        }
                    });

                    Query q=mRef.child("Faculty").child(ChatApp.user.getUsername()).child(username);
                    q.orderByChild("seen").equalTo("0").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null)
                            {
                                user.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

    private void chatState() {
        inflatedLayout=inflater.inflate(R.layout.other_chat_fragment,null,false);
        relativeLayout.addView(inflatedLayout);

        //Setting up recyclerView
        mList=new ArrayList<>();
        recyclerView= (RecyclerView) inflatedLayout.findViewById(R.id.recView2);
        messageAdapter=new MessageAdapter(mList,getActivity());
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        loadMessages(nameStr);

        //Declaring messageField and send button
        final EditText messageView= (EditText) inflatedLayout.findViewById(R.id.message);
        ImageView sendBtn=(ImageView) inflatedLayout.findViewById(R.id.send);


        //setting send button functionality
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageView);
            }
        });


    }

    private void sendMessage(EditText messageView) {
        if(!TextUtils.isEmpty(messageView.getText()))
        {
            Map map=new HashMap();
            map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("link","default");
            map.put("sender",ChatApp.user.getCR());
            map.put("text",messageView.getText().toString());
            map.put("type","null");
            map.put("timestamp",System.currentTimeMillis());
            map.put("seen","0");

            messageView.setText("");

            String key=mRef.child("Faculty").child(nameStr).child(ChatApp.user.getUsername()).push().getKey();
            mRef.child("Faculty").child(nameStr).child(ChatApp.user.getUsername()).child(key).setValue(map);

            map.put("seen","1");

            key=mRef.child("Faculty").child(ChatApp.user.getUsername()).child(nameStr).push().getKey();
            mRef.child("Faculty").child(ChatApp.user.getUsername()).child(nameStr).child(key).setValue(map);


            //if this is the first message
            //then set a listener at the child
            if(counter==0)
            {
                loadMessages(nameStr);
            }
        }
    }

    private void loadMessages(final String nameStr) {

        final DatabaseReference mDatabase=mRef.child("Faculty").child(ChatApp.user.getUsername());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(nameStr))
                {
                    counter=1;
                    mDatabase.child(nameStr).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Messages messages=dataSnapshot.getValue(Messages.class);
                            if(mList.size()==0||messages.timestamp!=mList.get(mList.size()-1).timestamp) {
                                mList.add(messages);
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(mList.size() - 1);
                                String key=dataSnapshot.getKey();
                                Map map=new HashMap();
                                map.put("seen","1");
                                mRef.child("Faculty").child(ChatApp.user.getUsername()).child(nameStr).child(key).updateChildren(map);
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


    private void setUserData(TextView displ, CircleImageView pic, String name, String image) {
        displ.setText(name);
        Picasso.get().load(image).placeholder(R.drawable.default_pic).into(pic);
    }

}

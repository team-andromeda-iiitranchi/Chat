package chat.chat.chat;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static chat.chat.chat.AuthNotice.DIR;
import static chat.chat.chat.AuthNotice.FAC;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacDirChat extends Fragment {


    private View view;
    private LayoutInflater inflater;
    private View inflatedLayout;
    private RelativeLayout relativeLayout;
    private ArrayList<Messages> mList;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRef;
    private int counter=0;
    private EditText messageView;
    private AuthNotice authNotice;

    public FacDirChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_auth_chat, container, false);
        this.inflater=inflater;
        authNotice=(AuthNotice)getActivity();
        relativeLayout= (RelativeLayout) view.findViewById(R.id.authRelLayout);
        mRef= FirebaseDatabase.getInstance().getReference();
        chatState();
        return view;
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

        loadMessages();

        //Declaring messageField and send button
        messageView= (EditText) inflatedLayout.findViewById(R.id.message);
        ImageView sendBtn=(ImageView) inflatedLayout.findViewById(R.id.send);



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
                    authNotice.setChoice(FAC);

                } else {
                    Toast.makeText(getActivity(), "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


    }

    public void sendMessage(String link,String type,String text,Long timestamp) {
        String seen="seen";
        Map map=new HashMap();
        map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("link",link);
        map.put("sender", ChatApp.user.getCR());
        map.put("text",text);
        map.put("type",type);
        map.put("timestamp",timestamp);
        map.put(seen,"1");

        String key=mRef.child("Faculty").child(ChatApp.user.getUsername()).child("Director").push().getKey();
        mRef.child("Faculty").child(ChatApp.user.getUsername()).child("Director").child(key).setValue(map);

        map.put(seen,"0");

        key=mRef.child("Director").child(ChatApp.user.getUsername()).push().getKey();
        mRef.child("Director").child(ChatApp.user.getUsername()).child(key).setValue(map);


        //if this is the first message
        //then set a listener at the child
        if(counter==0)
        {
            loadMessages();
        }

    }

    private void loadMessages() {

        final DatabaseReference mDatabase=mRef.child("Faculty");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(ChatApp.user.getUsername())&&dataSnapshot.child(ChatApp.user.getUsername()).hasChild("Director"))
                {
                    counter=1;
                    mDatabase.child(ChatApp.user.getUsername()).child("Director").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Messages messages=dataSnapshot.getValue(Messages.class);
                            if(mList.size()==0||messages.timestamp!=mList.get(mList.size()-1).timestamp) {
                                mList.add(messages);
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(mList.size() - 1);
                                String key=dataSnapshot.getKey();
                                if(messages.getType()!=null)
                                    mRef.child("Faculty").child(ChatApp.user.getUsername()).child("Director").child(key).child("seen").setValue("1");
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

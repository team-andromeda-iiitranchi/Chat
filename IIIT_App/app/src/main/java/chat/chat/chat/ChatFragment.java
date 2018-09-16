package chat.chat.chat;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static chat.chat.chat.ChatActivity.TEMP_PHOTO_JPG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private UserAdapter mUserAdapter;
    private RecyclerView recyclerView,mRecyclerView;
    private MessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRef;
    private ImageView mSendBtn;
    public static EditText mMessage;
    private View inflatedLayout;
    private RelativeLayout relativeLayout;
    private final List<Users> usersList=new ArrayList<>();
    private final List<Messages> messagesList=new ArrayList<>();
    private View mView;
    static String receiver;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view;
        view=inflater.inflate(R.layout.fragment_chat,container,false);
        mView=view;
        FirebaseUser mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        relativeLayout=(RelativeLayout)view.findViewById(R.id.container_layout);
        if(mCurrentUser!=null) {
            mRef = FirebaseDatabase.getInstance().getReference();
            final String uid=mCurrentUser.getUid();
            final DatabaseReference databaseReference=mRef.child("Users").child(uid).child("CR");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String isCR = dataSnapshot.getValue().toString();
                    if (isCR.equals("true")) {
                        inflateForCR(view);
                    } else {
                        inflateForOthers(view);
                    }
                    Log.e("INFLATE : ", "CR update " + isCR);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return view;
    }

    private void inflateForOthers(View view) {
        //Inflation
        LayoutInflater inflater1=LayoutInflater.from(getContext());
        inflatedLayout=inflater1.inflate(R.layout.other_chat_fragment,null,false);
        relativeLayout.addView(inflatedLayout);
        messageAdapter=new MessageAdapter(messagesList,getActivity());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView=(RecyclerView)inflatedLayout.findViewById(R.id.recView2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(messageAdapter);
        final FirebaseUser mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        loadMessages(mRef.child(ChatApp.rollInfo).child("CR").child("messages").child(mCurrentUser.getUid()));
        receiver=mCurrentUser.getUid();
        if(mCurrentUser!=null) {
            mSendBtn = (ImageView) inflatedLayout.findViewById(R.id.send);
            mMessage = (EditText) inflatedLayout.findViewById(R.id.message);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    boolean isConnected = info != null && info.isConnectedOrConnecting();
                    if (isConnected) {
                        if (!TextUtils.isEmpty(mMessage.getText())) {
                            sendMessage(mMessage.getText().toString(), mCurrentUser.getUid());
                            mMessage.setText("");
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mSendBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    boolean isConnected = info != null && info.isConnectedOrConnecting();
                    if (isConnected) {
                        PickerDialogFragment pickerDialogFragment = new PickerDialogFragment();
                        pickerDialogFragment.show(getActivity().getFragmentManager(), "picker");
                    }
                    else {
                        Toast.makeText(getActivity(), "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

    }

    private void loadMessages(DatabaseReference mRootRef) {
        FirebaseUser mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentUser!=null) {
            mRootRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    if(messagesList.isEmpty())
                    {
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }
                    else if(!messages.isEqual(messagesList.get(messagesList.size()-1))) {
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }
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
    }

    private void inflateForCR(View view) {
        //Inflation
        LayoutInflater inflater1=LayoutInflater.from(getActivity());
        inflatedLayout=inflater1.inflate(R.layout.cr_chat_layout,null,false);
        relativeLayout.addView(inflatedLayout);

        //setting adapter for relative layout
        mUserAdapter = new UserAdapter(usersList,ChatFragment.this);
        recyclerView = (RecyclerView) inflatedLayout.findViewById(R.id.recView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mUserAdapter);
        loadUsers();
    }
    public void inflateForCROnItemClicked(final String uid)
    {
        receiver=uid;
        messagesList.clear();
        //Add back button on toolbar
        final OptionsActivity optionsActivity=(OptionsActivity)getActivity();
        final android.support.v7.widget.Toolbar toolbar=optionsActivity.getToolBar();
        final ActionBar actionBar=optionsActivity.getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //Add listener to toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersList.clear();
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                ((ViewGroup)inflatedLayout.getParent()).removeView(inflatedLayout);


                optionsActivity.initDrawer();


                inflateForCR(mView);
            }
        });


        //Inflate Chat Page
        ((ViewGroup)inflatedLayout.getParent()).removeView(inflatedLayout);
        LayoutInflater inflater1=LayoutInflater.from(getContext());
        inflatedLayout=inflater1.inflate(R.layout.other_chat_fragment,null,false);
        relativeLayout.addView(inflatedLayout);


        messageAdapter=new MessageAdapter(messagesList,getActivity());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView=(RecyclerView)inflatedLayout.findViewById(R.id.recView2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(messageAdapter);
        final FirebaseUser mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        loadMessages(mRef.child(ChatApp.rollInfo).child("CR").child("messages").child(uid));
        if(mCurrentUser!=null) {
            mSendBtn = (ImageView) inflatedLayout.findViewById(R.id.send);
            mMessage = (EditText) inflatedLayout.findViewById(R.id.message);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    boolean isConnected = info != null && info.isConnectedOrConnecting();
                    if (isConnected) {
                    if (!TextUtils.isEmpty(mMessage.getText())) {
                        sendMessage(mMessage.getText().toString(),uid);
                        mMessage.setText("");
                    }
                    }
                    else
                    {
                        Toast.makeText(optionsActivity, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            mSendBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    boolean isConnected = info != null && info.isConnectedOrConnecting();
                    if (isConnected) {
                        PickerDialogFragment pickerDialogFragment = new PickerDialogFragment();
                        pickerDialogFragment.show(getActivity().getFragmentManager(), "picker");
                    }
                    else
                    {
                        Toast.makeText(optionsActivity, "Not connected to the Internet!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

    }

    /*
            view = inflater.inflate(R.layout.fragment_chat, container, false);
            mUserAdapter = new UserAdapter(usersList);
            recyclerView = (RecyclerView) view.findViewById(R.id.recView);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(mUserAdapter);
            loadUsers();

    */
    private void loadUsers() {
        Query q=mRef.child("Users").orderByChild("latestTimestamp");
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uid=dataSnapshot.getKey().toString();
                Users users = dataSnapshot.getValue(Users.class);
                String rollInf=users.getUsername().substring(0,8);
                if (users!=null&&users.getCR().equals("false")&&ChatApp.rollInfo.equalsIgnoreCase(rollInf)) {
                    usersList.add(0, users);
                    mUserAdapter.notifyDataSetChanged();
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
    private void sendMessage(final String message,final String uid) {
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child(ChatApp.rollInfo).child("CR").child("messages").child(uid).push();
        final String key=mDatabase.getKey();

        final Map map=new HashMap();
        map.put("type","null");
        map.put("link","default");
        map.put("timestamp", ServerValue.TIMESTAMP);
        map.put("text",message);
        map.put("sender","Student");
        map.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());


        DatabaseReference userUpdate=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userUpdate.child("latestTimestamp").setValue(ServerValue.TIMESTAMP);
        if(uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            userUpdate.child("isUnseen").setValue("true");
        }

        final DatabaseReference databaseReference=mRef.child(ChatApp.rollInfo).child("CR").child("messages");
        databaseReference.child(uid).child(key).setValue(map);
    }
}

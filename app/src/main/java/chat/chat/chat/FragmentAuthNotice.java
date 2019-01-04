package chat.chat.chat;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import chat.chat.ChatApp;
import chat.chat.R;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAuthNotice extends Fragment {


    public FragmentAuthNotice() {
        // Required empty public constructor
    }
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<Messages> mList;
    LinearLayoutManager linearLayoutManager;
    MessageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_auth_notice, container, false);
        fab= (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm= (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info=cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if(isConnected) {
                    Intent intent = new Intent(getActivity(), NoticeComposerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getActivity(),"Not Connected to the Internet!",Toast.LENGTH_LONG).show();
                }
            }
        });

        mList=new ArrayList<>();
        recyclerView= (RecyclerView) view.findViewById(R.id.recAuth);
        adapter=new MessageAdapter(mList,getActivity());
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
        if(ChatApp.user.getCR().equals("faculty")) {
            loadMessages(mRef.child("Faculty").child(ChatApp.user.getUsername()).child("Notices").child("An"));
        }
        else
        {
            loadMessages(mRef.child("Director").child("Notices").child("An"));
        }
        return view;
    }
    public void loadMessages(DatabaseReference mRef)
    {
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                mList.add(messages);
                adapter.notifyDataSetChanged();
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

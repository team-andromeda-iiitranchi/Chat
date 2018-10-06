package chat.chat.chat;


import android.content.Intent;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollFragment extends Fragment {

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private PollAdapter pollAdapter;
    private List<Poll> mList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRef;
    private List votedList;

    public PollFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mList=new ArrayList<>();
        View view=inflater.inflate(R.layout.fragment_poll, container, false);
        floatingActionButton=(FloatingActionButton)view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    Intent i = new Intent(getActivity(), AddPoll.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getActivity(), "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(ChatApp.user.getCR().equals("false"))
        {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }

        pollAdapter=new PollAdapter(mList,getActivity());
        recyclerView=(RecyclerView)view.findViewById(R.id.recView3);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(pollAdapter);
        pollAdapter.notifyDataSetChanged();
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef= FirebaseDatabase.getInstance().getReference();
        //mList=new ArrayList<>();

        mRef.child(ChatApp.rollInfo).child("Poll").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll poll=dataSnapshot.getValue(Poll.class);
                if(mList.size()!=0)
                {
                    mList.add(0,poll);
                }
                else
                {
                    mList.add(poll);
                }
                pollAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll poll=dataSnapshot.getValue(Poll.class);
                for(int i=0;i<mList.size();i++)
                {
                    if(mList.get(i).timestamp==poll.timestamp) {
                        mList.remove(i);
                        mList.add(i,poll);
                        pollAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Poll poll=dataSnapshot.getValue(Poll.class);
                for(int i=0;i<mList.size();i++)
                {
                    if(mList.get(i).timestamp==poll.timestamp) {
                        mList.remove(i);
                        pollAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}

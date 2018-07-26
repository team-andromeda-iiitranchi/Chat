package chat.chat.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import chat.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {
    private RecyclerView recyclerView;
    ImageView noticeTextView;
    private LinearLayoutManager linearLayoutManager;
    private List<Hashtag> mList=new ArrayList<>();
    private HashtagAdapter hashtagAdapter;

    private DatabaseReference mRef;

    public NoticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_notice, container, false);
        noticeTextView=(ImageView)view.findViewById(R.id.noticeTextView);
        noticeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ChatActivity.class));
            }
        });

        mRef= FirebaseDatabase.getInstance().getReference();

        hashtagAdapter=new HashtagAdapter(mList,NoticeFragment.this);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView=(RecyclerView)view.findViewById(R.id.noticeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(hashtagAdapter);

        loadHashtags();

        return view;

    }
    public void loadHashtags()
    {

        mRef.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name=dataSnapshot.getKey();
                Hashtag hashtag=new Hashtag();
                hashtag.setName(name);
                mList.add(hashtag);
                hashtagAdapter.notifyDataSetChanged();
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
    public void onItemClicked(final String name)
    {
        mRef.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(name))
                {
                    Intent i=new Intent(getActivity(),NoticeViewer.class);
                    startActivity(i);
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

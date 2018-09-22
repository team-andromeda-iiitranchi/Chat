package chat.chat.chat;


import android.content.Intent;
import android.net.Uri;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import chat.chat.ChatApp;
import chat.chat.R;

import static android.app.Activity.RESULT_OK;
import static chat.chat.chat.ChatActivity.TEMP_PHOTO_JPG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {
    ImageView noticeTextView;

    private DatabaseReference mRef;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        mRef= FirebaseDatabase.getInstance().getReference();

        scrollView= (ScrollView) view.findViewById(R.id.noticeScroll);
        linearLayout= (LinearLayout) view.findViewById(R.id.linearLayoutNotice);
        //LinearLayout.LayoutParams params=new LinearLayout.LayoutParams();
        mRef.child(ChatApp.rollInfo).child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key=dataSnapshot.getKey();
                if(!key.equals("An"))
                {
                    if(getActivity()!=null) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(key);
                        textView.setTextSize(20);
                        int pad = 20;
                        textView.setPadding(pad, pad, pad, pad);
                        textView.setBackground(getResources().getDrawable(R.drawable.user_border));
                        linearLayout.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), NoticeViewer.class);
                                intent.putExtra("Name", key);
                                startActivity(intent);
                            }
                        });
                    }
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

        return view;

    }

}

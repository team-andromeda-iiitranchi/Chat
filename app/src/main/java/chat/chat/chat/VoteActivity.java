package chat.chat.chat;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

public class VoteActivity extends AppCompatActivity {
    private TextView title,description;
    private DatabaseReference mRef;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        title= (TextView) findViewById(R.id.title);
        description= (TextView) findViewById(R.id.description);
        linearLayout= (LinearLayout) findViewById(R.id.linearLayout);
        final String pushId=getIntent().getStringExtra("pushId");
        mRef= FirebaseDatabase.getInstance().getReference().child(ChatApp.rollInfo).child("Poll").child(pushId);
        final ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Poll poll=dataSnapshot.getValue(Poll.class);
                title.setText(poll.getTitle());
                description.setText(poll.getDescription());
                Map map=poll.getOptionsMap();
                linearLayout.removeAllViews();
                Iterator iterator=map.entrySet().iterator();
                while(iterator.hasNext())
                {
                    final TextView textView=new TextView(linearLayout.getContext());
                    textView.setBackgroundResource(R.drawable.border);
                    Map.Entry pair= (Map.Entry) iterator.next();
                    textView.setText(pair.getKey().toString());
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8,20,8,20);
                    textView.setLayoutParams(params);
                    textView.setTextSize(20);
                    int pad=20;
                    textView.setPadding(pad+pad,pad,pad,pad);
                    linearLayout.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                            NetworkInfo info = cm.getActiveNetworkInfo();
                            boolean isConnected = info != null && info.isConnectedOrConnecting();
                            if (isConnected) {
                                final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(ChatApp.rollInfo).child("Poll").child(pushId).child("optionsMap").child(textView.getText().toString());
                                mRef.runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long votes = mutableData.getValue(Long.class);
                                        votes++;
                                        mRef.setValue(votes);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                        Intent intent = new Intent(VoteActivity.this, OptionsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                        finish();
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("polls");
                                        Map map = new HashMap();
                                        map.put(pushId, "1");
                                        databaseReference.updateChildren(map);
                                        Toast.makeText(VoteActivity.this, "Your vote was submitted!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            else {
                                Toast.makeText(VoteActivity.this, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRef.addListenerForSingleValueEvent(valueEventListener);

    }
}

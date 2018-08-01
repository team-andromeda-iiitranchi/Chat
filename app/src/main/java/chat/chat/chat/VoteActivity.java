package chat.chat.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.Map;

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
        String pushId=getIntent().getStringExtra("pushId");
        mRef= FirebaseDatabase.getInstance().getReference().child("Poll").child(pushId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Poll poll=dataSnapshot.getValue(Poll.class);
                title.setText(poll.getTitle());
                description.setText(poll.getDescription());
                Map map=poll.getOptionsMap();
                Iterator iterator=map.entrySet().iterator();
                while(iterator.hasNext())
                {
                    TextView textView=new TextView(linearLayout.getContext());
                    textView.setBackgroundResource(R.drawable.border);
                    Map.Entry pair= (Map.Entry) iterator.next();
                    textView.setText(pair.getKey().toString());
                    int pad=15;
                    textView.setPadding(pad+pad,pad,pad,pad);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(VoteActivity.this, "Hmm...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    linearLayout.addView(textView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

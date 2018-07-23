package chat.chat.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ServerValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import chat.chat.R;

public class AddPoll extends AppCompatActivity {
    private Button mAddBtn;
    private EditText title,description;
    private String titleStr,descriptionStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poll);
        mAddBtn=(Button)findViewById(R.id.addBtn);
        title=(EditText)findViewById(R.id.title) ;
        description=(EditText)findViewById(R.id.description);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleStr=title.getText().toString();
                descriptionStr=description.getText().toString();
                setPoll(titleStr,descriptionStr);
            }
        });
    }

    private void setPoll(final String titleStr,final String descriptionStr) {
        final long timestamp=System.currentTimeMillis();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=(int)dataSnapshot.getChildrenCount();
                Poll poll=new Poll(titleStr,descriptionStr,timestamp,0,0,0,count);
                DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
                DatabaseReference mRootRef=mRef.child("Poll").push();
                String key=mRootRef.getKey();
                mRef.child("Poll").child(key).setValue(poll);
                Toast.makeText(getApplicationContext(),"Successfully added the poll!",Toast.LENGTH_LONG).show();
                title.setText("");
                description.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

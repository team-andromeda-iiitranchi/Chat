package chat.chat.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chat.chat.ChatApp;
import chat.chat.R;

public class CategoryViewer extends AppCompatActivity {

    private LinearLayout linearLayout;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_viewer);



        linearLayout= (LinearLayout)findViewById(R.id.linearLayoutCategory);
        mRef= FirebaseDatabase.getInstance().getReference();
        if(ChatApp.user.getCR().equals("director"))
        {
            mRef=mRef.child("Director").child("Notices");
        }
        else
        {
            mRef=mRef.child("Faculty").child(ChatApp.user.getUsername()).child("Notices");
        }
        //LinearLayout.LayoutParams params=new LinearLayout.LayoutParams();
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key=dataSnapshot.getKey();
                if(!key.equals("An"))
                {
                    TextView textView=new TextView(CategoryViewer.this);
                    textView.setText(key);
                    textView.setTextSize(20);
                    int pad=20;
                    textView.setPadding(pad,pad,pad,pad);
                    textView.setBackground(getResources().getDrawable(R.drawable.user_border));
                    linearLayout.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(CategoryViewer.this,NoticeViewer.class);
                            intent.putExtra("Name",key);
                            startActivity(intent);
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
}

package chat.chat.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import chat.chat.R;

public class LibraryActivity extends AppCompatActivity {
    private StorageReference mStorage;
    private DatabaseReference mRef;
    private ScrollView scrollView;
    private FloatingActionButton fab;
    private LinearLayout linearLayout;
    private int state;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        getSupportActionBar().setTitle("E-Library");

        mStorage= FirebaseStorage.getInstance().getReference().child("Books");
        mRef= FirebaseDatabase.getInstance().getReference().child("Books");

        scrollView= (ScrollView) findViewById(R.id.libScroll);
        linearLayout=(LinearLayout)findViewById(R.id.libList);
        state=1;
        loadTopics();

        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LibraryActivity.this,AddBook.class));
            }
        });

    }
    @Override
    public void onBackPressed()
    {
        if(state==2)
        {
            state=1;
            linearLayout.removeAllViews();
            loadTopics();
        }
        else if(state==1)
        {
            Intent intent=new Intent(LibraryActivity.this,OptionsActivity.class);
            startActivity(intent);
        }
    }
    public void downloadAndShow(String name,String bookName)
    {
        String root= Environment.getExternalStorageDirectory().toString();
        File myFile=new File(root+"/ChatApp/Books/"+name+"/");
        if(!myFile.exists())
        {
            myFile.mkdirs();
        }
        myFile=new File(myFile,bookName+".pdf");
        if(myFile.exists()&&myFile.length()!=0)
        {
            generateIntentAndShow(myFile);
        }
        else
        {
            try {
                Toast.makeText(LibraryActivity.this,"Downloading!",Toast.LENGTH_LONG).show();

                myFile.createNewFile();
                final File finalMyFile = myFile;
                mStorage.child(name).child(bookName+".pdf").getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        generateIntentAndShow(finalMyFile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LibraryActivity.this, "Failed to Download!", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void generateIntentAndShow(File myFile)
    {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setData(Uri.fromFile(myFile));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }
    public void showBooks(final String name, final int pad)
    {
        linearLayout.removeAllViews();
        state=2;

        mRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren())
                {
                    String bookName=dataSnapshot2.getKey();
                    final TextView textView1=new TextView(linearLayout.getContext());
                    textView1.setText(bookName);
                    textView1.setPadding(pad,pad,pad,pad);
                    textView1.setBackground(getDrawable(R.drawable.user_border));
                    textView1.setTextSize(20);
                    linearLayout.addView(textView1);
                    final String finalBookName = bookName;
                    textView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadAndShow(name, finalBookName);
                            textView1.setOnClickListener(null);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void loadTopics()
    {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    final String name=dataSnapshot1.getKey();
                    final TextView textView=new TextView(linearLayout.getContext());
                    textView.setText(name);
                    textView.setBackground(getDrawable(R.drawable.user_border));
                    textView.setTextSize(20);
                    final int pad=30;
                    textView.setPadding(pad,pad,pad,pad);
                    linearLayout.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showBooks(name,pad);
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

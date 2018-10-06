package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

        //getSupportActionBar().setTitle("E-Library");

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
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    Intent i=new Intent(LibraryActivity.this, AddBook.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(LibraryActivity.this, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
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
            //Intent intent=new Intent(LibraryActivity.this,OptionsActivity.class);
            //startActivity(intent);
            finish();
        }
    }
    public void downloadAndShow(String name,String bookName,View view)
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
        else {
            view.setOnClickListener(null);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            boolean isConnected = info != null && info.isConnectedOrConnecting();
            if (isConnected) {
                try {
                    final ProgressDialog mProgress=new ProgressDialog(this);
                    mProgress.setTitle("Downloading...");
                    mProgress.setMessage("Please wait while your document is being downloaded.");
                    mProgress.setCanceledOnTouchOutside(true);
                    mProgress.show();

                    myFile.createNewFile();
                    final File finalMyFile = myFile;
                    mStorage.child(name).child(bookName + ".pdf").getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            mProgress.dismiss();
                            generateIntentAndShow(finalMyFile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(LibraryActivity.this, "Failed to Download!", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(LibraryActivity.this,"Not Connected to the Internet!",Toast.LENGTH_LONG).show();
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
                    textView1.setTextColor(getResources().getColor(R.color.black_app));
                    textView1.setBackground(getDrawable(R.drawable.user_border));
                    textView1.setTextSize(20);
                    linearLayout.addView(textView1);
                    final String finalBookName = bookName;
                    textView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadAndShow(name, finalBookName,textView1);
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
        final ProgressDialog mProgress=new ProgressDialog(this);
        mProgress.setTitle("Loading...");
        mProgress.setMessage("Please wait while the docs are being loaded.");
        mProgress.setCanceledOnTouchOutside(true);
        mProgress.show();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgress.dismiss();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    final String name=dataSnapshot1.getKey();
                    final TextView textView=new TextView(linearLayout.getContext());
                    textView.setText(name);
                    textView.setTextColor(getResources().getColor(R.color.black_app));
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

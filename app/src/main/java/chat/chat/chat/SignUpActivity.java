package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.FirebaseException;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import chat.chat.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText mUser,mName,mEmail, mPass,mCpass;
    private Button mSignUp;
    private String rollInfo;
    private FirebaseAuth mAuth;
    private Map map;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");

        mUser=(EditText)findViewById(R.id.username);
        mName=(EditText)findViewById(R.id.name);
        mEmail=(EditText)findViewById(R.id.email_id);
        mPass=(EditText)findViewById(R.id.pwd);
        mCpass=(EditText)findViewById(R.id.cpwd);
        mSignUp=(Button)findViewById(R.id.button);

        mAuth=FirebaseAuth.getInstance();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();

                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    String name, user, email, pass, cpass;
                    name = mName.getText().toString();
                    user = mUser.getText().toString();
                    email = mEmail.getText().toString();
                    suEmail=email;
                    user=(user.indexOf("fac")==-1&&user.indexOf("dir")==-1)?user.toUpperCase():user;
                    suUser=user;
                    suName=name;
                    if (user.length() < 8) {
                        Toast.makeText(getApplicationContext(), "Invalid Regisration No.!", Toast.LENGTH_LONG).show();
                    } else {

                        rollInfo = user.substring(0, 8);    // 2017ugcs

                        pass = mPass.getText().toString();
                        suPass=pass;
                        cpass = mCpass.getText().toString();
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cpass)) {
                            Toast.makeText(getApplicationContext(), "Empty Field!", Toast.LENGTH_LONG).show();
                        } else if ((TextUtils.isEmpty(email)&& !Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                            Toast.makeText(getApplicationContext(), "Invalid Email!", Toast.LENGTH_LONG).show();
                        } else if (!pass.equals(cpass)) {
                            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                        } else {
//                            user = user + "@abc.com";   // email
//                            createUser(name, user, pass);   // user --- email
                            createUserWithEmail();
                        }


                    }
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Not Connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String suName, suUser, suEmail, suPass;
    ProgressDialog mProgress;

    public void createUserWithEmail(){
        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Creating Account");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        checkUserExist();
    }

    public void checkUserExist()
    {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
        Query q=mRef.child("Users").orderByChild("username").equalTo(suUser);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null)
                {
                    boolean userExist = false;
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        userExist  = true;
                    }

                    if(!userExist){
                        signUpSetup();
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(SignUpActivity.this, "Registration number already registered", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void signUpSetup(){
        mAuth.createUserWithEmailAndPassword(suEmail, suPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid;
                    map = new HashMap();

                    uid = mAuth.getCurrentUser().getUid();

                    mRef = FirebaseDatabase.getInstance().getReference();

                    Map map1 = new HashMap();

                    String keyForFirstPoll = mRef.child("Users").child(uid).child("polls").push().getKey();


                    map1.put(keyForFirstPoll, "1");

                    map.put("Name", suName);

                    if (suUser.indexOf("fac") == -1 && suUser.indexOf("dir") == -1) {
                        map.put("CR", "false");
                    } else if (suUser.indexOf("fac") != -1) {
                        map.put("CR", "faculty");
                    } else {
                        map.put("CR", "director");
                    }

                    map.put("email", suEmail);
                    map.put("username", suUser);
                    map.put("latestTimestamp", ServerValue.TIMESTAMP);
                    map.put("isUnseen", "true");
                    map.put("polls", map1);
                    map.put("imageLink", "null");
                    mRef.child("Users").child(uid).setValue(map);

                    if (suUser.indexOf("fac") == -1 && suUser.indexOf("dir") == -1) {
                        sections(uid);
                    } else if (suUser.indexOf("fac") != -1) {
                        faculty(suUser, uid, suName);
                    }

                    //mRef.child("CR").child("messages").child(uid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                    Toast.makeText(getApplicationContext(), "Authentication Successful!", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(SignUpActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occured!", Toast.LENGTH_LONG).show();
                }
                mProgress.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(SignUpActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    public void faculty(String user, String uid, String name)
    {
        user=user.substring(0,user.indexOf("@"));
        String key=mRef.child("Faculty").child(user).child("Notices").child("An").push().getKey();
        final Map map=new HashMap();
        long timestamp=System.currentTimeMillis();
        map.put("text",name+" joined the chat!");
        map.put("sender","faculty");
        map.put("timestamp",timestamp);
        map.put("from",uid);
        map.put("type","null");
        map.put("link","default");
        mRef.child("Faculty").child(user).child("Notices").child(key).setValue(map);
        List<String> sectionsList=new ArrayList<>();
        mRef.child("Sections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    String section=d.getKey();
                    String key=mRef.child(section).child("message").child("An").push().getKey();
                    mRef.child(section).child("message").child("An").child(key).setValue(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sections(String uid)
    {

        DatabaseReference databaseReference=mRef.child(rollInfo).child("CR").child("messages").child(uid).push();
        String messageId=databaseReference.getKey();
        Map map2=new HashMap();
        map2.put("type","null");
        map2.put("link","default");
        map2.put("timestamp",ServerValue.TIMESTAMP);
        map2.put("text","Send your messages from here.");
        map2.put("sender","Student");
        map2.put("from",uid);
        mRef.child(rollInfo).child("CR").child("messages").child(uid).child(messageId).setValue(map2);
        DatabaseReference mRef1=mRef.child("Sections");
        //since sections are never incremented from 1 to 2
        //1 is added to count when considering for poll
        mRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null)
                {
                    if(dataSnapshot.hasChild(rollInfo))
                    {
                        mRef.child("Sections").child(rollInfo).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                Long val=mutableData.getValue(Long.class);
                                val+=1;
                                mRef.child("Sections").child(rollInfo).setValue(val);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            }
                        });
                    }
                    else
                    {
                        mRef.child("Sections").child(rollInfo).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                Long val=1l;
                                mRef.child("Sections").child(rollInfo).setValue(val);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Null hai sections!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

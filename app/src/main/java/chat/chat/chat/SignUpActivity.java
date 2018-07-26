package chat.chat.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import chat.chat.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText mUser,mName,mPass,mCpass;
    private Button mSignUp;
    FirebaseAuth mAuth;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUser=(EditText)findViewById(R.id.username);
        mName=(EditText)findViewById(R.id.name);
        mPass=(EditText)findViewById(R.id.pwd);
        mCpass=(EditText)findViewById(R.id.cpwd);
        mSignUp=(Button)findViewById(R.id.button);
        mAuth=FirebaseAuth.getInstance();
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, user, pass, cpass;
                name = mName.getText().toString();
                user = mUser.getText().toString();
                pass = mPass.getText().toString();
                cpass = mCpass.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cpass)) {
                    Toast.makeText(getApplicationContext(), "Empty Field!", Toast.LENGTH_LONG).show();
                } else if (!pass.equals(cpass)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                } else {
                    user=user+"@abc.com";
                    createUser(name,user,pass);
                }

            }
        });


    }
    public void createUser(final String name,final String user,final String pass)
    {
        mAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    String uid;

                    uid=mAuth.getCurrentUser().getUid();
                    mRef= FirebaseDatabase.getInstance().getReference();
                    Map map=new HashMap();
                    map.put("Name",name);
                    map.put("CR","false");
                    map.put("username",user.substring(0,user.indexOf("@")));
                    map.put("latestTimestamp",ServerValue.TIMESTAMP);
                    map.put("isUnseen","true");
                    mRef.child("Users").child(uid).setValue(map);
                    DatabaseReference databaseReference=mRef.child("CR").child("messages").child(uid).push();
                    String messageId=databaseReference.getKey();
                    Map map1=new HashMap();
                    map1.put("seen","false");
                    map1.put("timestamp",ServerValue.TIMESTAMP);
                    map1.put("text","Send your messages from here.");
                    map1.put("from",uid);
                    mRef.child("CR").child("messages").child(uid).child(messageId).setValue(map1);
                    //mRef.child("CR").child("messages").child(uid).child("timestamp").setValue(ServerValue.TIMESTAMP);
                    Toast.makeText(getApplicationContext(),"Authentication Successful!",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Some error occured!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

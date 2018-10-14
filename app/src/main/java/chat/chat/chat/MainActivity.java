package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import chat.chat.ChatApp;
import chat.chat.R;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private DatabaseReference usersRef;
    private EditText user;
    private EditText password;
    private Button logIn,signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth=FirebaseAuth.getInstance();
        user=(EditText)findViewById(R.id.user);
        password=(EditText)findViewById(R.id.password);
        dbRef = FirebaseDatabase.getInstance().getReference();
        usersRef= dbRef.child("Users");

        logIn=(Button)findViewById(R.id.logIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    String name, pass;
                    name = user.getText().toString();
                    pass = password.getText().toString();
                    final String pwd = pass;
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pass)) {
                        Toast.makeText(getApplicationContext(), "Empty Field!", Toast.LENGTH_LONG).show();
                    } else if (name.length() < 8) {
                        Toast.makeText(MainActivity.this, "Invalid Registration No.!", Toast.LENGTH_SHORT).show();
                    } else {
                        final ProgressDialog mProgress=new ProgressDialog(MainActivity.this);
                        mProgress.setTitle("Logging In");
                        mProgress.setMessage("Please Wait...");
                        mProgress.setCanceledOnTouchOutside(false);
                        mProgress.show();
//                      name = name + "@abc.com";

                        final String finalName = name;

                        Query q = usersRef.orderByChild("username").equalTo(finalName);

                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Users user=null;
                               for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                               {
                                   user=dataSnapshot1.getValue(Users.class);
                               }
                                if(user!=null) {
                                    mAuth.signInWithEmailAndPassword(user.getEmail(), pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            mProgress.dismiss();
                                            if (task.isSuccessful()) {
                                                Intent i=new Intent(MainActivity.this, OptionsActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                startActivity(i);

                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    mProgress.dismiss();
                                    Toast.makeText(MainActivity.this, "User Not Found!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Not connected to the Internet!", Toast.LENGTH_SHORT).show();
                }
            }

        });
        signUp=(Button)findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SignUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package chat.chat.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import chat.chat.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText mUser,mName,mPass,mCpass;
    private Button mSignUp;
    FirebaseAuth mAuth;
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
                String name,user,pass,cpass;
                name=mName.getText().toString();
                user=mUser.getText().toString();
                pass=mPass.getText().toString();
                cpass=mCpass.getText().toString();
                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(user)||TextUtils.isEmpty(pass)||TextUtils.isEmpty(cpass))
                {
                    Toast.makeText(getApplicationContext(),"Empty Field!",Toast.LENGTH_LONG).show();
                }
                else if(!pass.equals(cpass))
                {
                    Toast.makeText(getApplicationContext(),"Passwords do not match!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    user=user+"@abc.com";
                    mAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
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
        });

    }

}

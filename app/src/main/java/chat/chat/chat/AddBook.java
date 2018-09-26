package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import chat.chat.R;

public class AddBook extends AppCompatActivity {
    private EditText mtopic,mname;
    private Button select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setTitle("Add Book");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); 
        
        mname= (EditText) findViewById(R.id.bookName);
        mtopic=(EditText) findViewById(R.id.topic);
        select= (Button) findViewById(R.id.select);
        
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info=cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if(isConnected){
                    String topic = mtopic.getText().toString();
                    String name = mname.getText().toString();
                    topic = modify(topic);
                    name = modify(name);
                    if (!TextUtils.isEmpty(mname.getText()) && !TextUtils.isEmpty(mtopic.getText())) {
                        if (check(topic) && check(name)) {
                            Intent intent = new Intent();
                            intent.setType("application/pdf");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(AddBook.this, "Invalid names!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddBook.this, "Empty Fields!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(AddBook.this,"Not Connected to the Internet!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            try {
                
                final String topic,name;
                topic=mtopic.getText().toString();
                name=mname.getText().toString();
                InputStream fos=getContentResolver().openInputStream(data.getData());
                StorageReference mStorage= FirebaseStorage.getInstance().getReference();
                final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                final String finalTopic = modify(topic);
                final String finalName = modify(name+".pdf");
                final ProgressDialog mProgress=new ProgressDialog(this);
                mProgress.setTitle("Adding book");
                mProgress.setMessage("Your book is being uploaded");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                mStorage.child("Books").child(finalTopic).child(finalName).putStream(fos).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgress.dismiss();
                        mRef.child("Books").child(finalTopic).child(finalName.substring(0,finalName.indexOf("."))).setValue(1);
                        finish();
                        startActivity(new Intent(AddBook.this,LibraryActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddBook.this, "There was some error uploading the file!", Toast.LENGTH_SHORT).show();
                    }
                });
                
                
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean check(String str)
    {
        for(int i=0;i<str.length();i++)
        {
            char c=str.charAt(i);
            if(c=='.'||c=='#'||c=='['||c==']'||c=='$')
            {
                return false;
            }
        }
        return true;
    }
    public String modify(String str)
    {
        String newStr="";
        char d='0';
        for(int i=0;i<str.length();i++)
        {
            char c=str.charAt(i);

            if(i==0||(d==' '&&Character.isLetter(c)))
            {
                c=Character.toUpperCase(c);
            }
            else
            {
                if(Character.isLetter(c))
                {
                    c=Character.toLowerCase(c);
                }
            }
            d=c;
            newStr=newStr+c;
        }
        return newStr;
    }

}

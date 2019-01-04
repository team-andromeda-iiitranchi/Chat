package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import chat.chat.R;

public class AuthImageActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private ImageView send;
    private EditText messageView;
    private ProgressDialog mProgress;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_image);
        toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        send= (ImageView) findViewById(R.id.imgSendBtn);
        messageView= (EditText) findViewById(R.id.imgTitle);


        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Uploading Image");
        mProgress.setMessage("Please wait while the image is being uploaded");
        mProgress.setCanceledOnTouchOutside(false);


        final Uri fileUri= (Uri) getIntent().getExtras().get("imageUri");
        imageView= (ImageView) findViewById(R.id.imageView);
        Picasso.get().load(fileUri).into(imageView);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(messageView.getText()))
                {
                    final String text=messageView.getText().toString();
                    mProgress.show();
                    final Long timestamp=System.currentTimeMillis();
                    final StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("Auth").child("A"+timestamp+".jpg");
                    UploadTask task=mStorage.putFile(fileUri);
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(AuthImageActivity.this, "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Task<Uri> uriTask=task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful())
                            {
                                mProgress.dismiss();
                                throw task.getException();
                            }
                            return mStorage.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            mProgress.dismiss();
                            String link=task.getResult().toString();
                            Intent data=new Intent();
                            data.putExtra("text",text);
                            data.putExtra("timestamp",timestamp);
                            data.putExtra("link",link);
                            setResult(RESULT_OK,data);
                            messageView.setText("");
                            finish();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Enter a Message",Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

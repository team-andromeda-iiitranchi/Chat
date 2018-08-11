package chat.chat.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private Button setPic;
    private CircleImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        
        setPic= (Button) findViewById(R.id.setPic);
        picture= (CircleImageView) findViewById(R.id.picture);
        
        setPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });
        getSupportActionBar().setTitle("Account");

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("thumbnails").child(uid + ".jpg");
            String filePath=data.getData().getPath();
            try {
                InputStream inputStream=getContentResolver().openInputStream(data.getData());

                Bitmap compressedFile= BitmapFactory.decodeStream(inputStream);
                if(inputStream!=null)
                {
                    inputStream.close();
                }
                ByteArrayOutputStream boas=new ByteArrayOutputStream();
                compressedFile.compress(Bitmap.CompressFormat.JPEG,40,boas);
                byte ar[]=boas.toByteArray();

                UploadTask uploadTask=mStorage.putBytes(ar);
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return  mStorage.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(AccountActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            //Toast.makeText(AccountActivity.this,"See if storage permission has been granted to app!",Toast.LENGTH_LONG).show();
        }
    }
}

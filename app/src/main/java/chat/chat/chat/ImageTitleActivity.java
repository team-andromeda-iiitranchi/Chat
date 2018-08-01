package chat.chat.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.chat.R;
import id.zelory.compressor.Compressor;


public class ImageTitleActivity extends AppCompatActivity {
    private ImageView mSendBtn;
    private EditText editText;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_selector_layout);
        imageView=(ImageView) findViewById(R.id.selectedImage);
        mSendBtn=(ImageView)findViewById(R.id.imgSendBtn);
        editText=(EditText)findViewById(R.id.imgTitle);
        final Uri photo=(Uri)getIntent().getExtras().get("image");
        final String filePath = getIntent().getStringExtra("path");
        mRef=FirebaseDatabase.getInstance().getReference();
//        final Bitmap compressedFile = (Bitmap)getIntent().getParcelableExtra("image_bm");
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    try {

                        startActivity(new Intent(ImageTitleActivity.this,ChatActivity.class));
                        Bitmap compressedFile = BitmapFactory.decodeFile(filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedFile.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                        byte[] arr = baos.toByteArray();

                        final Messages message=new Messages();
                        final String uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                        message.setText(editText.getText().toString());
                        editText.setText("");

                        final List<String> categ;
                        categ=message.getHashTag();
                        final long timestamp=System.currentTimeMillis();
                        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("Uploads").child("A"+timestamp+".jpg");


                        //Upload the image
                        UploadTask uploadTask = mStorageRef.putBytes(arr);
                        mSendBtn.setOnClickListener(null);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ImageTitleActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>(){

                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful())
                                {
                                    throw task.getException();
                                }
                                return mStorageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful())
                                {
                                    File file=new File(filePath);
                                    file.delete();
                                    int count=0;//to check if message has been pushed
                                    for(int i=0;i<categ.size();i++) {
                                        String ctgry=categ.get(i);
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("message").child(ctgry).push();
                                        String key = mDatabase.getKey();
                                        Uri uri=task.getResult();
                                        Map map = new HashMap();
                                        map.put("timestamp", timestamp);
                                        map.put("text", message.getText());
                                        map.put("from", uid);
                                        map.put("type","image");
                                        map.put("link",uri.toString());
                                        mRef.child("message").child(ctgry).child(key).setValue(map);
                                        count++;
                                    }
                                    //Message has not been sent due to improper hashtag
                                    if(count==0)
                                    {
                                        Toast.makeText(ImageTitleActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(ImageTitleActivity.this, "Task Successful!", Toast.LENGTH_LONG).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(ImageTitleActivity.this,"Failed!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ImageTitleActivity.this, "There was an error uploading the image!", Toast.LENGTH_SHORT).show();
                    }finally {
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(ImageTitleActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();
                }
            }
        });
        imageView.setFocusable(true);
        imageView.setImageURI(photo);
    }

    private DatabaseReference mRef;
}

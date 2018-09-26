package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
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

import chat.chat.ChatApp;
import chat.chat.R;
import id.zelory.compressor.Compressor;


public class ImageTitleActivity extends AppCompatActivity implements ChooserDialog.ChooserDialogListener{
    private ImageView mSendBtn;
    private EditText editText;
    private ImageView imageView;
    private String context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_selector_layout);
        imageView=(ImageView) findViewById(R.id.selectedImage);
        mSendBtn=(ImageView)findViewById(R.id.imgSendBtn);
        editText=(EditText)findViewById(R.id.imgTitle);
        final Uri photo=(Uri)getIntent().getExtras().get("image");
        final String filePath = getIntent().getStringExtra("path");
        context=getIntent().getStringExtra("context");
        mRef=FirebaseDatabase.getInstance().getReference();
        final UploadHelper uploadHelper=new UploadHelper(ImageTitleActivity.this,context);
//        final Bitmap compressedFile = (Bitmap)getIntent().getParcelableExtra("image_bm");
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    final ProgressDialog mProgress=new ProgressDialog(ImageTitleActivity.this);
                    mProgress.setTitle("Uploading...");
                    mProgress.setMessage("Please wait while your image is being uploaded.");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    try {


                        final Messages message=new Messages();
                        final String uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                        message.setText(editText.getText().toString());
                        editText.setText("");

                        final List<String> categ;
                        categ=message.getHashTag();
                        final long timestamp=System.currentTimeMillis();
                        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("Uploads").child("A"+timestamp+".jpg");
                        File file=new File(filePath);
                        Uri fp=Uri.fromFile(file);

                        //Upload the image
                        UploadTask uploadTask = mStorageRef.putFile(fp);
                        mSendBtn.setOnClickListener(null);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgress.dismiss();
                                Toast.makeText(ImageTitleActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>(){

                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                mProgress.dismiss();
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(ImageTitleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    throw task.getException();
                                }
                                activityCaller();
                                return mStorageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful())
                                {
                                    File file=new File(filePath);
                                    file.delete();
                                    mRef=FirebaseDatabase.getInstance().getReference();
                                    if(context.equals("ChatActivity")) {
                                        uploadHelper.putAtRef(mRef, categ, task, timestamp, message, uid,"image");
                                    }
                                    else if(context.equals("ChatFragment"))
                                    {
                                        try {
                                            String receiver = getIntent().getStringExtra("receiver");
                                            uploadHelper.putAtCRRef(mRef, task, timestamp, message, uid, receiver, "image");
                                        }
                                        catch (Exception e)
                                        {

                                        }
                                    }
                                    else if(context.equals("NoticeComposerActivity"))
                                    {
                                        initList();
                                        ImageTitleActivity.this.link=task.getResult().toString();
                                        ImageTitleActivity.this.text=message.getText();
                                        ImageTitleActivity.this.timestamp=timestamp;
                                        //startActivity(new Intent(ImageTitleActivity.this,AuthNotice.class));
                                    }
                                    finish();

                                }
                                else
                                {
                                    Toast.makeText(ImageTitleActivity.this,"Failed!",Toast.LENGTH_LONG).show();
                                }
                                mProgress.dismiss();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ImageTitleActivity.this, "There was an error uploading the image!", Toast.LENGTH_SHORT).show();
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

    public String text;
    long timestamp;
    public String link;
    public  void activityCaller()
    {
        if(context.equals("ChatActivity"))
        {
            startActivity(new Intent(ImageTitleActivity.this,ChatActivity.class));
        }
        else if(context.equals("ChatFragment"))
        {
            startActivity(new Intent(ImageTitleActivity.this,OptionsActivity.class));
        }
    }

    private DatabaseReference mRef;
    static String mList[];
    List<Integer> selectedItems;
    List<String> tempList=new ArrayList<>();
    @Override
    public void onPositiveButtonClicked(String[] mList, List<Integer> selectedItems) {
        this.mList=mList;
        this.selectedItems=selectedItems;

        sendMessage();

    }
    public void sendMessage()
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String sender = ChatApp.user.getCR();
        Map map = new HashMap();
        editText.setText("");
        map.put("text", text);
        map.put("from", uid);
        map.put("sender", sender);
        map.put("link", link);
        map.put("type", "image");
        map.put("timestamp", timestamp);
        Messages messages = new Messages(uid, text, sender, "default", "null", timestamp);
        List<String> categ = messages.getHashTag();
        String authStr = "To:\n";
        for (int i = 0; i < selectedItems.size(); i++) {
            String str = ImageTitleActivity.mList[selectedItems.get(i)];
            if (str.indexOf("fac") != -1) {
                ifFaculty(mDatabase, str, categ, map);
            } else {
                ifSection(mDatabase, str, categ, map);
            }
            authStr += str + "\n";
        }
        authStr += "\n" + text;
        if (ChatApp.user.getCR().equals("director")) {
            map.put("text", authStr);
            for (int i = 0; i < categ.size(); i++) {
                String ctgry = categ.get(i);
                String key = mDatabase.child("Director").child("Notices").child(ctgry).push().getKey();
                mDatabase.child("Director").child("Notices").child(ctgry).child(key).setValue(map);
            }
        }
        if (ChatApp.user.getCR().equals("faculty")) {
            map.put("text", authStr);
            for (int i = 0; i < categ.size(); i++) {
                String ctgry = categ.get(i);
                String key = mDatabase.child("Faculty").child(ChatApp.user.getUsername()).child("Notices").child(ctgry).push().getKey();
                mDatabase.child("Faculty").child(ChatApp.user.getUsername()).child("Notices").child(ctgry).child(key).setValue(map);
            }
        }
        startActivity(new Intent(ImageTitleActivity.this, AuthNotice.class));
        finish();
    }
    public void initList() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Sections");
        mDatabase.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                for(MutableData d:mutableData.getChildren())
                {
                    tempList.add(d.getKey());
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                DatabaseReference mReference=FirebaseDatabase.getInstance().getReference().child("Faculty");
                mReference.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                        for(MutableData d:mutableData.getChildren())
                        {
                            if(!d.getKey().equals(ChatApp.user.getUsername()))
                            {
                                tempList.add(d.getKey());
                            }
                        }

                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        mList=new String[tempList.size()];
                        for(int i=0;i<mList.length;i++)
                        {
                            mList[i]=tempList.get(i);
                        }
                        ChooserDialog c=new ChooserDialog();
                        c.show(getFragmentManager(),"dialog");
                    }
                });
            }
        });
    }
    public void ifFaculty(DatabaseReference mDatabase, String str, List<String> categ, Map map)
    {
        int count=0;
        for(int i=0;i<categ.size();i++)
        {
            String ctgry=categ.get(i);
            String key=mDatabase.child("Faculty").child(str).child("Notices").child(ctgry).push().getKey();
            mDatabase.child("Faculty").child(str).child("Notices").child(ctgry).child(key).setValue(map);
            count++;
        }
        if(count==0)
        {
            Toast.makeText(ImageTitleActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }
    }
    public void ifSection(DatabaseReference mDatabase, String str, List<String> categ, Map map)
    {
        int count=0;
        for(int i=0;i<categ.size();i++)
        {
            String ctgry=categ.get(i);
            String key=mDatabase.child(str).child("message").child(ctgry).push().getKey();
            mDatabase.child(str).child("message").child(ctgry).child(key).setValue(map);
            count++;
        }
        if(count==0)
        {
            Toast.makeText(ImageTitleActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }
    }
}

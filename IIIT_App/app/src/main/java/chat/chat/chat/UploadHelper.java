package chat.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;

public class UploadHelper {
    private static final String TEMP_PHOTO_JPG = "temp_photo.jpg";
    private String context;
    public UploadHelper(Context mContext,String context) {
        this.mContext=mContext;
        this.context=context;
    }
    private List<Integer> selectedItems;
    private Context mContext;
    private EditText mMessage;

    public UploadHelper(Context mContext, EditText mMessage, String context) {
        this.mContext = mContext;
        this.mMessage=mMessage;
        this.context=context;
    }
    public UploadHelper(Context mContext, EditText mMessage, String context,List<Integer> selectedItems) {
        this.mContext = mContext;
        this.mMessage=mMessage;
        this.context=context;
        this.selectedItems=selectedItems;
    }

    public void makeTempAndUpload(Intent intent, final Uri uri, String type) {
        final Messages messages=new Messages();
        String state = Environment.getExternalStorageState();
        File mFileTemp;
        if(Environment.MEDIA_MOUNTED.equals(state)){
            mFileTemp = new File(Environment.getExternalStorageDirectory(), type);

        }else{
            mFileTemp = new File(mContext.getFilesDir(), type);
        }
        messages.setText(mMessage.getText().toString());
        mMessage.setText("");

        try {
            InputStream io = mContext.getContentResolver().openInputStream(uri);
            FileOutputStream fo = new FileOutputStream(mFileTemp);

            copyStream(io,fo);

            fo.close();
            io.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(mContext,"See if Storage Permission has been granted!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(type.equals(TEMP_PHOTO_JPG)) {
            intent.putExtra("path", mFileTemp.getAbsolutePath());
            mContext.startActivity(intent);
        }
        else
        {
            final long timestamp=System.currentTimeMillis();
            String name="A"+timestamp+".pdf";
            final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Uploads").child(name);

            try {
                FileInputStream fis=new FileInputStream(mFileTemp);
                UploadTask uploadTask=storageReference.putStream(fis);
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            List<String> list = messages.getHashTag();
                            DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
                            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if(context.equals("ChatActivity")) {
                                putAtRef(mRef, list, task, timestamp, messages, uid, "doc");
                            }
                            else if(context.equals("NoticeComposerActivity"))
                            {
                                ((NoticeComposerActivity)mContext).getLink(task.getResult(),"doc",messages.getText(),timestamp);
                            }
                            else
                            {
                                try {
                                    String receiver = ChatFragment.receiver;
                                    putAtCRRef(mRef, task, timestamp, messages, uid, receiver, "doc");
                                }
                                catch (Exception e)
                                {}
                            }
                        }
                    }

                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    private void copyStream(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer))!= -1){
            output.write(buffer,0,bytesRead);
        }
    }
    public void putAtCRRef(DatabaseReference mRef, Task<Uri> task, long timestamp, Messages message, String uid,String receiver,String type) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(ChatApp.rollInfo).child("CR").child(receiver).push();
        String key = mDatabase.getKey();
        Uri uri=task.getResult();
        Map map = new HashMap();
        map.put("timestamp", timestamp);
        map.put("text", message.getText());
        map.put("from", uid);
        map.put("type",type);
        map.put("sender",(ChatApp.user.getCR().equals("true")||ChatApp.user.getCR().equals("false"))?"Student":ChatApp.user.getCR());
        map.put("link",uri.toString());
        mRef.child(ChatApp.rollInfo).child("CR").child("messages").child(receiver).child(key).setValue(map);
    }

    public void putAtRef(DatabaseReference mRef, List<String> categ, Task<Uri> task, long timestamp, Messages message, String uid,String type)
    {
        int count=0;//to check if message has been pushed
        for(int i=0;i<categ.size();i++) {
            String ctgry=categ.get(i);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(ChatApp.rollInfo).child("message").child(ctgry).push();
            String key = mDatabase.getKey();
            Uri uri = task.getResult();

            Map map = new HashMap();
            map.put("timestamp", timestamp);
            map.put("text", message.getText());
            map.put("from", uid);
            map.put("sender",(ChatApp.user.getCR().equals("true")||ChatApp.user.getCR().equals("false"))?"Student":ChatApp.user.getCR());
            map.put("type",type);
            map.put("link", uri.toString());

            mRef.child(ChatApp.rollInfo).child("message").child(ctgry).child(key).setValue(map);
            count++;
        }
        //Message has not been sent due to improper hashtag
        if(count==0)
        {
            Toast.makeText(mContext, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(mContext, "Task Successful!", Toast.LENGTH_LONG).show();
        }

    }
}

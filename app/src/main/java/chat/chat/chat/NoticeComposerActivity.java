package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.R;

import static chat.chat.chat.ChatActivity.TEMP_PHOTO_JPG;

public class NoticeComposerActivity extends AppCompatActivity implements ChooserDialog.ChooserDialogListener{
    private EditText mMessage;
    private Button mSendBtn,mDocSend;
    static String mList[];
    String text;
    private List<String> selectedSections=new ArrayList<>(),selectedFaculty=new ArrayList<>();
    int caller=8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_composer);
        //getSupportActionBar().setTitle("Notice");

        mMessage= (EditText) findViewById(R.id.notice);
        mSendBtn= (Button) findViewById(R.id.sendBtn);
        mDocSend=(Button) findViewById(R.id.docSendBtn);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    caller = 0;
                    if (!TextUtils.isEmpty(mMessage.getText())) {
                        text = mMessage.getText().toString();
                        initList();
                    } else {
                        Toast.makeText(NoticeComposerActivity.this, "Empty Field!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(NoticeComposerActivity.this, "Not connected to the internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDocSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                boolean isConnected = info != null && info.isConnectedOrConnecting();
                if (isConnected) {
                    //UploadHelper uploadHelper=new UploadHelper();
                    PickerDialogFragment f = new PickerDialogFragment();
                    f.show(getFragmentManager(), "Picker");
                }
                else
                {
                    Toast.makeText(NoticeComposerActivity.this, "Not connected to the internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void initList()
    {
        final List<String> list=new ArrayList<>();
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("Sections");

        mRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                for(MutableData d:mutableData.getChildren())
                {
                    list.add(d.getKey());
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Faculty");
                    reference.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            for (MutableData d : mutableData.getChildren()) {
                                if(!d.getKey().equals(ChatApp.user.getUsername())) {
                                    list.add(d.getKey());
                                }
                            }

                            return Transaction.success(mutableData);
                        }


                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            mList = new String[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                mList[i] = list.get(i);
                            }
                            ChooserDialog c = new ChooserDialog();
                            c.show(getFragmentManager(), "ChooserDialog");
                        }
                    });

            }
        });


    }
    List<Integer> selectedItems;
    @Override
    public void onPositiveButtonClicked(String[] mList, List<Integer> selectedItems) {
        this.selectedItems=selectedItems;
        if(caller==0) {
            sendMessage("null","default",System.currentTimeMillis());
        }
        else {
            sendMessage(type,link,timestamp);
        }
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
            Toast.makeText(NoticeComposerActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(NoticeComposerActivity.this, "Your category was not well defined!", Toast.LENGTH_LONG).show();
        }
    }
    private final int IMG=0;
    private final int DOC=1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UploadHelper uploadHelper=new UploadHelper(NoticeComposerActivity.this,mMessage,"NoticeComposerActivity",selectedItems);
        if(requestCode==IMG)
        {
            if(resultCode==RESULT_OK)
            {
                caller=1;
                Intent intent=new Intent(NoticeComposerActivity.this,ImageTitleActivity.class);
                Uri photo=(Uri)data.getData();
                intent.putExtra("image",photo);
                intent.putExtra("context","NoticeComposerActivity");
                uploadHelper.makeTempAndUpload(intent,photo,TEMP_PHOTO_JPG);
                finish();
            }
        }
        else if(requestCode==DOC)
        {
            if(resultCode==RESULT_OK)
            {
                caller=1;

                if(!TextUtils.isEmpty(mMessage.getText())) {
                    Uri uri = data.getData();
                    ChatApp.mProgress=new ProgressDialog(this);
                    ChatApp.mProgress.setTitle("Uploading...");
                    ChatApp.mProgress.setMessage("Please wait while your document is being uploaded.");
                    ChatApp.mProgress.setCanceledOnTouchOutside(true);
                    ChatApp.mProgress.show();
                    uploadHelper.makeTempAndUpload(new Intent(), uri, "temp_doc.pdf");
                }
                else
                {
                    Toast.makeText(this, "Please add a message first!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    public void sendMessage(String type,String link,long timestamp)
    {
        if(selectedItems!=null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String sender = ChatApp.user.getCR();
            Map map = new HashMap();
            if(txt==null)
            {
                txt=text;
            }
            mMessage.setText("");
            map.put("text", txt);
            map.put("from", uid);
            map.put("sender", sender);
            map.put("link", link);
            map.put("type", type);
            map.put("timestamp", timestamp);
            Messages messages = new Messages(uid, txt, sender, "default", "null", timestamp);
            List<String> categ = messages.getHashTag();
            String authStr = "To:\n";
            for (int i = 0; i < selectedItems.size(); i++) {
                String str = NoticeComposerActivity.mList[selectedItems.get(i)];
                if (str.indexOf("fac") != -1) {
                    ifFaculty(mDatabase, str, categ, map);
                } else {
                    ifSection(mDatabase, str, categ, map);
                }
                authStr += str + "\n";
            }
            authStr =txt+ "\n\n" +authStr ;
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
            startActivity(new Intent(NoticeComposerActivity.this, AuthNotice.class));
            finish();
        }
    }
    public void getLink(Uri uri,String type,String txt,long timestamp)
    {
        this.txt=txt;
        link=uri.toString();
        this.type=type;
        this.timestamp=timestamp;
        ChatApp.mProgress.dismiss();
        initList();
    }

    String link,type,txt;
    long timestamp;
}

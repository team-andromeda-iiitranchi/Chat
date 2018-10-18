package chat.chat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import chat.chat.ChatApp;
import chat.chat.R;

public class AuthNotice extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private AuthPagerAdapter authPagerAdapter;
    public Toolbar toolbar;
    public static final int STUDENT=1;
    public static final int AUTH=2;
    public static final int DIR=3;
    public static final int FAC=4;
    private ProgressDialog mProgress;
    private int choice;
    private FragmentAuthChat fragmentAuthChat;
    private FragmentStudentChat fragmentStudentChat;
    private DirFacChat dirFacChat;
    private FacDirChat facDirChat;
    private DirPagerAdapter dirPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_notice);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Uploading Document");
        mProgress.setMessage("Please wait while your document is being uploaded.");
        mProgress.setCanceledOnTouchOutside(false);



        if(ChatApp.user.getCR().equals("faculty")) {
            tabLayout = (TabLayout) findViewById(R.id.authTabLayout);
            authPagerAdapter = new AuthPagerAdapter(getSupportFragmentManager(),AuthNotice.this);
            mViewPager = (ViewPager) findViewById(R.id.authPager);
            mViewPager.setAdapter(authPagerAdapter);
            mViewPager.setOffscreenPageLimit(4);
            tabLayout.setupWithViewPager(mViewPager);
            mViewPager.setCurrentItem(1);

        }
        else
        {
            tabLayout=(TabLayout)findViewById(R.id.authTabLayout);
            dirPagerAdapter=new DirPagerAdapter(getSupportFragmentManager(),AuthNotice.this);
            mViewPager=(ViewPager)findViewById(R.id.authPager);
            mViewPager.setAdapter(dirPagerAdapter);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(0);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }
    void setFragmentStudentChat(FragmentStudentChat fragmentStudentChat)
    {
        this.fragmentStudentChat=fragmentStudentChat;
    }
    void setFragmentAuthChat(FragmentAuthChat fragmentAuthChat)
    {
        this.fragmentAuthChat=fragmentAuthChat;
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(ChatApp.user.getCR().equals("faculty")) {
            if (fragmentAuthChat == null && fragmentStudentChat == null) {
                finish();
            } else if (fragmentAuthChat.state == 1 && fragmentStudentChat.state == 1) {
                finish();
            } else if (fragmentAuthChat.state == 2 && fragmentStudentChat.state == 2) {
                fragmentAuthChat.toggle();
                fragmentStudentChat.toggle();
            } else if (fragmentAuthChat.state == 2) {
                fragmentAuthChat.toggle();
            } else {
                fragmentStudentChat.toggle();
            }
        }
        else
        {
            if(dirFacChat !=null&& dirFacChat.state==2)
            {
                dirFacChat.toggle();
            }
            else
            {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.account)
        {
            Intent intent=new Intent(AuthNotice.this,AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(AuthNotice.this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            finish();
        }
        else if(item.getItemId()==R.id.archives)
        {
            Intent i=new Intent(AuthNotice.this,LibraryActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.categories)
        {
            Intent i=new Intent(AuthNotice.this,CategoryViewer.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    private final int IMG=0;
    private final int DOC=1;
    public static final int IMG_UPLD=3;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG)
        {
            if(resultCode==RESULT_OK)
            {
                Intent i=new Intent(AuthNotice.this,AuthImageActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.putExtra("imageUri",data.getData());
                startActivityForResult(i,IMG_UPLD);
            }
        }
        else if(requestCode==DOC)
        {
            if(resultCode==RESULT_OK)
            {
                mProgress.show();
                String text="";
                Long timestamp=System.currentTimeMillis();
                String type="doc";
                Uri fileUri=data.getData();
                StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("Uploads").child("A"+timestamp+".pdf");
                upload(mStorage,fileUri,text,timestamp,type);
            }
        }
        else if(requestCode==IMG_UPLD)
        {
            if(resultCode==RESULT_OK) {
                String text = data.getStringExtra("text");
                String linkUri = data.getStringExtra("link");
                Long timestamp=data.getLongExtra("timestamp",0);
                String type = "image";
                String link = linkUri;
                if (choice == STUDENT) {
                    fragmentStudentChat.sendMessage(link, type, text,timestamp);
                } else if(choice==AUTH){
                    fragmentAuthChat.sendMessage(link, type, text,timestamp);
                }
                else if(choice==DIR)
                {
                    dirFacChat.sendMessage(link,type,text,timestamp);
                }
                else
                {
                    facDirChat.sendMessage(link,type,text,timestamp);
                }
            }
        }
    }

    private void upload(final StorageReference mStorage, Uri fileUri, final String text, final Long timestamp, final String type) {
        UploadTask uploadTask= mStorage.putFile(fileUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(AuthNotice.this, "Failed to Upload!", Toast.LENGTH_SHORT).show();
            }
        });
        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful())
                {
                    mProgress.dismiss();
                    throw task.getException();
                }
                return  mStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                mProgress.dismiss();
                String link=task.getResult().toString();
                if(choice==STUDENT)
                {
                    fragmentStudentChat.sendMessage(link,type,text,timestamp);
                }
                else if(choice==AUTH)
                {
                    fragmentAuthChat.sendMessage(link,type,text,timestamp);
                }
                else if(choice==DIR)
                {
                    dirFacChat.sendMessage(link,type,text,timestamp);
                }
                else
                {
                    facDirChat.sendMessage(link,type,text,timestamp);
                }
            }
        });

    }

    public void setChoice(int ch)
    {
        choice=ch;
    }
    public Toolbar getToolbar()
    {
        return toolbar;
    }

    public void setDirFacChat(DirFacChat dirFacChat) {
        this.dirFacChat = dirFacChat;
    }

    public void setFacDirChat(FacDirChat facDirChat) {
        this.facDirChat = facDirChat;
    }
}


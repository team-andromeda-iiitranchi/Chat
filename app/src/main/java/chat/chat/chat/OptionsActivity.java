package chat.chat.chat;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chat.chat.ChatApp;
import chat.chat.DeveloperActivity;
import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static chat.chat.chat.AuthNotice.IMG_UPLD;
import static chat.chat.chat.ChatActivity.TEMP_PHOTO_JPG;

public class OptionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private RelativeLayout.LayoutParams params;
    private NavigationView navigationView;
    private TextView linkTV;
    private List<Messages> mList;
    private View inflatedView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout relativeLayout;
    private MessageAdapter messageAdapter;
    private EditText messageView;
    private ImageView sendBtn;
    private LinearLayout linearLayout;
    private DatabaseReference mRef;
    private String nameStr;
    private AppBarLayout appBar;
    static int state=2;
    private String nameFac;
    private int counter=0;
    private String currentUid;
    private String facUid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ProgressDialog mProgress=new ProgressDialog(this);
        relativeLayout= (RelativeLayout) findViewById(R.id.view_pager);
        appBar= (AppBarLayout) findViewById(R.id.appBarLayout);
        params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading Document");
        progressDialog.setMessage("Please wait while your document is being uploaded.");
        progressDialog.setCanceledOnTouchOutside(false);


        mProgress.setTitle("Loading!");
        mProgress.setMessage("Getting User's Data");
        mProgress.setCanceledOnTouchOutside(false);
        initDrawer();
        initPager();
        mRef=FirebaseDatabase.getInstance().getReference();
        navigationView.setCheckedItem(R.id.notices);
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser mUser=firebaseAuth.getCurrentUser();
                if(mUser==null)
                {

                    mProgress.dismiss();
                    Intent i=new Intent(OptionsActivity.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    finish();
                }
                else
                {
                    initPager();
                    DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final List votedList=new ArrayList();


                    Query q=mRef.child("Users").orderByKey().equalTo(uid);
                    q.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Users users= dataSnapshot.getValue(Users.class);
                            ChatApp.user=users;
                            ChatApp.rollInfo=users.getUsername().substring(0,8);

                            if(!(users.getCR().equalsIgnoreCase("Faculty")||users.getCR().equalsIgnoreCase("Director"))) {
                                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                                mViewPager.setAdapter(mSectionsPagerAdapter);
                                mViewPager.setOffscreenPageLimit(3);
                                tabLayout.setupWithViewPager(mViewPager);
                                mViewPager.setCurrentItem(1);

                                //if not CR remove faculty options from drawer
                                if(ChatApp.user.getCR().equals("false")) {
                                    Menu menu = navigationView.getMenu();
                                    MenuItem facItem = menu.findItem(R.id.faculty);
                                    facItem.setEnabled(false);
                                    facItem.setVisible(false);
                                }


                                Map map = users.getPolls();
                                if (map != null) {
                                    Iterator iterator = map.entrySet().iterator();
                                    while (iterator.hasNext()) {
                                        Map.Entry pair = (Map.Entry) iterator.next();
                                        votedList.add(pair.getKey());
                                    }
                                }
                            }
                            else
                            {
                                Intent i=new Intent(OptionsActivity.this,AuthNotice.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(i);
                                finish();
                            }
                            mProgress.dismiss();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });



    }

    private void initPager() {
        mViewPager = (ViewPager) findViewById(R.id.tabPager);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(state==1&&ChatApp.user.getCR().equals("true"))
        {
            state=2;
            ChatFragment cf=mSectionsPagerAdapter.getCf();
            cf.toggle(this,getSupportActionBar());

        }
        else if(ChatApp.user.getCR().equals("true")&&state==4){
            toggle();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options_activits,menu);
        return true;
    }
    public void initDrawer()
    {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader=navigationView.getHeaderView(0);
        linkTV= (TextView) navHeader.findViewById(R.id.linkTV);
        linkTV.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(OptionsActivity.this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            unsubscribe();
            finish();
        }
        if(item.getItemId()==R.id.account)
        {
            Intent intent=new Intent(OptionsActivity.this,AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void unsubscribe() {

        if(ChatApp.user.getUsername().indexOf("fac")==-1&&ChatApp.user.getUsername().indexOf("dir")==-1)
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(ChatApp.user.getUsername().substring(0,8));
            FirebaseMessaging.getInstance().unsubscribeFromTopic("students");
        }
        else if(ChatApp.user.getUsername().indexOf("fac")!=-1)
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("faculty");
        }
        else
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("director");
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.notices)
        {
            showNotices();
        }
        else if(id==R.id.library)
        {
            Intent intent=new Intent(OptionsActivity.this,LibraryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        if(id==R.id.faculty)
        {
            showFaculty();
        }
        if(id==R.id.devs)
        {
            startActivity(new Intent(OptionsActivity.this, DeveloperActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showNotices() {
        state=1;
        relativeLayout.removeAllViews();
        appBar.removeView(tabLayout);
        params.topMargin=(int)getResources().getDimension(R.dimen.twice_app_bar_height);
        relativeLayout.setLayoutParams(params);
        relativeLayout.addView(mViewPager);
        appBar.addView(tabLayout);
    }

    private void showFaculty() {
        state=3;
        appBar.removeView(tabLayout);
        params.topMargin= (int) getResources().getDimension(R.dimen.app_bar_height);
        relativeLayout.setLayoutParams(params);
        //chat view for chat with fac
        listState();

    }
    void listState()
    {
        relativeLayout.removeAllViews();
        inflatedView=LayoutInflater.from(this).inflate(R.layout.auth_chat_layout,null,false);
        relativeLayout.addView(inflatedView);
        linearLayout= (LinearLayout) inflatedView.findViewById(R.id.linearLayout);

        Query q=mRef.child("Users");
        q.orderByChild("CR").equalTo("faculty").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final View user=LayoutInflater.from(OptionsActivity.this).inflate(R.layout.all_users_layout,null,false);
                linearLayout.addView(user);
                String image=dataSnapshot.child("imageLink").getValue().toString();
                final String name=dataSnapshot.child("Name").getValue().toString();
                final String uid=dataSnapshot.getKey();
                final String username=dataSnapshot.child("username").getValue().toString();

                final CircleImageView pic= (CircleImageView) user.findViewById(R.id.picture);
                TextView displayName= (TextView) user.findViewById(R.id.displayName);
                final CircleImageView dot= (CircleImageView) user.findViewById(R.id.dot);
                //set onClickListener on CircleImageView to display
                //profile picture of the user
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserImgDialogUtil dialogUtil=new UserImgDialogUtil();
                        dialogUtil.showDialog(pic,OptionsActivity.this,uid);
                    }
                });


                Picasso.get().load(image).placeholder(R.drawable.default_pic).into(pic);
                displayName.setText(name);

                user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nameStr=username;
                        nameFac=name;
                        facUid=uid;
                        toggle();
                    }
                });
                currentUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                mRef.child("lastSeen").child(currentUid).child(uid).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getValue().toString().equals("1"))
                        {
                            dot.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getValue().toString().equals("1"))
                        {
                            dot.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void toggle() {
        if(state==3)
        {
            state=4;
            setBackEnabled();
            chatState();
        }
        else
        {
            state=3;
            initDrawer();
            listState();
        }
    }

    private void setBackEnabled() {

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackDisabled();
                toggle();
            }
        });
    }

    private void setBackDisabled() {
        getSupportActionBar().setTitle("IIIT RANCHI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    void chatState()
    {
        getSupportActionBar().setTitle(nameFac);
        relativeLayout.removeView(inflatedView);
        inflatedView= LayoutInflater.from(this).inflate(R.layout.other_chat_fragment,null,false);
        relativeLayout.addView(inflatedView);

        recyclerView= (RecyclerView) inflatedView.findViewById(R.id.recView2);
        mList=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(this);
        messageAdapter=new MessageAdapter(mList,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        loadMessages();


        messageView= (EditText) inflatedView.findViewById(R.id.message);
        sendBtn= (ImageView) inflatedView.findViewById(R.id.send);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(messageView.getText()))
                {
                    String text=messageView.getText().toString();
                    messageView.setText("");
                    sendMessage(text,"null","default",System.currentTimeMillis());
                }
            }
        });
        sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PickerDialogFragment pickerDialogFragment=new PickerDialogFragment();
                pickerDialogFragment.show(getFragmentManager(), "picker");

                return true;
            }
        });
    }

    private void loadMessages() {
        mRef.child("lastSeen").child(currentUid).child(facUid).child("unseen").setValue(0);
        mRef.child(ChatApp.rollInfo.substring(0,8)).child("CR").child(nameStr).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                if(mList.size()==0||mList.get(mList.size()-1).getTimestamp()!=messages.getTimestamp())
                {
                    mList.add(messages);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(mList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String text,String type,String link,Long timestamp) {

        String curUid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map map=new HashMap();
        map.put("text",text);
        map.put("from",curUid);
        map.put("timestamp",timestamp);
        map.put("type",type);
        map.put("link",link);
        map.put("sender","Student");

        String key=mRef.child(ChatApp.rollInfo.substring(0,8)).child("CR").child(nameStr).push().getKey();
        mRef.child(ChatApp.rollInfo.substring(0,8)).child("CR").child(nameStr).child(key).setValue(map);

        mRef.child("lastSeen").child(facUid).child(ChatApp.rollInfo.substring(0,8)).child("unseen").setValue(1);

    }

    public Toolbar getToolBar()
    {
        return toolbar;
    }
    private final int IMG=0;
    private final int DOC=1;
    private EditText mMessage;
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mMessage=ChatFragment.mMessage;
        UploadHelper uploadHelper = new UploadHelper(OptionsActivity.this, mMessage,"ChatFragment");
        if (requestCode == IMG&&state<3) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(OptionsActivity.this, ImageTitleActivity.class);
                Uri photo = data.getData();
                intent.putExtra("image", photo);
                intent.putExtra("context","ChatFragment");
                intent.putExtra("receiver",ChatFragment.receiver);
                uploadHelper.makeTempAndUpload(intent, photo, TEMP_PHOTO_JPG);
            }
        } else if (requestCode == DOC&&state<3) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(mMessage.getText())) {
                    Uri uri = data.getData();
                    uploadHelper.makeTempAndUpload(new Intent(), uri, "temp_doc.pdf");
                } else {
                    Toast.makeText(OptionsActivity.this, "Please add a message first!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode==IMG&&state>=3)
        {
            if(resultCode==RESULT_OK)
            {
                Intent i=new Intent(OptionsActivity.this,AuthImageActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.putExtra("imageUri",data.getData());
                startActivityForResult(i,IMG_UPLD);
            }
        }
        else if(requestCode==DOC&&state>=3)
        {
            if(resultCode==RESULT_OK)
            {
                progressDialog.show();
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
            if(resultCode==RESULT_OK)
            {
                String text=data.getStringExtra("text");
                String link=data.getStringExtra("link");
                String type="image";
                Long timestamp=data.getLongExtra("timestamp",0);
                sendMessage(text,type,link,timestamp);
            }
        }
    }
    private void upload(final StorageReference mStorage, Uri fileUri, final String text, final Long timestamp, final String type) {
        UploadTask uploadTask= mStorage.putFile(fileUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(OptionsActivity.this, "Failed to Upload!", Toast.LENGTH_SHORT).show();
            }
        });
        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful())
                {
                    progressDialog.dismiss();
                    throw task.getException();
                }
                return  mStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressDialog.dismiss();
                String link=task.getResult().toString();
                sendMessage(text,type,link,timestamp);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser mUser=firebaseAuth.getCurrentUser();
                if(mUser==null)
                {
                    Intent i=new Intent(OptionsActivity.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    finish();
                }
                else
                {
                    DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();


                    Query q=mRef.child("Users").orderByKey().equalTo(uid);
                    q.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Users users= dataSnapshot.getValue(Users.class);
                            ChatApp.user=users;
                            ChatApp.rollInfo=users.getUsername().substring(0,8);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }
}

package chat.chat.chat;

import android.app.Activity;
import android.app.FragmentContainer;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.List;

import chat.chat.ChatApp;
import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter {
    private List mList;
    private ChatFragment chatFragment;

    public UserAdapter(List mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    private Context mContext;
    public UserAdapter() {
    }

    public UserAdapter(List mList,ChatFragment chatFragment) {
        this.mList = mList;
        this.chatFragment=chatFragment;
    }

    private View mView;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_layout,parent,false);
        mView=view;
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Users users=(Users) mList.get(position);
        if(users.getIsUnseen().equals("true"))
        {
            mView.setBackgroundResource(R.color.pollColor);
        }
        ((UserHolder)holder).bind(users);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String username=users.getUsername();
                final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("Users");
                Query q=mRef.orderByChild("username").equalTo(username);
                q.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String uid=dataSnapshot.getKey().toString();

                        mRef.child(uid).child("isUnseen").setValue("false");
                        chatFragment.inflateForCROnItemClicked(uid);
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
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    private class UserHolder extends RecyclerView.ViewHolder{
        TextView displayName;
        CircleImageView circleImageView;
        public UserHolder(View itemView) {
            super(itemView);
            displayName=(TextView)itemView.findViewById(R.id.displayName);
            circleImageView= (CircleImageView) itemView.findViewById(R.id.picture);
        }
        public void bind(Users users)
        {
            displayName.setText(users.getName());
            DatabaseReference mReference=FirebaseDatabase.getInstance().getReference();
            Query q=mReference.child("Users").orderByChild("username").equalTo(users.getUsername());
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String uid="";
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        uid=dataSnapshot1.getKey();
                    }
                    if(!uid.equals("")) {
                        UserImgDialogUtil dialogUtil=new UserImgDialogUtil();
                        dialogUtil.showDialog(circleImageView,chatFragment.getContext(),uid);
                        setUserImage(circleImageView, uid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public void setUserImage(final CircleImageView userImage, String uid) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
        mRef.child("Users").child(uid).child("imageLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    String link = dataSnapshot.getValue().toString();
                    if (!link.equals("null")) {
                        Picasso.get().load(link).placeholder(R.drawable.default_pic).into(userImage);
                    } else {
                        Picasso.get().load(R.drawable.default_pic).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

package chat.chat.chat;

import android.app.Activity;
import android.app.FragmentContainer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.awt.font.TextAttribute;
import java.util.List;

import chat.chat.R;

public class UserAdapter extends RecyclerView.Adapter {
    private List mList;
    private ChatFragment chatFragment;
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
        ((UserHolder)holder).bind(users);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Log.e("ITEMCLICK :",""+position);
                String username=users.getUsername();
                DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("Users");
                Query q=mRef.orderByChild("username").equalTo(username);
                q.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String uid=dataSnapshot.getKey();
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
        public UserHolder(View itemView) {
            super(itemView);
            displayName=(TextView)itemView.findViewById(R.id.displayName);
        }
        public void bind(Users users)
        {
            displayName.setText(users.getName());
        }
    }
}

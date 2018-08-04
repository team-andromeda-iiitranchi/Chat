package chat.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chat.chat.R;

public class PollAdapter extends RecyclerView.Adapter {
    public PollAdapter()
    {

    }
    private View mView;
    private Context context;
    private DatabaseReference mRef;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_layout,parent,false);
        mView=view;
        return new PollHolder(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Poll poll=(Poll)mList.get(position);
        ((PollHolder)holder).onBind(poll,position);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private List<Poll> mList;
    public PollAdapter(List<Poll> mList,Context context)
    {
        this.mList=mList;
        this.context=context;
    }
    class PollHolder extends RecyclerView.ViewHolder
    {
        TextView title,description;
        View view;
        public PollHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.title2);
            description=(TextView)itemView.findViewById(R.id.description2);
            view=itemView;
        }
        public void onBind(Poll poll, final int position)
        {
            title.setText(poll.getTitle());
            description.setText(poll.getDescription());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mRef=FirebaseDatabase.getInstance().getReference();
                    mRef.child("Users").child(uid).child("polls").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = 0;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String data = dataSnapshot1.getValue().toString();
                                if (data.equals("0"))
                                    count++;
                            }
                            int count2 = 0;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String k = dataSnapshot1.getKey();
                                String data = dataSnapshot1.getValue().toString();
                                if (data.equals("0")) {

                                    if (count - 1 - count2 == position) {
                                        Intent i = new Intent(context, VoteActivity.class);
                                        i.putExtra("pushId", k);
                                        context.startActivity(i);
                                        break;
                                    }
                                    count2++;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Users").child(uid).child("CR").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String isCr=dataSnapshot.getValue().toString();
                            if(isCr.equals("true"))
                            {
                                //start dialog
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
            });

        }
    }
}

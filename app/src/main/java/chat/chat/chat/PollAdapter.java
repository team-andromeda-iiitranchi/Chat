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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        ((PollHolder)holder).onBind(poll);
        mRef=FirebaseDatabase.getInstance().getReference();
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRef.child("Poll").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count=0;
                        String pushId="";
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            if(count==mList.size()-1-position)
                            {
                                pushId=dataSnapshot1.getKey();
                                Intent intent=new Intent(context,VoteActivity.class);
                                intent.putExtra("pushId",pushId);
                                context.startActivity(intent);
                            }
                            count++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "" +databaseError.getCode(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
        public PollHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.title2);
            description=(TextView)itemView.findViewById(R.id.description2);
        }
        public void onBind(Poll poll)
        {
            title.setText(poll.getTitle());
            description.setText(poll.getDescription());
        }
    }
}

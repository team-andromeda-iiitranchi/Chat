package chat.chat.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import chat.chat.ChatApp;
import chat.chat.R;

public class PollAdapter extends RecyclerView.Adapter {
    public PollAdapter()
    {

    }
    private View mView;
    private Context context;
    private DatabaseReference mRef;
    private String uid;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_layout,parent,false);
        mView=view;
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser!=null){
            uid = fUser.getUid();
        }
        mRef=FirebaseDatabase.getInstance().getReference();
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
        TextView title,description,voted;
        View view;
        public PollHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.title2);
            description=(TextView)itemView.findViewById(R.id.description2);
            view=itemView;
            voted=(TextView)itemView.findViewById(R.id.voted);
        }
        public void onBind(Poll poll, final int position)
        {
            title.setText(poll.getTitle());
            description.setText(poll.getDescription());
            identify(position,2,voted);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    identify(position,0, voted);
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
                                identify(position,1, voted);
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
    public void identify(final int position, final int type, final TextView voted)
    {

        mRef.child("Users").child(uid).child("polls").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count=0;
                long total=dataSnapshot.getChildrenCount();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String key=dataSnapshot1.getKey();
                    String value=dataSnapshot1.getValue().toString();
                    if(count!=0)
                    {
                        if(total-count-1==position)
                        {
                            if(type==1)
                            {
                                //long press
                                PollDialog pollDialog=new PollDialog();
                                Bundle bundle=new Bundle();
                                bundle.putString("pushId",key);
                                pollDialog.setArguments(bundle);
                                Activity activity=(Activity)context;
                                pollDialog.show(activity.getFragmentManager(),"pollDialog");
                            }
                            else if(type==0)
                            {
                                //onClickListener
                                if(value.equals("0"))
                                {
                                    Intent i=new Intent(context,VoteActivity.class);
                                    i.putExtra("pushId",key);
                                    context.startActivity(i);
                                }
                                else
                                {
                                    Toast.makeText(context, "You've already put your vote!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                //cheks if user has voted or not
                                if(value.equals("0")) {
                                    voted.setText("Not Voted");
                                }
                                else
                                {
                                    voted.setText("Voted");
                                }
                            }
                        }
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

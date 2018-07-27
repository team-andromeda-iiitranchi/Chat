package chat.chat.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import chat.chat.R;

class MessageAdapter extends RecyclerView.Adapter
{
    List<Messages> mList;
    private static final int RECIEVED_MESSAGE=1;
    private static final int SENT_MESSAGE=2;
    MessageAdapter(List<Messages> list)
    {
        mList=list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==RECIEVED_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }
        else
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_layout,parent,false);
            return  new SentMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages messages=(Messages)mList.get(position);
        if(getItemViewType(position)==RECIEVED_MESSAGE) {
            ((ReceivedMessageHolder) holder).bind(messages);
        }
        else
        {
            ((SentMessageHolder)holder).bind(messages);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages=(Messages)mList.get(position);
        if(messages.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            return SENT_MESSAGE;
        }
        return RECIEVED_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView displayName,text,recTimeView;
        public ReceivedMessageHolder(View view) {
            super(view);
            displayName=(TextView)view.findViewById(R.id.displayname);
            text=(TextView)view.findViewById(R.id.messview);
            recTimeView=(TextView)view.findViewById(R.id.recTimeView);
        }
        void bind(Messages messages)
        {
            DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();

            mRef.child("Users").child(messages.getFrom()).child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null) {
                        String name = dataSnapshot.getValue().toString();
                        displayName.setText(name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            text.setText(messages.getText());
            recTimeView.setText(date(messages.getTimestamp()));
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView text;
        TextView timeView;

        public SentMessageHolder(View itemView) {
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.text);
            timeView=(TextView)itemView.findViewById(R.id.timeView);
        }
        void bind(Messages messages)
        {
            text.setText(messages.getText());
            timeView.setText(date(messages.getTimestamp()));
        }
    }

  public String date(long timeStamp)
  {
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
      String dateString = formatter.format(new Date(Long.parseLong(""+timeStamp)));
      dateString=dateString.toUpperCase();
      return dateString;
  }
}
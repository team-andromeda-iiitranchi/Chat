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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import chat.chat.R;

public class HashtagAdapter extends RecyclerView.Adapter{
    public NoticeFragment noticeFragment;
    public HashtagAdapter(List mList,NoticeFragment noticeFragment) {
        this.noticeFragment=noticeFragment;
        this.mList = mList;
    }

    private List mList;
    private View mView ;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_layout,null,false);
        mView=view;
        return new HashtagHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((HashtagHolder) holder).onBind((Hashtag) mList.get(position));

        //attaching Listener
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hashtag hashtag=(Hashtag)mList.get(position);
               noticeFragment.onItemClicked(hashtag.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    class HashtagHolder extends RecyclerView.ViewHolder{
        private TextView hashtagView;
        public HashtagHolder(View itemView) {
            super(itemView);
            hashtagView=(TextView)itemView.findViewById(R.id.hashtagView);
        }
        public void onBind(Hashtag hashtag)
        {
            hashtagView.setText(hashtag.getName());
        }
    }
}

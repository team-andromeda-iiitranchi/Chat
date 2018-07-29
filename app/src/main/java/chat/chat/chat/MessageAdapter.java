package chat.chat.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import chat.chat.R;

class MessageAdapter extends RecyclerView.Adapter
{
    List<Messages> mList;
    private static final int RECIEVED_MESSAGE=1;
    private static final int SENT_MESSAGE=2;
    private static final int SENT_FILE=3;
    private static final int RECEIVED_FILE=4;
    private ImageView downloadView;
    private final int IMG=0;
    //private static final int SENT_DOC=5;

    public MessageAdapter(List<Messages> mList, ChatActivity chatActivity) {
        this.mList = mList;
        this.chatActivity = chatActivity;
    }

    private View mView;
    private TextView tv;
    public MessageAdapter(List<Messages> mList, ChatFragment chatFragment) {
        this.mList = mList;
        this.chatFragment = chatFragment;
    }

    private ChatFragment chatFragment;
    private ChatActivity chatActivity;
    MessageAdapter(List<Messages> list)
    {
        mList=list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==RECIEVED_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view_layout, parent, false);
            mView=view;
            return new ReceivedMessageHolder(view);
        }
        else if(viewType==SENT_MESSAGE)
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_layout,parent,false);
            mView=view;
            return  new SentMessageHolder(view);
        }
        else
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_file_layout,parent,false);
            mView=view;
            return new SentImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages messages=(Messages)mList.get(position);
        if(chatActivity!=null) {
            final ClipboardManager clipboardManager = (ClipboardManager) chatActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            final TextView tv;
            if(getItemViewType(position)==RECIEVED_MESSAGE||getItemViewType(position)==SENT_MESSAGE) {
                if (getItemViewType(position) == RECIEVED_MESSAGE) {
                    tv = (TextView) mView.findViewById(R.id.messview);
                } else
                    tv = (TextView) mView.findViewById(R.id.text);
                if(tv!=null) {
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String message = tv.getText().toString();
                            ClipData myClip;
                            myClip = ClipData.newPlainText("text", message);
                            clipboardManager.setPrimaryClip(myClip);

                            Toast.makeText(chatActivity.getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
        if(chatFragment!=null) {
            final ClipboardManager clipboardManager = (ClipboardManager) chatFragment.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

            if (getItemViewType(position) == RECIEVED_MESSAGE || getItemViewType(position) == SENT_MESSAGE) {
                if (getItemViewType(position) == RECIEVED_MESSAGE) {
                    tv = (TextView) mView.findViewById(R.id.messview);
                } else {
                    tv = (TextView) mView.findViewById(R.id.text);
                }
                if(tv!=null) {
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String message = tv.getText().toString();
                            ClipData myClip;
                            myClip = ClipData.newPlainText("text", message);
                            clipboardManager.setPrimaryClip(myClip);

                            Toast.makeText(chatFragment.getActivity(), "Text Copied", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }

        if(getItemViewType(position)==RECIEVED_MESSAGE) {
            ((ReceivedMessageHolder) holder).bind(messages);
        }
        else if(getItemViewType(position)==SENT_MESSAGE)
        {
            ((SentMessageHolder)holder).bind(messages);
        }
        else if(getItemViewType(position)==SENT_FILE)
        {
            ((SentImageHolder) holder).bind(messages);
        }
        else
        {

        }
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages=(Messages)mList.get(position);
        String currentUserUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(messages.getType().equals("null")&&messages.getFrom().equals(currentUserUid))
        {
            return SENT_MESSAGE;
        }
        else if(messages.getType().equals("null")&&!(messages.getFrom().equals(currentUserUid))) {
            return RECIEVED_MESSAGE;
        }
        else if(messages.getFrom().equals(currentUserUid))
        {
            return SENT_FILE;
        }
        else
        {
            return RECEIVED_FILE;
        }
    }

    void loadImage(final Messages messages, final ImageView downloadView)
    {
        final String root = Environment.getExternalStorageDirectory().toString();
        File myFile = new File(root + "/ChatApp");
        final String name = "A" + messages.getTimestamp() + ".jpg";
        if(!myFile.exists())
        {
            myFile.mkdirs();
        }
        myFile = new File(myFile, name);
        if (!myFile.exists()) {
            downloadView.setImageResource(R.drawable.download_icon);
            final File finalMyFile = myFile;
            downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Picasso.get().load(messages.getLink()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            try {
                                FileOutputStream fos = new FileOutputStream(finalMyFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finally {
                                Picasso.get().load(finalMyFile).into(downloadView);
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                }
            });
        }
        else {
            Picasso.get().load(myFile).into(downloadView);
            final File finalMyFile1 = myFile;
            downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(chatActivity, "Gallery!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    private class SentImageHolder extends RecyclerView.ViewHolder
    {
        TextView messageView,timeView;
        ImageView downloadView;
        public SentImageHolder(View itemView) {
            super(itemView);
            messageView=(TextView)itemView.findViewById(R.id.text);
            timeView=(TextView)itemView.findViewById(R.id.timeView);
            downloadView=(ImageView)itemView.findViewById(R.id.downloadOption);
        }
        public void bind(final Messages messages)
        {
            messageView.setText(messages.getText());
            timeView.setText(date(messages.getTimestamp()));
            loadImage(messages,downloadView);
        }

    }
    private class ReceivedImageHolder extends RecyclerView.ViewHolder {

        public ReceivedImageHolder(View itemView) {
            super(itemView);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView displayName,text,recTimeView;
        ImageView displayPic;
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
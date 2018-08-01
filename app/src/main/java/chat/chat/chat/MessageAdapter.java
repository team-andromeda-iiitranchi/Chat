package chat.chat.chat;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    public MessageAdapter(List<Messages> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    private Context mContext;
    private ImageView downloadView;
    private final int IMG=0;
    //private static final int SENT_DOC=5;

    private View mView;

    private TextView tv;
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
        else if(viewType==SENT_FILE)
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_file_layout,parent,false);
            mView=view;
            return new SentImageHolder(view);
        }
        else
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.received_file_layout,parent,false);
            mView=view;
            return new ReceivedImageHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages messages=(Messages)mList.get(position);
        final ClipboardManager clipboardManager;
        clipboardManager=(ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        final TextView tv;
        if(getItemViewType(position)==RECIEVED_MESSAGE||getItemViewType(position)==SENT_MESSAGE) {
            if (getItemViewType(position) == RECIEVED_MESSAGE) {
                tv = (TextView) mView.findViewById(R.id.messview);
            } else
                tv = (TextView) mView.findViewById(R.id.text);
            if(tv!=null) {
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String message = tv.getText().toString();
                        ClipData myClip;
                        myClip = ClipData.newPlainText("text", message);
                        clipboardManager.setPrimaryClip(myClip);
                        Toast.makeText(mContext,"Text Copied!",Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
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
            ((ReceivedImageHolder)holder).bind(messages);
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
                                downloadView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showInGallery(finalMyFile, messages);
                                    }
                                });
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
                    showInGallery(finalMyFile1,messages);
                }
            });
        }
    }

    private void showInGallery(File finalMyFile, Messages messages) {
        Intent intent = new Intent(mContext, ImageViewer.class);
        intent.putExtra("file", finalMyFile);
        intent.putExtra("text", messages.getText());
        mContext.startActivity(intent);

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
            if(messages.getType().equals("image")) {
                loadImage(messages, downloadView);
            }
            else
            {
                loadDoc(downloadView, messages);
            }
        }

    }
    private class ReceivedImageHolder extends RecyclerView.ViewHolder
    {
        ImageView downloadView;
        TextView messageView,timeView,displayName;
        public ReceivedImageHolder(View itemView) {
            super(itemView);
            downloadView=(ImageView)itemView.findViewById(R.id.downloadoption);
            messageView=(TextView) itemView.findViewById(R.id.messview);
            timeView=(TextView)itemView.findViewById(R.id.recTimeView);
            displayName=(TextView)itemView.findViewById(R.id.displayname);
        }
        public void bind(Messages messages)
        {
            messageView.setText(messages.getText());
            timeView.setText(date(messages.getTimestamp()));
            if(messages.getType().equals("image")) {
                loadImage(messages, downloadView);
            }
            else
            {
                loadDoc(downloadView,messages);
            }
            loadDisplayName(messages,displayName);

        }
    }

    private void loadDoc(final ImageView downloadView, final Messages messages) {
        String root=Environment.getExternalStorageDirectory().toString();
        File myFile=new File(root);
        if(!myFile.exists())
        {
            myFile.mkdirs();
        }
        final String name="A"+messages.getTimestamp()+".pdf";
        myFile=new File(root+"/ChatApp",name);
        if(!myFile.exists())
        {
            downloadView.setImageResource(R.drawable.download_icon);
            final File finalMyFile = myFile;
            downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        finalMyFile.createNewFile();
                        StorageReference mStorage=FirebaseStorage.getInstance().getReference().child("Uploads").child(name);
                        mStorage.getFile(finalMyFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                downloadView.setImageResource(R.drawable.doc);
                                downloadView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        displayPDF(finalMyFile);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            downloadView.setImageResource(R.drawable.doc);
            final File finalMyFile1 = myFile;
            downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayPDF(finalMyFile1);
                }
            });
        }

    }
    private void displayPDF(File finalMyFile) {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setData(Uri.fromFile(finalMyFile));
        intent.setAction(Intent.ACTION_VIEW);
        mContext.startActivity(intent);
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
            loadDisplayName(messages,displayName);
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
    public void loadDisplayName(Messages messages, final TextView displayName)
    {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();

        mRef.child("Users").child(messages.getFrom()).child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    String name = dataSnapshot.getValue().toString();
                    if (displayName != null) {
                        displayName.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

  public String date(long timeStamp)
  {
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
      String dateString = formatter.format(new Date(Long.parseLong(""+timeStamp)));
      dateString=dateString.toUpperCase();
      return dateString;
  }
}
package chat.chat.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserImgDialogUtil {
    UserImgDialogUtil()
    {
        
    }
    void showDialog(CircleImageView circleImageView, final Context mContext, final String uid)
    {
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
                final View v= LayoutInflater.from(mContext).inflate(R.layout.view_profile_dialog,null);
                final TextView displayName= (TextView) v.findViewById(R.id.displayName);
                final ImageView img= (ImageView) v.findViewById(R.id.propic);
                DatabaseReference mDb= FirebaseDatabase.getInstance().getReference();
                Query q=mDb.child("Users").orderByKey().equalTo(uid);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Users user=dataSnapshot.child(uid).getValue(Users.class);
                        if(!user.getImageLink().equals("null")) {
                            Picasso.get().load(user.getImageLink()).placeholder(R.drawable.default_pic).into(img);
                            displayName.setText(user.getName());
                            dialog.setView(v);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}

package chat.chat.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.util.ObjectIdMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PollDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle bundle=getArguments();
        final String pushId=bundle.getString("pushId");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        String arr[]={"End Poll"};
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                {
                    final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    mRef.child("Poll").child(pushId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Poll poll=dataSnapshot.getValue(Poll.class);
                            Map<String,Long> options=poll.getOptionsMap();
                            Iterator iterator=options.entrySet().iterator();
                            String message="#Poll ended:\n"+"Title : "+poll.getTitle()+"\nDescription : "+poll.getDescription()+"\n";
                            while(iterator.hasNext())
                            {
                                Map.Entry pair= (Map.Entry) iterator.next();
                                String key= (String) pair.getKey();
                                double val=((double)(100*(Long)pair.getValue()))/poll.getTotal();
                                String appendStr="\n"+String.format(key+" : %.2f",val)+"%(of Total)";
                                message+=appendStr;
                            }
                            long timestamp=System.currentTimeMillis();
                            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Map putMap=new HashMap();
                            putMap.put("from",uid);
                            putMap.put("timestamp",timestamp);
                            putMap.put("text",message);
                            putMap.put("link","default");
                            putMap.put("type","null");
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("message").child("Poll").push();
                            String key=databaseReference.getKey();
                            databaseReference.setValue(putMap);
                            mRef.child("Poll").child(pushId).removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                            {
                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                                Map<String,Object> map = dataSnapshot1.child("polls").getValue(genericTypeIndicator );
                                String uid=dataSnapshot1.getKey();
                                if(map.get(pushId)!=null)
                                {
                                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("Users").child(uid).child("polls").child(pushId).removeValue();
                                }
                            }
                            getActivity().startActivity(new Intent(getActivity(),getActivity().getClass()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        return builder.create();
    }
    public PollDialog(){

    }

}

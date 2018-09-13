package chat.chat.chat;

import android.app.Activity;
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

import chat.chat.ChatApp;

public class PollDialog extends DialogFragment {
    Activity activity;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle bundle=getArguments();
        activity=(Activity)getActivity();
        final String pushId=bundle.getString("pushId");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        String arr[]={"End Poll"};
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                {
                    final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    mRef.child(ChatApp.rollInfo).child("Poll").child(pushId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Poll poll=dataSnapshot.getValue(Poll.class);
                            Map<String,Long> options=poll.getOptionsMap();
                            Iterator iterator=options.entrySet().iterator();
                            Iterator iterator1=options.entrySet().iterator();
                            String message="#Poll ended:\n"+"Title : "+poll.getTitle()+"\nDescription : "+poll.getDescription()+"\n";
                            long tot=0;
                            while(iterator.hasNext())
                            {
                                Map.Entry pair= (Map.Entry) iterator.next();
                                tot+=(long)pair.getValue();

                            }
                            while(iterator1.hasNext())
                            {
                                Map.Entry pair= (Map.Entry) iterator1.next();
                                String key= (String) pair.getKey();
                                double val=((double)(100*(Long)pair.getValue()))/tot;
                                String appendStr="\n"+String.format(key+" : %.2f",val)+"%";
                                message+=appendStr;
                            }
                            String appendStr="\n\n% Voted :"+String.format("%.2f",(((double)tot)/poll.getTotal()*100))+"%";
                            message+=appendStr;
                            long timestamp=System.currentTimeMillis();
                            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Map putMap=new HashMap();
                            putMap.put("from",uid);
                            putMap.put("timestamp",timestamp);
                            putMap.put("text",message);
                            if(ChatApp.user.getCR().equals("true")||ChatApp.user.getCR().equals("false")) {
                                putMap.put("sender", "Student");
                            }
                            else
                            {
                                putMap.put("sender",ChatApp.user.getCR());
                            }
                            putMap.put("link","default");
                            putMap.put("type","null");
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(ChatApp.rollInfo).child("message").child("Poll").push();
                            String key=databaseReference.getKey();
                            mRef.child(ChatApp.rollInfo).child("message").child("An").child(key).setValue(putMap);
                            databaseReference.setValue(putMap);
                            mRef.child(ChatApp.rollInfo).child("Poll").child(pushId).removeValue();
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
                            activity.startActivity(new Intent(activity,OptionsActivity.class));
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

package chat.chat.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChooserDialog extends DialogFragment {


    public interface ChooserDialogListener {
        public void onPositiveButtonClicked(String[] mList, List<Integer> selectedItems);
    }

    ChooserDialogListener listener;
    int click = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ChooserDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " needs to implement listener");
        }
    }

    String mList[];
    List<Integer> selectedItems = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String activityName=getActivity().getClass().getSimpleName();
        if(activityName.equals("NoticeComposerActivity")) {
            mList = NoticeComposerActivity.mList;
        }
        else
        {
            mList=ImageTitleActivity.mList;
        }
        builder.setTitle("Select Recipients")
                .setMultiChoiceItems(mList, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            selectedItems.add(i);
                        } else if (selectedItems.contains(i)) {
                            selectedItems.remove(Integer.valueOf(i));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositiveButtonClicked(mList, selectedItems); }
                })
                .setNeutralButton("Select All", null)
                .setNegativeButton("Clear All",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button button=((AlertDialog)dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ListView listView = ((AlertDialog) dialogInterface).getListView();
                        for (int a = 0; a < listView.getChildCount(); a++) {
                            listView.setItemChecked(a, false);
                            if(selectedItems.contains(Integer.valueOf(a))){
                                selectedItems.remove(Integer.valueOf(a));
                            }
                        }
                    }
                });
                Button button1=((AlertDialog)dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ListView listView = ((AlertDialog) dialogInterface).getListView();
                        for (int a = 0; a < listView.getChildCount(); a++) {
                            listView.setItemChecked(a, true);
                            if(!selectedItems.contains(Integer.valueOf(a))) {
                                selectedItems.add(Integer.valueOf(a));
                            }
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

}

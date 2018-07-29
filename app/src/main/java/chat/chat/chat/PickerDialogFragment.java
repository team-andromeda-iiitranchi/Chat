package chat.chat.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class PickerDialogFragment extends DialogFragment{
    private final int IMG=0;
    private final int DOC=1;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        String[] arr={"Image","Document"};
        builder.setTitle("Choose the Type of Document");
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent();
                int type;
                if(i==0)
                {
                    intent.setType("image/*");
                    type=IMG;
                }
                else
                {
                    intent.setType("application/pdf");
                    type=DOC;
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,"Select File"),type);
            }
        });
        return builder.create();
    }
}

package chat.chat.DeveloperFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import chat.chat.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevFragment extends Fragment {


    public DevFragment() {
        // Required empty public constructor
    }


    private View view;
    private CircleImageView pic;
    private TextView name, regNo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=LayoutInflater.from(getContext()).inflate(R.layout.developer_card,null);
        pic= (CircleImageView) view.findViewById(R.id.pic);
        name= (TextView) view.findViewById(R.id.name);
        regNo= (TextView) view.findViewById(R.id.regNo);
        String nameDev=getArguments().getString("name");
        String regNoStr=getArguments().getString("reg");
        int id=getArguments().getInt("id");
        Picasso.get().load(id).into(pic);
        name.setText(nameDev);
        regNo.setText(regNoStr);
        return view;
    }
}

package chat.chat.DeveloperFragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private TextView name, regNo,credit;
    private ImageView githubImg,linkedInImg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=LayoutInflater.from(getContext()).inflate(R.layout.developer_card,null);
        pic= (CircleImageView) view.findViewById(R.id.pic);
        name= (TextView) view.findViewById(R.id.name);
        regNo= (TextView) view.findViewById(R.id.regNo);
        String nameDev=getArguments().getString("name");
        String regNoStr=getArguments().getString("reg");
        String creditStr=getArguments().getString("credit");
        credit= (TextView) view.findViewById(R.id.credit);
        githubImg= (ImageView) view.findViewById(R.id.git);
        linkedInImg= (ImageView) view.findViewById(R.id.linked);
        credit.setText(creditStr);
        String github=getArguments().getString("github");
        String linkedIn=getArguments().getString("linkedIn");
        onClickFunc(githubImg,github);
        onClickFunc(linkedInImg,linkedIn);
        int id=getArguments().getInt("id");
        Picasso.get().load(id).into(pic);
        name.setText(nameDev);
        regNo.setText(regNoStr);
        return view;
    }
    public void onClickFunc(ImageView img, final String link)
    {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(link));
                getActivity().startActivity(intent);
            }
        });
    }
}

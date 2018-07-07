package chat.chat.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import chat.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {
    ImageView noticeTextView;

    public NoticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_notice, container, false);
        noticeTextView=(ImageView)view.findViewById(R.id.noticeTextView);
        noticeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ChatActivity.class));
            }
        });
        return view;

    }

}

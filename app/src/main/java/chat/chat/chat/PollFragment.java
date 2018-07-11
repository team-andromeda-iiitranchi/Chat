package chat.chat.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollFragment extends Fragment {

    private FloatingActionButton floatingActionButton;

    public PollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_poll, container, false);
        floatingActionButton=(FloatingActionButton)view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AddPoll.class);
                startActivity(i);
            }
        });
        return view;
    }

}

package chat.chat.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAuthChat extends Fragment {


    public FragmentAuthChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth_chat, container, false);
    }

}

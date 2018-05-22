package com.example.aj.commenton;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RegistrationFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration,container,false);

//        Bundle args = getArguments();
//        ((TextView) rootView.findViewById(R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.registration_text);
    }

}

package com.designthinking.quokka;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.designthinking.quokka.event.EventManager;
import com.designthinking.quokka.event.IEventListener;
import com.designthinking.quokka.event.messages.OnLoadingFinish;
import com.designthinking.quokka.event.messages.OnLoadingStart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment implements IEventListener {

    private ViewGroup root;

    public LoadingFragment() {
        // Required empty public constructor
    }

    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventManager.getInstance().addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        root = view.findViewById(R.id.loading_root);

        return view;
    }

    public void onLoadingStart(OnLoadingStart event){
        root.setVisibility(View.VISIBLE);
    }

    public void onLoadingFinish(OnLoadingFinish event){
        root.setVisibility(View.GONE);
    }
}
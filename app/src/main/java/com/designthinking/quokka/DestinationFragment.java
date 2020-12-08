package com.designthinking.quokka;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.designthinking.quokka.place.Place;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {

    public enum State { FASTEST, CHEAPEST, NONE }

    private State current = State.NONE;

    private AutoCompleteTextView searchText;
    private ToggleButton fastestToggleBtn;
    private ToggleButton cheapestToggleBtn;

    private Place destination;

    private RouteTypeChangeListener routeTypeChangeListener;
    private DestinationChangeListener destinationChangeListener;

    public DestinationFragment() {
        // Required empty public constructor
    }

    public static DestinationFragment newInstance() {
        DestinationFragment fragment = new DestinationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_destination, container, false);

        searchText = view.findViewById(R.id.search);
        fastestToggleBtn = view.findViewById(R.id.fastest);
        cheapestToggleBtn = view.findViewById(R.id.cheapest);

        fastestToggleBtn.setOnClickListener(v -> setState(State.FASTEST));
        cheapestToggleBtn.setOnClickListener(v -> setState(State.CHEAPEST));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Place.getNames());
        searchText.setAdapter(adapter);
        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                destination = Place.search(((TextView)view).getText().toString()).get(0);
                destinationChangeListener.onChanged(destination);
                searchText.clearFocus();
            }
        });
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(destination == null) searchText.setText("");
                    else searchText.setText(destination.name);

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    public void setState(State state){
        fastestToggleBtn.setChecked(false);
        cheapestToggleBtn.setChecked(false);

        switch (state){
            case FASTEST:
                fastestToggleBtn.setChecked(true);
                break;
            case CHEAPEST:
                cheapestToggleBtn.setChecked(true);
                break;
        }

        current = state;

        routeTypeChangeListener.onChanged(state);
    }

    public State getState(){
        return current;
    }

    public Place getDestination(){
        return destination;
    }

    public void setRouteTypeChangeListener(RouteTypeChangeListener listener){
        this.routeTypeChangeListener = listener;
    }

    public void setDestinationChangeListener(DestinationChangeListener listener){
        this.destinationChangeListener = listener;
    }

    public void setRouteTypeVisibility(boolean visibility){
        fastestToggleBtn.setVisibility(visibility ? View.VISIBLE : View.GONE);
        cheapestToggleBtn.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public interface RouteTypeChangeListener{
        void onChanged(State state);
    }

    public interface DestinationChangeListener{
        void onChanged(Place place);
    }
}
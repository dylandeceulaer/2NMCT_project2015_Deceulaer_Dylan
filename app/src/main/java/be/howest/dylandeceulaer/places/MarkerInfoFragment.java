package be.howest.dylandeceulaer.places;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerInfoFragment extends Fragment {
    TextView textViewTitle;
    ImageButton imageButtonClose;
    ImageButton imageButtonDelete;
    Marker currentMarker;

    public MarkerInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_marker_info, container, false);
        textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
        imageButtonClose = (ImageButton) v.findViewById(R.id.imageButtonClose);
        imageButtonDelete = (ImageButton) v.findViewById(R.id.imageViewDelete);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
                SaveInfo();
            }
        });
        imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMarker != null){
                    getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
                    currentMarker.remove();
                }
            }
        });


        return v;
    }

    public void UpdateInfo(Marker marker){
        if(currentMarker != null)
            SaveInfo();

       if(marker.getTitle().equals("Unnamed Marker") || marker.getTitle().isEmpty())
            textViewTitle.setText("");
        else
            textViewTitle.setText(marker.getTitle());
        currentMarker = marker;
    }
    private void SaveInfo(){
        if(textViewTitle.getText().toString().isEmpty())
            currentMarker.setTitle("Unnamed Marker");
        else
            currentMarker.setTitle(textViewTitle.getText().toString());

    }



}

package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerInfoFragment extends Fragment {
    EditText textViewTitle;
    ImageButton imageButtonClose;
    ImageButton imageButtonDelete;
    ImageButton imageButtonZoom;
    EditText textViewStraatnaam;
    Marker currentMarker;
    MarkerInfo currentMarkerInfo;
    Data data;
    MapInteractionListener mMainActivity;

    public interface MapInteractionListener{
        public void onMapZoom(LatLng loc, float zoom);
    }

    public MarkerInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MapInteractionListener) activity;
        data = new Data(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_marker_info, container, false);
        textViewTitle = (EditText) v.findViewById(R.id.textViewTitle);
        imageButtonClose = (ImageButton) v.findViewById(R.id.imageButtonClose);
        imageButtonDelete = (ImageButton) v.findViewById(R.id.imageViewDelete);
        textViewStraatnaam = (EditText) v.findViewById(R.id.EditTextStraatnaam);
        imageButtonZoom = (ImageButton) v.findViewById(R.id.imageButtonZoom);

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
                    data.deleteMarker(currentMarkerInfo);
                }
            }
        });
        textViewTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId== EditorInfo.IME_ACTION_DONE){
                    currentMarker.setTitle(textViewTitle.getText().toString());
                    currentMarkerInfo.setTitel(textViewTitle.getText().toString());
                    data.updateMarker(currentMarkerInfo);
                    ((InputMethodManager)getActivity().getSystemService(
                            getActivity().INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(textViewTitle.getWindowToken(), 0);
                    currentMarker.showInfoWindow();
                    return true;
                }
                return false;
            }
        });
        textViewStraatnaam.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId== EditorInfo.IME_ACTION_DONE){
                    currentMarkerInfo.setAdres(textViewStraatnaam.getText().toString());
                    data.updateMarker(currentMarkerInfo);
                    ((InputMethodManager)getActivity().getSystemService(
                            getActivity().INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(textViewTitle.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        imageButtonZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onMapZoom(currentMarker.getPosition(),(float) 17);
            }
        });

        //TODO: vervangen door adapter enal




        ((ImageButton) v.findViewById(R.id.home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_home));
                currentMarkerInfo.setMarkerData(Data.MARKER.getMarker(R.drawable.custom_marker_home));
            }
        });

        ((ImageButton) v.findViewById(R.id.poi)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_poi));
                currentMarkerInfo.setMarkerData(Data.MARKER.getMarker(R.drawable.custom_marker_poi));

            }
        });


        return v;
    }

    public void UpdateInfo(Marker marker,MarkerInfo markerinfo){
        if(currentMarker != null)
            SaveInfo();

       if(marker.getTitle().equals("Unnamed Marker") || marker.getTitle().isEmpty())
            textViewTitle.setText("");
        else
            textViewTitle.setText(markerinfo.getTitel());


        //opgezocht

        //TODO: kijken of al bestaat in adapter
        if(markerinfo.getAdres().equals("")) {
            Geocoder geoCoder = new Geocoder( getActivity().getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                textViewStraatnaam.setText(addresses.get(0).getAddressLine(0));
                markerinfo.setAdres(addresses.get(0).getAddressLine(0));
                data.updateMarker(markerinfo);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            textViewStraatnaam.setText(markerinfo.getAdres());
        }

        currentMarker = marker;
        currentMarkerInfo = markerinfo;
    }
    private void SaveInfo(){
        if(textViewTitle.getText().toString().isEmpty()) {
            currentMarker.setTitle("Unnamed Marker");
            currentMarkerInfo.setTitel("Unnamed Marker");
            data.updateMarker(currentMarkerInfo);
        }

        else {
            if(!currentMarkerInfo.getTitel().equals(textViewStraatnaam.getText().toString()))
            currentMarker.setTitle(textViewTitle.getText().toString());
            currentMarkerInfo.setTitel(textViewTitle.getText().toString());
            data.updateMarker(currentMarkerInfo);
        }

        if(!textViewStraatnaam.getText().equals(currentMarkerInfo.getAdres())){
            currentMarkerInfo.setAdres(textViewStraatnaam.getText().toString());
            data.updateMarker(currentMarkerInfo);
        }

    }




}

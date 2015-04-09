package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        com.google.android.gms.maps.MapFragment mapFrag = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map));
        getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();

        mapFrag.getMapAsync(this);
        return v;
        //LatLngBounds b = new LatLngBounds(loc, loc);

        //PendingResult res = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient,"Bakker",b,)
    }



    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, true);
        Location location = mLocationManager.getLastKnownLocation(provider);


        if(location != null)
        PanMap(location,(float)17);
        else{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

    }

    private void PanMap(Location location,float zoom){
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        if(zoom < googleMap.getMaxZoomLevel())
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);
        else
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.getMaxZoomLevel()), 2000, null);

    }

    @Override
    public void onLocationChanged(Location location) {
        PanMap(location,(float)17);
        mLocationManager.removeUpdates(this);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("")
                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
        markerInfoFragment.UpdateInfo(m);
        getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
        markerInfoFragment.UpdateInfo(marker);
        getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
        return false;
    }
}

package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private onMarkerClickInfoListener mMainActivity;
    private Data data;

    public MapFragment() {
        // Required empty public constructor
    }

    public interface onMarkerClickInfoListener{
        public void onMarkerClick(Marker marker, MarkerInfo markerinfo);
        public void onMarkerInfoClose();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMainActivity.onMarkerInfoClose();
        com.google.android.gms.maps.MapFragment mapFrag = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFrag.getMapAsync(this);
        return v;
    }


    private void loadSavedMarkers(){
        List<MarkerInfo> markers = data.getSavedMarkers();

        if(markers == null) System.out.println("twerkt ni");

        if(markers != null) {

            for (int i = 0; i < markers.size(); i++) {
                Marker m = googleMap.addMarker(new MarkerOptions()
                        .position(markers.get(i).getPositie())
                        .title(markers.get(i).getTitel())
                        .icon(BitmapDescriptorFactory.fromResource(markers.get(i).getMarkerData().getMarker())));
            }
        }
    }

    public void addMarkerOnCurrentPosition(){
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), true));
        LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
        PanMap(loc,(float) 17);
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("")
                .icon( BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_home)));

        SharedPreferences settings = getActivity().getSharedPreferences("hallo", 0);

        MarkerInfo markerInfo = new MarkerInfo("",loc,"", Data.MARKER.getMarker(R.drawable.custom_marker_home));
        long id = data.addMarker(markerInfo);
        markerInfo.setId(id);
        mMainActivity.onMarkerClick(m,markerInfo);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (onMarkerClickInfoListener) activity;
        data = new Data(activity);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        loadSavedMarkers();
        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, true);
        Location location = mLocationManager.getLastKnownLocation(provider);


        if(location != null)
        PanMap(new LatLng(location.getLatitude(), location.getLongitude()),(float)17);
        else{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

    }

    public void PanMap(LatLng loc,float zoom){

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        if(zoom < googleMap.getMaxZoomLevel())
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1000, null);
        else
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.getMaxZoomLevel()), 2000, null);

    }

    @Override
    public void onLocationChanged(Location location) {
        PanMap(new LatLng(location.getLatitude(), location.getLongitude()),(float)17);
        mLocationManager.removeUpdates(this);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("")
                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        MarkerInfo markerInfo = new MarkerInfo("",latLng,"", Data.MARKER.getMarker(R.drawable.custom_marker_home));
        long id = data.addMarker(markerInfo);
        markerInfo.setId(id);


        mMainActivity.onMarkerClick(m,markerInfo);
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
        mMainActivity.onMarkerClick(marker,data.getMarkerByPosition(marker));
        return false;
    }
}

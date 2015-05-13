package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private onMarkerClickInfoListener mMainActivity;
    private Data data;
    private static View v;
    private ProgressBar progressBar;

    public MapFragment() {
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMainActivity.onMarkerInfoClose();
    }

    public interface onMarkerClickInfoListener{
        public void onMarkerClick(Marker marker, MarkerInfo markerinfo);
        public void onMarkerInfoClose();
        public void onMarkerInfoClick(Marker marker, MarkerInfo markerInfo);
        public void onMarkerInfoCloseInit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        //wanneer de gebruiker de app heeft gepauseerd en terug komt word zijn evt. nieuwe locatie opgevraagt en getoond.
        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(new Criteria(),true), 0, 0, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);

        DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.container_drawer);
        ActionBarDrawerToggle  mDrawerToggle = new ActionBarDrawerToggle(getActivity(),mDrawerLayout,toolbar,R.string.open,R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle.syncState();


        com.google.android.gms.maps.MapFragment mapFrag = getMapFragment();

        mapFrag.getMapAsync(this);
        mMainActivity.onMarkerInfoCloseInit();


        final Geocoder geoCoder = new Geocoder( getActivity().getBaseContext(), Locale.getDefault());

        final EditText editText = (EditText) v.findViewById(R.id.editTextSearch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    List<Address> addr = geoCoder.getFromLocationName(editText.getText().toString(), 1);
                    if(addr.size() > 0){
                        Address a = addr.get(0);
                        PanMap(new LatLng(a.getLatitude(),a.getLongitude()),17);
                    }else{
                        Toast.makeText(getActivity(),"No results to query!",Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException ex){
                    Toast.makeText(getActivity(),"No results to query!",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        ImageButton imageButtonSearch = (ImageButton) v.findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Address> addr = geoCoder.getFromLocationName(editText.getText().toString(), 1);
                    if(addr.size() > 0){
                        Address a = addr.get(0);
                        PanMap(new LatLng(a.getLatitude(),a.getLongitude()),15);
                    }else{
                        Toast.makeText(getActivity(),"No results to query!",Toast.LENGTH_SHORT);
                    }
                }catch (IOException ex){
                    Toast.makeText(getActivity(),"No results to query!",Toast.LENGTH_SHORT);
                }

            }
        });

        return v;
    }

    private com.google.android.gms.maps.MapFragment getMapFragment() {
        FragmentManager fm = null;


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }

        return (com.google.android.gms.maps.MapFragment) fm.findFragmentById(R.id.map);
    }


    private void loadSavedMarkers(){
        List<MarkerInfo> markers = data.getSavedMarkers();

        if(markers != null) {

            for (int i = 0; i < markers.size(); i++) {
                Marker m = googleMap.addMarker(new MarkerOptions()
                        .position(markers.get(i).getPositie())
                        .title(markers.get(i).getTitel())
                        .icon(BitmapDescriptorFactory.fromResource(markers.get(i).getMarkerData().getMarker())));
        }            }

    }

    private void showLocationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("To use this application, the location services need to be enabled.")
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(viewIntent);
                        progressBar.setProgress(View.VISIBLE);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressBar.setProgress(View.INVISIBLE);
                    }
                }).setTitle("Location is disabled");
        Dialog d = builder.create();
        d.show();

    }

    public void addMarkerOnCurrentPosition(){
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), true));
        if(location == null){
            if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                showLocationDialog();
            else{
                mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(new Criteria(),true), 0, 0, this);
                progressBar.setVisibility(View.VISIBLE);
            }

        }else {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            PanMap(loc, (float) 17);
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title("")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_home)));

            MarkerInfo markerInfo = new MarkerInfo("", loc, "", Data.MARKER.getMarker(R.drawable.custom_marker_home));
            long id = data.addMarker(markerInfo);
            markerInfo.setId(id);
            mMainActivity.onMarkerClick(m, markerInfo);
        }
    }

    public void ShowCurrentPosition(){
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), true));
        if(location == null){
            if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                showLocationDialog();
            else {
                mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(new Criteria(),true), 0, 0, this);
                progressBar.setVisibility(View.VISIBLE);
            }
        }else {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            PanMap(loc, (float) 15);
            mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(new Criteria(),true),0,0,this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (onMarkerClickInfoListener) activity;
        data = new Data(activity);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                Data data = new Data(getActivity());
                final MarkerInfo info = data.getMarkerByPosition(marker);

                View v = getActivity().getLayoutInflater().inflate(R.layout.infowindowlayout,null);
                TextView titel = (TextView) v.findViewById(R.id.textViewTitel);
                TextView adres = (TextView) v.findViewById(R.id.textViewAdres);
                ImageView imageViewMarker = (ImageView) v.findViewById(R.id.imageViewMarker);

                titel.setText(marker.getTitle());
                if(info == null) return null;
                adres.setText(info.getAdres());
                imageViewMarker.setImageResource(info.getMarkerData().getMarker());

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        mMainActivity.onMarkerInfoClick(marker,info);
                    }
                });


                return v;
            }
        });

        loadSavedMarkers();
        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, true);

        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            showLocationDialog();
        }

        Location location = mLocationManager.getLastKnownLocation(provider);

        if(location != null)
        PanMap(new LatLng(location.getLatitude(), location.getLongitude()),(float)17);
        else{
            progressBar.setVisibility(View.VISIBLE);
            mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(criteria,true), 0, 0, this);
        }
    }

    public void PanMap(LatLng loc,float zoom){
        progressBar.setVisibility(View.INVISIBLE);
        if(googleMap != null) { //Heel af en toe is googleMap null voor een onbekende reden, meestal na lang niets doen.
            if (zoom < googleMap.getMaxZoomLevel())
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom), 1000, null);
            else
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, googleMap.getMaxZoomLevel()), 1000, null);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        progressBar.setVisibility(View.INVISIBLE);
        PanMap(new LatLng(location.getLatitude(), location.getLongitude()),(float)17);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_home)));

        MarkerInfo markerInfo = new MarkerInfo("",latLng,"", Data.MARKER.getMarker(R.drawable.custom_marker_home));
        long id = data.addMarker(markerInfo);
        markerInfo.setId(id);


        mMainActivity.onMarkerInfoClick(m,markerInfo);
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

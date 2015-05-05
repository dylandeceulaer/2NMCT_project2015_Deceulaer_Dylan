package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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


        com.google.android.gms.maps.MapFragment mapFrag = getMapFragment();
//        com.google.android.gms.maps.MapFragment mapFrag = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map));
        //        SupportMapFragment mapFrag = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        mapFrag.getMapAsync(this);
        mMainActivity.onMarkerInfoClose();
        return v;
    }

    private com.google.android.gms.maps.MapFragment getMapFragment() {
        FragmentManager fm = null;

        System.out.println("sdk: " + Build.VERSION.SDK_INT);
        System.out.println("release: " + Build.VERSION.RELEASE);

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
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).setTitle("Location is disabled");
        // Create the AlertDialog object and return it
        Dialog d = builder.create();
        d.show();

    }

    public void addMarkerOnCurrentPosition(){
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), true));
        if(location == null){
            if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                showLocationDialog();
            else{
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                ((MainActivity)getActivity()).progressBar.setVisibility(View.VISIBLE);
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
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                ((MainActivity)getActivity()).progressBar.setVisibility(View.VISIBLE);

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
                ImageButton imageButtonZoom = (ImageButton) v.findViewById(R.id.imageButtonZoom);
                ImageButton imageButtonDir = (ImageButton) v.findViewById(R.id.imageButtonDir);

                titel.setText(marker.getTitle());
                adres.setText(info.getAdres());
                imageViewMarker.setImageResource(info.getMarkerData().getMarker());

                imageButtonZoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PanMap(marker.getPosition(),(float) 17);
                    }
                });

                final Location huidigeLocatie = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), true));
                imageButtonDir.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q="+info.getAdres()));
                                //Uri.parse("http://maps.google.com/maps?   saddr=" + huidigeLocatie.getLatitude() + "," + huidigeLocatie.getLatitude() + "&daddr=" + latitude + "," + longitude));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER );
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        mMainActivity.onMarkerClick(marker,info);
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
            ((MainActivity)getActivity()).progressBar.setVisibility(View.VISIBLE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        }

    }

    public void PanMap(LatLng loc,float zoom){

        ((MainActivity)getActivity()).progressBar.setVisibility(View.INVISIBLE);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, googleMap.getCameraPosition().zoom));
        if(zoom < googleMap.getMaxZoomLevel())
            if(googleMap.getCameraPosition().zoom < zoom)
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1500, null);
        else
            if(googleMap.getCameraPosition().zoom < zoom)
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.getMaxZoomLevel()), 1500, null);

    }

    @Override
    public void onLocationChanged(Location location) {
        ((MainActivity)getActivity()).progressBar.setVisibility(View.INVISIBLE);
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

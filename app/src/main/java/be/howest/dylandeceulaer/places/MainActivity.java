package be.howest.dylandeceulaer.places;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MainActivity extends ActionBarActivity implements MapFragment.onMarkerClickInfoListener, MarkerInfoFragment.MapInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.container, new MapFragment(), "Map").commit();
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.kleur));
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.addMarkerCurrentPosition:
                ((MapFragment) getFragmentManager().findFragmentByTag("Map")).addMarkerOnCurrentPosition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMarkerClick(Marker marker) {
        MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
        markerInfoFragment.UpdateInfo(marker);
        getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).addToBackStack(null).commit();

    }

    @Override
    public void onMarkerInfoClose() {
        getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
    }

    @Override
    public void onMapZoom(LatLng loc, float zoom) {
        ((MapFragment) getFragmentManager().findFragmentByTag("Map")).PanMap(loc,zoom);

    }
}

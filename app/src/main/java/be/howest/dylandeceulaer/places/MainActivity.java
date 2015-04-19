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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MainActivity extends ActionBarActivity implements MapFragment.onMarkerClickInfoListener, MarkerInfoFragment.MapInteractionListener {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.container, new MapFragment(), "Map").commit();
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.kleur));


        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 24));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);


        final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        decorView.addView(progressBar);

        ViewTreeObserver observer = progressBar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View contentView = decorView.findViewById(android.R.id.content);
                progressBar.setY(contentView.getY() + 60);

                ViewTreeObserver observer = progressBar.getViewTreeObserver();
                observer.removeOnGlobalLayoutListener(this);
            }
        });
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
            case R.id.CurrentPosition:
                ((MapFragment) getFragmentManager().findFragmentByTag("Map")).ShowCurrentPosition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMarkerClick(Marker marker, MarkerInfo info) {
        MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
        markerInfoFragment.UpdateInfo(marker,info);
        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).addToBackStack(null).commit();

    }

    @Override
    public void onMarkerInfoClose() {
        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
    }

    @Override
    public void onMapZoom(LatLng loc, float zoom) {
        ((MapFragment) getFragmentManager().findFragmentByTag("Map")).PanMap(loc,zoom);

    }
}

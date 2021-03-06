package be.howest.dylandeceulaer.places;


import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MainActivity extends ActionBarActivity implements MapFragment.onMarkerClickInfoListener, MarkerInfoFragment.MapInteractionListener,DrawerMenuFragment.onShowMarkerListener {


    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.container, new MapFragment(), "Map").commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.container_drawer);
        ActionBarDrawerToggle  mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle.syncState();


    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer (Gravity.START);
        } else if(!getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container).isHidden()) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
            trans.hide(getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
        }else{
            super.onBackPressed();
        }
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



        switch (id){

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
        ((DrawerMenuFragment) getSupportFragmentManager().findFragmentById(R.id.drawermenufragment)).update();

    }

    @Override
    public void onMarkerInfoClose() {
        if(!getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container).isHidden()) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
            MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
            trans.hide(markerInfoFragment).commit();
            markerInfoFragment.SaveInfo();
            UpdateList();

        }

    }

    @Override
    public void onMarkerInfoClick(Marker marker, MarkerInfo markerInfo) {
        MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
        markerInfoFragment.UpdateInfo(marker,markerInfo);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        trans.show(getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container)).commit();
    }

    @Override
    public void onMarkerInfoCloseInit() {
        if(!getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container).isHidden()) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
            MarkerInfoFragment markerInfoFragment = (MarkerInfoFragment) getSupportFragmentManager().findFragmentById(R.id.marker_info_fragment_container);
            trans.hide(markerInfoFragment).commit();
        }
    }

    @Override
    public void onMapZoom(LatLng loc, float zoom) {
        ((MapFragment) getFragmentManager().findFragmentByTag("Map")).PanMap(loc,zoom);

    }

    @Override
    public void UpdateList() {
        ((DrawerMenuFragment) getSupportFragmentManager().findFragmentById(R.id.drawermenufragment)).update();
    }

    @Override
    public void onShowMarker(LatLng loc) {
        ((MapFragment) getFragmentManager().findFragmentByTag("Map")).PanMap(loc,17);
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.container_drawer);
        mDrawerLayout.closeDrawers();
    }


}

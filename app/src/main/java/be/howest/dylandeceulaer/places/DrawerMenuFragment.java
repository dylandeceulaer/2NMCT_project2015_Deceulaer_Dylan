package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    onShowMarkerListener mMainActivity;

    public interface onShowMarkerListener{
        public void onShowMarker(LatLng loc);

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (onShowMarkerListener) activity;
    }

    private AdresAdapter mAdapter;

    public DrawerMenuFragment() {
        // Required empty public constructor
    }

    public void update(){
        getLoaderManager().restartLoader(0,null,this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new AdresAdapter(getActivity(),
                R.layout.cel_drawer_address,
                null,
                new String[]{
                        DatabaseHelper.colTitle,
                        DatabaseHelper.colAddress,
                        DatabaseHelper.colPlaats
                },
                new int[]{
                      R.id.editTextTitle,
                        R.id.textViewAddress,
                        R.id.textViewPlaats
                },0
        );
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawer_menu, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new MarkerLoader(getActivity());

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = (Cursor) mAdapter.getItem(position);
        LatLng loc = new LatLng(c.getDouble(c.getColumnIndex(DatabaseHelper.colPosLat)),c.getDouble(c.getColumnIndex(DatabaseHelper.colPosLong)));

        mMainActivity.onShowMarker(loc);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    class AdresAdapter extends SimpleCursorAdapter{

        public AdresAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            ImageView icon = (ImageView) view.findViewById(R.id.imageViewMarkerDrawer);



            int colnr = cursor.getColumnIndex(DatabaseHelper.colMarkerIcon);
            Data.MARKER marker = Data.MARKER.getMarker(cursor.getInt(colnr));
            icon.setImageResource(marker.getMarker());

        }
    }


}

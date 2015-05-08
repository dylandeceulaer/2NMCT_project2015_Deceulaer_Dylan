package be.howest.dylandeceulaer.places;


import android.app.Activity;
import android.content.Intent;
import android.database.MatrixCursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.BaseColumns;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    Spinner spinnerMarkers;
    onShowMarkerListener mMainActivity;
    SearchView searchView;
    Cursor cursor;
    Cursor origineleData;
    Cursor filteredData;
    Data data;

    public interface onShowMarkerListener{
        public void onShowMarker(LatLng loc);

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (onShowMarkerListener) activity;
        data = new Data(activity);
    }

    private AdresAdapter mAdapter;

    public DrawerMenuFragment() {
        // Required empty public constructor
    }

    public void update(){
        if(mAdapter != null) {
            getLoaderManager().restartLoader(0, null, this);
            mAdapter.notifyDataSetChanged();
        }
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
        View v =  inflater.inflate(R.layout.fragment_drawer_menu, container, false);

        searchView = (SearchView) v.findViewById(R.id.searchViewDrawer);
        spinnerMarkers = (Spinner) v.findViewById(R.id.SpinnerMarkers);

        spinnerMarkers.setAdapter(new ArrayAdapter<Data.MARKER>(this.getActivity(),android.R.layout.simple_list_item_1,Data.MARKER.values()));


        spinnerMarkers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.swapCursor(filterByCategory((Data.MARKER)parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                spinnerMarkers.setVisibility(View.VISIBLE);

                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerMarkers.setVisibility(View.INVISIBLE);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.swapCursor(filter(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.swapCursor(filter(newText));
                return false;
            }
        });
        return v;
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
        //mAdapter.swapCursor(data);
        origineleData = data;
        filteredData = filterByCategory((Data.MARKER)spinnerMarkers.getSelectedItem());
        mAdapter.swapCursor(filteredData);
        searchView.setQuery("",false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    class AdresAdapter extends SimpleCursorAdapter{

        public AdresAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            cursor = c;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            ImageView icon = (ImageView) view.findViewById(R.id.imageViewMarkerDrawer);

            int colnrmarker = cursor.getColumnIndex(DatabaseHelper.colMarkerIcon);
            Data.MARKER marker = Data.MARKER.getMarker(cursor.getInt(colnrmarker));
            icon.setImageResource(marker.getMarker());

            int colnrAdres = cursor.getColumnIndex(DatabaseHelper.colAddress);
            final String adr = cursor.getString(colnrAdres);

            int colnrPlaats = cursor.getColumnIndex(DatabaseHelper.colPlaats);
            final String pl = cursor.getString(colnrPlaats);

            ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButtonDir);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + adr+","+pl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER );
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });

        }
    }
    public Cursor filter(String query){
        if(filteredData == null) filteredData = mAdapter.getCursor();
        if(filteredData == null) return filteredData;

        String[] allColumnsMatrix = {BaseColumns._ID,
                DatabaseHelper.colTitle,
                DatabaseHelper.colAddress,
                DatabaseHelper.colPosLat,
                DatabaseHelper.colPosLong,
                DatabaseHelper.colMarkerIcon,
                DatabaseHelper.colPlaats};

        MatrixCursor newCursor = new MatrixCursor(allColumnsMatrix);

        int colnrID = filteredData.getColumnIndex(BaseColumns._ID);
        int colnrTitle = filteredData.getColumnIndex(DatabaseHelper.colTitle);
        int colnrAddress = filteredData.getColumnIndex(DatabaseHelper.colAddress);
        int colnrPosLat = filteredData.getColumnIndex(DatabaseHelper.colPosLat);
        int colnrPosLong = filteredData.getColumnIndex(DatabaseHelper.colPosLong);
        int colnrMarkerIcon = filteredData.getColumnIndex(DatabaseHelper.colMarkerIcon);
        int colnrPlaats = filteredData.getColumnIndex(DatabaseHelper.colPlaats);

        if(filteredData.moveToFirst()){
            do{
                if(filteredData.getString(colnrTitle).toLowerCase().contains(query.toLowerCase().trim())
                        || filteredData.getString(colnrPlaats).toLowerCase().contains(query.toLowerCase().trim())
                        || filteredData.getString(colnrAddress).toLowerCase().contains(query.toLowerCase().trim())){
                    MatrixCursor.RowBuilder row = newCursor.newRow();
                    row.add(filteredData.getLong(colnrID));
                    row.add(filteredData.getString(colnrTitle));
                    row.add(filteredData.getString(colnrAddress));
                    row.add(filteredData.getDouble(colnrPosLat));
                    row.add(filteredData.getDouble(colnrPosLong));
                    row.add( filteredData.getInt(colnrMarkerIcon));
                    row.add( filteredData.getString(colnrPlaats));
                }
            }while (filteredData.moveToNext());
        }

        return newCursor;
    }

    public Cursor filterByCategory(Data.MARKER marker){
        if(origineleData == null) origineleData = mAdapter.getCursor();

        if(marker.getNaam().equals("All categories")){
            filteredData = origineleData;
            return filteredData;
        }


        String[] allColumnsMatrix = {BaseColumns._ID,
                DatabaseHelper.colTitle,
                DatabaseHelper.colAddress,
                DatabaseHelper.colPosLat,
                DatabaseHelper.colPosLong,
                DatabaseHelper.colMarkerIcon,
                DatabaseHelper.colPlaats};

        MatrixCursor newCursor = new MatrixCursor(allColumnsMatrix);

        int colnrID = origineleData.getColumnIndex(BaseColumns._ID);
        int colnrTitle = origineleData.getColumnIndex(DatabaseHelper.colTitle);
        int colnrAddress = origineleData.getColumnIndex(DatabaseHelper.colAddress);
        int colnrPosLat = origineleData.getColumnIndex(DatabaseHelper.colPosLat);
        int colnrPosLong = origineleData.getColumnIndex(DatabaseHelper.colPosLong);
        int colnrMarkerIcon = origineleData.getColumnIndex(DatabaseHelper.colMarkerIcon);
        int colnrPlaats = origineleData.getColumnIndex(DatabaseHelper.colPlaats);

        if(origineleData.moveToFirst()){
            do{
                if(origineleData.getInt(colnrMarkerIcon) == marker.getMarker()){
                    MatrixCursor.RowBuilder row = newCursor.newRow();
                    row.add(origineleData.getLong(colnrID));
                    row.add(origineleData.getString(colnrTitle));
                    row.add(origineleData.getString(colnrAddress));
                    row.add(origineleData.getDouble(colnrPosLat));
                    row.add(origineleData.getDouble(colnrPosLong));
                    row.add( origineleData.getInt(colnrMarkerIcon));
                    row.add( origineleData.getString(colnrPlaats));
                }
            }while (origineleData.moveToNext());
        }

        filteredData = newCursor;
        return filteredData;
    }

}

package be.howest.dylandeceulaer.places;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.JsonReader;
import android.util.JsonToken;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 4/05/2015.
 */
public class MarkerLoader extends AsyncTaskLoader<Cursor> {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor mCursor;
    private static Object lock = new Object();
    private String[] allColumns = { DatabaseHelper.colMarkerInfoID,
            DatabaseHelper.colTitle,
            DatabaseHelper.colAddress,
            DatabaseHelper.colPosLat,
            DatabaseHelper.colPosLong,
            DatabaseHelper.colMarkerIcon,
            DatabaseHelper.colPlaats};
    private String[] allColumnsMatrix = {BaseColumns._ID,
            DatabaseHelper.colTitle,
            DatabaseHelper.colAddress,
            DatabaseHelper.colLoc,
            DatabaseHelper.colMarkerIcon,
            DatabaseHelper.colPlaats};

    public MarkerLoader(Context context) {
        super(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    protected void onStartLoading() {
        if(mCursor != null) deliverResult(mCursor);
        if(takeContentChanged() || mCursor == null) forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        if(mCursor == null) loadCursor();
        return mCursor;
    }

    //TODO:sorteren

    private void loadCursor() {
        synchronized (lock){
            open();
            if(mCursor != null) return;
            MatrixCursor cursor = new MatrixCursor(allColumnsMatrix);

            Cursor c = database.query(DatabaseHelper.markerInfoTable,allColumns,null,null,null,null,null);

            c.moveToFirst();
            while (!c.isAfterLast()){
                MatrixCursor.RowBuilder row = cursor.newRow();
                row.add(c.getLong(0));
                row.add(c.getString(1));
                row.add(c.getString(2));
                row.add(new LatLng(c.getDouble(3),c.getDouble(4)));
                row.add( c.getInt(5));
                row.add( c.getString(6));
                c.moveToNext();
            }
            close();
            mCursor = cursor;
        }
    }


    private MarkerInfo cursorToMarkerInfo(Cursor cursor){
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String address = cursor.getString(2);
        LatLng pos = new LatLng(cursor.getDouble(3),cursor.getDouble(4));
        Data.MARKER marker = Data.MARKER.getMarker(cursor.getInt(5));
        String plaats = cursor.getString(6);

        MarkerInfo mi = new MarkerInfo(title,pos,address,marker);
        mi.setPlaats(plaats);
        mi.setId(id);

        return mi;


        //return null;
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        System.out.println("hallo");
        if(database == null)System.out.println("hallo2");
    }
    public void close() {
        dbHelper.close();
    }

}

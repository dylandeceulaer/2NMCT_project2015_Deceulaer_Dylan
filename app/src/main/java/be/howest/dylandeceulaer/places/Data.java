package be.howest.dylandeceulaer.places;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 12/04/2015.
 */
public class Data {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.colMarkerInfoID,
    DatabaseHelper.colTitle,
    DatabaseHelper.colAddress,
    DatabaseHelper.colPosLat,
    DatabaseHelper.colPosLong,
    DatabaseHelper.colMarkerIcon,
    DatabaseHelper.colPlaats};

    public enum MARKER{
        EMPTY("All categories",0,0),
        HOME("Home locations",R.drawable.custom_marker_home,R.drawable.custom_marker_home_icon),
        POI("Point of interests",R.drawable.custom_marker_poi,R.drawable.custom_marker_poi_icon),
        MUSEUMS("Museums",R.drawable.custom_marker_museum,R.drawable.custom_marker_museum_icon),
        TRANSPORT("Transport",R.drawable.custom_marker_transport,R.drawable.custom_marker_transport_icon),
        DRINKS("Drinks and food",R.drawable.custom_marker_drinks,R.drawable.custom_marker_drinks_icon);

        private String naam;
        private int marker;
        private int markerIcon;

        MARKER(String naam, int marker, int markerIcon){
            this.naam = naam;
            this.marker = marker;
            this.markerIcon = markerIcon;
        }

        public String getNaam() {
            return naam;
        }

        public int getMarker() {
            return marker;
        }

        public int getMarkerIcon() {
            return markerIcon;
        }

        public static MARKER getMarker(int id){
            for(MARKER marker : MARKER.values()){
                if(marker.getMarker() == id) return marker;
            }
            return MARKER.HOME;
        }


        @Override
        public String toString() {
            return naam;
        }

    }

    public Data(Context context) {
        dbHelper = new DatabaseHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addMarker(MarkerInfo markerInfo){
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.colTitle,markerInfo.getTitel());
        values.put(DatabaseHelper.colAddress, markerInfo.getAdres());
        values.put(DatabaseHelper.colPosLat, markerInfo.getPositie().latitude);
        values.put(DatabaseHelper.colPosLong, markerInfo.getPositie().longitude);
        values.put(DatabaseHelper.colMarkerIcon, markerInfo.getMarkerData().getMarker());
        values.put(DatabaseHelper.colPlaats,markerInfo.getPlaats());


        long a =  database.insert(DatabaseHelper.markerInfoTable, null, values);
        close();
        return a;
    }

    public List<MarkerInfo> getSavedMarkers(){
        open();

        List<MarkerInfo> lijst = new ArrayList<MarkerInfo>();

        Cursor cursor = database.query(DatabaseHelper.markerInfoTable,allColumns,null,null,null,null,null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){

            lijst.add(cursorToMarkerInfo(cursor));

            cursor.moveToNext();
        }
        close();
        return lijst;
    }
    private MarkerInfo cursorToMarkerInfo(Cursor cursor){

        if(cursor.getCount()<1)
            return null;
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String address = cursor.getString(2);
        LatLng pos = new LatLng(cursor.getDouble(3),cursor.getDouble(4));
        MARKER marker = MARKER.getMarker(cursor.getInt(5));
        String plaats = cursor.getString(6);

        MarkerInfo mi = new MarkerInfo(title,pos,address,marker);
        mi.setPlaats(plaats);
        mi.setId(id);

        return mi;
    }
    public MarkerInfo getMarkerByPosition(Marker marker){
        open();

        MarkerInfo res;
        String whereClause = DatabaseHelper.colPosLat+" = ? AND "+DatabaseHelper.colPosLong+" = ?";
        String [] whereArgs = new String [] {
                ""+marker.getPosition().latitude,
                ""+marker.getPosition().longitude
        };

        Cursor cursor = database.query(DatabaseHelper.markerInfoTable,allColumns,whereClause,whereArgs,null,null,null);

        cursor.moveToFirst();

        res = cursorToMarkerInfo(cursor);
        close();

        return res;
    }

    public void deleteMarker(MarkerInfo marker){
        open();

        database.delete(DatabaseHelper.markerInfoTable,DatabaseHelper.colMarkerInfoID+" = "+marker.getId(),null);
        close();
    }

    public void updateMarker(MarkerInfo markerInfo){

        open();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.colTitle,markerInfo.getTitel());
        values.put(DatabaseHelper.colAddress, markerInfo.getAdres());
        values.put(DatabaseHelper.colPosLat, markerInfo.getPositie().latitude);
        values.put(DatabaseHelper.colPosLong, markerInfo.getPositie().longitude);
        values.put(DatabaseHelper.colMarkerIcon, markerInfo.getMarkerData().getMarker());
        values.put(DatabaseHelper.colPlaats,markerInfo.getPlaats());

        database.update(DatabaseHelper.markerInfoTable,values,DatabaseHelper.colMarkerInfoID+" = "+markerInfo.getId(),null);

        close();
    }


}

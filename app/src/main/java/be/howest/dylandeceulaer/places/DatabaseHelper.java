package be.howest.dylandeceulaer.places;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dylan on 12/04/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName="MarkerDB";
    static final String markerInfoTable="MarkerInfos";
    static final String colMarkerInfoID="MarkerInfoId";
    static final String colTitle="Title";
    static final String colAddress="Adress";
    static final String colPlaats="Plaats";

    static final String colPosLat="Latitude";
    static final String colPosLong="Longitude";

    static final String colMarkerIcon="colMarkerIcon";


    DatabaseHelper(Context context){
        super(context,dbName,null,33);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + markerInfoTable + "(" + colMarkerInfoID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + colTitle + " TEXT, "
                + colAddress + " TEXT, "
                + colPlaats + " TEXT, "
                + colPosLat + " REAL NOT NULL ,"
                + colPosLong + " REAL NOT NULL ,"
                + colMarkerIcon + " INTEGER NOT NULL "
                + ");");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+markerInfoTable);
        onCreate(db);


    }
}

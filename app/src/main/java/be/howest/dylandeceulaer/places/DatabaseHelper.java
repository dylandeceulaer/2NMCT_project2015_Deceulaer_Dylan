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
    static final String colMPosition="Position";
    static final String colAddress="Adress";
    static final String colLoc="Loc";
    static final String colPlaats="Plaats";
    static final String colMarker="Marker";

    static final String positionTable = "Positions";
    static final String colPosID="PositionId";
    static final String colPosLat="Latitude";
    static final String colPosLong="Longitude";

    static final String markerTable = "Markers";
    static final String colMarkerId="MarkerId";
    static final String colMarkerName="MarkerName";
    static final String colMarkerI="colMarkerI";
    static final String colMarkerIcon="colMarkerIcon";

    static final String viewEmps="ViewEmps";

    DatabaseHelper(Context context){
        super(context,dbName,null,33);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        //db.execSQL("CREATE TABLE "+positionTable+" ("+colPosID+ " INTEGER PRIMARY KEY , "+ colPosLat+ " INTEGER,"+colPosLong+" INTEGER)");
        //db.execSQL("CREATE TABLE "+markerTable+" ("+colMarkerId+ " INTEGER PRIMARY KEY , "+ colMarkerName+ " TEXT,"+colMarkerI+" INTEGER),"+colMarkerIcon+" INTEGER)");


        db.execSQL("CREATE TABLE "+markerInfoTable+"("+colMarkerInfoID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + colTitle+" TEXT, "
                +colAddress+" TEXT, "
                +colPlaats+" TEXT, "
                +colPosLat+" REAL NOT NULL ,"
                +colPosLong+" REAL NOT NULL ,"
                +colMarkerIcon+" INTEGER NOT NULL "
                //"FOREIGN KEY ("+colMPosition+") REFERENCES"+positionTable+" ("+colPosID+"),"+
                //"FOREIGN KEY ("+colMarker+") REFERENCES"+markerTable+" ("+colMarkerId+"));");
                +");");

        /*
        db.execSQL("CREATE TRIGGER fk_markinfopos_posid " +
                " BEFORE INSERT "+
                " ON "+markerInfoTable+
                " FOR EACH ROW BEGIN"+
                " SELECT CASE WHEN ((SELECT "+colPosID+
                " FROM "+positionTable+
                "WHERE "+colPosID+
                "=new."+colMPosition+
                " ) IS NULL)"+
                " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
                "  END;");

        db.execSQL("CREATE TRIGGER fk_markinfomark_markerid " +
                " BEFORE INSERT "+
                " ON "+markerInfoTable+
                " FOR EACH ROW BEGIN"+
                " SELECT CASE WHEN ((SELECT "+colMarkerId+
                " FROM "+markerTable+
                "WHERE "+colMarkerId+
                "=new."+colMarker+
                " ) IS NULL)"+
                " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
                "  END;");

        db.execSQL("CREATE VIEW "+viewEmps+
                        " AS SELECT "+markerInfoTable+"."+colMarkerInfoID+" AS _id,"+
                        " "+markerInfoTable+"."+colTitle+","+
                        " "+markerInfoTable+"."+colAddress+","+
                        " "+positionTable+"."+colPosLat+""+
                        " "+positionTable+"."+colPosLong+""+
                        " FROM "+markerInfoTable+" JOIN "+positionTable+
                        " ON "+markerInfoTable+"."+positionTable+" ="+colMPosition+"."+colPosID
        );
        */
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+markerInfoTable);
        db.execSQL("DROP TABLE IF EXISTS "+positionTable);
        db.execSQL("DROP TABLE IF EXISTS "+markerTable);


        db.execSQL("DROP TRIGGER IF EXISTS fk_markinfomark_markerid");
        db.execSQL("DROP TRIGGER IF EXISTS fk_markinfopos_posid");
        db.execSQL("DROP VIEW IF EXISTS "+viewEmps);
        onCreate(db);


    }
}

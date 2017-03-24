package website.comas.cbtapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 *Things to consider. Add location and map out people's most recent rating?
 * Predictive elements?
 * Music (like thumb piano hits) as you look at a map
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DateRateAndThree.db";
    public static final String DR3_TABLE_NAME = "dr3"; //Rating, date, 3 good things
    public static final String DR3_COLUMN_ID = "id";
    public static final String DR3_COLUMN_DATE = "date";
    public static final String DR3_COLUMN_RATING = "rating";
    public static final String DR3_COLUMN_THINGONE = "thing1";
    public static final String DR3_COLUMN_THINGTWO = "thing2";
    public static final String DR3_COLUMN_THINGTHREE = "thing3";
    private HashMap hp;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table dr3" +
                        "(id integer primary key, date text, rating integer, thing1 text, thing2 text, thing3 text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dr3");
        onCreate(db);
    }

    public boolean insertDailyRating(String date, int rating, String thing1, String thing2, String thing3){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("rating", rating);
        contentValues.put("thing1", thing1);
        contentValues.put("thing2", thing2);
        contentValues.put("thing3", thing3);
        db.insert("dr3", null, contentValues);
        return true;
    }

    public boolean updateDailyRating(String date, int rating, String thing1, String thing2, String thing3){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rating", rating);
        contentValues.put("thing1", thing1);
        contentValues.put("thing2", thing2);
        contentValues.put("thing3", thing3);
        db.update("dr3", contentValues, "date = ?", new String[]{date});
        return true;
    }

    public int getIDfromDate(String date){ //TODO get ID from date in database. Translate Cursor to String/Int
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {DR3_COLUMN_ID, DR3_COLUMN_DATE};
        Cursor c = db.query(DR3_TABLE_NAME, columns, "date=?", new String[] {date}, null, null, null, null); //query database for ID given date
        //db.execSQL("select date from dr3 where date="+date+"");
        if(c.moveToFirst() == true){
            String idStr = c.getString(c.getColumnIndex(DR3_COLUMN_ID));
        }
        c.close();
        db.close();
        return id;
    }

    public boolean entryToday(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {DR3_COLUMN_ID, DR3_COLUMN_DATE};
        Cursor c = db.query(DR3_TABLE_NAME, columns, "date=?", new String[] {date}, null, null, null, null); //query database if entry that day given date
        return c.moveToFirst();
    }

    public Cursor getInfoByDate(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res = db.rawQuery("select * from dr3 where date=" +date+"", null);
        String[] columns = {DR3_COLUMN_DATE, DR3_COLUMN_RATING, DR3_COLUMN_THINGONE, DR3_COLUMN_THINGTWO, DR3_COLUMN_THINGTHREE};
        Cursor c = db.query(DR3_TABLE_NAME, columns, "date=?", new String[]{date}, null, null, null, null); //query database for rating given date
        // db.close();
        return c;
    }

    public int getRating(String date){
        int rating = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res = db.rawQuery("select * from dr3 where date=" +date+"", null);
        String[] columns = {DR3_COLUMN_DATE, DR3_COLUMN_RATING};
        Cursor c = db.query(DR3_TABLE_NAME, columns, "date=?", new String[] {date}, null, null, null, null); //query database for rating given id
        if(c.moveToFirst() == true){
            String ratStr = c.getString(c.getColumnIndex(DR3_COLUMN_RATING));
            rating = Integer.parseInt(ratStr);
        }
        c.close();
        db.close();
        return rating;
    }

    public String getThings(String date, String whichThing){ //get daily 3 good things
        String thing = "Nothing written";
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res = db.rawQuery("select * from dr3 where date=" +date+"", null);
        String[] columns = {DR3_COLUMN_DATE, whichThing};
        Cursor c = db.query(DR3_TABLE_NAME, columns, "date=?", new String[] {date}, null, null, null, null); //query database for rating given date
        if(c.moveToFirst() == true){
            thing = c.getString(c.getColumnIndex(whichThing));
        }
        c.close();
        db.close();
        return thing;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " +DR3_TABLE_NAME, null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DR3_TABLE_NAME);
        return numRows;
    }

}

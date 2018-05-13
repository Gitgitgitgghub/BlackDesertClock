package hello780831.gmail.com.blackdesertclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kami on 2018/2/26.
 */

public class DataBase extends SQLiteOpenHelper {
    private static final int version = 1;
    private static final String DB_NAME = "BlackDesertClock.db";
    private static final String TABLE1 = "Clock";
    private static final String TABLE2 = "Energy_Clock";
    private static DataBase dataBase = null;
    String CREATE_TABLE1 = "CREATE TABLE "+ TABLE1 +
            "(_id INTEGER PRIMARY KEY," +
            "_Group INTEGER," +
            "Item INTEGER," +
            "Start NUMERIC," +
            "Repeat NUMERIC," +
            "Type INTEGER," +
            "Hour INTEGER," +
            "Minute INTEGER," +
            "AlarmTime NUMERIC);";
    String CREATE_TABLE2 = "CREATE TABLE "+ TABLE2 +
            "(_id INTEGER PRIMARY KEY," +
            "targetTime NUMERIC," +
            "tree NUMERIC," +
            "bell NUMERIC," +
            "start NUMERIC," +
            "now INTEGER," +
            "target INTEGER);";
    private DataBase(Context context) {
        super(context,DB_NAME, null, version);
    }
    public static DataBase getDataBase(Context context){
        if (dataBase == null){
            dataBase = new DataBase(context);
        }
        return dataBase;
    }

    private void insert(List<ArrayList<Clock>> childList){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Log.d("kk", "childListSIZE "+childList.size());
        for (int i = 0; i< childList.size(); i++){
            ArrayList<Clock> dataList = childList.get(i);
            for (int k = 0; k< dataList.size(); k++){
                Clock clock = dataList.get(k);
                contentValues.put("_Group",clock.getGroup());
                contentValues.put("Item",clock.getItem());
                contentValues.put("Start",clock.isStart());
                contentValues.put("Repeat",clock.isRepeat());
                contentValues.put("Type",clock.getType());
                contentValues.put("Hour",clock.getHour());
                contentValues.put("Minute",clock.getMinute());
                if (clock.getAlarmTime() == null){
                    contentValues.put("AlarmTime",0);
                }else {
                    contentValues.put("AlarmTime",clock.getAlarmTime().getTime());
                }
                db.insert(TABLE1,null,contentValues);
            }
        }
        Log.d("kk", "saveData! ");
    }
    private void insertEnergyClock(EnergyClock energyClock){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("now",energyClock.getNow());
        contentValues.put("target",energyClock.getTarget());
        boolean b = false;
        if (energyClock.isBell()){
            b =true;
        }else {
            b = false;
        }
        contentValues.put("bell",b);
        if (energyClock.isTree()){
            b =true;
        }else {
            b = false;
        }
        contentValues.put("tree",b);
        if (energyClock.isStart()){
            b =true;
        }else {
            b = false;
        }
        contentValues.put("start",b);
        int date;
        if (energyClock.getTargetTime() == null){
            date = 0 ;
            contentValues.put("targetTime",date);
        }else {
            contentValues.put("targetTime",energyClock.getTargetTime().getTime());
        }
        db.insert(TABLE2,null,contentValues);

    }
    public List<ArrayList<Clock>> queryAll(){
        List<ArrayList<Clock>> childList = new ArrayList<>();
        ArrayList<Clock> dataList1 = new ArrayList<>();
        ArrayList<Clock> dataList2 = new ArrayList<>();
        ArrayList<Clock> dataList3 = new ArrayList<>();
        ArrayList<Clock> dataList4 = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM "+TABLE1,null);
        int x;
        boolean b ;
        while (cursor.moveToNext()){
            Date date = new Date();
            Clock clock = new Clock();
            clock.setGroup(cursor.getInt(1));
            clock.setItem(cursor.getInt(2));
            x = cursor.getInt(3);
            if (x == 0){
                b = false;
            }else {
                b = true;
            }
            clock.setStart(b);
            x =cursor.getInt(4);
            if (x == 0){
                b = false;
            }else {
                b =true;
            }
            clock.setRepeat(b);
            clock.setType(cursor.getInt(5));
            clock.setHour(cursor.getInt(6));
            clock.setMinute(cursor.getInt(7));
            if (cursor.getLong(8) == 0){
                clock.setAlarmTime(null);
            }else {
                date.setTime(cursor.getLong(8));
                clock.setAlarmTime(date);
            }
            switch (cursor.getInt(1)){
                case 0 :
                    dataList1.add(clock);
                    break;
                case 1 :
                    dataList2.add(clock);
                    break;
                case 2 :
                    dataList3.add(clock);
                    break;
                case 3 :
                    dataList4.add(clock);
                    break;
            }
        }
        childList.add(dataList1);
        childList.add(dataList2);
        childList.add(dataList3);
        childList.add(dataList4);
        return childList;
    }
    public EnergyClock queryEnergyClock(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM "+TABLE2,null);
        EnergyClock energyClock = new EnergyClock(null,false,false,false,0,0);
        while (cursor.moveToNext()){
            if (cursor.getLong(1) == 0){
                energyClock.setTargetTime(null);
            }else {
                Date date = new Date();
                date.setTime(cursor.getLong(1));
                energyClock.setTargetTime(date);
            }
            boolean b ;
            if (cursor.getInt(2) == 0){
                b = false;
            }else {
                b = true;
            }
            energyClock.setTree(b);
            if (cursor.getInt(3) == 0){
                b = false;
            }else {
                b = true;
            }
            energyClock.setBell(b);
            if (cursor.getInt(4) == 0){
                b = false;
            }else {
                b = true;
            }
            energyClock.setStart(b);
            energyClock.setNow(cursor.getInt(5));
            energyClock.setTarget(cursor.getInt(6));
        }
        return energyClock;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE1);
        sqLiteDatabase.execSQL(CREATE_TABLE2);
        Log.d("kk", "創建完成");
    }
    public void saveData(List<ArrayList<Clock>> childList){
        deleteData();
        insert(childList);
    }
    public void saveEnergyClock(EnergyClock energyClock){
        deleteEnergyClock();
        insertEnergyClock(energyClock);
    }

    private void deleteEnergyClock(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE2,null,null);
    }
    private void deleteData(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE1,null,null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

package com.englite3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.englite3.utils.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DbOperator {
    private SQLiteDatabase db;

    public DbOperator(Context context, String dbname){
        db = new DbOpenHelper(context, dbname).getWritableDatabase();
    }

    public void reCreateWordTable(){
        db.execSQL(DbOpenHelper.WORD_TABLE_DELETE);
        db.execSQL(DbOpenHelper.WORD_TABLE_CREATE);
    }

    public long addOneWord(Word word){
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.EN, word.getEn());
        values.put(DbOpenHelper.CN, word.getCn());
        values.put(DbOpenHelper.PRON, word.getPron());
        values.put(DbOpenHelper.COMBO, word.getCombo());
        values.put(DbOpenHelper.LEVEL, word.getLv());
        values.put(DbOpenHelper.E, word.getE());
        values.put(DbOpenHelper.FLAG, word.getFlag());
        long ret = db.insert(DbOpenHelper.WORD_TABLE_NAME, null, values);
        return ret;
    }

    public List<Word> selectWordsByLevel(int min, int max, int number){
        String[] span = {Integer.toString(min), Integer.toString(max), Integer.toString(number)};
        Cursor c = db.query(DbOpenHelper.WORD_TABLE_NAME, null, DbOpenHelper.LEVEL + " between ? and ? and " + DbOpenHelper.FLAG + " > 0", span, null, null, null);
        List<Word> lst = new ArrayList<Word>(), ret = new ArrayList<Word>();
        c.moveToFirst();
        int colNum = c.getColumnCount(), tmp;
        String en, cn, pron, combo;
        int lv, e, flag;
        do{
            for(int i=0;i<colNum;++i){
                tmp = c.getColumnIndex(DbOpenHelper.EN);
                en = c.getString(tmp);
                tmp = c.getColumnIndex(DbOpenHelper.CN);
                cn = c.getString(tmp);
                tmp = c.getColumnIndex(DbOpenHelper.PRON);
                pron = c.getString(tmp);
                tmp = c.getColumnIndex(DbOpenHelper.COMBO);
                combo = c.getString(tmp);
                tmp = c.getColumnIndex(DbOpenHelper.LEVEL);
                lv = c.getInt(tmp);
                tmp = c.getColumnIndex(DbOpenHelper.E);
                e = c.getInt(tmp);
                tmp = c.getColumnIndex(DbOpenHelper.FLAG);
                flag = c.getInt(tmp);
                lst.add(new Word(en, cn, pron, combo, lv, e, flag));
            }

        }while (c.moveToNext());

        if(lst.size() > number){
            Random random = new Random();
            int count = 0, x;
            while(count < number){
                x = random.nextInt(lst.size());
                ret.add(lst.get(x));
                lst.remove(x);
                count += 1;
            }
        }else{
            ret = lst;
        }
        return ret;
    }
}

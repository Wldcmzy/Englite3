package com.englite3.database;

import static com.englite3.utils.Tools.fastPower;
import static com.englite3.utils.Tools.randint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.englite3.Config;
import com.englite3.utils.Word;

import java.util.ArrayList;
import java.util.List;

public class DbOperator {
    private SQLiteDatabase db;

    public DbOperator(Context context, String dbname){
        db = new DbOpenHelper(context, dbname).getWritableDatabase();
    }
    /*
    重建WORD表
     */
    public void reCreateWordTable(){
        db.execSQL(DbOpenHelper.WORD_TABLE_DELETE);
        db.execSQL(DbOpenHelper.WORD_TABLE_CREATE);
    }
    /*
    向WORD表中增加一个单词
     */
    public void addOneWord(Word word){
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.EN, word.getEn());
        values.put(DbOpenHelper.CN, word.getCn());
        values.put(DbOpenHelper.PRON, word.getPron());
        values.put(DbOpenHelper.COMBO, word.getCombo());
        values.put(DbOpenHelper.LEVEL, word.getLv());
        values.put(DbOpenHelper.E, word.getE());
        values.put(DbOpenHelper.FLAG, word.getFlag());
        db.insert(DbOpenHelper.WORD_TABLE_NAME, null, values);
    }

    /*
    获取词库所有单词
     */
    public List<Word> selectAll(){
        return selectWordsByLevel(-1, fastPower(2, Config.WORD_MAX_E + 2), 0x3fffffff, false);
    }

    /*
    根据单词等级选出指定数量个单词
     */
    public List<Word> selectWordsByLevel(int min, int max, int number, boolean isflag){
        List<Word> ret = new ArrayList<Word>();
        try {
            Cursor c;
            if (isflag) {
                String[] columns = {"*"};
                String[] args = {Integer.toString(min), Integer.toString(max), "0"};
                c = db.query(DbOpenHelper.WORD_TABLE_NAME, columns, " ( " + DbOpenHelper.LEVEL + " between ? and ? ) and ( " + DbOpenHelper.FLAG + " > ? );", args, null, null, null);
            } else {
                String[] columns = {"*"};
                String[] args = {Integer.toString(min), Integer.toString(max)};
                c = db.query(DbOpenHelper.WORD_TABLE_NAME, columns, DbOpenHelper.LEVEL + " between ? and ? ;", args, null, null, null);
            }
            List<Word> lst = new ArrayList<Word>();
            c.moveToFirst();
            int tmp;
            String en, cn, pron, combo;
            int lv, e, flag;
            do{
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
            }while (c.moveToNext());
            if(lst.size() > number){
                int count = 0, x;
                while(count < number){
                    x = randint(0, lst.size() - 1);
                    ret.add(lst.get(x));
                    lst.remove(x);
                    count += 1;
                }
            }else{
                ret = lst;
            }
        }catch (Exception e){
            Log.e("at DbOperator", e.getMessage());
        }
        return ret;
    }
    /*
    获取已规划单词的梯度等级, 共有100 80 60 40 20 0六个梯度
    eg:
    对于100%的单词,等级达到了x;
    对于80%的单词,等级达到了y;
     */
    public int[] getWordsLevel(){
        int[] ret = new int[6];
        String[] columns = {DbOpenHelper.LEVEL};
        String[] args = {"0"};
        Cursor c = db.query(DbOpenHelper.WORD_TABLE_NAME, columns,DbOpenHelper.FLAG + " > ?", args,null,null,DbOpenHelper.LEVEL);
        int cnt = c.getCount();
        if(cnt != 0){
            int[] arr = new int[cnt];
            int tmp;
            c.moveToFirst();
            for(int i=0; i<cnt; i++){
                tmp = c.getColumnIndex(DbOpenHelper.LEVEL);
                arr[i] = c.getInt(tmp);
//            Log.d("levels", "arr[" + i + "] = " + arr[i]);
                if(!c.moveToNext()) break;
            }
            if(cnt > 0) cnt -= 1;
            double rate= 0;
            for(int i=0; i<5; i++){
                tmp = (int)(cnt * rate);
                ret[i] = arr[tmp];
                rate += 0.2;
            }
            ret[5] = arr[arr.length - 1];
        }
        return ret;
    }
    /*
    flag = false: 获取词库中的单词总数
    flag = true: 获取词库中已规划单词的总数
     */
    public int getWordsNumber(boolean flag){
        String sql = "SELECT count(*) FROM " + DbOpenHelper.WORD_TABLE_NAME;
        if(flag == true) sql += " WHERE " + DbOpenHelper.FLAG + " > 0 ";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    /*
    随机将指定数量个未规划单词加入规划
     */
    public int randomAddFlagWords(int number){
        String[] columns = {DbOpenHelper.EN};
        String[] args = {"0"};
        Cursor c = db.query(DbOpenHelper.WORD_TABLE_NAME, columns,DbOpenHelper.FLAG + " = ?", args,null,null,null);
        c.moveToFirst();
        int cnt = c.getCount();
        if(cnt == 0){
            return 0;
        }else if(cnt < number){
            number = cnt;
        }
        List<String> lst = new ArrayList<String>();
        int tmp;
        do{
            tmp = c.getColumnIndex(DbOpenHelper.EN);
            lst.add(c.getString(tmp));
        }while (c.moveToNext());
        for(int i=0; i<number; i++){
            int x = randint(0, lst.size() - 1);
            String en = lst.get(x);
            ContentValues values = new ContentValues();
            values.put(DbOpenHelper.FLAG, 1);
            String[] args_ = {en};
            db.update(DbOpenHelper.WORD_TABLE_NAME, values, DbOpenHelper.EN + " = ? ", args_);
            lst.remove(x);
        }
        return number;
    }
    /*
    更新一个单词
     */
    public void updateLevel(Word word){
        String en = word.getEn();
        int lv = word.getLv(), e = word.getE();
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.LEVEL, lv);
        values.put(DbOpenHelper.E, e);
        String[] args_ = {en};
        db.update(DbOpenHelper.WORD_TABLE_NAME, values, DbOpenHelper.EN + " = ? ", args_);
    }
}

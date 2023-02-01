package com.englite3.utils;

import android.util.Log;

/*
用于记录单词信息
 */
public class Word {
    private String en, cn, pron, combo;
    private int lv, e, flag;

    private int repeatRightTimes, totalWrongTimes;

    public Word(String en, String cn, String pron, String combo, int lv, int e, int flag) {
        this.en = en;
        this.cn = cn;
        this.pron = pron;
        this.combo = combo;
        this.lv = lv;
        this.e = e;
        this.flag = flag;
//        Log.w("at Word", "new a Word:" + en);

        repeatRightTimes = 0;
        totalWrongTimes = 0;
    }

    public Word(String[] wordarr){
        this.en = wordarr[0];
        this.cn = wordarr[1];
        this.pron = wordarr[2];
        this.combo = wordarr[3];
        this.lv = Integer.parseInt(wordarr[4]);
        this.e = Integer.parseInt(wordarr[5]);
        this.flag = Integer.parseInt(wordarr[6]);


        repeatRightTimes = 0;
        totalWrongTimes = 0;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getCombo() {
        return combo;
    }

    public void setCombo(String combo) {
        this.combo = combo;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getRepeatRightTimes() {
        return repeatRightTimes;
    }

    public void setRepeatRightTimes(int repeatRightTimes) {
        this.repeatRightTimes = repeatRightTimes;
    }

    public int getTotalWrongTimes() {
        return totalWrongTimes;
    }

    public void setTotalWrongTimes(int totalWrongTimes) {
        this.totalWrongTimes = totalWrongTimes;
    }
}

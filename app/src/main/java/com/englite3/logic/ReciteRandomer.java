package com.englite3.logic;

import static com.englite3.utils.Api.fastPower;
import static com.englite3.utils.Api.randint;

import android.util.Log;

import com.englite3.database.DbOperator;
import com.englite3.utils.Word;

import java.util.List;

public class ReciteRandomer {
    private List<Word> pool;
    private DbOperator dop;
    private Word wordActive, wordPrepare;

    public ReciteRandomer(DbOperator dop, int mn, int mx, int num) {
        this.dop = dop;
        this.pool = dop.selectWordsByLevel(mn, mx, num);
        wordActive = wordPrepare = null;
    }

    private Word randomChoice(){
        Word word = null;
        if(pool.size() > 0){
            int x = randint(0, pool.size() - 1);
            word = pool.get(x);
            pool.remove(x);
        } else{
            return null;
        }
        return word;
    }

    public Word next(){
        if(wordPrepare == null){
            wordActive = randomChoice();
        }else{
            wordActive = wordPrepare;
        }
        wordPrepare = randomChoice();
        return wordActive;
    }

    /*
    判断用户是否背过一个单词
    判断依据: 在本次背诵中,积累错误totalWrong次的单词需要至少连续背过repeatRight次,算背过,否则算没背过
    返回值: true代表判断为背过
     */
    private boolean verify(Word word, int totalWrong, int repeatRight){
        if(word.getTotalWrongTimes() >= totalWrong && word.getRepeatRightTimes() + 1 < repeatRight){
            word.setRepeatRightTimes(word.getRepeatRightTimes() + 1);
            return false;
        }
        return true;
    }


    private boolean seriesVerify(Word word){
        if(verify(word, 5, 5)) return true;
        if(verify(word, 3, 4)) return true;
        if(verify(word, 1, 3)) return true;
        return false;
    }

    public boolean grasp(Word word){
        if(seriesVerify(word) == false){
            word.setRepeatRightTimes(word.getRepeatRightTimes() + 1);
            pool.add(word);
            Log.d("at ReciteRender", "grasp:" + word.getEn() + " but system judge not grasp");
            return false;
        }else{
            if(word.getTotalWrongTimes() == 0){
                word.setE(word.getE() + 1);
                int lim = fastPower(2, word.getE());
                word.setLv(randint(lim, lim << 1));
                Log.d("at ReciteRender", "grasp:" + word.getEn() + "set E, LV = " + word.getE() + ", " +  word.getLv());
            }
            dop.updateLevel(word);
            Log.d("at ReciteRender", "remove from temp wordlist:" + word.getEn());
            return true;
        }
    }

    public void unfamiliar(Word word){
        word.setE(0);
        word.setLv(0);
        word.setTotalWrongTimes(word.getTotalWrongTimes() + 1);
        word.setRepeatRightTimes(0);
        pool.add(word);
        Log.d("at ReciteRender", "unfamiliar:" + word.getEn() + "set E, LV = 0, 0");
    }
}

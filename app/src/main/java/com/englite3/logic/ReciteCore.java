package com.englite3.logic;

import static com.englite3.utils.Tools.fastPower;
import static com.englite3.utils.Tools.randint;

import android.util.Log;

import com.englite3.Config;
import com.englite3.database.DbOperator;
import com.englite3.utils.Word;

import java.util.List;

public class ReciteCore {
    private List<Word> pool;
    private DbOperator dop;
    private Word wordActive, wordPrepare;

    public ReciteCore(DbOperator dop, int mn, int mx, int num) {
        this.dop = dop;
        this.pool = dop.selectWordsByLevel(mn, mx, num, true);
        wordActive = wordPrepare = null;
    }

    /*
    从单词列表中随机挑选一个单词
     */
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

    /*
    得到下一个要背的单词
    这个方法保证了在剩下多余一个单词的情况下不会连续出现相同的单词
     */
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
    若单词积累错误次数达到totalWrong,判断用户是否背过这个单词
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


    /*
    判断用户是否背过一个单词
     */
    private boolean seriesVerify(Word word){
        if(!verify(word, 5, 5)) return false;
        if(!verify(word, 3, 4)) return false;
        if(!verify(word, 1, 3)) return false;
        return true;
    }

    /*
    返回剩余单词数
     */
    public int getCount(){
        int wordRemain = pool.size();
        if(wordPrepare != null) wordRemain += 1;
        return wordRemain;
    }

    /*
    对单词执行背过操作
     */
    public int grasp(Word word){
        if(word == null) return 0;
        if(seriesVerify(word) == false){
            word.setRepeatRightTimes(word.getRepeatRightTimes() + 1);
            pool.add(word);
            Log.d("at ReciteRender", "grasp: " + word.getEn() + " but system judge not grasp");
        }else{
            if(word.getTotalWrongTimes() == 0){
                int e = word.getE();
                if(e < Config.WORD_MAX_E) e += 1;
                word.setE(e);
                int lim = fastPower(2, e);
                word.setLv(randint(lim >> 1, lim));
                Log.d("at ReciteRender", "grasp: " + word.getEn() + "set E, LV = " + word.getE() + ", " +  word.getLv());
            }
            dop.updateLevel(word);
            Log.d("at ReciteRender", "remove from temp wordlist: " + word.getEn());
        }
        return getCount();
    }

    /*
    对单词执行忘记操作
     */
    public void unfamiliar(Word word){
        if(word == null) return ;
        word.setE(0);
        word.setLv(0);
        word.setTotalWrongTimes(word.getTotalWrongTimes() + 1);
        word.setRepeatRightTimes(0);
        pool.add(word);
        dop.updateLevel(word);
        Log.d("at ReciteRender", "unfamiliar: " + word.getEn() + "set E, LV = 0, 0");
    }
}

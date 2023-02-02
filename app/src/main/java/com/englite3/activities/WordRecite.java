package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.englite3.Config;
import com.englite3.R;
import com.englite3.database.DbOperator;
import com.englite3.logic.ReciteCore;
import com.englite3.utils.Word;

public class WordRecite extends AppCompatActivity implements View.OnClickListener{
    private String dbname;
    private DbOperator dop;
    private TextView t_en, t_cn, t_pron, t_combo, remainnum;
    private Button b_unfamiliar, b_grasp;
    private Word word;
    private ReciteCore rc;
    private boolean hide;

    private int wordRemain;

    private void initView(){
        t_en = findViewById(R.id.en);
        t_cn = findViewById(R.id.cn);
        t_pron = findViewById(R.id.pron);
        t_combo = findViewById(R.id.combo);
        remainnum = findViewById(R.id.remainnum);

        b_grasp = findViewById(R.id.grasp);
        b_grasp.setOnClickListener(this);
        b_unfamiliar = findViewById(R.id.unfamiliar);
        b_unfamiliar.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_recite);

        Intent tmpintent = getIntent();
        dbname = tmpintent.getStringExtra("dbname");
        int mn = tmpintent.getIntExtra("mn", 0);
        int mx = tmpintent.getIntExtra("mx", 0);
        int num = tmpintent.getIntExtra("num", 0);

        dop = new DbOperator(this, Config.Dbprefix + dbname);
        rc = new ReciteCore(dop, mn, mx, num);

        initView();

        wordRemain = rc.getCount();
        hide = false;
        repack();
    }


    public void onClick(View v) {
        int x;
        switch (v.getId()) {
            case R.id.grasp:
                if(!hide){
                    wordRemain = rc.grasp(word);
                }
                repack();
                break;
            case R.id.unfamiliar:
                if(!hide){
                    rc.unfamiliar(word);
                }
                repack();
                break;
            default:
                break;
        }
    }

    private void repack(){
        remainnum.setText("剩余单词数:" + wordRemain);
        if(hide){
            if(word != null){
                t_en.setText(word.getEn());
                t_cn.setText(word.getCn());
                t_pron.setText(word.getPron());
                t_combo.setText(word.getCombo());
                b_grasp.setText("认识");
                b_unfamiliar.setText("忘记");
            }
        }else{
            word = rc.next();
            if(word == null){
                t_en.setText("已经没有单词了");
            }else{
                t_en.setText(word.getEn());
            }
            t_cn.setText("");
            t_pron.setText("");
            t_combo.setText("");
            b_grasp.setText("点击继续");
            b_unfamiliar.setText("点击继续");
        }
        hide = (!hide);
    }
}
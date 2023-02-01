package com.englite3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.englite3.Config;
import com.englite3.R;
import com.englite3.database.DbOperator;
import com.englite3.logic.ReciteRandomer;
import com.englite3.utils.Word;

public class WordRecite extends AppCompatActivity implements View.OnClickListener{
    private String dbname;
    private DbOperator dop;
    private TextView t_en, t_cn, t_pron, t_combo;
    private Button b_unfamiliar, b_grasp;
    private Word word;
    private ReciteRandomer rr;
    private boolean hide;

    private void initView(){
        t_en = findViewById(R.id.en);
        t_cn = findViewById(R.id.cn);
        t_pron = findViewById(R.id.pron);
        t_combo = findViewById(R.id.combo);

        b_grasp = findViewById(R.id.grasp);
        b_grasp.setOnClickListener(this);
        b_unfamiliar = findViewById(R.id.grasp);
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
        rr = new ReciteRandomer(dop, mn, mx, num);
        initView();
        hide = false;
        repack();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grasp:
                rr.grasp(word);
                if(true){

                }
                repack();
                break;
            case R.id.unfamiliar:
                rr.unfamiliar(word);
                repack();
                break;
            default:
                break;
        }
    }

    private void repack(){
        if(hide){
            if(word != null){
                t_en.setText(word.getEn());
                t_cn.setText(word.getCn());
                t_pron.setText(word.getPron());
                t_combo.setText(word.getCombo());
            }
        }else{
            word = rr.next();
            if(word == null){
                t_en.setText("已经没有单词了");
            }else{
                t_en.setText(word.getEn());
            }
            t_cn.setText("");
            t_pron.setText("");
            t_combo.setText("");
        }
        hide = (!hide);
    }
}
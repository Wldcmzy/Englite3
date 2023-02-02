package com.englite3.activities;

import static com.englite3.logic.Functions.downloadDatabase;
import static com.englite3.logic.Functions.queryDatabaseList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.englite3.R;
import com.englite3.adapters.DatabaseNameAdapter;

import java.util.ArrayList;
import java.util.List;

public class CloudQuery extends AppCompatActivity implements View.OnClickListener{
    private Button query_db_list;
    private DatabaseNameAdapter adapter;
    private List<String> db_list;
    private ListView listView;
    private int uselessCounter;

    private void initView(){
        query_db_list = findViewById(R.id.query_db_list);
        query_db_list.setOnClickListener(this);

        listView = findViewById(R.id.list_view);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showdb();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_query);
        initView();
        db_list = new ArrayList<String>();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.query_db_list:
                db_list = queryDatabaseList(this);
                showdb();
                break;
            default:
                break;
        }
    }

    public void showdb(){
        adapter = new DatabaseNameAdapter(this, R.layout.list_member, db_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("at Cloud listview", "" + db_list.get(position));
                String dbname = db_list.get(position).split("_")[0];
                confirm(dbname);
            }
        });
    }

    private void confirm(String databasename){
        AlertDialog.Builder builder = new AlertDialog.Builder(CloudQuery.this);
        builder.setMessage("名字重复的数据库会被覆盖掉, 你确定要下载数据库" + databasename + "吗?")
                .setTitle("二次询问")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadDatabase(CloudQuery.this,databasename);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("不,刚才我手滑了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

}
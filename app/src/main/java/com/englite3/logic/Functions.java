package com.englite3.logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.englite3.Config;
import com.englite3.database.DbOperator;
import com.englite3.utils.AddrInfo;
import com.englite3.utils.UserInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Functions {

    public Functions(){
        ;
    }

    /*
    保存用户信息username, password到文件Config.user_file_name
     */
    public static void saveUserInfo(Context context, String username, String password){
        try{
            String data = username + Config.SEP + password;
//            String filename = context.getFilesDir().getPath() + File.separator + Config.user_file_name;
            FileOutputStream fos = context.openFileOutput(Config.user_file_name, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.e("at Os save userinfo", e.getMessage() + "<<<<<<<<<<<<<<<<<");
        }
    }

    /*
    从文件Config.user_file_name读取用户信息
     */
    public static UserInfo getUserInfo(Context context){
        UserInfo ui = null;
        try {
            FileInputStream fis = context.openFileInput(Config.user_file_name);
            byte[] buff = new byte[1024];
            int len;
            String data = "";
            while ((len = fis.read(buff)) >= 0) {
                data += new String(buff, 0, len);
            }
            fis.close();
            String[] dataArray = data.split(Config.SEP);

            ui = new UserInfo(dataArray[0], dataArray[1]);

        }catch (Exception e){
            Log.e("at Os", e.getMessage());
        }
        return ui;
    }

    /*
    保存用户的云端设置信息到文件Config.addr_file_name
     */
    public static void saveCloudAddr(Context context, String host,String port, boolean ifaes, String pubkey){
        try{
            String data = host + Config.SEP + port + Config.SEP + Boolean.toString(ifaes);
            FileOutputStream fos = context.openFileOutput(Config.addr_file_name, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            fos = context.openFileOutput(Config.key_file_name, Context.MODE_PRIVATE);
            fos.write(pubkey.getBytes());
            fos.close();
            Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.e("at Os", e.getMessage() + "<<<<<<<<<<<<<<<<<");
        }
    }

    /*
    从文件Config.addr_file_name读取用户云端配置信息
     */
    public static AddrInfo getCloudAddr(Context context){
        AddrInfo ai = null;
        try {
            FileInputStream fis = context.openFileInput(Config.addr_file_name);
            byte[] buff = new byte[1024];
            int len;
            String data = "";
            while ((len = fis.read(buff)) >= 0) {
                data += new String(buff, 0, len);
            }
            fis.close();
            String[] dataArray = data.split(Config.SEP);

            fis = context.openFileInput(Config.key_file_name);
            data = "";
            while ((len = fis.read(buff)) >= 0) {
                data += new String(buff, 0, len);
            }
            fis.close();
            ai = new AddrInfo(dataArray[0], dataArray[1],dataArray[2].equals("true"),data);

        }catch (Exception e){
            Log.e("at Os", e.getMessage());
        }
        return ai;
    }

    /*
    像云端服务器请求服务器的所有词库, 返回值List<String>中, String的格式为: "词库名_描述信息"
     */
    public static List<String> queryDatabaseList(Context context){
        AddrInfo ai = getCloudAddr(context);
        UserInfo ui = getUserInfo(context);
        List<String> db_list = new ArrayList<>();
        if(ai == null){
            Toast.makeText(context, "服务器配置读取失败", Toast.LENGTH_SHORT).show();
        }else if(ui == null){
            Toast.makeText(context, "用户信息读取失败", Toast.LENGTH_SHORT).show();
        }
        else {
            Tcp tcp = new Tcp(context, ai, ui);
            db_list = tcp.query_db_list();
            String sta = db_list.get(0);
            Toast.makeText(context, sta, Toast.LENGTH_SHORT).show();
            if(db_list.size() > 0) db_list.remove(0);
        }
        return db_list;
    }

    /*
    从云端服务器指定下载一个词库
     */
    public static void downloadDatabase(Context context, String dbname){
        try{
            AddrInfo ai = getCloudAddr(context);
            UserInfo ui = getUserInfo(context);
            if(ai == null){
                Toast.makeText(context, "服务器配置读取失败", Toast.LENGTH_SHORT).show();
            }else if(ui == null){
                Toast.makeText(context, "用户信息读取失败", Toast.LENGTH_SHORT).show();
            }else{
                Tcp tcp = new Tcp(context, ai, ui);
                String sta = tcp.download_db(dbname);
                Toast.makeText(context, sta, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Log.e("at Os download", e.getMessage());
        }
    }

    public static void uploadDatabase(Context context, String dbname){
        try{
            AddrInfo ai = getCloudAddr(context);
            UserInfo ui = getUserInfo(context);
            if(ai == null){
                Toast.makeText(context, "服务器配置读取失败", Toast.LENGTH_SHORT).show();
            }else if(ui == null){
                Toast.makeText(context, "用户信息读取失败", Toast.LENGTH_SHORT).show();
            }else{
                Tcp tcp = new Tcp(context, ai, ui);
                String sta = tcp.upload_db(dbname);
                Toast.makeText(context, sta, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.e("at Os upload", e.getMessage());
        }

    }

    /*
    从本地数据库目录中获取本地拥有的词库列表
     */
    public static List<String> getDbName(Context context) {
        String path = Config.DatabaseDirPath;
        File file=new File(path);
        File[] files=file.listFiles();

        if (files == null){
            Log.e("at OS getDbName", "files is null");
            return null;
        }

        List<String> s = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            String x =files[i].getName();
            if(x.startsWith(Config.Dbprefix) && x.endsWith(".db")) {
                s.add(x.replace(Config.Dbprefix, ""));
            }
        }
        return s;
    }

    /*
    在指定词库dop中, 把num个新单词加入规划
     */
    public static void randomAddFlagWords(Context context, DbOperator dop, int num){
        if(num <= 0){
            Toast.makeText(context, "请检查增加数量", Toast.LENGTH_SHORT).show();
            return ;
        }
        int r = dop.randomAddFlagWords(num);
        String s = "已增加" + r +"个单词进入规划";
        if(r < num){
            s = "单词数量不足," + s;
        }
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

    }

}

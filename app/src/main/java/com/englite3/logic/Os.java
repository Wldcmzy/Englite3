package com.englite3.logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.englite3.Config;
import com.englite3.utils.AddrInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class Os {

    public Os(){
        ;
    }

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
            Log.e("erp", e.getMessage() + "<<<<<<<<<<<<<<<<<");
        }
    }

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
            Log.e("sdf", e.getMessage());
        }
        return ai;
    }
}

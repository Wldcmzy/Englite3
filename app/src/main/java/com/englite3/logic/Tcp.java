package com.englite3.logic;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.englite3.Config;
import com.englite3.logic.Rsa;

public class Tcp {
    private Context context;
    public Tcp(Context context){
        this.context = context;
    }

    public void query_db_list(String host, String port){
        new Thread() {
            public void run() {
                boolean know_error = false;
                Looper.prepare();
                try {
                    InetAddress serverip= InetAddress.getByName(host);
                    Socket client=new Socket(serverip, Integer.parseInt(port));

                    OutputStream socketOut=client.getOutputStream();
                    InputStream socketIn=client.getInputStream();

                    byte receive[] = new byte[Config.buffersize];

                    String firstData = Integer.toString(Config.QUERY_DB_LIST);
                    socketOut.write(firstData.getBytes("utf-8"));

                    int len;
                    String rev;
                    List<String> lst = new ArrayList<>();
                    boolean exit = false;

                    while(true){
                        len=socketIn.read(receive);
                        rev=new String(receive,0,len);
                        if(rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.END)){
                            rev = rev.substring(0,rev.length() - Config.END.length());
                            exit = true;
                        }
                        if(rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.SEP)){
                            rev = rev.substring(0,rev.length() - Config.SEP.length());
                        }
                        String[] revs = rev.split(Config.SEP);
                        for(int i=0; i<revs.length; i ++){
                            if(!revs[i].equals("")){
                                lst.add(revs[i]);
                                Log.d("daf", revs[i]);
                            }
                        }

                        if (exit) {
                            Log.d("sdaf", ">>>>>>>>>>>>>>>>>>>ok 传输完成....");
                            Toast.makeText(context, "传输完成", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    socketOut.close();
                    socketIn.close();
                    client.close();

                }catch(ConnectException e){
                    Toast.makeText(context, "链接异常", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Log.i("tcp-error", e.getMessage() + e.getStackTrace() + e.getClass());
                    if(! know_error){
                        Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                    }
                }
                Looper.loop();
            }
        }.start();//启动线程
    }
}

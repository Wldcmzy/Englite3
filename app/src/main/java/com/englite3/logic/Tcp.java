package com.englite3.logic;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.englite3.Config;
import com.englite3.logic.Rsa;

public class Tcp {
    public static final String transOK = "传输完成";
    public static final String linkERROR = "链接异常";
    public static final String unknowERROR = "未知错误";
    public static final String verfyDENY = "身份验证失败";

    public static final int AesMod = 1, RsaMod = 2, NoneMod = 0; // 数据加密方式
    /*
    程序的不同阶段采用的加密方式
    例如
    首次消息由客户端发送aes密钥给服务器，加密方式为Rsa
    其他消息为aes加密通信
    */
    private int firstMod, nextMod;
    public static final String charset = "utf-8";

    private int timeoutLim = 2000;
    private String pubkey;
    private String aeskey;

    public static List<String> recvs = new ArrayList<String>();


    public Tcp(){
        firstMod = nextMod = NoneMod;
    }
    public Tcp(boolean ifaes, String pubkey){
        if(ifaes){
            firstMod = RsaMod;
            nextMod = AesMod;
        }else{
            firstMod = nextMod = NoneMod;
        }
        this.pubkey = pubkey;
    }

    private class ServerDenyException extends Exception {
        public ServerDenyException() {
            super();
        }
        public ServerDenyException(String s) {
            super(s);
        }
    }

    private String generateAesKey(){
        return "123";
    }

    private Socket newClient(String host, String port) {
        Socket client = null;
        try{
            SocketAddress address = new InetSocketAddress(host, Integer.parseInt(port));
            client=new Socket();
            client.connect(address, this.timeoutLim);
            Log.d("at TCP", "client ok");
        }catch (Exception e){
            Log.e("at TCP e",e.getMessage());
            client = null;
        }
        return client;
    }
    private void closeClient(Socket sk) throws IOException{
        InputStream i= sk.getInputStream();
        OutputStream o= sk.getOutputStream();
        i.close();
        o.close();
        sk.close();
    }

    private void send(Socket sk, String data, int mod) throws IOException {
        try{
            byte[] bytes = data.getBytes(Tcp.charset);
            if(mod == Tcp.AesMod){
                // 暂无加密
            }
            else if(mod == Tcp.RsaMod){
                // 暂无加密

            }
            OutputStream out = sk.getOutputStream();
            out.write(bytes);
        }catch(IOException e){
            Log.e("SEND",e.getMessage());
        }
    }

    private String recv(Socket sk, int mod) throws IOException{
        String rev = null;
        try{
            byte bytes[] = new byte[Config.buffersize];
            InputStream in = sk.getInputStream();
            int len = in.read(bytes);
            if(mod == Tcp.AesMod){
                // 暂无加密
                rev=new String(bytes,0,len);
            }
            else if(mod == Tcp.RsaMod){
                // 暂无加密
                rev=new String(bytes,0,len);
            }else{
                rev=new String(bytes,0,len);
            }
        } catch (IOException e){
            Log.e("RECV",e.getMessage());
        }
        return rev;
    }

    public List<String> query_db_list(String host, String port, String username, String password){
        Thread t = new Thread() {
            @Override
            public void run() {
                List<String> revlst = new ArrayList<String>();
//                Looper.prepare();
                try {
                    Socket client = newClient(host, port);
                    if(client == null){
                        throw new ConnectException("未能成功链接服务器");
                    }

                    String rev;
                    boolean exit = false;

                    String key = generateAesKey();
                    aeskey = key;
                    send(client, key, firstMod);

                    rev = recv(client, nextMod);

                    String login_and_query_msg = username + "_" + password + "_" + Integer.toString(Config.QUERY_DB_LIST);
                    send(client, login_and_query_msg, nextMod);

                    rev = recv(client, nextMod);
                    if(rev.equals(Config.DENY)){
                        throw new ServerDenyException("未通过身份验证,服务器拒绝服务");
                    }else{
                        while(true){
                            rev = recv(client, nextMod);
                            if(rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.END)){
                                rev = rev.substring(0,rev.length() - Config.END.length());
                                exit = true;
                                Log.d("safe", "can break");
                            }
                            if(!(exit && rev.equals(""))){

                                if(rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.SEP)){
                                    rev = rev.substring(0,rev.length() - Config.SEP.length());
                                }

                                String[] revs = rev.split(Config.SEP);
                                for(int i=0; i<revs.length; i ++){
                                    if(!revs[i].equals("")){
                                        revlst.add(revs[i]);
                                    }
                                }
                            }
                            if (exit) {
//                            Toast.makeText(context, "传输完成", Toast.LENGTH_SHORT).show();
                                revlst.add(0, Tcp.transOK);
                                break;
                            }
                        }
                    }
                    closeClient(client);
                    Log.d("atTcp", "Socket  ||||||   closed");
//                    Looper.myLooper().quit();

                }catch(ConnectException e){
//                    Toast.makeText(context, "链接异常", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.linkERROR);
                    Log.e("at TCP_query_db_lst", e.getMessage());
                }catch(ServerDenyException e){
                    revlst.add(0, Tcp.verfyDENY);
                }
                catch(Exception e){
//                    Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.unknowERROR);
                    Log.e("at TCP_query_db_lst", e.getMessage());
                }
//                Looper.loop();
                Tcp.setRecvs(revlst);
            }
        };
        t.start();
        int x = 0;
        while(t.isAlive()){
            Log.d("at Tcp query_db_lst", "task is Alive........");
            SystemClock.sleep(200);
            if(x > 5000){
                List<String> ee = new ArrayList<String>();
                ee.add("超时");
                Tcp.setRecvs(ee);
                break;
            }
            x += 200;
        }
        return Tcp.getRecvs();
    }

    public static List<String> getRecvs() {
        return recvs;
    }

    public static void setRecvs(List<String> recvs) {
        Tcp.recvs = recvs;
    }


    public String download_db(FileOutputStream fos, String dbname, String host, String port, String username, String password) {
        Thread t = new Thread() {
            @Override
            public void run() {
                List<String> revlst = new ArrayList<String>();
//                Looper.prepare();
                try {
                    Socket client = newClient(host, port);
                    if (client == null) {
                        throw new ConnectException("未能成功链接服务器");
                    }

                    String rev;
                    boolean exit = false;

                    String key = generateAesKey();
                    aeskey = key;
                    send(client, key, firstMod);

                    rev = recv(client, nextMod);

                    String login_and_query_msg = username + "_" + password + "_" + Integer.toString(Config.DOWNLOAD_DB) + "_" + dbname;
                    send(client, login_and_query_msg, nextMod);

                    rev = recv(client, nextMod);
                    if (rev.equals(Config.DENY)) {
                        throw new ServerDenyException("未通过身份验证,服务器拒绝服务");
                    } else {
                        while (true) {
                            rev = recv(client, nextMod);
                            Log.e("safe", rev);
                            if (rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.END)) {
                                rev = rev.substring(0, rev.length() - Config.END.length());
                                exit = true;
                                Log.d("safe", "can break");
                            }
                            if (!(exit && rev.equals(""))) {
                                if (rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.SEP)) {
                                    rev = rev.substring(0, rev.length() - Config.SEP.length());
                                }

                                String[] revs = rev.split(Config.SEP);
                                for (int i = 0; i < revs.length; i++) {
                                    if (!revs[i].equals("")) {
                                        ;
                                    }
                                }
                            }
                            Log.e("safe", "OOOOOKKKKKK");
                            if (exit) {
//                            Toast.makeText(context, "传输完成", Toast.LENGTH_SHORT).show();
                                revlst.add(0, Tcp.transOK);
                                break;
                            }
                        }
                    }
                    closeClient(client);
                    Log.d("atTcp", "Socket  ||||||   closed");
//                    Looper.myLooper().quit();

                } catch (ConnectException e) {
//                    Toast.makeText(context, "链接异常", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.linkERROR);
                    Log.e("at TCP_downloadDB", e.getMessage());
                } catch (ServerDenyException e) {
                    revlst.add(0, Tcp.verfyDENY);
                } catch (Exception e) {
//                    Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.unknowERROR);
                    Log.e("at TCP_query_db_lst", e.getMessage());
                }
//                Looper.loop();
                Tcp.setRecvs(revlst);
            }
        };
        t.start();
        int x = 0;
        while (t.isAlive()) {
            Log.d("at Tcp query_db_lst", "task is Alive........");
            SystemClock.sleep(200);
            if (x > 5000) {
                List<String> ee = new ArrayList<String>();
                ee.add("超时");
                Tcp.setRecvs(ee);
                break;
            }
            x += 200;
        }
        return Tcp.getRecvs().get(0);
    }
}

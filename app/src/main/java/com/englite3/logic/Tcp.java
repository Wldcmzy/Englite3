package com.englite3.logic;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.englite3.Config;
import com.englite3.database.DbOperator;
import com.englite3.utils.AddrInfo;
import com.englite3.utils.UserInfo;
import com.englite3.utils.Word;

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

    private int socket_timeoutLim = 2000;
//    private int thread_timeoutLim = 30000, thread_check_delay = 200;
    private String pubkey;
    private String aeskey;
    private AddrInfo ai;
    private UserInfo ui;
    private Context context;

    public static List<String> recvs = new ArrayList<String>();


    public Tcp(Context context, UserInfo ui){
        firstMod = nextMod = NoneMod;
        this.ui = ui;
        this.context = context;
    }
    public Tcp(Context context, AddrInfo ai, UserInfo ui){
        if(ai.isIfaes()){
            firstMod = RsaMod;
            nextMod = AesMod;
        }else{
            firstMod = nextMod = NoneMod;
        }
        this.pubkey = ai.getPubkey();
        this.ai = ai;
        this.ui = ui;
        this.context = context;
    }

    private class ServerDenyException extends Exception {
        public ServerDenyException() {
            super();
        }
        public ServerDenyException(String s) {
            super(s);
        }
    }

    private class DBException extends Exception {
        public DBException() {
            super();
        }
        public DBException(String s) {
            super(s);
        }
    }

    /*
    随机生成AES密钥
    未实现
     */
    private String generateAesKey(){
        return "123";
    }

    /*
    新建一个客户端的Socket
     */
    private Socket newClient(String host, String port) {
        Socket client = null;
        try{
            SocketAddress address = new InetSocketAddress(host, Integer.parseInt(port));
            client=new Socket();
            client.connect(address, this.socket_timeoutLim);
            Log.d("at TCP", "client ok");
        }catch (Exception e){
            Log.e("at TCP e",e.getMessage());
            client = null;
        }
        return client;
    }

    /*
    关闭Socket
     */
    private void closeClient(Socket sk) throws IOException{
        InputStream i= sk.getInputStream();
        OutputStream o= sk.getOutputStream();
        i.close();
        o.close();
        sk.close();
    }

    /*
    发送一条Tcp信息
    加密未实现
     */
    private void send(Socket sk, String data, int mod) throws IOException {
        try{
            byte[] bytes = data.getBytes(Tcp.charset), btyes2;
            if(mod == Tcp.AesMod){
                // 暂无加密
            }
            else if(mod == Tcp.RsaMod){
                // 暂无加密

            }
            OutputStream out = sk.getOutputStream();
//            while(bytes.length > 1024){
//                bytes2 =
//            }
            out.write(bytes);
        }catch(IOException e){
            Log.e("SEND",e.getMessage());
        }
    }

    /*
    接收一条socket信息
    加密未实现
     */
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


    /*
    向服务器请求服务器的词库列表
    注意, 这个函数对长信息未作处理, 若词库列表较长, 应该会出问题
     */
    public List<String> query_db_list(){
        Thread t = new Thread() {
            @Override
            public void run() {
                List<String> revlst = new ArrayList<String>();
//                Looper.prepare();
                try {
                    Socket client = newClient(ai.getHost(), ai.getPort());
                    if(client == null){
                        throw new ConnectException("未能成功链接服务器");
                    }

                    String rev;
                    boolean exit = false;

                    String key = generateAesKey();
                    aeskey = key;
                    send(client, key, firstMod);

                    rev = recv(client, nextMod);

                    String login_and_query_msg = ui.getUsername() + "_" + ui.getPassword() + "_" + Integer.toString(Config.QUERY_DB_LIST);
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


    /*
    从服务器下载一个指定的数据库
     */
    public String download_db(String dbname) {
        Thread t = new Thread() {
            @Override
            public void run() {
                List<String> revlst = new ArrayList<String>();
                List<Word> wordlist = new ArrayList<Word>();
//                Looper.prepare();
                try {
                    Socket client = newClient(ai.getHost(), ai.getPort());
                    if (client == null) {
                        throw new ConnectException("未能成功链接服务器");
                    }

                    String rev;
                    boolean exit = false;

                    String key = generateAesKey();
                    aeskey = key;
                    send(client, key, firstMod);

                    rev = recv(client, nextMod);

                    String login_and_query_msg = ui.getUsername() + "_" + ui.getPassword() + "_" + Integer.toString(Config.DOWNLOAD_DB) + "_" + dbname;
                    send(client, login_and_query_msg, nextMod);

                    rev = recv(client, nextMod);
                    if (rev.equals(Config.DENY)) {
                        throw new ServerDenyException("未通过身份验证,服务器拒绝服务");
                    } else {
                        String remain = "";

                        while (true) {
                            rev = recv(client, nextMod);
                            //-------------XYZ----------------------------------
//                          // 已经开始在石山上堆石了
                            // 这段if语句的作用是：解决报文中Config.SEP 、 Config.END 被切开的问题
                            if (rev.length() < Config.END.length()){
                                Log.e("info", rev);
                                if (remain != "") {
                                    String temp_x = remain + rev;
                                    if (temp_x.length() >= Config.END.length()) {
                                        if (temp_x.substring(temp_x.length() - Config.END.length(), temp_x.length()).equals(Config.END)) {
                                            exit = true;
                                        }
                                    }
                                }
                                if (exit == false){
                                    remain = remain + rev;
                                    continue;
                                }
                            }else
                                //-----------------------XYZ---------------------------------
                            if (rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.END)) {
                                rev = rev.substring(0, rev.length() - Config.END.length());
                                exit = true;
                                Log.d("safe", "can break");
                            }
                            Log.d("safe", "ok1");
//                            int cc = 0;
                            boolean noendTag = false;
                            if (!(rev.equals(""))) {
                                if (rev.length() < Config.SEP.length()){
                                    noendTag = true;
                                }else if (rev.substring(rev.length() - Config.END.length(), rev.length()).equals(Config.SEP)) {
                                    rev = rev.substring(0, rev.length() - Config.SEP.length());
                                    noendTag = false;
                                }else{
                                    noendTag = true;
                                }
                                if(remain.length() > 0){
                                    rev = remain + rev;
                                    remain = "";
                                }
                                Log.d("safe", "ok2");
                                String[] revs = rev.split(Config.SEP);
                                for (int i = 0; i < revs.length; i++) {
                                    if (revs[i].length() > 0) {
                                        if(noendTag && i == (revs.length - 1)){
                                            remain = revs[i];
                                        }else{
                                            Log.d("safe", "ok3");
                                            String[] wordinfo = revs[i].split(Config.WORDSEP);
//                                            dop.addOneWord(new Word(wordinfo));
                                            Log.d("safe", "ok4");
                                            wordlist.add(new Word(wordinfo));
//                                            cc += 1;
//                                            if(cc % 1000 == 1){
//                                                Log.e("sdfsdfsd", cc + "");
//                                            }
                                        }
                                    }
                                }
                            }
                            Log.d("safe", "ok5");
                            if (exit) {
//                            Toast.makeText(context, "传输完成", Toast.LENGTH_SHORT).show();
                                revlst.add(0, Tcp.transOK);
                                DbOperator dop = new DbOperator(context, Config.Dbprefix + dbname);
                                dop.reCreateWordTable();
                                for(int i=0; i<wordlist.size(); i++){
                                    dop.addOneWord(wordlist.get(i));
                                }
                                break;
                            }
                        }
                    }
                    closeClient(client);
                    Log.d("atTcp download", "Socket  ||||||   closed");
//                    Looper.myLooper().quit();

                } catch (ConnectException e) {
//                    Toast.makeText(context, "链接异常", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.linkERROR);
                    Log.e("at TCP_downloadDB err01", e.getMessage());
                } catch (ServerDenyException e) {
                    revlst.add(0, Tcp.verfyDENY);
                } catch (Exception e) {
//                    Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.unknowERROR);
                    Log.e("at TCP_downloadDB err02", e.getMessage() + " " + e.toString());
                }
//                Looper.loop();
                Tcp.setRecvs(revlst);
            }
        };
        t.start();
        int x = 0;
        while (t.isAlive()) {
            Log.d("at Tcp downloadDB", "task is Alive........");
            SystemClock.sleep(200);
            if (x > 30000) {
                List<String> ee = new ArrayList<String>();
                ee.add("超时");
                Tcp.setRecvs(ee);
                break;
            }
            x += 200;
        }
        return Tcp.getRecvs().get(0);
    }

    public String upload_db(String dbname) {
        Thread t = new Thread() {
            @Override
            public void run() {
                List<String> revlst = new ArrayList<String>();
//                Looper.prepare();
                try {
                    Socket client = newClient(ai.getHost(), ai.getPort());
                    if (client == null) {
                        throw new ConnectException("未能成功链接服务器");
                    }

                    String rev;
                    boolean exit = false;

                    String key = generateAesKey();
                    aeskey = key;
                    send(client, key, firstMod);

                    rev = recv(client, nextMod);

                    String login_and_query_msg = ui.getUsername() + "_" + ui.getPassword() + "_" + Integer.toString(Config.UPLAOD_DB) + "_" + dbname;
                    send(client, login_and_query_msg, nextMod);

                    rev = recv(client, nextMod);
                    if (rev.equals(Config.DENY)) {
                        throw new ServerDenyException("未通过身份验证,服务器拒绝服务");
                    } else {
                        DbOperator dop = new DbOperator(context, Config.Dbprefix + dbname);
                        List<Word> wordlist = dop.selectAll();
                        Word w;
                        int cnt = 0;
                        for(int i=0; i<wordlist.size(); i++) {
                            w = wordlist.get(i);
                            String s = "";
                            s += w.getEn() + Config.WORDSEP;
                            s += w.getCn() + Config.WORDSEP;
                            s += w.getPron() + Config.WORDSEP;
                            s += w.getCombo() + Config.WORDSEP;
                            s += Integer.toString(w.getLv()) + Config.WORDSEP;
                            s += Integer.toString(w.getE())  + Config.WORDSEP;
                            s += Integer.toString(w.getFlag());
                            s += Config.SEP;

                            send(client, s, nextMod);
                            if(cnt >= 100){
                                cnt -= 100;
                                SystemClock.sleep(10);
                            }
                        }
                        send(client, Config.END, nextMod);
                    }
                    closeClient(client);
                    Log.d("atTcp upload", "Socket  ||||||   closed");
//                    Looper.myLooper().quit();
                    revlst.add(Tcp.transOK);

                } catch (ConnectException e) {
//                    Toast.makeText(context, "链接异常", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.linkERROR);
                    Log.e("at TCP_uploadDB", e.getMessage());
                } catch (ServerDenyException e) {
                    revlst.add(0, Tcp.verfyDENY);
                } catch (Exception e) {
//                    Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                    revlst.add(0, Tcp.unknowERROR);
                    Log.e("at TCP_uploadDB", e.getMessage());
                }
//                Looper.loop();
                Tcp.setRecvs(revlst);
            }
        };
        t.start();
        int x = 0;
        while (t.isAlive()) {
            Log.d("at Tcp uploadDB", "task is Alive........");
            SystemClock.sleep(200);
            if (x > 30000) {
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

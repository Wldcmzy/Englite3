package com.englite3;

public class Config {
    public static  final String DatabaseDirPath = "/data/user/0/com.englite3/databases/";
    public static final String Dbprefix = "Englite3DataBaseZ";
    public static final int buffersize = 2048;
    public static final String addr_file_name = "addr.txt";
    public static final String user_file_name = "user.txt";
    public static final String key_file_name = "key.pem";


    public static final int QUERY_DB_LIST = 1;
    public static final int SAVE_DB = 2;
    public static final int DOWNLOAD_DB = 3;

    public static final String SEP = "_#QuQ#_";
    public static final String END = "_#XvX#_";
    public static final String ERR = "@ERROR@";
    public static final String WORDSEP = "_#>_<#_";

    public static final String DENY = "DENY";
    public static final String NODB = "NODB";

    public static final int WORD_MAX_LEVEL = 33554432; // 2^25

    public Config(){

    }
}

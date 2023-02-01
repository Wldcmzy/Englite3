package com.englite3.utils;

/*
用于记录用户云端配置信息
 */
public class AddrInfo {
    private String host, port, pubkey;
    private boolean ifaes;

    public AddrInfo(){
        ;
    }

    public AddrInfo(String host, String port, boolean ifaes, String pubkey){
        this.host = host;
        this.port = port;
        this.pubkey = pubkey;
        this.ifaes = ifaes;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public boolean isIfaes() {
        return ifaes;
    }

    public void setIfaes(boolean ifaes) {
        this.ifaes = ifaes;
    }
}

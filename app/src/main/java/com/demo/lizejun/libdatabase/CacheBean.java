package com.demo.lizejun.libdatabase;


public class CacheBean {

    private long time;
    private String url;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CacheBean{" +
                "time=" + time +
                ", url='" + url + '\'' +
                '}';
    }
}

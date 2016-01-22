/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xaurora.util;

import java.sql.Time;

/**
 *
 * @author Lee
 */
public class BrowsedPage {
    private String url;
    private String title;
    private int length;
    private String device;
    private Time time;
    
    public BrowsedPage(){
        
    }
    
    public BrowsedPage(String url, String title, int length, String device, Time time){
        this.url = url;
        this.title = title;
        this.length = length;
        this.device = device;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}

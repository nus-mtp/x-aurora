package xaurora.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import xaurora.system.TimeManager;

public class DataFileMetaData {
    private String filename;
    private String url;
    private String source;
    private long length;
    private long lastModified;
    private String lastModifeidDateTime;
    private static final String SOURCE_UNKNOWN = "unknown";

    public DataFileMetaData(String filename, String url) {
        this.filename = filename;
        this.url = url;
        this.getHostFromURL();
    }

    private void getHostFromURL() {
        this.source = SOURCE_UNKNOWN;
        try {
            URL sourceURL = new URL(this.url);
            this.source = sourceURL.getHost();
        } catch (MalformedURLException e) {

            //e.printStackTrace(); log here
        }
    }

    public void addFileMetaData(long length, long lastModified) {
        this.length = length;
        this.lastModified = lastModified;
    }

    public String getFilename() {
        return this.filename;
    }
    
    public String getUrl() {
        String[] urls = this.url.split("\n");
        return urls[0];
    }

    public String getSource() {
        return this.source;
    }

    public String getLength() {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(this.length / 1024.0) + " KB";
    }

    public long getLastModified() {
        return this.lastModified;
    }
    
    public String getLastModifiedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        this.lastModifeidDateTime = sdf.format(lastModified);
        return this.lastModifeidDateTime;
    }

    public String getCreateTime() {
        return TimeManager.formatDateInMilliseconds(this.lastModified);
    }
}

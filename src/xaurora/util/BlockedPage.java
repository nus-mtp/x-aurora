package xaurora.util;

/**
 *
 * @author Lee
 */
public class BlockedPage {
    private String url;
    private boolean isEnabled;
    
    public BlockedPage(){
        this.url = null;
        this.isEnabled = true;
    }
    
    public BlockedPage(String url, boolean isEnabled){
        this.url = url;
        this.isEnabled = isEnabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}

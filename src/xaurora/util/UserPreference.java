package xaurora.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class UserPreference {
    //System Pane
    private boolean isRunOnStartUp;
    private boolean isHideInToolbar;
    //Hotkeys Pane
    private KeyCode[] extendWordHotkey;
    private KeyCode[] reduceWordHotkey;
    private KeyCode[] extendSentenceHotkey;
    private KeyCode[] reduceSentenceHotkey;
    private KeyCode[] extendParagraphHotkey;
    private KeyCode[] reduceParagraphHotkey;
    //Text Editor Pane
    private int numMatchingTextDisplay;
    private boolean isShowTextSource;
    private Color boxColour;
    private Color textColour;
    private double boxTransparency;
    //Blocked List Pane
    private ArrayList<BlockedPage> blockedList;
    //Path Pane
    private String contentPath;
    private boolean isShowPreviewText;
    private String clearCachesTime;
    //Storage Pane
    private String maxTextSizeStored;
    private String previewTextLength;
    
    private static final int numPreferences = 19;
    private static UserPreference instance = null;
    
    private UserPreference(){
    	initPreferences();
    }
    
    public static UserPreference getInstance(){
        if (instance == null){
            instance = new UserPreference();
        }
        return instance;
    }

    public void initPreferences(){
        //System Pane
        isRunOnStartUp = true;
        isHideInToolbar = true;
        //Hotkeys Pane
        extendWordHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.Z};
        reduceWordHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.X};
        extendSentenceHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.C};
        reduceSentenceHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.V};
        extendParagraphHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.B};
        reduceParagraphHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.N};
        //Text Editor Pane
        numMatchingTextDisplay = 5;
        isShowTextSource = true;
        boxColour = Color.WHITE;
        textColour = Color.BLACK;
        boxTransparency = 0;
        //Blocked List Pane
        blockedList = new ArrayList<BlockedPage>();
        //Path Pane
        contentPath = "C:/User/Desktop";
        isShowPreviewText = true;
        clearCachesTime = "device is off";
        //Storage Pane
        maxTextSizeStored = "100MB";
        previewTextLength = "one sentence";
    }
    
    public void readPreferences(String username) {
        String filename = username + ".in";

        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String[] settings = new String[numPreferences];
            for (int i = 0; i < numPreferences; i++) {
                settings[i] = bufferedReader.readLine();
            }
            
            int index = 0;
            //System Pane
            isRunOnStartUp = toBoolean(settings[index++]);
            isHideInToolbar = toBoolean(settings[index++]);

            //Hotkeys Pane
            String[] hotkey;
            try{
            	hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            	extendWordHotkey = new KeyCode[hotkey.length];

            	for (int i = 0; i < hotkey.length; i++){
            		try{
            			extendWordHotkey[i] = KeyCode.valueOf(hotkey[i]);
            		}catch(IllegalArgumentException | NullPointerException ex){
            			extendWordHotkey = getDefaultExtendWordHotkey();
            			break;
            		}
            	}
            }catch(IndexOutOfBoundsException | NullPointerException ex){
            	extendWordHotkey = getDefaultExtendWordHotkey();
            }
            
            try{
            	hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            	reduceWordHotkey = new KeyCode[hotkey.length];
            	for (int i = 0; i < hotkey.length; i++){
            		try{
            			reduceWordHotkey[i] = KeyCode.valueOf(hotkey[i]);
            		}catch(IllegalArgumentException | NullPointerException ex){
            			reduceWordHotkey = getDefaultReduceWordHotkey();
            			break;
            		}
            	}
            }catch(IndexOutOfBoundsException | NullPointerException ex){
            	reduceWordHotkey = getDefaultReduceWordHotkey();
            }

            try{
            	hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            	extendSentenceHotkey = new KeyCode[hotkey.length];
            	for (int i = 0; i < hotkey.length; i++){
            		try{
            			extendSentenceHotkey[i] = KeyCode.valueOf(hotkey[i]);
            		}catch(IllegalArgumentException | NullPointerException ex){
            			extendSentenceHotkey = getDefaultExtendSentenceHotkey();
            			break;
            		}
            	}
            }catch(IndexOutOfBoundsException | NullPointerException ex){
            	extendSentenceHotkey = getDefaultExtendSentenceHotkey();
            }

            try{
            	hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            	reduceSentenceHotkey = new KeyCode[hotkey.length];
            	for (int i = 0; i < hotkey.length; i++){
            		try{
            			reduceSentenceHotkey[i] = KeyCode.valueOf(hotkey[i]);
            		}catch(IllegalArgumentException | NullPointerException ex){
            			reduceSentenceHotkey = getDafaultReduceSentenceHotkey();
            			break;
            		}
            	}
            }catch(IndexOutOfBoundsException | NullPointerException ex){
            	reduceSentenceHotkey = getDafaultReduceSentenceHotkey();
            }

            try{
            	hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            	extendParagraphHotkey = new KeyCode[hotkey.length];
            	for (int i = 0; i < hotkey.length; i++){
            		try{
            			extendParagraphHotkey[i] = KeyCode.valueOf(hotkey[i]);
            		}catch(IllegalArgumentException | NullPointerException ex){
            			extendParagraphHotkey = getDefaultExtendParagraphHotkey();
            			break;
            		}
            	}
            }catch(IndexOutOfBoundsException | NullPointerException ex){
            	extendParagraphHotkey = getDefaultExtendParagraphHotkey();
            }


            try{
            	hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            	reduceParagraphHotkey = new KeyCode[hotkey.length];
            	for (int i = 0; i < hotkey.length; i++){
            		try{
            			reduceParagraphHotkey[i] = KeyCode.valueOf(hotkey[i]);
            		}catch(IllegalArgumentException | NullPointerException ex){
            			reduceParagraphHotkey = getDefaultReduceParagraphHotkey();
            			break;
            		}
            	}
            }catch(IndexOutOfBoundsException | NullPointerException ex){
            	reduceParagraphHotkey = getDefaultReduceParagraphHotkey();
            }

            //Text Editor Pane
            try{
            	numMatchingTextDisplay = Integer.valueOf(settings[index++]);
            }catch(NumberFormatException ex){
                numMatchingTextDisplay = getDefaultNumMatchingTextDisplay();
            }
            isShowTextSource = toBoolean(settings[index++]);
            try{
                boxColour = Color.valueOf(settings[index++]);
            }catch(NullPointerException | IllegalArgumentException ex){
                boxColour = getDefaultBoxColour();
            }
            try{
                textColour = Color.valueOf(settings[index++]);
            }catch(NullPointerException | IllegalArgumentException ex){
                textColour = getDefaultTextColour();
            } 
            try{
                boxTransparency = Double.valueOf(settings[index++]);;
            }catch(NumberFormatException | NullPointerException ex){
                boxTransparency = getDefaultBoxTransparency();
            }
            
            //Blocked List Pane
            try{
            	String[] s = settings[index++].split("\\s+");
            	blockedList = new ArrayList<BlockedPage>();
            	for (int i=0; i < s.length/2; i++){
            		BlockedPage page = new BlockedPage(s[2*i], Boolean.valueOf(s[(2*i)+1]));
            		blockedList.add(page);
            	}
            }catch(NullPointerException ex){
            	blockedList = new ArrayList<BlockedPage>();
            }

            //Path Pane
            try{
            	String filepath = settings[index++];
            	File file = new File(filepath);
            	if (file.isDirectory()){
            		contentPath = filepath;
            	}else{
            		contentPath = getDefaultContentPath();
            	}
            }catch(NullPointerException | IllegalArgumentException ex){
            	contentPath = getDefaultContentPath();
            }
            isShowPreviewText = toBoolean(settings[index++]);
            clearCachesTime = settings[index++];
            checkClearCachesTime();
            
            //Storage Pane
            maxTextSizeStored = settings[index++];
            checkMaxTextSizeStored();
            previewTextLength = settings[index++];
            checkPreviewTextLength();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            initPreferences();
        } catch (IOException ex) {
            System.out.println("Error reading file " + filename);
        }
    }
    
    private boolean toBoolean(String str){
    	if (str != null && str.equalsIgnoreCase("FALSE")){
    		return false;
    	}else{
    		return true;
    	}
    }
    
	private void checkClearCachesTime() {
		if (clearCachesTime == null || 
				!clearCachesTime.equals("device is off") || 
				!clearCachesTime.equals("one day") ||
				!clearCachesTime.equals("one week") ||
				!clearCachesTime.equals("never")){
			clearCachesTime = getDefaultClearCachesTime();
		}	
	}
	

	private void checkMaxTextSizeStored() {
		if (maxTextSizeStored == null ||
				!maxTextSizeStored.equals("100MB") || 
				!maxTextSizeStored.equals("500MB") ||
				!maxTextSizeStored.equals("1GB") ||
				!maxTextSizeStored.equals("unlimited"))
		maxTextSizeStored = getDefaultMaxTextSizeStored();
	}

    private void checkPreviewTextLength() {
    	if (previewTextLength == null || 
    			!previewTextLength.equals("one sentence") || 
				!previewTextLength.equals("two sentences") ||
				!previewTextLength.equals("three words") ||
				!previewTextLength.equals("one paragraph"))
		previewTextLength = getDefaultPreviewTextLength();
	}

    public void writePreferences(String username) {
        String filename = username + ".in";

        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(String.valueOf(isRunOnStartUp));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(isHideInToolbar));
            bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(extendWordHotkey));
            bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(reduceWordHotkey));
            bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(extendSentenceHotkey));
            bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(reduceSentenceHotkey));
            bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(extendParagraphHotkey));
            bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(reduceParagraphHotkey));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(numMatchingTextDisplay));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(isShowTextSource));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(boxColour));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(textColour));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(boxTransparency));
            bufferedWriter.newLine();
            for (int i=0; i < blockedList.size(); i++){
                BlockedPage page = blockedList.get(i);
                bufferedWriter.write(page.getUrl() + " " + String.valueOf(page.getIsEnabled()) + " ");
            }
            bufferedWriter.newLine();         
            bufferedWriter.write(String.valueOf(contentPath));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(isShowPreviewText));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(clearCachesTime));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(maxTextSizeStored));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(previewTextLength));
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Error writing file " + filename);
        }
    }
    
    public boolean isRunOnStartUp() {
        return isRunOnStartUp;
    }

    public boolean isHideInToolbar() {
        return isHideInToolbar;
    }

    public KeyCode[] getExtendWordHotkey() {
        return extendWordHotkey;
    }

    public KeyCode[] getReduceWordHotkey() {
        return reduceWordHotkey;
    }

    public KeyCode[] getExtendSentenceHotkey() {
        return extendSentenceHotkey;
    }

    public KeyCode[] getReduceSentenceHotkey() {
        return reduceSentenceHotkey;
    }

    public KeyCode[] getExtendParagraphHotkey() {
        return extendParagraphHotkey;
    }

    public KeyCode[] getReduceParagraphHotkey() {
        return reduceParagraphHotkey;
    }
    
    public KeyCode[][] getAllHotkeys(){
        KeyCode[][] hotkeys = null;
        
        KeyCode[] ewh = getExtendWordHotkey();
        KeyCode[] rwh = getReduceWordHotkey();
        KeyCode[] esh = getExtendSentenceHotkey();
        KeyCode[] rsh = getReduceSentenceHotkey();
        KeyCode[] eph = getExtendParagraphHotkey();
        KeyCode[] rph = getReduceParagraphHotkey();
        
        for (int i=0; i < 6; i++){
            KeyCode[] hk = null;
            switch(i){
                case 0: hk = ewh; break;
                case 1: hk = rwh; break;
                case 2: hk = esh; break;
                case 3: hk = rsh; break;
                case 4: hk = eph; break;
                case 5: hk = rph; break; 
            }
            hotkeys[i] = hk;
        }
        
        return hotkeys;
    }

    public int getNumMatchingTextDisplay() {
        return numMatchingTextDisplay;
    }

    public boolean isShowTextSource() {
        return isShowTextSource;
    }

    public Color getBoxColour() {
        return boxColour;
    }

    public Color getTextColour() {
        return textColour;
    }

    public double getBoxTransparency() {
        return boxTransparency;
    }
    
    public ArrayList<BlockedPage> getBlockedList(){
        return blockedList;
    }

    public String getContentPath() {
        return contentPath;
    }

    public boolean isShowPreviewText() {
        return isShowPreviewText;
    }

    public String getClearCachesTime() {
        return clearCachesTime;
    }
    
    public int getClearCachesTimeInHours(){
    	int time;
    	
    	switch(clearCachesTime){
    	case "device is off": time = -1;
    	case "one day": time = 24;
    	case "one week": time = 24*7;
    	case "never": time = Integer.MAX_VALUE;
    	default: time = 24;	
    	}
    	
    	return time;
    }

    public String getMaxTextSizeStored() {
        return maxTextSizeStored;
    }

    public String getPreviewTextLength() {
        return previewTextLength;
    }

    public static int getNumPreferences() {
        return numPreferences;
    }
    
    public KeyCode[] getDefaultExtendWordHotkey(){
        return extendWordHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.Z};
    }

    public KeyCode[] getDefaultReduceWordHotkey() {
        return reduceWordHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.X};
    }

    public KeyCode[] getDefaultExtendSentenceHotkey() {
        return extendSentenceHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.C};
    }

    public KeyCode[] getDafaultReduceSentenceHotkey() {
        return reduceSentenceHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.V};
    }

    public KeyCode[] getDefaultExtendParagraphHotkey() {
        return extendParagraphHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.B};
    }

    public KeyCode[] getDefaultReduceParagraphHotkey() {
        return reduceParagraphHotkey = new KeyCode[]{KeyCode.CONTROL, KeyCode.ALT, KeyCode.N};
    }
    
    public int getDefaultNumMatchingTextDisplay() {
        return numMatchingTextDisplay = 5;
    }

    public Color getDefaultBoxColour() {
        return boxColour = Color.WHITE;
    }

    public Color getDefaultTextColour() {
        return textColour = Color.BLACK;
    }

    public double getDefaultBoxTransparency() {
        return boxTransparency = 0;
    }

    public String getDefaultContentPath() {
        return contentPath = "C:/User/Desktop";
    }

    public String getDefaultClearCachesTime() {
        return clearCachesTime = "device is off";
    }

    public String getDefaultMaxTextSizeStored() {
        return maxTextSizeStored = "100MB";
    }

    public String getDefaultPreviewTextLength() {
        return previewTextLength = "one sentence";
    }

    public void setIsRunOnStartUp(boolean isRunOnStartUp) {
        this.isRunOnStartUp = isRunOnStartUp;
    }

    public void setIsHideInToolbar(boolean isHideInToolbar) {
        this.isHideInToolbar = isHideInToolbar;
    }

    public void setExtendWordHotkey(KeyCode[] extendWordHotkey) {
        this.extendWordHotkey = extendWordHotkey;
    }

    public void setReduceWordHotkey(KeyCode[] reduceWordHotkey) {
        this.reduceWordHotkey = reduceWordHotkey;
    }

    public void setExtendSentenceHotkey(KeyCode[] extendSentenceHotkey) {
        this.extendSentenceHotkey = extendSentenceHotkey;
    }

    public void setReduceSentenceHotkey(KeyCode[] reduceSentenceHotkey) {
        this.reduceSentenceHotkey = reduceSentenceHotkey;
    }

    public void setExtendParagraphHotkey(KeyCode[] extendParagraphHotkey) {
        this.extendParagraphHotkey = extendParagraphHotkey;
    }

    public void setReduceParagraphHotkey(KeyCode[] reduceParagraphHotkey) {
        this.reduceParagraphHotkey = reduceParagraphHotkey;
    }

    public void setNumMatchingTextDisplay(int numMatchingTextDisplay) {
        this.numMatchingTextDisplay = numMatchingTextDisplay;
    }

    public void setIsShowTextSource(boolean isShowTextSource) {
        this.isShowTextSource = isShowTextSource;
    }

    public void setBoxColour(Color boxColour) {
        this.boxColour = boxColour;
    }

    public void setTextColour(Color textColour) {
        this.textColour = textColour;
    }

    public void setBoxTransparency(double boxTransparency) {
        this.boxTransparency = boxTransparency;
    }
    
    public void setBlockedList(ArrayList<BlockedPage> blockedList){
        this.blockedList = blockedList;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public void setIsShowPreviewText(boolean isShowPreviewText) {
        this.isShowPreviewText = isShowPreviewText;
    }

    public void setClearCachesTime(String clearCachesTime) {
        this.clearCachesTime = clearCachesTime;
    }

    public void setMaxTextSizeStored(String maxTextSizeStored) {
        this.maxTextSizeStored = maxTextSizeStored;
    }

    public void setPreviewTextLength(String previewTextLength) {
        this.previewTextLength = previewTextLength;
    }

}

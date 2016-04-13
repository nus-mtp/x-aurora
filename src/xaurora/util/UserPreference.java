package xaurora.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import xaurora.util.Hotkeys;

public class UserPreference {
    //System Pane
    private boolean isRunOnStartUp;
    private boolean isHideInToolbar;
    //Hotkeys Pane
    Hotkeys hotkeys;
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
    
    private static final int numHotkeys = 6;
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
        hotkeys = new Hotkeys();
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
            String[] codes;
            for (int i=0; i < numHotkeys; i++){
            	try{
                	codes = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            		KeyCode[] tempCodes = new KeyCode[codes.length];
            		boolean isCorrect = true;
                	for (int j = 0; j < codes.length; j++){
                		try{
                			tempCodes[j] = KeyCode.valueOf(codes[j]);
                		}catch(IllegalArgumentException | NullPointerException ex){
                			isCorrect = false;
                			hotkeys.setHotkeyCodes(i, getDefaultHotkeyCodes(i));
                			break;
                		}
                	}
                	if (isCorrect){
                		hotkeys.setHotkeyCodes(i, tempCodes);
                	}
            	}catch(IndexOutOfBoundsException | NullPointerException ex){
            		hotkeys.setHotkeyCodes(i, getDefaultHotkeyCodes(i));
            	}
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
            
            for (int i=0; i < numHotkeys; i++){
            	bufferedWriter.write(Arrays.toString(hotkeys.getHotkeyCodes(i)));
                bufferedWriter.newLine();
            }

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

    public KeyCode[] getHotkeyCodes(int index){
    	return hotkeys.getHotkeyCodes(index);
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
    
    public KeyCode[] getDefaultHotkeyCodes(int index){
    	return hotkeys.getDefaultHotkeyCodes(index);
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

    public void setHotkeyCodes(int index, KeyCode[] codes){
    	hotkeys.setHotkeyCodes(index, codes);
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

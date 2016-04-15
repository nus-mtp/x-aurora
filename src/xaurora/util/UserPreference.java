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


/**
 * 
 * @author Lee
 *
 */
public class UserPreference {
    //System Pane preferences
    private boolean isRunOnStartUp;
    private boolean isHideInToolbar;
    //Hotkeys Pane preferences
    Hotkeys hotkeys;
    //Text Editor Pane preferences
    private int numMatchingTextDisplay;
    private boolean isShowTextSource;
    private Color boxColour;
    private Color textColour;
    private double boxTransparency;
    //Blocked List Pane preferences
    private ArrayList<BlockedPage> blockedList;
    //Path Pane preferences
    private String contentPath;
    private boolean isShowPreviewText;
    private String clearCachesTime;
    //Storage Pane preferences
    private String maxTextSizeStored;
    private String previewTextLength;
    
    private static final int numHotkeys = 6;
    private static final int numPreferences = 19;
    private static UserPreference instance = null;
    
    private UserPreference(){
    	initPreferences();
    }
    
    /**
     * makes UserPreference a Singleton class
     * @return an instance of UserPreference
     */
    public static UserPreference getInstance(){
        if (instance == null){
            instance = new UserPreference();
        }
        return instance;
    }

    /**
     * initialize preferences with initial value
     */
    public void initPreferences(){
        //System Pane preferences
        isRunOnStartUp = true;
        isHideInToolbar = true;
        //Hotkeys Pane preferences
        hotkeys = new Hotkeys();
        //Text Editor Pane preferences
        numMatchingTextDisplay = 5;
        isShowTextSource = true;
        boxColour = Color.WHITE;
        textColour = Color.BLACK;
        boxTransparency = 0;
        //Blocked List Pane preferences
        blockedList = new ArrayList<BlockedPage>();
        //Path Pane preferences
        contentPath = "C:/User/Desktop";
        isShowPreviewText = true;
        clearCachesTime = "device is off";
        //Storage Pane preferences
        maxTextSizeStored = "100MB";
        previewTextLength = "one sentence";
    }
    
    /**
     * read preferences from file, run integrity check for each preference
     * and load the correct value to UI 
     * @param username
     */
    public void readPreferences(String username) {
        String filename = username + ".in";

        try {
        	//read preferences from file
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String[] settings = new String[numPreferences];
            for (int i = 0; i < numPreferences; i++) {
                settings[i] = bufferedReader.readLine();
            }
            
            int index = 0;
            
            //System Pane preferences
            isRunOnStartUp = toBoolean(settings[index++]);
            isHideInToolbar = toBoolean(settings[index++]);

            //Hotkeys Pane preferences
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

            //Text Editor Pane preferences
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
            
            //Blocked List Pane preferences
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

            //Path Pane preferences
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
            
            //Storage Pane preferences
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
    
    /**
     * convert a String to Boolean
     * @param str
     * @return a boolean value
     */
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

    /**
     * write preferences to file
     * @param username
     */
    public void writePreferences(String username) {
        String filename = username + ".in";

        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            //System Pane preferences
            bufferedWriter.write(String.valueOf(isRunOnStartUp));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(isHideInToolbar));
            bufferedWriter.newLine();
            //Hotkey Pane preferences
            for (int i=0; i < numHotkeys; i++){
            	bufferedWriter.write(Arrays.toString(hotkeys.getHotkeyCodes(i)));
                bufferedWriter.newLine();
            }
            //Text Editor Pane preferences
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
            //Blocked List Pane preferences 
            for (int i=0; i < blockedList.size(); i++){
                BlockedPage page = blockedList.get(i);
                bufferedWriter.write(page.getUrl() + " " + String.valueOf(page.getIsEnabled()) + " ");
            }
            bufferedWriter.newLine();       
            //Path Pane preferences
            bufferedWriter.write(String.valueOf(contentPath));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(isShowPreviewText));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(clearCachesTime));
            bufferedWriter.newLine();
            //Storage Pane preferences
            bufferedWriter.write(String.valueOf(maxTextSizeStored));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(previewTextLength));
            
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Error writing file " + filename);
        }
    }
 
    /** getters **/
    
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
    
    /** default value getters **/
    
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

    /** setters **/
    
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

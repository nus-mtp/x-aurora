package xaurora.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private String dataPath;
    private boolean isShowPreviewText;
    private String clearCachesTime;
    //Storage Pane
    private String maxTextSizeStored;
    private String previewTextLength;
    
    private static final int numPreferences = 19;
    private static UserPreference instance = null;
    
    private UserPreference(){
        
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
        dataPath = "C:/User/Desktop";
        isShowPreviewText = true;
        clearCachesTime = "device is off";
        //Storage Pane
        maxTextSizeStored = "100MB";
        previewTextLength = "one sentence";
    }
    
    public void readPreferences() {
        String filename = "preferences.txt";

        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String[] settings = new String[numPreferences];
            for (int i = 0; i < numPreferences; i++) {
                settings[i] = bufferedReader.readLine();
            }
            
            int index = 0;
            //System Pane
            isRunOnStartUp = Boolean.valueOf(settings[index++]);
            isHideInToolbar = Boolean.valueOf(settings[index++]);

            //Hotkeys Pane
            String[] hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            extendWordHotkey = new KeyCode[hotkey.length];
            for (int i = 0; i < hotkey.length; i++){
                extendWordHotkey[i] = KeyCode.valueOf(hotkey[i]);
            }
            
            hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            reduceWordHotkey = new KeyCode[hotkey.length];
            for (int i = 0; i < hotkey.length; i++){
                reduceWordHotkey[i] = KeyCode.valueOf(hotkey[i]);
            }
            
            hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            extendSentenceHotkey = new KeyCode[hotkey.length];
            for (int i = 0; i < hotkey.length; i++){
                extendSentenceHotkey[i] = KeyCode.valueOf(hotkey[i]);
            }
            
            hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            reduceSentenceHotkey = new KeyCode[hotkey.length];
            for (int i = 0; i < hotkey.length; i++){
                reduceSentenceHotkey[i] = KeyCode.valueOf(hotkey[i]);
            }
            
            hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            extendParagraphHotkey = new KeyCode[hotkey.length];
            for (int i = 0; i < hotkey.length; i++){
                extendParagraphHotkey[i] = KeyCode.valueOf(hotkey[i]);
            }
            
            hotkey = settings[index].substring(1, settings[index++].length() - 1).split(",\\s+");
            reduceParagraphHotkey = new KeyCode[hotkey.length];
            for (int i = 0; i < hotkey.length; i++){
                reduceParagraphHotkey[i] = KeyCode.valueOf(hotkey[i]);
            }
            
            //Text Editor Pane
            numMatchingTextDisplay = Integer.valueOf(settings[index++]);
            isShowTextSource = Boolean.valueOf(settings[index++]);
            boxColour = Color.valueOf(settings[index++]);
            textColour = Color.valueOf(settings[index++]);
            boxTransparency = Double.valueOf(settings[index++]);;
            
            //Blocked List Pane
            String[] s = settings[index++].split("\\s+");
            blockedList = new ArrayList<BlockedPage>();
            for (int i=0; i < s.length/2; i++){
                BlockedPage page = new BlockedPage(s[2*i], Boolean.valueOf(s[(2*i)+1]));
                blockedList.add(page);
            }
            
            //Path Pane
            dataPath = settings[index++];
            isShowPreviewText = Boolean.valueOf(settings[index++]);
            clearCachesTime = settings[index++];
            
            //Storage Pane
            maxTextSizeStored = settings[index++];
            previewTextLength = settings[index++];

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            initPreferences();
        } catch (IOException ex) {
            System.out.println("Error reading file " + filename);
        }
    }

    public void writePreferences() {
        String filename = "preferences.txt";

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
            bufferedWriter.write(String.valueOf(dataPath));
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

    public String getDataPath() {
        return dataPath;
    }

    public boolean isShowPreviewText() {
        return isShowPreviewText;
    }

    public String getClearCachesTime() {
        return clearCachesTime;
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

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
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

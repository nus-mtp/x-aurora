package xaurora.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javafx.scene.paint.Color;

public class UserPreference {


    //System Pane
    private boolean isRunOnStartUp;
    private boolean isHideInToolbar;
    //Hotkeys Pane
    private String[] extendWordHotkey;
    private String[] reduceWordHotkey;
    private String[] extendSentenceHotkey;
    private String[] reduceSentenceHotkey;
    private String[] extendParagraphHotkey;
    private String[] reduceParagraphHotkey;
    //Text Editor Pane
    private int numMatchingTextDisplay;
    private boolean isShowTextSource;
    private Color boxColour;
    private Color textColour;
    private int boxTransparency;
    //Blocked List Pane
    //Path Pane
    private String dataPath;
    private boolean isShowPreviewText;
    private String clearCachesTime;
    //Storage Pane
    private int maxTextSizeStored;
    private String previewTextLength;
    private static final int numPreferences = 18;

    public void initPreferences(){
        //System Pane
        isRunOnStartUp = true;
        isHideInToolbar = true;
        //Hotkeys Pane
        extendWordHotkey = new String[]{"Ctrl", "Alt", "Z"};
        reduceWordHotkey = new String[]{"Ctrl", "Alt", "X"};
        extendSentenceHotkey = new String[]{"Ctrl", "Alt", "C"};
        reduceSentenceHotkey = new String[]{"Ctrl", "Alt", "V"};
        extendParagraphHotkey = new String[]{"Ctrl", "Alt", "B"};
        reduceParagraphHotkey = new String[]{"Ctrl", "Alt", "N"};
        //Text Editor Pane
        numMatchingTextDisplay = 5;
        isShowTextSource = true;
        boxColour = Color.WHITE;
        textColour = Color.BLACK;
        boxTransparency = 0;
        //Blocked List Pane
        //Path Pane
        dataPath = "C:/User/Desktop";
        isShowPreviewText = true;
        clearCachesTime = "device is off";
        //Storage Pane
        maxTextSizeStored = 100;
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
            //System Pane
            isRunOnStartUp = Boolean.valueOf(settings[0]);
            isHideInToolbar = Boolean.valueOf(settings[1]);
            //Hotkeys Pane
            extendWordHotkey = settings[2].substring(1, settings[2].length() - 1).split(",\\s+");
            reduceWordHotkey = settings[3].substring(1, settings[3].length() - 1).split(",\\s+");
            extendSentenceHotkey = settings[4].substring(1, settings[4].length() - 1).split(",\\s+");
            reduceSentenceHotkey = settings[5].substring(1, settings[5].length() - 1).split(",\\s+");
            extendParagraphHotkey = settings[6].substring(1, settings[6].length() - 1).split(",\\s+");
            reduceParagraphHotkey = settings[7].substring(1, settings[7].length() - 1).split(",\\s+");
            //Text Editor Pane
            numMatchingTextDisplay = Integer.valueOf(settings[8]);
            isShowTextSource = Boolean.valueOf(settings[9]);
            boxColour = Color.valueOf(settings[10]);
            textColour = Color.valueOf(settings[11]);
            boxTransparency = Integer.valueOf(settings[12]);;
            //Blocked List Pane
            //Path Pane
            dataPath = settings[13];
            isShowPreviewText = Boolean.valueOf(settings[14]);
            clearCachesTime = settings[15];
            //Storage Pane
            maxTextSizeStored = Integer.valueOf(settings[16]);
            previewTextLength = settings[17];

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

    public String[] getExtendWordHotkey() {
        return extendWordHotkey;
    }

    public String[] getReduceWordHotkey() {
        return reduceWordHotkey;
    }

    public String[] getExtendSentenceHotkey() {
        return extendSentenceHotkey;
    }

    public String[] getReduceSentenceHotkey() {
        return reduceSentenceHotkey;
    }

    public String[] getExtendParagraphHotkey() {
        return extendParagraphHotkey;
    }

    public String[] getReduceParagraphHotkey() {
        return reduceParagraphHotkey;
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

    public int getBoxTransparency() {
        return boxTransparency;
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

    public int getMaxTextSizeStored() {
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

    public void setExtendWordHotkey(String[] extendWordHotkey) {
        this.extendWordHotkey = extendWordHotkey;
    }

    public void setReduceWordHotkey(String[] reduceWordHotkey) {
        this.reduceWordHotkey = reduceWordHotkey;
    }

    public void setExtendSentenceHotkey(String[] extendSentenceHotkey) {
        this.extendSentenceHotkey = extendSentenceHotkey;
    }

    public void setReduceSentenceHotkey(String[] reduceSentenceHotkey) {
        this.reduceSentenceHotkey = reduceSentenceHotkey;
    }

    public void setExtendParagraphHotkey(String[] extendParagraphHotkey) {
        this.extendParagraphHotkey = extendParagraphHotkey;
    }

    public void setReduceParagraphHotkey(String[] reduceParagraphHotkey) {
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

    public void setBoxTransparency(int boxTransparency) {
        this.boxTransparency = boxTransparency;
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

    public void setMaxTextSizeStored(int maxTextSizeStored) {
        this.maxTextSizeStored = maxTextSizeStored;
    }

    public void setPreviewTextLength(String previewTextLength) {
        this.previewTextLength = previewTextLength;
    }
>>>>>>> cddd6f55378b1c1fdcdaab00c9597cc6c00b406a
}

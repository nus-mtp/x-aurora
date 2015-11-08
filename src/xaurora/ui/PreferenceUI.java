/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xaurora.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.Arrays;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import static javafx.scene.layout.BorderPane.setMargin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Lee
 */
public class PreferenceUI extends Application{          
    
    //System Pane
    boolean runOnStartUp;
    boolean hideInToolbar;
    //Hotkeys Pane
    String[] extendWordHotkey;
    String[] reduceWordHotkey;
    String[] extendSentenceHotkey;
    String[] reduceSentenceHotkey;
    String[] extendParagraphHotkey;
    String[] reduceParagraphHotkey;
    //Text Editor Pane
    int numMatchingTextDisplay;
    boolean showTextSource;
    Color boxColour;
    Color textColour;
    int boxTransparency;
    //Blocked List Pane
    //Path Pane
    String dataPath;
    boolean showPreviewText;
    String clearCachesTime;
    //Storage Pane
    int maxTextSizeStored;
    String previewTextLength;
    float usedSpace;
    float usedPercentage;
    
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage stage){
        stage.setTitle("x-aurora");      
        Scene scene = createScene();
        stage.setScene(scene);
        stage.show();
    }
    
    private Scene createScene(){
        BorderPane border = new BorderPane();
        
        TabPane tabs = new TabPane();
        Tab tabSetting = new Tab("Setting");
        tabSetting.setContent(createSettingPane());
        Tab tabTutorial = new Tab("Tutorial");
        tabTutorial.setContent(createTutorialPane());
        Tab tabAboutUs = new Tab("About Us");
        tabAboutUs.setContent(createAboutUsPane());
        Tab tabDataManaging = new Tab("Data Managing");
        tabDataManaging.setContent(createDataManagingPane());
        tabs.getTabs().addAll(tabSetting, tabTutorial, tabAboutUs, tabDataManaging);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Image image = new Image("File:dropbox.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        Label email = new Label("user@example.com");
        email.setGraphic(imageView);
        
        border.setCenter(tabs);
        
        Scene scene = new Scene(border, 550, 400);
        return scene;
    }
    
    private BorderPane createSettingPane(){
        initSettings();
        BorderPane border = new BorderPane();
        
        TabPane tabs = new TabPane();
        Tab tabSystem = new Tab("System");
        tabSystem.setContent(createSystemPane());
        Tab tabHotkeys = new Tab("Hotkeys");
        tabHotkeys.setContent(createHotkeysPane());
        Tab tabTextEditor = new Tab("Text Editor");
        tabTextEditor.setContent(createTextEditorPane());
        Tab tabBlockedList = new Tab("Blocked List");
        tabBlockedList.setContent(createBlockedListPane());
        Tab tabPath = new Tab("Path");
        tabPath.setContent(createPathPane());
        Tab tabStorage = new Tab("Storage");
        tabStorage.setContent(createStoragePane());
        tabs.getTabs().addAll(tabSystem, tabHotkeys, tabTextEditor, tabBlockedList, tabPath, tabStorage);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        //tabs.setSide(Side.LEFT);
        
        HBox hbox = new HBox();
        Button okButton = new Button("OK");
        okButton.setPrefWidth(70);
        okButton.setOnAction(event -> {writeSettings(); exit(0);});
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(70);
        cancelButton.setOnAction(event -> {exit(1);});
        Button applyButton = new Button("Apply");
        applyButton.setOnAction(event -> {writeSettings();});
        applyButton.setPrefWidth(70);
        hbox.getChildren().addAll(okButton, cancelButton, applyButton);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        border.setCenter(tabs);
        border.setBottom(hbox);
        
        border.setCenter(tabs);
        border.setBottom(hbox);

        return border;
    } 
    
    private void initSettings(){
    //System Pane
    runOnStartUp = true;
    hideInToolbar = true;
    //Hotkeys Pane
    extendWordHotkey = new String[]{"Ctrl","Alt","Z"};
    reduceWordHotkey = new String[]{"Ctrl","Alt","X"};
    extendSentenceHotkey = new String[]{"Ctrl","Alt","C"};
    reduceSentenceHotkey = new String[]{"Ctrl","Alt","V"};
    extendParagraphHotkey = new String[]{"Ctrl","Alt","B"};
    reduceParagraphHotkey = new String[]{"Ctrl","Alt","N"};
    //Text Editor Pane
    numMatchingTextDisplay = 5;
    showTextSource = true;
    boxColour = Color.WHITE;
    textColour = Color.BLACK;
    boxTransparency = 0;
    //Blocked List Pane
    //Path Pane
    dataPath = "C:/User/Desktop";
    showPreviewText = true;
    clearCachesTime = "device is off";
    //Storage Pane
    maxTextSizeStored = 100;
    previewTextLength = "one sentence";
    usedSpace = 2;
    usedPercentage = 10;
    }
    
    private BorderPane createTutorialPane(){
        BorderPane border = new BorderPane();
        
        Image image = new Image("File:dropbox.png");
        ImageView imageView = new ImageView(image);
        
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        Button previousButton = new Button("Previous");
        previousButton.setPrefWidth(70);
        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(70);
        hbox.getChildren().addAll(previousButton, nextButton);
        hbox.setAlignment(Pos.CENTER);
        
        border.setCenter(imageView);
        border.setBottom(hbox);
        setMargin(hbox, new Insets(0, 10, 20, 10));
        
        return border;
    }
    
    private GridPane createAboutUsPane(){
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setAlignment(Pos.CENTER);
        
        Label label = new Label("The main objective behind developing this application is to make it easier to copy and paste across documents and information on the webpages. The program aims at minimizing the act of window switching, visually searching for the sentence one needs to select/highlight and copy the text. Thereby reducing the time spent, increasing the efficiency of the user and making it a useful tool while working heavily on text-processing, such as drafting research papers or a thesis. This auto-complete mechanism will not entirely replace the copy-paste procedure; rather it would enhance the original functionality.");
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        grid.getChildren().add(label);
        
        return grid;
    }
    
    private BorderPane createDataManagingPane(){
        BorderPane border = new BorderPane();
        TableView table = new TableView();
        TableColumn contentCol = new TableColumn("Content");
        TableColumn lengthCol = new TableColumn("Length");
        TableColumn sourceCol = new TableColumn("Source");
        TableColumn deviceCol = new TableColumn("Device");
        TableColumn timeCol = new TableColumn("Time");
        TableColumn statusCol = new TableColumn("Status");
        TableColumn deleteCol = new TableColumn("Delete");
        table.getColumns().addAll(contentCol, lengthCol, sourceCol, 
                deviceCol, timeCol, statusCol, deleteCol);
        table.setEditable(false);
        
        border.setCenter(table);
        
        return border;
    }
    
    private GridPane createSystemPane(){
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        Label label1 = new Label("Run on start up");
        Label label2 = new Label("Hide in toolbar when close");
        CheckBox checkbox1 = new CheckBox();
        checkbox1.setSelected(runOnStartUp);
        CheckBox checkbox2 = new CheckBox();
        checkbox2.setSelected(hideInToolbar);
        grid.add(label1, 0, 0);
        grid.add(checkbox1, 1, 0);
        grid.add(label2, 0, 1);
        grid.add(checkbox2, 1, 1);
        
        return grid;
    }
    
    private GridPane createHotkeysPane(){
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        Label[] labels = new Label[6];
        labels[0] = new Label("Extend Words: ");
        labels[1] = new Label("Reduce Words: ");
        labels[2] = new Label("Extend Sentences: ");
        labels[3] = new Label("Reduce Sentences: ");
        labels[4] = new Label("Extend Paragraphs: ");
        labels[5] = new Label("Reduce Paragraphs: ");
        
        TextField[] textFields = new TextField[6];
        for (int i=0; i < textFields.length; i++){
            textFields[i] = new TextField();
            textFields[i].setMaxWidth(50);
            textFields[i].setAlignment(Pos.CENTER);
        }
        
        ChoiceBox[] boxes = new ChoiceBox[12];
        Label[] plus = new Label[12];
        for (int i=0; i < boxes.length; i++){
            boxes[i] = new ChoiceBox();
            boxes[i].setItems(FXCollections.observableArrayList("Ctrl", "Alt", "Shift"));
            plus[i] = new Label("+");
        }
        
        for (int i=0; i < labels.length; i++){
            String[] hotkey;
            switch(i){
                case 0: hotkey = extendWordHotkey; break;
                case 1: hotkey = reduceWordHotkey; break;
                case 2: hotkey = extendSentenceHotkey; break;
                case 3: hotkey = reduceSentenceHotkey; break;
                case 4: hotkey = extendParagraphHotkey; break;
                case 5: hotkey = reduceParagraphHotkey; break; 
                default: hotkey = new String[3];    
            }
            boxes[2*i].setValue(hotkey[0]);
            boxes[2*i+1].setValue(hotkey[1]);
            textFields[i].setText(hotkey[2]);
        }
        
        for (int i=0; i < labels.length; i++){
            grid.add(labels[i], 0, i);
            grid.add(boxes[2*i], 1, i);
            grid.add(plus[2*i], 2, i);
            grid.add(boxes[2*i+1], 3, i);
            grid.add(plus[2*i+1], 4, i);
            grid.add(textFields[i], 5, i);
        }

        return grid;
    }
    
    private GridPane createTextEditorPane(){
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        Label label1 = new Label("Number of matching text displayed");
        Label label2 = new Label("Show source of text");
        Label label3 = new Label("Box Colour");
        Label label4 = new Label("Text Colour");
        Label label5 = new Label("Box Transparency");
        
        Spinner spinner = new Spinner();
        SpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
        spinner.setValueFactory(svf);
        spinner.setEditable(true);
        spinner.setPrefWidth(70);
        spinner.increment(numMatchingTextDisplay-1);

        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(showTextSource);
        
        ColorPicker boxColorPicker = new ColorPicker();
        boxColorPicker.setValue(boxColour);
        boxColorPicker.setStyle("-fx-color-label-visible: false;");
        
        ColorPicker textColorPicker = new ColorPicker();
        textColorPicker.setValue(textColour);
        textColorPicker.setStyle("-fx-color-label-visible: false;");
        
        Slider transparency = new Slider();
        transparency.setMin(0);
        transparency.setMax(100);
        transparency.setShowTickLabels(true);
        transparency.setShowTickMarks(true);
        transparency.setMajorTickUnit(50);
        transparency.setValue(boxTransparency);
      
        grid.add(label1, 0, 0);
        grid.add(label2, 0, 1);
        grid.add(label3, 0, 2);
        grid.add(label4, 0, 3);
        grid.add(label5, 0, 4);
        grid.add(spinner, 1, 0);
        grid.add(checkbox, 1, 1);
        grid.add(boxColorPicker, 1, 2);
        grid.add(textColorPicker, 1, 3);
        grid.add(transparency, 1, 4);
        
        return grid;
    }
    
    private BorderPane createBlockedListPane(){
        BorderPane border = new BorderPane();
        TableView table = new TableView();
        TableColumn UrlCol = new TableColumn("Website URL");
        UrlCol.setMinWidth(400);
        TableColumn toggleCol = new TableColumn("Enable/Disable");
        toggleCol.setMinWidth(100);
        TableColumn deleteCol = new TableColumn("Delete");
        deleteCol.setPrefWidth(50);
        deleteCol.setMinWidth(50);
        table.getColumns().addAll(UrlCol, toggleCol, deleteCol);
        table.setEditable(false);
        
        //ToggleButton toggleButton = new ToggleButton("Enable");
        //toggleCol.setGraphic(toggleButton);
        
        TextField urlField = new TextField();
        urlField.setPromptText("add url of websites to block");
        urlField.setMinWidth(400);
        Button addButton = new Button("Add to blocked list");
        addButton.setMinWidth(150);
        HBox hbox = new HBox();
        hbox.getChildren().addAll(urlField, addButton);

        border.setCenter(table);
        border.setBottom(hbox);
        return border;
    }
    
    private GridPane createPathPane(){
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        Label label1 =  new Label("Store data at: ");
        TextField pathField = new TextField();
        pathField.setEditable(false);
        pathField.setMinWidth(300);
        pathField.setText(dataPath);
        Button pathButton = new Button("Browse");
        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(showPreviewText);
        Label label2 = new Label("Store preview text as caches to improve matching speed");
        Label label3 = new Label("Clear caches after ");
        ChoiceBox cb = new ChoiceBox();
        cb.setItems(FXCollections.observableArrayList("device is off", "one day", "one week", "never"));
        cb.setValue(clearCachesTime);
        
        grid.add(label1, 0, 0);
        grid.add(pathField, 0, 1);
        grid.add(pathButton, 1, 1);
        grid.add(label2, 0, 2);
        grid.add(checkbox, 1, 2);
        grid.add(label3, 0, 3);
        grid.add(cb, 1, 3);
        
        return grid;
    }
    
    private GridPane createStoragePane(){
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        Label label1 = new Label("Store single text size of at most");
        ChoiceBox cb1 = new ChoiceBox();
        cb1.setItems(FXCollections.observableArrayList("100MB", "500MB", "1GB", "unlimited"));
        cb1.setValue(maxTextSizeStored + "MB");
        Label label2 = new Label("Preview text length");
        ChoiceBox cb2 = new ChoiceBox();
        cb2.setItems(FXCollections.observableArrayList("one sentence", "two sentence", "three words", "one paragraph"));
        cb2.setValue(previewTextLength);
        Label label3 = new Label("Used space: " + usedSpace + "/20.0 GB");
        Label label4 = new Label("Used percentage: " + usedPercentage + "%");  
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
               new PieChart.Data("Used Space", 10), new PieChart.Data("FreeSpace", 90));
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setPrefSize(200, 200);
       
        grid.add(label1, 0, 0);
        grid.add(cb1, 1, 0);
        grid.add(label2, 0, 1);
        grid.add(cb2, 1, 1);
        grid.add(pieChart, 0, 2, 1, 4);
        grid.add(label3, 1, 3);
        grid.add(label4, 1, 4);
        
        return grid;
    }
    
    private void readSettings(){
        String filename = "settings.txt";
        String line = null;
        
        try{
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null){
                System.out.println(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File " + filename + " not found");
        } catch (IOException ex) {
            System.out.println("Error reading file " + filename);
        }
    }
    
    private void writeSettings(){
        String filename = "settings.txt";
        
        try{
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(String.valueOf(runOnStartUp)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(hideInToolbar)); bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(extendWordHotkey)); bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(reduceWordHotkey)); bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(extendSentenceHotkey)); bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(reduceSentenceHotkey)); bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(extendParagraphHotkey)); bufferedWriter.newLine();
            bufferedWriter.write(Arrays.toString(reduceParagraphHotkey)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(numMatchingTextDisplay)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(showTextSource)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(boxColour)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(textColour)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(boxTransparency)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(dataPath)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(showPreviewText)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(clearCachesTime)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(maxTextSizeStored)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(previewTextLength)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(usedSpace)); bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(usedPercentage));
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Error writing file " + filename);
        }
    }
}
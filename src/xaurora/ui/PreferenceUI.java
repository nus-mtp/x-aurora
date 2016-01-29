package xaurora.ui;

import java.io.File;
import static java.lang.System.exit;
import java.util.ArrayList;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import static javafx.scene.layout.BorderPane.setMargin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import xaurora.util.BlockedPage;
import xaurora.util.UserPreference;
import xaurora.util.BrowsedPage;

/**
 *
 * @author Lee
 */
public class PreferenceUI extends Application{          
    private static final int sceneWidth = 600;
    private static final int sceneHeight = 400;
    private static final int tabWidth = 600;
    private static final int tabHeight = 400;
    private static final int imageWidth = 50;
    private static final int imageHeight = 50;
    private static final int buttonWidth = 70;
    private static final int textFieldWidth = 50;
    private static final int spinnerWidth = 70;
    private static final int pieChartWidth = 200;
    private static final int pieChartHeight = 200;
    private static final int topOffset = 10;
    private static final int rightOffset = 10;
    private static final int bottomOffset = 10;
    private static final int leftOffset = 10;  
    private static final int hGap = 50;
    private static final int vGap = 15;
    private static final int numHotkeys = 6;
    private static final int minMatchingDisplayed = 1;
    private static final int maxMatchingDisplayed = 20;
    private static final int majorTickUnit = 50;
    
    UserPreference preferences = new UserPreference();
    Stage stage;
    
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle("x-aurora");      
        Scene preferenceScene = createPreferenceScene();
        stage.setScene(preferenceScene);
        stage.show();
    }
    
    public Scene createPreferenceScene(){    
        BorderPane border = new BorderPane();
        AnchorPane anchor = new AnchorPane();
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
        tabs.setTabMinHeight(44);
        tabs.setPrefSize(tabWidth, tabHeight);
        
        Image image = new Image("File:dropbox.png"); //dummy value
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);
        Label labelEmail = new Label("user@example.com"); //dummy value
        labelEmail.setGraphic(imageView);
        
        anchor.getChildren().addAll(tabs, labelEmail);
        AnchorPane.setTopAnchor(labelEmail, 0.0);
        AnchorPane.setRightAnchor(labelEmail, 0.0);
        
        //border.setCenter(tabs);
        border.setCenter(anchor);
        Scene scene = new Scene(border, sceneWidth, sceneHeight);
        return scene;
    }

    public BorderPane createSettingPane(){
        preferences.readPreferences();
        BorderPane border = new BorderPane();
        
        TabPane tabs = new TabPane();
        Tab tabSystem = new Tab("System");
        tabSystem.setId("System");
        tabSystem.setContent(createSystemPane());
        Tab tabHotkeys = new Tab("Hotkeys");
        tabHotkeys.setId("Hotkeys");
        tabHotkeys.setContent(createHotkeysPane());
        Tab tabTextEditor = new Tab("Text Editor");
        tabTextEditor.setId("TextEditor");
        tabTextEditor.setContent(createTextEditorPane());
        Tab tabBlockedList = new Tab("Blocked List");
        tabBlockedList.setId("BlockedList");
        tabBlockedList.setContent(createBlockedListPane());
        Tab tabPath = new Tab("Path");
        tabPath.setId("Path");
        tabPath.setContent(createPathPane());
        Tab tabStorage = new Tab("Storage");
        tabStorage.setId("Storage");
        tabStorage.setContent(createStoragePane());
        tabs.getTabs().addAll(tabSystem, tabHotkeys, tabTextEditor, tabBlockedList, tabPath, tabStorage);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        //tabs.setSide(Side.LEFT);

        HBox hbox = new HBox();
        Button okButton = new Button("OK");
        okButton.setPrefWidth(buttonWidth);
        okButton.setOnAction(event -> {preferences.writePreferences(); exit(0);});
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(buttonWidth);
        cancelButton.setOnAction(event -> {exit(1);});
        Button applyButton = new Button("Apply");
        applyButton.setPrefWidth(buttonWidth);
        applyButton.setOnAction(event -> {preferences.writePreferences();});
        hbox.getChildren().addAll(okButton, cancelButton, applyButton);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        
        border.setCenter(tabs);
        border.setBottom(hbox);

        return border;
    } 
    
    private BorderPane createTutorialPane(){
        BorderPane border = new BorderPane();
        
        Image image = new Image("File:dropbox.png");
        ImageView imageView = new ImageView(image);
        
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        Button previousButton = new Button("Previous");
        previousButton.setPrefWidth(buttonWidth);
        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(buttonWidth);
        hbox.getChildren().addAll(previousButton, nextButton);
        hbox.setAlignment(Pos.CENTER);
        
        border.setCenter(imageView);
        border.setBottom(hbox);
        setMargin(hbox, new Insets(0, 10, 20, 10));
        
        return border;
    }
    
    private GridPane createAboutUsPane(){
        GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
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
        TableColumn urlCol = new TableColumn("Url");
        TableColumn titleCol = new TableColumn("Title");
        TableColumn lengthCol = new TableColumn("Length");
        TableColumn deviceCol = new TableColumn("Device");
        TableColumn timeCol = new TableColumn("Time");
        TableColumn deleteCol = new TableColumn("Delete");
        table.getColumns().addAll(urlCol, titleCol, lengthCol, deviceCol, timeCol, deleteCol);
        table.setEditable(false);
        
        ObservableList<BrowsedPage> browsedPages = FXCollections.observableArrayList();
        //ArrayList<browsedPage> browsedList = getBrowsedList(); //from database
        table.setItems(browsedPages);
        
        Callback<TableColumn<BrowsedPage, Boolean>, TableCell<BrowsedPage, Boolean>> deleteCellFactory = 
                new Callback<TableColumn<BrowsedPage, Boolean>, TableCell<BrowsedPage, Boolean>>(){
            @Override
            public TableCell call(TableColumn<BrowsedPage, Boolean> param) {
                final TableCell<BrowsedPage, Boolean> cell =  new TableCell<BrowsedPage, Boolean>(){
                    Button deleteButton = new Button("X");
                    
                    @Override
                    public void updateItem(Boolean item, boolean isEmpty){
                        super.updateItem(item, isEmpty);
                        if (isEmpty){
                            setGraphic(null);
                            setText(null);
                        }else{
                            setGraphic(deleteButton);
                            setText(null);
                            deleteButton.setOnAction(event -> {
                                BrowsedPage page = getTableView().getItems().get(getIndex());
                                browsedPages.remove(page);
                                //signal database to delete 
                            });
                        }
                    }
                };
                return cell;        
            }
        };
        
        deleteCol.setCellFactory(deleteCellFactory);
        border.setCenter(table);
        
        return border;
    }
    
    private GridPane createSystemPane(){
        GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        Label labelRunOnStartUp = new Label("Run on start up");
        Label labelHideInToolbar = new Label("Hide in toolbar when close");
        CheckBox checkboxRunOnStartUp = new CheckBox();
        checkboxRunOnStartUp.setId("checkbox1");
        checkboxRunOnStartUp.setSelected(preferences.isRunOnStartUp());
        checkboxRunOnStartUp.setOnAction(event -> {preferences.setIsRunOnStartUp(!preferences.isRunOnStartUp());});
        CheckBox checkboxHideInToolbar = new CheckBox();
        checkboxHideInToolbar.setId("checkbox2");
        checkboxHideInToolbar.setSelected(preferences.isHideInToolbar());
        checkboxHideInToolbar.setOnAction(event -> {preferences.setIsHideInToolbar(!preferences.isHideInToolbar());});
        
        grid.add(labelRunOnStartUp, 0, 0);
        grid.add(checkboxRunOnStartUp, 1, 0);
        grid.add(labelHideInToolbar, 0, 1);
        grid.add(checkboxHideInToolbar, 1, 1);
        
        return grid;
    }
    
    private GridPane createHotkeysPane(){
        GridPane grid = new GridPane();
        grid.setHgap(hGap-20);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        Label[] labelHotkeys = new Label[numHotkeys];
        labelHotkeys[0] = new Label("Extend Words: ");
        labelHotkeys[1] = new Label("Reduce Words: ");
        labelHotkeys[2] = new Label("Extend Sentences: ");
        labelHotkeys[3] = new Label("Reduce Sentences: ");
        labelHotkeys[4] = new Label("Extend Paragraphs: ");
        labelHotkeys[5] = new Label("Reduce Paragraphs: ");     
        
        ChoiceBox[] choiceboxHotkeys = new ChoiceBox[2*numHotkeys];
        Label[] plus = new Label[2*numHotkeys];
        for (int i=0; i < choiceboxHotkeys.length; i++){
            choiceboxHotkeys[i] = new ChoiceBox();
            choiceboxHotkeys[i].setItems(FXCollections.observableArrayList("Ctrl", "Alt", "Shift"));
            plus[i] = new Label("+");
        }

        TextField[] textFields = new TextField[numHotkeys];
        for (int i = 0; i < textFields.length; i++) {
            textFields[i] = new TextField();
            textFields[i].setMaxWidth(textFieldWidth);
            textFields[i].setAlignment(Pos.CENTER);
        }
        
        for (int i=0; i < labelHotkeys.length; i++){
            String[] hotkey;
            switch(i){
                case 0: hotkey = preferences.getExtendWordHotkey(); break;
                case 1: hotkey = preferences.getReduceWordHotkey(); break;
                case 2: hotkey = preferences.getExtendSentenceHotkey(); break;
                case 3: hotkey = preferences.getReduceSentenceHotkey(); break;
                case 4: hotkey = preferences.getExtendParagraphHotkey(); break;
                case 5: hotkey = preferences.getReduceParagraphHotkey(); break; 
                default: hotkey = new String[3];    
            }
            choiceboxHotkeys[2*i].setValue(hotkey[0]);
            choiceboxHotkeys[2*i+1].setValue(hotkey[1]);
            textFields[i].setText(hotkey[2]);
        }

        choiceboxHotkeys[0].setOnAction(event -> {
            String[] hotkey = preferences.getExtendWordHotkey();
            hotkey[0] = (String) choiceboxHotkeys[0].getSelectionModel().getSelectedItem();
            preferences.setExtendWordHotkey(hotkey);
        });
        choiceboxHotkeys[1].setOnAction(event -> {
            String[] hotkey = preferences.getExtendWordHotkey();
            hotkey[1] = (String) choiceboxHotkeys[1].getSelectionModel().getSelectedItem();
            preferences.setExtendWordHotkey(hotkey);
        });
        choiceboxHotkeys[2].setOnAction(event -> {
            String[] hotkey = preferences.getReduceWordHotkey();
            hotkey[0] = (String) choiceboxHotkeys[2].getSelectionModel().getSelectedItem();
            preferences.setReduceWordHotkey(hotkey);
        });
        choiceboxHotkeys[3].setOnAction(event -> {
            String[] hotkey = preferences.getReduceWordHotkey();
            hotkey[1] = (String) choiceboxHotkeys[3].getSelectionModel().getSelectedItem();
            preferences.setReduceWordHotkey(hotkey);
        });
        
        choiceboxHotkeys[4].setOnAction(event -> {
            String[] hotkey = preferences.getExtendSentenceHotkey();
            hotkey[0] = (String) choiceboxHotkeys[4].getSelectionModel().getSelectedItem();
            preferences.setExtendSentenceHotkey(hotkey);
        });
        choiceboxHotkeys[5].setOnAction(event -> {
            String[] hotkey = preferences.getExtendSentenceHotkey();
            hotkey[1] = (String) choiceboxHotkeys[5].getSelectionModel().getSelectedItem();
            preferences.setExtendSentenceHotkey(hotkey);
        });
        choiceboxHotkeys[6].setOnAction(event -> {
            String[] hotkey = preferences.getReduceSentenceHotkey();
            hotkey[0] = (String) choiceboxHotkeys[6].getSelectionModel().getSelectedItem();
            preferences.setReduceSentenceHotkey(hotkey);
        });
        choiceboxHotkeys[7].setOnAction(event -> {
            String[] hotkey = preferences.getReduceSentenceHotkey();
            hotkey[1] = (String) choiceboxHotkeys[7].getSelectionModel().getSelectedItem();
            preferences.setReduceSentenceHotkey(hotkey);
        });
        
        choiceboxHotkeys[8].setOnAction(event -> {
            String[] hotkey = preferences.getExtendParagraphHotkey();
            hotkey[0] = (String) choiceboxHotkeys[8].getSelectionModel().getSelectedItem();
            preferences.setExtendParagraphHotkey(hotkey);
        });
        choiceboxHotkeys[9].setOnAction(event -> {
            String[] hotkey = preferences.getExtendParagraphHotkey();
            hotkey[1] = (String) choiceboxHotkeys[9].getSelectionModel().getSelectedItem();
            preferences.setExtendParagraphHotkey(hotkey);
        });
        choiceboxHotkeys[10].setOnAction(event -> {
            String[] hotkey = preferences.getReduceParagraphHotkey();
            hotkey[0] = (String) choiceboxHotkeys[10].getSelectionModel().getSelectedItem();
            preferences.setReduceParagraphHotkey(hotkey);
        });
        choiceboxHotkeys[11].setOnAction(event -> {
            String[] hotkey = preferences.getReduceParagraphHotkey();
            hotkey[1] = (String) choiceboxHotkeys[11].getSelectionModel().getSelectedItem();
            preferences.setReduceParagraphHotkey(hotkey);
        });
        
        textFields[0].setOnKeyPressed(key ->{
            String[] hotkey = preferences.getExtendWordHotkey();
            hotkey[2] = key.getText().toUpperCase();
            preferences.setExtendWordHotkey(hotkey);
        });
        textFields[1].setOnKeyPressed(key ->{
            String[] hotkey = preferences.getReduceWordHotkey();
            hotkey[2] = key.getText().toUpperCase();
            preferences.setReduceWordHotkey(hotkey);
        });
        textFields[2].setOnKeyPressed(key ->{
            String[] hotkey = preferences.getExtendSentenceHotkey();
            hotkey[2] = key.getText().toUpperCase();
            preferences.setExtendSentenceHotkey(hotkey);
        });
        textFields[3].setOnKeyPressed(key ->{
            String[] hotkey = preferences.getReduceSentenceHotkey();
            hotkey[2] = key.getText().toUpperCase();
            preferences.setReduceSentenceHotkey(hotkey);
        });
        textFields[4].setOnKeyPressed(key ->{
            String[] hotkey = preferences.getExtendParagraphHotkey();
            hotkey[2] = key.getText().toUpperCase();
            preferences.setExtendParagraphHotkey(hotkey);
        });
        textFields[5].setOnKeyPressed(key ->{
            String[] hotkey = preferences.getReduceParagraphHotkey();
            hotkey[2] = key.getText().toUpperCase();
            preferences.setReduceParagraphHotkey(hotkey);
        });
       
        for (int i=0; i < labelHotkeys.length; i++){
            grid.add(labelHotkeys[i], 0, i);
            grid.add(choiceboxHotkeys[2*i], 1, i);
            grid.add(plus[2*i], 2, i);
            grid.add(choiceboxHotkeys[2*i+1], 3, i);
            grid.add(plus[2*i+1], 4, i);
            grid.add(textFields[i], 5, i);
        }

        return grid;
    }
    
    private String[] getHotkey(int index){
        switch(index){
            case 0: ; return preferences.getExtendWordHotkey();
            case 1: ; return preferences.getReduceWordHotkey();
            case 2: ; return preferences.getExtendSentenceHotkey();
            case 3: ; return preferences.getReduceSentenceHotkey();
            case 4: ; return preferences.getExtendParagraphHotkey();
            case 5: ; return preferences.getReduceParagraphHotkey(); 
        }
        return null;
    }
    
    private void setHotkey(int index, String[] hotkey){
        switch(index){
            case 0: ; preferences.setExtendWordHotkey(hotkey);
            case 1: ; preferences.setReduceWordHotkey(hotkey);
            case 2: ; preferences.setExtendSentenceHotkey(hotkey);
            case 3: ; preferences.setReduceSentenceHotkey(hotkey);
            case 4: ; preferences.setExtendParagraphHotkey(hotkey);
            case 5: ; preferences.setReduceParagraphHotkey(hotkey); 
        }
    }
    
    private GridPane createTextEditorPane(){
        GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        Label[] labels = new Label[5];
        labels[0] = new Label("Number of matching text displayed");
        labels[1] = new Label("Show source of text");
        labels[2] = new Label("Box Colour");
        labels[3] = new Label("Text Colour");
        labels[4] = new Label("Box Transparency");
        
        Spinner spinner = new Spinner();
        SpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(minMatchingDisplayed, maxMatchingDisplayed);
        spinner.setValueFactory(svf);
        spinner.setEditable(true);
        spinner.setPrefWidth(spinnerWidth);
        spinner.increment(preferences.getNumMatchingTextDisplay()-1);
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {preferences.setNumMatchingTextDisplay((int) newValue);});

        CheckBox cbShowTextSource = new CheckBox();
        cbShowTextSource.setSelected(preferences.isShowTextSource());
        cbShowTextSource.setOnAction(event -> {preferences.setIsShowTextSource(!preferences.isShowTextSource());});
        
        ColorPicker boxColorPicker = new ColorPicker();
        boxColorPicker.setValue(preferences.getBoxColour());
        boxColorPicker.setStyle("-fx-color-label-visible: false;");
        boxColorPicker.setOnAction(event -> {preferences.setBoxColour(boxColorPicker.getValue());});
        
        ColorPicker textColorPicker = new ColorPicker();
        textColorPicker.setValue(preferences.getTextColour());
        textColorPicker.setStyle("-fx-color-label-visible: false;");
        textColorPicker.setOnAction(event -> {preferences.setTextColour(textColorPicker.getValue());});
        
        Slider transparency = new Slider();
        transparency.setMin(0);
        transparency.setMax(100);
        transparency.setShowTickLabels(true);
        transparency.setShowTickMarks(true);
        transparency.setMajorTickUnit(majorTickUnit);
        transparency.setValue(preferences.getBoxTransparency());
        transparency.valueProperty().addListener((obs, oldValue, newValue) -> {preferences.setBoxTransparency((double) newValue);});
      
        for (int i=0; i < labels.length; i++){
            grid.add(labels[i], 0, i);
        }
        grid.add(spinner, 1, 0);
        grid.add(cbShowTextSource, 1, 1);
        grid.add(boxColorPicker, 1, 2);
        grid.add(textColorPicker, 1, 3);
        grid.add(transparency, 1, 4);
        
        return grid;
    }
    
    private BorderPane createBlockedListPane(){
        BorderPane border = new BorderPane();
        TableView table = new TableView();      
        TableColumn urlCol = new TableColumn("Website URL");
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlCol.setMinWidth(200);
        TableColumn toggleCol = new TableColumn("Enable/Disable");
        toggleCol.setCellValueFactory(new PropertyValueFactory<>("isEnabled"));
        toggleCol.setMinWidth(100);
        TableColumn deleteCol = new TableColumn("Delete");
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("X"));
        deleteCol.setMinWidth(50);
        table.getColumns().addAll(urlCol, toggleCol, deleteCol);
        table.setEditable(false);
        
        ObservableList<BlockedPage> blockedPages = FXCollections.observableArrayList();
        ArrayList<BlockedPage> blockedList = preferences.getBlockedList();
        blockedPages.addAll(blockedList);
        table.setItems(blockedPages);
        
        Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> toggleCellFactory = 
                new Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>>(){
            @Override
            public TableCell call(TableColumn<BlockedPage, Boolean> param) {
                final TableCell<BlockedPage, Boolean> cell =  new TableCell<BlockedPage, Boolean>(){
                    ToggleButton toggleButton = new ToggleButton();
                    
                    @Override
                    public void updateItem(Boolean item, boolean isEmpty){
                        super.updateItem(item, isEmpty);
                        if (isEmpty){
                            setGraphic(null);
                            setText(null);
                        }else{
                            toggleButton.setText("Enabled");
                            toggleButton.setSelected(blockedList.get(getIndex()).getIsEnabled());
                            setGraphic(toggleButton);
                            setText(null);
                            toggleButton.setOnAction(event -> {
                                BlockedPage blockedPage = getTableView().getItems().get(getIndex());
                                blockedPage.setIsEnabled(!blockedPage.getIsEnabled());
                                blockedList.set(getIndex(), blockedPage);
                                preferences.setBlockedList(blockedList);
                            });
                        }
                    }
                };
                return cell;        
            }
        };
        
        Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> deleteCellFactory = 
                new Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>>(){
            @Override
            public TableCell call(TableColumn<BlockedPage, Boolean> param) {
                final TableCell<BlockedPage, Boolean> cell =  new TableCell<BlockedPage, Boolean>(){
                    Button deleteButton = new Button("X");
                    
                    @Override
                    public void updateItem(Boolean item, boolean isEmpty){
                        super.updateItem(item, isEmpty);
                        if (isEmpty){
                            setGraphic(null);
                            setText(null);
                        }else{
                            setGraphic(deleteButton);
                            setText(null);
                            deleteButton.setOnAction(event -> {
                                BlockedPage blockedPage = getTableView().getItems().get(getIndex());
                                blockedPages.remove(blockedPage);
                                blockedList.remove(blockedPage);
                                preferences.setBlockedList(blockedList);
                            });
                        }
                    }
                };
                return cell;        
            }
        };
        
        toggleCol.setCellFactory(toggleCellFactory);
        deleteCol.setCellFactory(deleteCellFactory);
        
        TextField urlField = new TextField();
        urlField.setPromptText("add url of websites to block");
        urlField.setMinWidth(400);
        Button addButton = new Button("Add to blocked list");
        addButton.setMinWidth(200);
        addButton.setOnAction(event -> {
            BlockedPage page = new BlockedPage(urlField.getText(), true);
            blockedPages.add(page);
            blockedList.add(page);
            preferences.setBlockedList(blockedList);
            urlField.clear();
        });
        
        HBox hbox = new HBox();
        hbox.getChildren().addAll(urlField, addButton);
        border.setCenter(table);
        border.setBottom(hbox);
        
        return border;
    }
    
    private GridPane createPathPane(){
        GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        Label labelPath =  new Label("Store data at: ");
        TextField pathField = new TextField();
        pathField.setEditable(false);
        pathField.setMinWidth(300);
        pathField.setText(preferences.getDataPath());
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            pathField.setText(selectedDirectory.getAbsolutePath());
            preferences.setDataPath(selectedDirectory.getAbsolutePath());
        });
                
        Label labelShowPreviewText = new Label("Store preview text as caches to improve matching speed");
        CheckBox checkboxShowPreviewText = new CheckBox();
        checkboxShowPreviewText.setSelected(preferences.isShowPreviewText());
        checkboxShowPreviewText.setOnAction(event -> {preferences.setIsShowPreviewText(!preferences.isShowPreviewText());});
        
        Label labelClearCachesTime = new Label("Clear caches after ");
        ChoiceBox cbClearCachesTime = new ChoiceBox();
        cbClearCachesTime.setItems(FXCollections.observableArrayList("device is off", "one day", "one week", "never"));
        cbClearCachesTime.setValue(preferences.getClearCachesTime());
        cbClearCachesTime.setOnAction(event -> {preferences.setClearCachesTime((String) cbClearCachesTime.getSelectionModel().getSelectedItem());});     
        
        grid.add(labelPath, 0, 0);
        grid.add(pathField, 0, 1);
        grid.add(browseButton, 1, 1);
        grid.add(labelShowPreviewText, 0, 2);
        grid.add(checkboxShowPreviewText, 1, 2);
        grid.add(labelClearCachesTime, 0, 3);
        grid.add(cbClearCachesTime, 1, 3);
        
        return grid;
    }
    
    private GridPane createStoragePane(){
        GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        Label labelMaxTextSize = new Label("Store single text size of at most");
        ChoiceBox cbMaxTextSize = new ChoiceBox();
        cbMaxTextSize.setItems(FXCollections.observableArrayList("100MB", "500MB", "1GB", "unlimited"));
        cbMaxTextSize.setValue(preferences.getMaxTextSizeStored());
        cbMaxTextSize.setOnAction(event -> {preferences.setMaxTextSizeStored((String) cbMaxTextSize.getSelectionModel().getSelectedItem());});     
        
        Label labelPreviewTextLength = new Label("Preview text length");
        ChoiceBox cbPreviewTextLength = new ChoiceBox();
        cbPreviewTextLength.setItems(FXCollections.observableArrayList("one sentence", "two sentence", "three words", "one paragraph"));
        cbPreviewTextLength.setValue(preferences.getPreviewTextLength());
        cbPreviewTextLength.setOnAction(event -> {preferences.setPreviewTextLength((String) cbPreviewTextLength.getSelectionModel().getSelectedItem());});     
        
        float totalSpace = 20; //dummy value
        float usedSpace = 2; //dummy value
        float freeSpace = totalSpace - usedSpace;
        Label labelUsedSpace = new Label("Used space: " + usedSpace + "/" + totalSpace);
        Label labelUsedPercentage = new Label("Used percentage: " + (usedSpace/totalSpace)*100 + "%");  
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
               new PieChart.Data("Used Space", usedSpace), new PieChart.Data("FreeSpace", freeSpace));
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setPrefSize(pieChartWidth, pieChartHeight);
       
        grid.add(labelMaxTextSize, 0, 0);
        grid.add(cbMaxTextSize, 1, 0);
        grid.add(labelPreviewTextLength, 0, 1);
        grid.add(cbPreviewTextLength, 1, 1);
        grid.add(pieChart, 0, 2, 1, 4);
        grid.add(labelUsedSpace, 1, 3);
        grid.add(labelUsedPercentage, 1, 4);
        
        return grid;
    }
}
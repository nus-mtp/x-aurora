package xaurora.ui;

import java.io.File;
import static java.lang.System.exit;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import static javafx.scene.layout.BorderPane.setMargin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import xaurora.io.DataFileIO;
import xaurora.system.SystemManager;
import xaurora.ui.VirtualKeyboard.Key;
import xaurora.util.BlockedPage;
import xaurora.util.UserPreference;
import xaurora.util.DataFileMetaData;

/**
 * User interface for users to set their preferences, also include a tutorial
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
    private static final int spinnerWidth = 70;
    private static final int pieChartWidth = 200;
    private static final int pieChartHeight = 200;
    private static final int topOffset = 10;
    private static final int rightOffset = 10;
    private static final int bottomOffset = 10;
    private static final int leftOffset = 10;  
    private static final int hGap = 50;
    private static final int vGap = 15;
    private static final int spacing = 20;
    private static final int numHotkeys = 6;
    private static final int minMatchingDisplayed = 1;
    private static final int maxMatchingDisplayed = 20;
    private static final int majorTickUnit = 50;
    private static final String stageTitle = "x-aurora";
    private static final String styleSheets = "style.css";
    private String username;
    UserPreference preferences = UserPreference.getInstance();
    DataFileIO dataFile = DataFileIO.instanceOf();
    SystemManager systemManager = SystemManager.getInstance();
    Stage stage;
    
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle(stageTitle);      
        Scene preferenceScene = createPreferenceScene();
        stage.setScene(preferenceScene);
        String styleSheetsPath = new File(styleSheets).getAbsolutePath().replace("\\", "/");
        preferenceScene.getStylesheets().add("File:///" + styleSheetsPath);
        stage.show();
    }
    
    /**
     * create the main scene
     * @return Scene
     */
    public Scene createPreferenceScene(){
    	Scene preferenceScene = new Scene(createPreferencePane(), sceneWidth, sceneHeight);
    	return preferenceScene;
    }

    /**
     * create the main Pane
     * @return PreferencePane
     */
    public BorderPane createPreferencePane(){
    	BorderPane border = new BorderPane();

    	TabPane preferenceTabs = createPreferenceTabs();
    	Label labelEmail = createLabelEmail();

    	AnchorPane anchor = new AnchorPane();
    	anchor.getChildren().addAll(preferenceTabs, labelEmail);
    	AnchorPane.setTopAnchor(labelEmail, 0.0);
    	AnchorPane.setRightAnchor(labelEmail, 0.0);

    	border.setCenter(anchor);
    	
    	return border;
    }

    /**
     * create four tabs: Setting, Tutorial, About Us, Data Managing
     * @return PreferenceTabs
     */
    private TabPane createPreferenceTabs(){
    	TabPane preferenceTabs = new TabPane();
    	Tab tabSetting = new Tab("Setting");
    	tabSetting.setId("Setting");
        tabSetting.setContent(createSettingPane());
        Tab tabTutorial = new Tab("Tutorial");
        tabTutorial.setId("Tutorial");
        tabTutorial.setContent(createTutorialPane());
        Tab tabAboutUs = new Tab("About Us");
        tabAboutUs.setId("AboutUs");
        tabAboutUs.setContent(createAboutUsPane());
        Tab tabDataManaging = new Tab("Data Managing");
        tabDataManaging.setId("DataManaging");
        tabDataManaging.setContent(createDataManagingPane());
        
        preferenceTabs.getTabs().addAll(tabSetting, tabTutorial, tabAboutUs, tabDataManaging);
        preferenceTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        preferenceTabs.setTabMinHeight(44);
        preferenceTabs.setPrefSize(tabWidth, tabHeight);
        
        return preferenceTabs;
    }

    /**
     * show current user's email
     * @return LabelEmail
     */
    private Label createLabelEmail(){
    	Image image = new Image("File:dropbox.png"); //dummy value
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);
        Label labelEmail = new Label("user@example.com"); //dummy value
        labelEmail.setGraphic(imageView);
        
        return labelEmail;
    }
    
    /**
     * Pane to set preferences
     * @return SettingPane
     */
    private BorderPane createSettingPane(){
        preferences.readPreferences(username);
        BorderPane border = new BorderPane();
        
        TabPane settingTabs = createSettingTabs();
        HBox okCancelApplyBox = createOkCancelApplyBox();
        
        border.setCenter(settingTabs);
        border.setBottom(okCancelApplyBox);

        return border;
    }
    
    /**
     * create six tabs: System, Hotkeys, Text Editor, Blocked List, Path, Storage
     * @return settingTabs
     */
    private TabPane createSettingTabs(){
    	TabPane settingTabs = new TabPane();
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
        
        settingTabs.getTabs().addAll(tabSystem, tabHotkeys, tabTextEditor, tabBlockedList, tabPath, tabStorage);
        settingTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        return settingTabs;
    }
    
    /**
     * create a HBox with three buttons: OK, Cancel, Apply
     * @return HBox
     */
    private HBox createOkCancelApplyBox(){
    	HBox hbox = new HBox();
    	
        Button okButton = new Button("OK");
        okButton.setId("okButton");
        okButton.setPrefWidth(buttonWidth);
        okButton.setOnAction(event -> {preferences.writePreferences(username); exit(0);});
        Button cancelButton = new Button("Cancel");
        cancelButton.setId("cancelButton");
        cancelButton.setPrefWidth(buttonWidth);
        cancelButton.setOnAction(event -> {exit(1);});
        Button applyButton = new Button("Apply");
        applyButton.setId("applyButton");
        applyButton.setPrefWidth(buttonWidth);
        applyButton.setOnAction(event -> {preferences.writePreferences(username);});
        
        hbox.getChildren().addAll(okButton, cancelButton, applyButton);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        
        return hbox;
    }
    
    /**
     * Pane to view tutorials
     * @return tutorialPane
     */
    private BorderPane createTutorialPane(){
        BorderPane border = new BorderPane();
        
        Image image = new Image("File:dropbox.png");
        ImageView imageView = new ImageView(image);
        HBox prevNextBox = createPrevNextBox();
        
        border.setCenter(imageView);
        border.setBottom(prevNextBox);
        
        return border;
    }
    
    /**
     * create a HBox with two buttons: Next, Previous
     * @return HBox
     */
    private HBox createPrevNextBox(){
    	HBox hbox = new HBox();
    	setMargin(hbox, new Insets(0, 10, 20, 10));
        hbox.setSpacing(spacing);
        
        Button previousButton = new Button("Previous");
        previousButton.setPrefWidth(buttonWidth);
        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(buttonWidth);
        
        hbox.getChildren().addAll(previousButton, nextButton);
        hbox.setAlignment(Pos.CENTER);
        
        return hbox;
    }
    
    /**
     * Pane for developers and software information
     * @return AboutUsPane
     */
    private GridPane createAboutUsPane(){
        GridPane grid = createGridPane();
        
        Label label = new Label("The main objective behind developing this application is to make it easier to copy and paste across documents and information on the webpages. The program aims at minimizing the act of window switching, visually searching for the sentence one needs to select/highlight and copy the text. Thereby reducing the time spent, increasing the efficiency of the user and making it a useful tool while working heavily on text-processing, such as drafting research papers or a thesis. This auto-complete mechanism will not entirely replace the copy-paste procedure; rather it would enhance the original functionality.");
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        grid.getChildren().add(label);
        
        return grid;
    }
    
    /**
     * Pane for data managing
     * @return DataManagingPane
     */
    private BorderPane createDataManagingPane(){
        BorderPane border = new BorderPane();
        TableView<DataFileMetaData> table = createDataTable();
        border.setCenter(table);

        return border;
    }
    
    /**
     * create Table structure for DataManagingPane
     * @return DataTable
     */
    private TableView<DataFileMetaData> createDataTable(){
    	TableView<DataFileMetaData> table = new TableView<DataFileMetaData>();
        TableColumn<DataFileMetaData, String> urlCol = new TableColumn<DataFileMetaData, String>("Url");
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        TableColumn<DataFileMetaData, String> sourceCol = new TableColumn<DataFileMetaData, String>("Source");
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("source"));
        TableColumn<DataFileMetaData, String> lengthCol = new TableColumn<DataFileMetaData, String>("Length");
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        TableColumn<DataFileMetaData, String> lastModifiedCol = new TableColumn<DataFileMetaData, String>("Last Modified");
        lastModifiedCol.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDateTime"));
        lastModifiedCol.setPrefWidth(100);
        TableColumn<DataFileMetaData, Boolean> deleteCol = new TableColumn<DataFileMetaData, Boolean>("Delete");
        
        ObservableList<DataFileMetaData> browsedPages = FXCollections.observableArrayList();
        ArrayList<DataFileMetaData> fileData = dataFile.getAllMetaData(systemManager);
        browsedPages.addAll(fileData);
        table.setItems(browsedPages);
        
        Callback<TableColumn<DataFileMetaData, Boolean>, TableCell<DataFileMetaData, Boolean>> 
        deleteCellFactory = createDeleteCellFactory(browsedPages);
        deleteCol.setCellFactory(deleteCellFactory);
        
        table.getColumns().add(urlCol);
        table.getColumns().add(sourceCol);
        table.getColumns().add(lengthCol);
        table.getColumns().add(lastModifiedCol);
        table.getColumns().add(deleteCol);
        table.setEditable(false);
        
        return table;
    }

    /**
     * create cell factory for delete cell in DataManaging Table
     * @param browsedPages
     * @return deleteCellFactory
     */
    private Callback<TableColumn<DataFileMetaData, Boolean>, TableCell<DataFileMetaData, Boolean>> 
    createDeleteCellFactory(ObservableList<DataFileMetaData> browsedPages){
    	Callback<TableColumn<DataFileMetaData, Boolean>, TableCell<DataFileMetaData, Boolean>> deleteCellFactory = 
    			new Callback<TableColumn<DataFileMetaData, Boolean>, TableCell<DataFileMetaData, Boolean>>(){
    		@Override
    		public TableCell<DataFileMetaData, Boolean> call(TableColumn<DataFileMetaData, Boolean> param) {
    			final TableCell<DataFileMetaData, Boolean> cell =  new TableCell<DataFileMetaData, Boolean>(){
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
    							DataFileMetaData data = getTableView().getItems().get(getIndex());
    							browsedPages.remove(getIndex());
    							dataFile.removeDataFile(data.getFilename());
    						});
    					}
    				}
    			};
    			cell.setAlignment(Pos.CENTER);
    			return cell;        
    		}
    	};

    	return deleteCellFactory;
    }

    /**
     * Pane for system preferences
     * @return SystemPane
     */
    private GridPane createSystemPane(){
        GridPane grid = createGridPane();
        
        Label labelRunOnStartUp = new Label("Run on start up");
        Label labelHideInToolbar = new Label("Hide in toolbar when close");
        CheckBox checkboxRunOnStartUp = new CheckBox();
        checkboxRunOnStartUp.setId("checkboxRunOnStartUp");
        checkboxRunOnStartUp.setSelected(preferences.isRunOnStartUp());
        checkboxRunOnStartUp.setOnAction(event -> {preferences.setIsRunOnStartUp(!preferences.isRunOnStartUp());});
        CheckBox checkboxHideInToolbar = new CheckBox();
        checkboxHideInToolbar.setId("checkboxHideInToolbar");
        checkboxHideInToolbar.setSelected(preferences.isHideInToolbar());
        checkboxHideInToolbar.setOnAction(event -> {preferences.setIsHideInToolbar(!preferences.isHideInToolbar());});
        
        grid.add(labelRunOnStartUp, 0, 0);
        grid.add(checkboxRunOnStartUp, 1, 0);
        grid.add(labelHideInToolbar, 0, 1);
        grid.add(checkboxHideInToolbar, 1, 1);

        return grid;
    }

    /**
     * Pane for hotkeys preferences
     * @return HotkeysPane
     */
    private BorderPane createHotkeysPane(){
    	BorderPane border = new BorderPane();
    	border.setPadding(new Insets(10));
    	VirtualKeyboard[] virtualKeyboards = new VirtualKeyboard[numHotkeys];      
    	Node[] keyboardNodes = new Node[numHotkeys];
    	HBox[] keyboardBoxes = new HBox[numHotkeys];

    	for (int i=0; i < numHotkeys; i++){
    		virtualKeyboards[i] = new VirtualKeyboard(i);
    		keyboardNodes[i] = virtualKeyboards[i].createNode();
    		setKeyboardHotkey(virtualKeyboards[i], i);
    		keyboardBoxes[i] = new HBox(6);
    		keyboardBoxes[i].getChildren().add(new Group(keyboardNodes[i]));
            keyboardBoxes[i].setAlignment(Pos.CENTER);
        }
        
        ChoiceBox<String> cbHotkeys = new ChoiceBox<String>();
        cbHotkeys.setItems(FXCollections.observableArrayList("extend word", "reduce word", "extend sentence",
                "reduce sentence", "extend paragraph", "reduce paragraph"));
        cbHotkeys.setValue("extend word");
        border.setCenter(keyboardBoxes[0]);
        cbHotkeys.setOnAction(event -> {
            int index = cbHotkeys.getSelectionModel().getSelectedIndex();
            border.setCenter(keyboardBoxes[index]);
            keyboardNodes[index].requestFocus();
        });
        border.setOnMouseClicked(event -> {
            int index = cbHotkeys.getSelectionModel().getSelectedIndex();
            border.setCenter(keyboardBoxes[index]);
            keyboardNodes[index].requestFocus();
        });
        
        HBox hbox = new HBox();
        hbox.getChildren().add(cbHotkeys);
        hbox.setAlignment(Pos.CENTER);
        border.setBottom(hbox);
        
        return border;
    }
    
    /**
     * set hotkeys
     * @param keyboard
     * @param index
     */
    private void setKeyboardHotkey(VirtualKeyboard keyboard, int index){
        KeyCode[] codes = preferences.getHotkeyCodes(index);

        for (int i = 0; i < codes.length; i++){
            Key key = keyboard.getKey(codes[i]);
            key.setPressed(true);
        }
    }
    
    /**
     * Pane for Text Editor preferences
     * @return TextEditorPane
     */
    private GridPane createTextEditorPane(){
        GridPane grid = createGridPane();
        
        Label[] labels = new Label[5];
        labels[0] = new Label("Number of matching text displayed");
        labels[1] = new Label("Show source of text");
        labels[2] = new Label("Box Colour");
        labels[3] = new Label("Text Colour");
        labels[4] = new Label("Box Transparency");
        
        Spinner<Integer> spinner = new Spinner<Integer>();
        spinner.setId("spinner");
        SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(minMatchingDisplayed, maxMatchingDisplayed);
        spinner.setValueFactory(svf);
        spinner.setEditable(true);
        spinner.setPrefWidth(spinnerWidth);
        spinner.increment(preferences.getNumMatchingTextDisplay()-1);
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {preferences.setNumMatchingTextDisplay((int) newValue);});

        CheckBox cbShowTextSource = new CheckBox();
        cbShowTextSource.setId("cbShowTextSource");
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
        transparency.setId("transparency");
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
    
    /**
     * Pane for adding web pages to Blocked List
     * @return BlockedListPane
     */
    private BorderPane createBlockedListPane(){
        BorderPane border = new BorderPane();
        
        ObservableList<BlockedPage> blockedPages = FXCollections.observableArrayList();
        ArrayList<BlockedPage> blockedList = preferences.getBlockedList();
        blockedPages.addAll(blockedList);
        
        TableView<BlockedPage> table = createBlockedPageTable(blockedPages, blockedList);
        HBox addPageBox = createAddPageBox(blockedPages, blockedList);
        
        border.setCenter(table);
        border.setBottom(addPageBox);
        
        return border;
    }
    
    /**
     * Table structure for BlockedList Pane
     * @param blockedPages
     * @param blockedList
     * @return BlockedPageTable
     */
    private TableView<BlockedPage> createBlockedPageTable(ObservableList<BlockedPage> blockedPages, ArrayList<BlockedPage> blockedList){
    	TableView<BlockedPage> table = new TableView<BlockedPage>();      
        TableColumn<BlockedPage, String> urlCol = new TableColumn<BlockedPage, String>("Website URL");
        urlCol.setId("urlCol");
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlCol.setMinWidth(300);
        TableColumn<BlockedPage, Boolean> toggleCol = new TableColumn<BlockedPage, Boolean>("Enable/Disable");
        toggleCol.setCellValueFactory(new PropertyValueFactory<>("isEnabled"));
        toggleCol.setMinWidth(100);
        TableColumn<BlockedPage, Boolean> deleteCol = new TableColumn<BlockedPage, Boolean>("Delete");
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("X"));
        deleteCol.setMinWidth(50);
        table.getColumns().add(urlCol);
        table.getColumns().add(toggleCol);
        table.getColumns().add(deleteCol);
        table.setEditable(false);
        table.setItems(blockedPages);
        
        Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> 
        toggleCellFactory = createToggleCellFactory(blockedList);
        toggleCol.setCellFactory(toggleCellFactory);
        Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> 
        deleteCellFactory = createDeleteCellFactory(blockedPages, blockedList);    
        deleteCol.setCellFactory(deleteCellFactory);
        
        return table;
    }
    
    /**
     * create cell factory for toggle cell in BlockedList Table
     * @param blockedList
     * @return toggleCellFactory
     */
    private Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> createToggleCellFactory(ArrayList<BlockedPage> blockedList){
    	Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> toggleCellFactory = 
                new Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>>(){
            @Override
            public TableCell<BlockedPage, Boolean> call(TableColumn<BlockedPage, Boolean> param) {
                final TableCell<BlockedPage, Boolean> cell =  new TableCell<BlockedPage, Boolean>(){
                    ToggleButton toggleButton = new ToggleButton();
                    
                    @Override
                    public void updateItem(Boolean item, boolean isEmpty){
                        super.updateItem(item, isEmpty);
                        if (isEmpty){
                            setGraphic(null);
                            setText(null);
                        }else{
                        	toggleButton.setId("toggleButton");
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
        
        return toggleCellFactory;
    }
    
    /**
     * create cell factory for delete cell in BlockedList Table
     * @param blockedPages
     * @param blockedList
     * @return deleteCellFactory
     */
    private Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> createDeleteCellFactory(ObservableList<BlockedPage> blockedPages, ArrayList<BlockedPage> blockedList){
    	Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>> deleteCellFactory = 
                new Callback<TableColumn<BlockedPage, Boolean>, TableCell<BlockedPage, Boolean>>(){
            @Override
            public TableCell<BlockedPage, Boolean> call(TableColumn<BlockedPage, Boolean> param) {
                final TableCell<BlockedPage, Boolean> cell =  new TableCell<BlockedPage, Boolean>(){
                    Button deleteButton = new Button("X");
                    
                    @Override
                    public void updateItem(Boolean item, boolean isEmpty){
                        super.updateItem(item, isEmpty);
                        if (isEmpty){
                            setGraphic(null);
                            setText(null);
                        }else{
                        	deleteButton.setId("deleteButton");
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
        
        return deleteCellFactory;
    }
    
    /**
     * create a HBox with a text field and an add button
     * @param blockedPages
     * @param blockedList
     * @return HBox
     */
    private HBox createAddPageBox(ObservableList<BlockedPage> blockedPages, ArrayList<BlockedPage> blockedList){
    	TextField urlField = new TextField();
    	urlField.setId("urlField");
        urlField.setPromptText("add url of websites to block");
        urlField.setMinWidth(400);
        Button addButton = new Button("Add to blocked list");
        addButton.setId("addButton");
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
        
        return hbox;
    }
    
    /**
     * Pane for Path preferences
     * @return PathPane
     */
    private GridPane createPathPane(){
        GridPane grid = createGridPane();
        
        Label labelContentPath =  new Label("Store content at: ");
        TextField contentPathField = new TextField();
        contentPathField.setId("contentPathField");
        contentPathField.setEditable(false);
        contentPathField.setMinWidth(300);
        contentPathField.setText(preferences.getContentPath());
        Button browseContentButton = new Button("Browse");
        browseContentButton.setId("browseContentButton");
        browseContentButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            contentPathField.setText(selectedDirectory.getAbsolutePath());
            preferences.setContentPath(selectedDirectory.getAbsolutePath());
        });
                
        Label labelShowPreviewText = new Label("Store preview text as caches to improve matching speed");
        CheckBox checkboxShowPreviewText = new CheckBox();
        checkboxShowPreviewText.setId("checkboxShowPreviewText");
        checkboxShowPreviewText.setSelected(preferences.isShowPreviewText());
        checkboxShowPreviewText.setOnAction(event -> {preferences.setIsShowPreviewText(!preferences.isShowPreviewText());});
        
        Label labelClearCachesTime = new Label("Clear caches after ");
        ChoiceBox<String> cbClearCachesTime = new ChoiceBox<String>();
        cbClearCachesTime.setId("cbClearCachesTime");
        cbClearCachesTime.setItems(FXCollections.observableArrayList("device is off", "one day", "one week", "never"));
        cbClearCachesTime.setValue(preferences.getClearCachesTime());
        cbClearCachesTime.setOnAction(event -> {preferences.setClearCachesTime(cbClearCachesTime.getSelectionModel().getSelectedItem());});     
        
        grid.add(labelContentPath, 0, 0);
        grid.add(contentPathField, 0, 1);
        grid.add(browseContentButton, 1, 1);
        grid.add(labelShowPreviewText, 0, 2);
        grid.add(checkboxShowPreviewText, 1, 2);
        grid.add(labelClearCachesTime, 0, 3);
        grid.add(cbClearCachesTime, 1, 3);
        
        return grid;
    }
    
    /**
     * Pane for Storage preferences
     * @return StoragePane
     */
    private GridPane createStoragePane(){
        GridPane grid = createGridPane();
        
        Label labelMaxTextSize = new Label("Store single text size of at most");
        ChoiceBox<String> cbMaxTextSize = new ChoiceBox<String>();
        cbMaxTextSize.setItems(FXCollections.observableArrayList("100MB", "500MB", "1GB", "unlimited"));
        cbMaxTextSize.setValue(preferences.getMaxTextSizeStored());
        cbMaxTextSize.setOnAction(event -> {preferences.setMaxTextSizeStored(cbMaxTextSize.getSelectionModel().getSelectedItem());});     
        
        Label labelPreviewTextLength = new Label("Preview text length");
        ChoiceBox<String> cbPreviewTextLength = new ChoiceBox<String>();
        cbPreviewTextLength.setItems(FXCollections.observableArrayList("one sentence", "two sentences", "three words", "one paragraph"));
        cbPreviewTextLength.setValue(preferences.getPreviewTextLength());
        cbPreviewTextLength.setOnAction(event -> {preferences.setPreviewTextLength(cbPreviewTextLength.getSelectionModel().getSelectedItem());});     
        
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
    
    /**
     * create a standardize GridPane
     * @return GridPane
     */
    private GridPane createGridPane(){
    	GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        return grid;
    }
    
    public void setUsername(String username){
    	this.username = username;
    }
}
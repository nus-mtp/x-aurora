package xaurora.ui;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import xaurora.dropboxV2.User;
import xaurora.system.SystemManager;
import xaurora.util.UserPreference;

/**
 * User interface for login to dropbox
 * @author Lee
 */
public class LoginUI extends Application{
    
    private Stage stage;
    private static final String stageTitle = "x-aurora";
    private static final String title = "x-aurora: simplify copy and paste";
    private static final String styleSheets = "style.css";
    private static final String dropboxIconPath = "File:dropbox.png";
    private static final int sceneWidth = 500;
    private static final int sceneHeight = 300;
    private static final int imageWidth = 140;
    private static final int imageHeight = 140;
    private static final int topOffset = 15;
    private static final int rightOffset = 10;
    private static final int bottomOffset = 15;
    private static final int leftOffset = 10;  
    private static final int hGap = 50;
    private static final int vGap = 10;
    private static final int spacing = 20;
    private static final String loginPage = ""; //dummy 
    private static final String skipWarning = "Cross device copy paste will not be available without loggin in";
    private static final String defaultUser = "default";
    PreferenceUI preferenceUI = new PreferenceUI();
    SystemManager systemManager = SystemManager.getInstance(); 
   
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle(stageTitle);      
        Scene loginScene = createLoginScene();
        stage.setScene(loginScene);
        String styleSheetsPath = new File(styleSheets).getAbsolutePath().replace("\\", "/");
        loginScene.getStylesheets().add("File:///" + styleSheetsPath);
        stage.show();
    }
    
    public Scene createLoginScene(){
        BorderPane borderPane = new BorderPane();
        HBox title = createTitle();
        GridPane grid = createLoginForm(borderPane);
        HBox hbox = createSkipLoginBar();
        
        borderPane.setTop(title);
        borderPane.setCenter(grid);
        borderPane.setBottom(hbox);
        
        Scene scene = new Scene(borderPane, sceneWidth, sceneHeight);
        return scene;
    }
    
    private HBox createTitle(){
        HBox hbox = createHBox();
        Label labelTitle = new Label();
        labelTitle.setText(title);
        hbox.getChildren().add(labelTitle);
        
        return hbox;
    }
    
    private GridPane createLoginForm(BorderPane borderPane){
        GridPane loginGrid = createGridPane();
        GridPane loadingGrid = createLoadingIndicator();

        ImageView dropboxIconView = createDropboxIconView();
        
        Button loginButton = new Button("Login to Dropbox");
        loginButton.setOnAction(event -> {
        	//direct to login website
            getHostServices().showDocument(loginPage);
            //show loading animation
            borderPane.setCenter(loadingGrid);
        	//successful login, direct to preferencesUI with dropbox user settings
        	buildDropboxUser();
        	stage.setScene(preferenceUI.createPreferenceScene());
        });
        
        loginGrid.add(dropboxIconView, 0, 0);
        loginGrid.add(loginButton, 1, 0);
        
        return loginGrid;
    }
    
    private HBox createSkipLoginBar(){
        HBox hbox = createHBox();
        Label labelWarning = new Label();
        labelWarning.setText(skipWarning);
        labelWarning.setWrapText(true);
        Button skipButton = new Button("Skip login");
        skipButton.setOnAction(event -> {
        	//skip login, direct to preferencesUI with default user settings
        	buildDefaultUser();
            stage.setScene(preferenceUI.createPreferenceScene());
        });
        hbox.getChildren().addAll(labelWarning, skipButton);
        
        return hbox;
    }
    
    private HBox createHBox(){
    	HBox hbox = new HBox();
        hbox.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        hbox.setSpacing(spacing);
        hbox.setAlignment(Pos.CENTER);
        
        return hbox;
    }
    
    private GridPane createGridPane(){
    	GridPane grid = new GridPane();
        grid.setHgap(hGap);
        grid.setVgap(vGap);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);
        
        return grid;
    }
    
    private ImageView createDropboxIconView(){
    	Image dropboxIcon = new Image(dropboxIconPath);
        ImageView dropboxIconView = new ImageView(dropboxIcon);
        dropboxIconView.setFitWidth(imageWidth);
        dropboxIconView.setFitHeight(imageHeight);
        
        return dropboxIconView;
    }
    
    private GridPane createLoadingIndicator(){
    	GridPane grid = createGridPane();
    	ProgressIndicator loadingIndicator = new ProgressIndicator();
    	Label labelLoading = new Label("Waiting for login");
    	grid.add(loadingIndicator, 0, 0);
    	grid.add(labelLoading, 0, 1);
    	
    	return grid;
    }
    
    private void buildDropboxUser(){
    	User user = new User();
    	UserPreference preferences = UserPreference.getInstance();
    	preferences.readPreferences(user.username);
    	preferenceUI.setUsername(user.username);
    	systemManager.changeUser(user.username, user.email, preferences.getContentPath(), preferences.getNumMatchingTextDisplay(), preferences.getClearCachesTimeInHours());
    }
    
    private void buildDefaultUser(){
    	UserPreference preferences = UserPreference.getInstance();
    	preferences.readPreferences(defaultUser);
    	preferenceUI.setUsername(defaultUser);
    	systemManager.reset();
    }
}

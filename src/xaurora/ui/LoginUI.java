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
import xaurora.system.SystemManager;

public class LoginUI extends Application{
    
    PreferenceUI preferenceUI = new PreferenceUI();
    private Stage stage;
    private static final String stageTitle = "x-aurora";
    private static final String name = "x-aurora: simplify copy and paste";
    private static final String styleSheets = "style.css";
    private static final String imagePath = "File:dropbox.png";
    private static final int sceneWidth = 500;
    private static final int sceneHeight = 300;
    private static final int topOffset = 15;
    private static final int rightOffset = 10;
    private static final int bottomOffset = 15;
    private static final int leftOffset = 10;  
    private static final String loginPage = "https://www.dropbox.com/1/oauth2/authorize?response_type=token&client_id=4tpptik431fwlqo&redirect_uri=https://www.dropbox.com/home";
    
   
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
        GridPane grid = createLoginForm();
        HBox hbox = createSkipLoginBar();
        
        borderPane.setTop(title);
        borderPane.setCenter(grid);
        borderPane.setBottom(hbox);
        
        Scene scene = new Scene(borderPane, sceneWidth, sceneHeight);
        return scene;
    }
    
    private HBox createTitle(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        hbox.setSpacing(20);
        
        Label title = new Label();
        title.setText(name);
        hbox.getChildren().add(title);
        hbox.setAlignment(Pos.CENTER);
        
        return hbox;
    }
    
    private GridPane createLoginForm(){
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(10);
        grid.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        grid.setAlignment(Pos.CENTER);

        Image dropboxIcon = new Image(imagePath);
        ImageView dropboxView = new ImageView(dropboxIcon);
        dropboxView.setFitHeight(140);
        dropboxView.setFitWidth(140);
        grid.add(dropboxView, 0, 0);
        
        Button loginButton = new Button("Login to Dropbox");
        loginButton.setOnAction(event -> {
            getHostServices().showDocument(loginPage);
            SystemManager.getInstance().login(true);
            grid.getChildren().clear();
            
            ProgressIndicator loadingIndicator = new ProgressIndicator();
        	Label labelLoading = new Label("Waiting for login");
        	grid.add(loadingIndicator, 0, 0);
        	grid.add(labelLoading, 0, 1);
        });
        grid.add(loginButton, 1, 0);
        
        return grid;
    }
    
    private HBox createSkipLoginBar(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(topOffset, rightOffset, bottomOffset, leftOffset));
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        Label warning = new Label();
        warning.setText("Cross device copy paste will not be available without loggin in");
        warning.setWrapText(true);
        Button skipButton = new Button("Skip login");
        skipButton.setOnAction(event -> {
            SystemManager.getInstance().login(false);
            stage.setScene(preferenceUI.createPreferenceScene());
        });
        hbox.getChildren().addAll(warning, skipButton);
        
        return hbox;
    }
}


package xaurora.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoginUI extends Application{
    
    private static final String loginPage = 
            "https://www.dropbox.com/1/oauth2/authorize?response_type=token&client_id=4tpptik431fwlqo&redirect_uri=https://www.dropbox.com/home";
    PreferenceUI preferenceUI = new PreferenceUI();
    private Stage stage;
    
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle("x-aurora");      
        Scene loginScene = createLoginScene();
        stage.setScene(loginScene);
        loginScene.getStylesheets().add("style.css");
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
        Scene scene = new Scene(borderPane, 500, 300);
        return scene;
    }
    
    private HBox createTitle(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 10, 15, 10));
        hbox.setSpacing(20);
        
        Label title = new Label("x-aurora: simplify copy and paste");
        hbox.getChildren().add(title);
        hbox.setAlignment(Pos.CENTER);
        
        return hbox;
    }
    
    private GridPane createLoginForm(){
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        /*****
        Image auroraIcon = new Image("File:Aurora.png");
        ImageView auroraView = new ImageView(auroraIcon);
        auroraView.setFitHeight(200);
        auroraView.setFitWidth(300);
        grid.add(auroraView, 0, 0, 3, 2);
        *****/
        Image dropboxIcon = new Image("File:dropbox.png");
        ImageView dropboxView = new ImageView(dropboxIcon);
        dropboxView.setFitHeight(140);
        dropboxView.setFitWidth(140);
        grid.add(dropboxView, 0, 0);
        
        Button loginButton = new Button("Login to Dropbox");
        loginButton.setOnAction(event -> {getHostServices().showDocument(loginPage);});
        grid.add(loginButton, 1, 0);
        
        /*****
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Email");
        grid.add(usernameTextField, 2, 0, 2, 1);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        grid.add(passwordField, 2, 1, 2, 1);
       
        CheckBox remember = new CheckBox("remember me");
        grid.add(remember, 2, 2);
        Button login = new Button("Login");
        grid.add(login, 3, 2);
       
        Hyperlink forgotPassword = new Hyperlink();
        forgotPassword.setText("forgot password?");
        grid.add(forgotPassword, 2, 3);
        Hyperlink register = new Hyperlink();
        register.setText("register");
        String registerUrl = "https://www.dropbox.com/1/oauth2/authorize?response_type=token&client_id=4tpptik431fwlqo&redirect_uri=https://www.dropbox.com/home";
        register.setOnAction(event -> {getHostServices().showDocument(registerUrl);});
        grid.add(register, 3, 3);
        *****/
        
        return grid;
    }
    
    private HBox createSkipLoginBar(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 10, 15, 10));
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        
        Label warning = new Label("Cross device copy paste will not be available without loggin in");
        warning.setWrapText(true);
        Button skipButton = new Button("Skip login");;
        skipButton.setOnAction(event -> {stage.setScene(preferenceUI.createPreferenceScene());});
        hbox.getChildren().addAll(warning, skipButton);
        
        return hbox;
    }
}

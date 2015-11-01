/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xaurora.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginUI extends Application{
     public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Login");
        BorderPane borderPane = new BorderPane();

        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Email");
        grid.add(usernameTextField, 0, 0, 2, 1);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        grid.add(passwordField, 0, 1, 2, 1);
       
        CheckBox remember = new CheckBox("remember me");
        grid.add(remember, 0, 2);
        Button login = new Button("Login");
        grid.add(login, 1, 2);
       
        Hyperlink forgotPassword = new Hyperlink();
        forgotPassword.setText("forgot password?");
        grid.add(forgotPassword, 0, 3);
        Hyperlink register = new Hyperlink();
        register.setText("register");
        grid.add(register, 1, 3);
        
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(20);
        Label warning = new Label("Cross device copy paste will not be available without loggin in");
        Button skip = new Button("skip login");
        hbox.getChildren().addAll(warning, skip);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        
        VBox vbox = new VBox();
        //Image image = new Image("dropbox.png");
        //ImageView imageView = new ImageView(image);
        //vbox.getChildren().add(imageView);
      
        borderPane.setCenter(grid);
        borderPane.setBottom(hbox);
        borderPane.setLeft(vbox);
        
        Scene scene = new Scene(borderPane, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

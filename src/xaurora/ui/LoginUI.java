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
        grid.setVgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);
        
        Image image = new Image("File:dropbox.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(140);
        imageView.setFitWidth(140);
        grid.add(imageView, 0, 0, 2, 4);
        
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
        grid.add(register, 3, 3);
        
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 10, 15, 10));
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        Label warning = new Label("Cross device copy paste will not be available without loggin in");
        warning.setWrapText(true);
        Button skip = new Button("skip login");
        hbox.getChildren().addAll(warning, skip);

        borderPane.setCenter(grid);
        borderPane.setBottom(hbox);
        
        Scene scene = new Scene(borderPane, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

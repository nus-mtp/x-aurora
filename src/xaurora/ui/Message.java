package xaurora.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * show different type of messages to the user
 * @author Lee
 */

public class Message {

	private static final String titleConfirmation = "Confirmation";
	private static final String titleInformation = "Information";
	private static final String titleWarning = "Warning";
	private static final String titleError = "Error";
	private static final String nullHeader = null;

	public void showConfirmation(String content){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(titleConfirmation);
		alert.setHeaderText(nullHeader);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public void showInformation(String content){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titleInformation);
		alert.setHeaderText(nullHeader);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public void showWarning(String content){
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(titleWarning);
		alert.setHeaderText(nullHeader);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public void showError(String content){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(titleError);
		alert.setHeaderText(nullHeader);
		alert.setContentText(content);
		alert.showAndWait();
	}
}

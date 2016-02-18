package xaurora.ui;

import java.util.List;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.PolygonBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VirtualKeyboard extends Application{
    @Override
    public void start(final Stage stage){
        final Keyboard keyboard = new Keyboard();
        final Scene scene = new Scene(new Group(keyboard.createNode()));
        stage.setScene(scene);
        stage.setTitle("Keyboard Example");
        stage.show();
    }
    
    public static void main(final String[] args){
        launch(args);
    }

    public VirtualKeyboard() {
        /*****
        // Cursor keys, with graphic instead of text
        final Button cursorLeft = createCursorKey(KeyCode.LEFT, 15.0, 5.0, 15.0, 15.0, 5.0, 10.0);
        final Button cursorRight = createCursorKey(KeyCode.RIGHT, 5.0, 5.0, 5.0, 15.0, 15.0, 10.0);
        final Button cursorUp = createCursorKey(KeyCode.UP, 10.0, 0.0, 15.0, 10.0, 5.0, 10.0);
        final Button cursorDown = createCursorKey(KeyCode.DOWN, 10.0, 10.0, 15.0, 0.0, 5.0, 0.0);
        final VBox cursorUpDown = new VBox(2);
        cursorUpDown.getChildren().addAll(cursorUp, cursorDown);

        final Button spaceBar = createKeyButton(" ", KeyCode.SPACE);
        spaceBar.setMaxWidth(Double.POSITIVE_INFINITY);
        HBox.setHgrow(spaceBar, Priority.ALWAYS);
    }
    * *****/
    }

    /*****
    // Utility method for creating cursor keys:
    private Button createCursorKey(KeyCode code, Double... points) {
        Button button = createKey("", code);
        final Node graphic = PolygonBuilder.create().points(points).build();
        graphic.setStyle("-fx-fill: -fx-mark-color;");
        button.setGraphic(graphic);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); return button; }
    ****
     */
    private class Keyboard {
        private final Key[][] keys;
        final String[][] keyTexts = new String[][]{
            {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="},
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]", "\\"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'"},
            {"shift", "z", "x", "c", "v", "b", "n", "m", ",", ".", "/"},
            {"ctrl", "meta", "alt", " ", " ", " ", " ", " "}
        };
        final KeyCode[][] keyCodes = new KeyCode[][]{
            {KeyCode.BACK_QUOTE, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
                KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7,
                KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0, KeyCode.SUBTRACT,
                KeyCode.EQUALS},
            {KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R, KeyCode.T, KeyCode.Y,
                KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.P, KeyCode.OPEN_BRACKET,
                KeyCode.CLOSE_BRACKET, KeyCode.BACK_SLASH},
            {KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F, KeyCode.G, KeyCode.H,
                KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.SEMICOLON, KeyCode.QUOTE},
            {KeyCode.SHIFT, KeyCode.Z, KeyCode.X, KeyCode.C, KeyCode.V, KeyCode.B, KeyCode.N,
                KeyCode.M, KeyCode.COMMA, KeyCode.PERIOD, KeyCode.SLASH},
            {KeyCode.CONTROL, KeyCode.META, KeyCode.ALT, KeyCode.SPACE, KeyCode.LEFT,
                KeyCode.UP, KeyCode.DOWN, KeyCode.RIGHT}
        };
        
        public Keyboard(){
            keys = new Key[5][];
            for (int row = 0; row < keyCodes.length; row++){
                keys[row] = new Key[keyCodes[row].length];
                for (int col = 0; col < keyCodes[row].length; col++) {
                    keys[row][col] = new Key(keyTexts[row][col], keyCodes[row][col]);
                }
            }
        }

        public Node createNode() {
            final VBox keyboardNode = new VBox(5);
            keyboardNode.setPadding(new Insets(10));
            keyboardNode.getStyleClass().add("virtual-keyboard");
            
            final List<Node> keyboardNodeChildren = keyboardNode.getChildren();
            for (int row = 0; row < keys.length; row++) {
                HBox hbox = new HBox(5);
                hbox.setAlignment(Pos.CENTER);
                keyboardNodeChildren.add(hbox);

                for (int col = 0; col < keys[row].length; col++) {
                    Key key = keys[row][col];
                    hbox.getChildren().add(key.createNode());
                }
            }

            installEventHandler(keyboardNode);
            return keyboardNode;
        }

        private void installEventHandler(final Parent keyboardNode) {
            final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>(){
                public void handle(final KeyEvent keyEvent){
                    final Key key = lookupKey(keyEvent.getCode());
                    if (key != null){
                        key.setPressed(!key.isPressed() && 
                                keyEvent.getEventType() == KeyEvent.KEY_PRESSED);
                        keyEvent.consume();
                    }
                }
            };
            
            keyboardNode.setOnKeyPressed(keyEventHandler);
        }

        private Key lookupKey(final KeyCode keyCode) {
            for (int row = 0; row < keys.length; row++) {
                for (int col = 0; col < keys[row].length; col++) {
                    Key key = keys[row][col];
                    if (key.getKeyCode() == keyCode) {
                        return key;
                    }
                }
            }

            return null;
        }
    }

    private class Key {
        private final String text;
        private final KeyCode keyCode;
        private final BooleanProperty pressedProperty;

        public Key(String text, KeyCode keyCode) {
            this.text = text;
            this.keyCode = keyCode;
            this.pressedProperty = new SimpleBooleanProperty(this, "pressed");
        }
        
        public String getText(){
            return this.text;
        }
        
        public KeyCode getKeyCode(){
            return this.keyCode;
        }
        
        public boolean isPressed(){
            return pressedProperty.get();
        }
        public void setPressed(final boolean value){
            pressedProperty.set(value);
        }

        // Creates a button with mutable text, and registers listener with it
        private Node createNode() {
            final StackPane keyNode = new StackPane();
            keyNode.setFocusTraversable(true);
            installEventHandler(keyNode);
            
            final Rectangle keyBackground = new Rectangle(20, 20);
            keyBackground.fillProperty().bind(Bindings.when(pressedProperty).then(Color.RED).otherwise(Color.WHITE));
            keyBackground.setStroke(Color.BLACK);
            keyBackground.setStrokeWidth(2);
            keyBackground.setArcWidth(12);
            keyBackground.setArcHeight(12);
            
            final Text keyText = new Text(text);
            
            keyNode.getChildren().addAll(keyBackground, keyText);
            return keyNode;
        }

        private void installEventHandler(final Node keyNode) {
            final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>(){
                public void handle(final KeyEvent keyEvent){
                    if (keyEvent.getCode() == keyCode){
                        setPressed(isPressed() && 
                                keyEvent.getEventType() == KeyEvent.KEY_PRESSED);
                        keyEvent.consume();
                    }
                }
            };
            
            keyNode.setOnKeyPressed(keyEventHandler);
        }
    }
}

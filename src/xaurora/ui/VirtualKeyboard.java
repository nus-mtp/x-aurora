package xaurora.ui;

import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.PolygonBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import xaurora.util.UserPreference;

public class VirtualKeyboard {

    private final Key[][] keys;
    int hotkeyIndex;
    UserPreference preferences = UserPreference.getInstance();
    final String[][] keyTexts = new String[][]{
        {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="},
        {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]", "\\"},
        {"a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'"},
        {"shift", "z", "x", "c", "v", "b", "n", "m", ",", ".", "/"},
        {"ctrl", "meta", "alt", " ", "arrow", "arrow", "arrow", "arrow"}
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

    public VirtualKeyboard() {
        keys = new Key[5][];
        this.hotkeyIndex = -1;
        for (int row = 0; row < keyCodes.length; row++) {
            keys[row] = new Key[keyCodes[row].length];
            for (int col = 0; col < keyCodes[row].length; col++) {
                keys[row][col] = new Key(keyTexts[row][col], keyCodes[row][col]);
            }
        }
    }
    
    public VirtualKeyboard(int hotkeyIndex) {
        keys = new Key[5][];
        this.hotkeyIndex = hotkeyIndex;
        for (int row = 0; row < keyCodes.length; row++) {
            keys[row] = new Key[keyCodes[row].length];
            for (int col = 0; col < keyCodes[row].length; col++) {
                keys[row][col] = new Key(keyTexts[row][col], keyCodes[row][col]);
            }
        }
    }
    
    public Key getKey(KeyCode keyCode){
        for (int i = 0; i < keys.length; i++){
            for (int j = 0; j < keys[i].length; j++){
                if (keys[i][j].getKeyCode() == keyCode){
                    return keys[i][j];
                }
            }
        }
        return null;
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
        final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                final Key key = lookupKey(keyEvent.getCode());
                if (key != null) {
                    key.setPressed(!key.isPressed() && keyEvent.getEventType() == KeyEvent.KEY_PRESSED);
                    if (key.isPressed() == true){
                        addKey(key);
                    } else {
                        removeKey(key);
                    }
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
    
    private void addKey(Key key) {
        KeyCode[] hotkey = getHotkey(hotkeyIndex);
        KeyCode[] newHotkey = new KeyCode[hotkey.length + 1];

        for (int i = 0; i < hotkey.length; i++) {
            newHotkey[i] = hotkey[i];
        }
        newHotkey[newHotkey.length-1] = key.getKeyCode();

        setHotkey(hotkeyIndex, newHotkey);       
    }
    
    private void removeKey(Key key){
        KeyCode[] hotkey = getHotkey(hotkeyIndex);
        KeyCode[] newHotkey = new KeyCode[hotkey.length - 1];

        int i = 0, j = 0;
        while (i < hotkey.length) {
            if (hotkey[i] == key.getKeyCode()) {
                i++;
            } else {
                newHotkey[j] = hotkey[i];
                i++;
                j++;
            }
        }

        setHotkey(hotkeyIndex, newHotkey);       
    }
    
    private KeyCode[] getHotkey(int index){
        KeyCode[] hotkey = null;
        switch(index){
            case 0: hotkey = preferences.getExtendWordHotkey(); break;
            case 1: hotkey = preferences.getReduceWordHotkey(); break;
            case 2: hotkey = preferences.getExtendSentenceHotkey(); break;
            case 3: hotkey = preferences.getReduceSentenceHotkey(); break; 
            case 4: hotkey = preferences.getExtendParagraphHotkey(); break;
            case 5: hotkey = preferences.getReduceParagraphHotkey(); break;
            default:    
        }
        return hotkey;
    }
    
    private void setHotkey(int index, KeyCode[] hotkey){
        switch(index){
            case 0: preferences.setExtendWordHotkey(hotkey); break;
            case 1: preferences.setReduceWordHotkey(hotkey); break;
            case 2: preferences.setExtendSentenceHotkey(hotkey); break;
            case 3: preferences.setReduceSentenceHotkey(hotkey); break; 
            case 4: preferences.setExtendParagraphHotkey(hotkey); break;
            case 5: preferences.setReduceParagraphHotkey(hotkey); break;
            default:    
        }
    }

    public class Key {

        private final String text;
        private final KeyCode keyCode;
        private final BooleanProperty pressedProperty;

        public Key(String text, KeyCode keyCode) {
            this.text = text;
            this.keyCode = keyCode;
            this.pressedProperty = new SimpleBooleanProperty(this, "pressed");
        }

        public String getText() {
            return this.text;
        }

        public KeyCode getKeyCode() {
            return this.keyCode;
        }

        public boolean isPressed() {
            return pressedProperty.get();
        }

        public void setPressed(final boolean value) {
            pressedProperty.set(value);
        }

        private Node createNode() {
            final StackPane keyNode = new StackPane();
            keyNode.setFocusTraversable(true);
            installEventHandler(keyNode);

            final Rectangle keyBackground = new Rectangle(30, 30);
            keyBackground.fillProperty().bind(Bindings.when(pressedProperty).then(Color.RED).otherwise(Color.WHITE));
            keyBackground.setStroke(Color.BLACK);
            keyBackground.setStrokeWidth(2);
            keyBackground.setArcWidth(12);
            keyBackground.setArcHeight(12);
            if (keyCode == KeyCode.SPACE) {
                keyBackground.setWidth(200);
            }

            final Text keyText = new Text(text);
            if (text.equals("arrow")) {
                final Rectangle arrow = new Rectangle(20, 20);
                Node graphic = null;
                if (keyCode == KeyCode.LEFT) {
                    graphic = createGraphic(15.0, 5.0, 15.0, 15.0, 5.0, 10.0);
                } else if (keyCode == KeyCode.UP) {
                    graphic = createGraphic(10.0, 0.0, 15.0, 10.0, 5.0, 10.0);
                } else if (keyCode == KeyCode.DOWN) {
                    graphic = createGraphic(10.0, 10.0, 15.0, 0.0, 5.0, 0.0);
                } else if (keyCode == KeyCode.RIGHT) {
                    graphic = createGraphic(5.0, 5.0, 5.0, 15.0, 15.0, 10.0);
                }
                arrow.setClip(graphic);
                keyNode.getChildren().addAll(keyBackground, arrow);
            } else {
                keyNode.getChildren().addAll(keyBackground, keyText);
            }
            return keyNode;
        }

        private Node createGraphic(Double... points) {
            final Node graphic = PolygonBuilder.create().points(points).build();
            graphic.setStyle("-fx-fill: -fx-mark-color;");
            return graphic;
        }

        private void installEventHandler(final Node keyNode) {
            final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent keyEvent) {
                    if (keyEvent.getCode() == keyCode) {
                        setPressed(!isPressed() && keyEvent.getEventType() == KeyEvent.KEY_PRESSED);
                        keyEvent.consume();
                    }
                }
            };

            keyNode.setOnKeyPressed(keyEventHandler);
        }
    }
}

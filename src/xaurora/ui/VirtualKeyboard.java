package xaurora.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.PolygonBuilder;

public class VirtualKeyboard {
  private final VBox root;
  private static final boolean shiftDown = false;
  private static final boolean ctrlDown = false;
  private static final boolean altDown = false;
  private static final boolean metaDown = false;
  /**
   * Creates a Virtual Keyboard. 
   * @param target The node that will receive KeyEvents from this keyboard. 
   * If target is null, KeyEvents will be dynamically forwarded to the focus owner
   * in the Scene containing this keyboard.
   */
  public VirtualKeyboard(ReadOnlyObjectProperty<Node> target) {
    this.root = new VBox(5);
    root.setPadding(new Insets(10));
    root.getStyleClass().add("virtual-keyboard");

    // Data for regular buttons; split into rows
    final String[][] key = new String[][] {
        { "`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=" },
        { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]", "\\" },
        { "a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'" },
        { "shift" , "z", "x", "c", "v", "b", "n", "m", ",", ".", "/" }, 
        { "ctrl", "meta", "alt"} 
    };

    final KeyCode[][] codes = new KeyCode[][] {
        { KeyCode.BACK_QUOTE, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
            KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7,
            KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0, KeyCode.SUBTRACT,
            KeyCode.EQUALS },
        { KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R, KeyCode.T, KeyCode.Y,
            KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.P, KeyCode.OPEN_BRACKET,
            KeyCode.CLOSE_BRACKET, KeyCode.BACK_SLASH },
        { KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F, KeyCode.G, KeyCode.H,
            KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.SEMICOLON, KeyCode.QUOTE },
        { KeyCode.SHIFT, KeyCode.Z, KeyCode.X, KeyCode.C, KeyCode.V, KeyCode.B, KeyCode.N,
            KeyCode.M, KeyCode.COMMA, KeyCode.PERIOD, KeyCode.SLASH },
        { KeyCode.CONTROL, KeyCode.META, KeyCode.ALT}
    };

    // Cursor keys, with graphic instead of text
    final Button cursorLeft = createCursorKey(KeyCode.LEFT, target, 15.0, 5.0, 15.0, 15.0, 5.0, 10.0);
    final Button cursorRight = createCursorKey(KeyCode.RIGHT, target, 5.0, 5.0, 5.0, 15.0, 15.0, 10.0);
    final Button cursorUp = createCursorKey(KeyCode.UP, target, 10.0, 0.0, 15.0, 10.0, 5.0, 10.0);
    final Button cursorDown = createCursorKey(KeyCode.DOWN, target, 10.0, 10.0, 15.0, 0.0, 5.0, 0.0);
    final VBox cursorUpDown = new VBox(2);
    cursorUpDown.getChildren().addAll(cursorUp, cursorDown);

    // build layout
    for (int row = 0; row < key.length; row++) {
      HBox hbox = new HBox(5);
      hbox.setAlignment(Pos.CENTER);
      root.getChildren().add(hbox);
      
      for (int k = 0; k < key[row].length; k++) {
        hbox.getChildren().add(createKeyButton(key[row][k], codes[row][k], target));
      }
    }

    final Button spaceBar = createKeyButton(" ", KeyCode.SPACE, target);
    spaceBar.setMaxWidth(Double.POSITIVE_INFINITY);
    HBox.setHgrow(spaceBar, Priority.ALWAYS);

    final HBox bottomRow = new HBox(5);
    bottomRow.setAlignment(Pos.CENTER);
    bottomRow.getChildren().addAll(spaceBar, cursorLeft, cursorUpDown, cursorRight);
    root.getChildren().add(bottomRow);    
  }
  
  /**
   * Creates a VirtualKeyboard which uses the focusProperty of the scene to which it is attached as its target
   */
  public VirtualKeyboard() {
    this(null);
  }
  
  /**
   * Visual component displaying this keyboard. The returned node has a style class of "virtual-keyboard".
   * Buttons in the view have a style class of "virtual-keyboard-button".
   * @return a view of the keyboard.
   */
  public Node view() {
    return root ;
  }

  // Creates a button with fixed text not responding to Shift
  private Button createKeyButton(final String text, final KeyCode code, final ReadOnlyObjectProperty<Node> target) {
    StringProperty textProperty = new SimpleStringProperty(text);
    Button button = createButton(textProperty, code, target);
    return button;
  }
  
  // Utility method for creating cursor keys:
  private Button createCursorKey(KeyCode code, ReadOnlyObjectProperty<Node> target, Double... points) {
    Button button = createKeyButton("", code, target);
    final Node graphic = PolygonBuilder.create().points(points).build();
    graphic.setStyle("-fx-fill: -fx-mark-color;");
    button.setGraphic(graphic);
    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    return button ;
  }
  
  // Creates a button with mutable text, and registers listener with it
  private Button createButton(final ObservableStringValue text, final KeyCode code, final ReadOnlyObjectProperty<Node> target) {
    final Button button = new Button();
    button.textProperty().bind(text);
        
    // Important not to grab the focus from the target:
    button.setFocusTraversable(false);
    
    // Add a style class for css:
    button.getStyleClass().add("virtual-keyboard-button");
    
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {

        final Node targetNode ;
        if (target != null) {
          targetNode = target.get();
        } else {
          targetNode = view().getScene().getFocusOwner();
        }
        
        if (targetNode != null) {
          final String character;
          if (text.get().length() == 1) {
            character = text.get();
          } else {
            character = KeyEvent.CHAR_UNDEFINED;
          }
          final KeyEvent keyPressEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_PRESSED, character, code);
          targetNode.fireEvent(keyPressEvent);
          final KeyEvent keyReleasedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_RELEASED, character, code);
          targetNode.fireEvent(keyReleasedEvent);
          if (character != KeyEvent.CHAR_UNDEFINED) {
            final KeyEvent keyTypedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_TYPED, character, code);
            targetNode.fireEvent(keyTypedEvent);
          }
        }
      }
    });
    return button;
  }

  // Utility method to create a KeyEvent from the Modifiers
  private KeyEvent createKeyEvent(Object source, EventTarget target,
      EventType<KeyEvent> eventType, String character, KeyCode code) {
    return new KeyEvent(source, target, eventType, character, code.toString(),
        code, shiftDown, ctrlDown, altDown, metaDown);
  }
}

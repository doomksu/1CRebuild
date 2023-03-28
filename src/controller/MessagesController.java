package controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 *
 * @author kneretin
 */
public class MessagesController extends AbstractController {

    @FXML
    private TextArea messagesArea;

    public void initialize() {
        messagesArea.setText("");
        messagesArea.setEditable(false);
    }

    public void setMessages(ArrayList<String> messages) {
        String mess = "";
        for (String message : messages) {
            mess += message + "\n";
        }
        messagesArea.setText(mess);
    }
}

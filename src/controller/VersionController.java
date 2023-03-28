package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import svod.Svod;

/**
 *
 * @author kneretin
 */
public class VersionController extends AbstractController {

    @FXML
    private Label versionNumberLabel;

    public void initialize() {
     versionNumberLabel.setText(Svod.versionNumber);
    }
}

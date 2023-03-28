package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import settings.SettingsReader;

/**
 *
 * @author kneretin
 */
public class SettingsController extends AbstractController {

    @FXML
    private TextField region_field, raion_field, regnumberPFR_field;
    @FXML
    private TextField KTOPFR_field, OKPO_field, KSP_field, OKEI_field;
    @FXML
    private TextArea ORGname_area, STRUCTname_area;
    //
    private ToggleGroup group;
    @FXML
    private RadioButton rgroup_empty_rbutton, rgroup_name_rbutton, rgroup_else_rbutton;
    @FXML
    private TextField rgroup_else_field;
    @FXML
    private Button save_button, close_button;
    @FXML
    private CheckBox split36byAccount;

    public void initialize() {
        loadSettings();
        setSettingsValues();
        //связывSаем радио кнопки в группу
        group = new ToggleGroup();
        rgroup_empty_rbutton.setToggleGroup(group);
        rgroup_empty_rbutton.setUserData(new RadioButtonId("rgroup_empty_rbutton"));
        rgroup_name_rbutton.setToggleGroup(group);
        rgroup_name_rbutton.setUserData(new RadioButtonId("rgroup_name_rbutton"));
        rgroup_else_rbutton.setToggleGroup(group);
        rgroup_else_rbutton.setUserData(new RadioButtonId("rgroup_else_rbutton"));
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                String id = group.getSelectedToggle().getUserData().toString();
                if (id.equals("rgroup_empty_rbutton") || id.equals("rgroup_name_rbutton")) {
                    rgroup_else_field.setEditable(false);
                    if (id.equals("rgroup_empty_rbutton")) {
                        SettingsReader.getInstance().setValue("OPTPRIM", "empty");
                    } else {
                        SettingsReader.getInstance().setValue("OPTPRIM", "name");
                    }
                } else {
                    rgroup_else_field.setEditable(true);
                    SettingsReader.getInstance().setValue("OPTPRIM", "else");
                }
            }
        });
        //
        String prim = SettingsReader.getInstance().getValue("OPTPRIM");
        switch (prim) {
            case "empty":
                group.selectToggle(rgroup_empty_rbutton);
                break;
            case "name":
                group.selectToggle(rgroup_name_rbutton);
                break;
            case "else":
                group.selectToggle(rgroup_else_rbutton);
                break;
            default:
                group.selectToggle(rgroup_empty_rbutton);
        }

        rgroup_else_field.setText(SettingsReader.getInstance().getValue("TPRIM"));
        rgroup_else_field.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    SettingsReader.getInstance().setValue("TPRIM", newValue);
                });
        //
        region_field.setText(SettingsReader.getInstance().getValue("TREGIONN"));
        region_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 3) {
                newValue = oldValue;
            }
            SettingsReader.getInstance().setValue("TREGIONN", newValue);
        });
        //
        raion_field.setText(SettingsReader.getInstance().getValue("TRAIONN"));
        raion_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 3) {
                newValue = oldValue;
            }
            SettingsReader.getInstance().setValue("TRAIONN", newValue);
        });
        //
        regnumberPFR_field.setText(SettingsReader.getInstance().getValue("TREGNPFR"));
        regnumberPFR_field.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TREGNPFR", newValue);
        });
        //
        ORGname_area.setText(SettingsReader.getInstance().getValue("TORGNAME"));
        ORGname_area.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TORGNAME", newValue);
        });
        //
        KTOPFR_field.setText(SettingsReader.getInstance().getValue("TKTOPFR"));
        KTOPFR_field.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TKTOPFR", newValue);
        });
        //
        OKPO_field.setText(SettingsReader.getInstance().getValue("TOKPO"));
        OKPO_field.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TOKPO", newValue);
        });
        //
        KSP_field.setText(SettingsReader.getInstance().getValue("TKSP"));
        KSP_field.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TKSP", newValue);
        });
        //
        OKEI_field.setText(SettingsReader.getInstance().getValue("TOKEY"));
        OKEI_field.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TOKEY", newValue);
        });
        //
        STRUCTname_area.setText(SettingsReader.getInstance().getValue("TSTRUKTNAME"));
        STRUCTname_area.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsReader.getInstance().setValue("TSTRUKTNAME", newValue);
        });
        //
        String split36Val = SettingsReader.getInstance().getValue("split36byAccount");
        split36byAccount.setSelected(Boolean.parseBoolean(split36Val));
        
        
        split36byAccount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               String val = Boolean.toString(split36byAccount.isSelected());
               SettingsReader.getInstance().setValue("split36byAccount", val);
            }
        });

        save_button.setOnAction(event -> {
            Alert alert = null;
            if (SettingsReader.getInstance().saveSettings(true) == false) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Установки не были изменены или произошла ошибка при записи");
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Установки были успешно сохранены");
            }
            alert.setTitle("ok");
            alert.setHeaderText(null);
            alert.showAndWait();
        });
        close_button.setOnAction(event -> {
            enteryPoint.closeAditionalWindow();
        });
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private void loadSettings() {
        SettingsReader sr = SettingsReader.getInstance();
    }

    private void setSettingsValues() {
        region_field.setText(SettingsReader.getInstance().getValue(""));

    }

    private class RadioButtonId {

        private String id = "";

        public RadioButtonId(String str) {
            id = str;
        }

        @Override
        public String toString() {
            return id;
        }

    }
}

package controller;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pkg1crebuild.EnteryPoint;
import service.LoggingService;
import settings.SettingsReader;
import svod.Svod;
import vedomosti.common.FormParseException;

/**
 * FXML Controller class
 *
 * @author kneretin
 */
public class MainController extends AbstractController {

    @FXML
    private Button IN_FILES_DIRECTORY_DIALOG, OUT_FILES_DIRECTORY_DIALOG, SETTINGS_BUTTON, LOADDATA_BUTTON;
    private File input_directory, output_directory;
    @FXML
    private TextField inFilesDirectoryName, outFilesDirectoryName, currentNumber, outFileNumberText;
    @FXML
    private ComboBox selectFormComboBox;
    @FXML
    private MenuItem versionMenuItem;
    @FXML
    private CheckBox makeFoldersCheckBox;
    
    @FXML 
    private Label versionLabel;

    private void bindView() {
        inFilesDirectoryName.setText(SettingsReader.getInstance().getValue("PFRFILES"));
        outFilesDirectoryName.setText(SettingsReader.getInstance().getValue("EXCELFILES"));
        currentNumber.setText(SettingsReader.getInstance().getValue("TNOMVED"));
        int selectionIndex = selectFormComboBox.getSelectionModel().getSelectedIndex();
        selectFormComboBox.getItems().clear();
        for (String vedomostName : SettingsReader.getInstance().getVedomostiList()) {
            selectFormComboBox.getItems().add(vedomostName);
        }
        selectFormComboBox.getSelectionModel().select(selectionIndex);
        if (selectionIndex == 0) {
            selectFormComboBox.getSelectionModel().selectFirst();
        }
        int selected_pril_index = selectFormComboBox.getSelectionModel().getSelectedIndex();
        int vedomostNumber = 0;
        if (selected_pril_index >= 0) {
            if (selectFormComboBox.getItems().get(selected_pril_index) != null) {
                String selected_pril = (String) selectFormComboBox.getSelectionModel().getSelectedItem();
                String prilNum = selected_pril.substring(selected_pril.indexOf("Прил."), selected_pril.lastIndexOf(")"));
                prilNum = prilNum.replace("Прил.", "").trim();
                prilNum = prilNum.replace("(", "").trim();
                prilNum = prilNum.replace(")", "").trim();
                vedomostNumber = 0;
                try {
                    vedomostNumber = Integer.parseInt(prilNum);
                    LoggingService.writeLog("vedomostNumber = " + vedomostNumber,"debug");
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("ERROR cant calculate prilType: " + prilNum,"debug");
                }
            }
        }
        outFileNumberText.setText(String.valueOf(SettingsReader.getInstance().getTodayOutFilesCount(vedomostNumber)));
        makeFoldersCheckBox.setSelected(SettingsReader.getInstance().isMakeFoldersForPril());
        versionLabel.setText(Svod.versionNumber);
    }

    public void initialize() {
        bindView();
        IN_FILES_DIRECTORY_DIALOG.setOnAction(x -> {
            input_directory = callFileChooser();
            inFilesDirectoryName.setText(input_directory.getAbsolutePath());
            SettingsReader.getInstance().setValue("PFRFILES", input_directory.getAbsolutePath());
            SettingsReader.getInstance().saveSettings(false);
            bindView();
        });
        OUT_FILES_DIRECTORY_DIALOG.setOnAction(x -> {
            output_directory = callFileChooser();
            outFilesDirectoryName.setText(output_directory.getAbsolutePath());
            SettingsReader.getInstance().setValue("EXCELFILES", output_directory.getAbsolutePath());
            SettingsReader.getInstance().saveSettings(false);
            bindView();
        });
        SETTINGS_BUTTON.setOnAction(x -> {
            enteryPoint.openSettingsWindow();
        });
        LOADDATA_BUTTON.setOnAction(x -> {
            String type = (String) selectFormComboBox.getSelectionModel().getSelectedItem();
            try {
                enteryPoint.getSvod().loadFiles(type);

                if (enteryPoint.getSvod().isDoneGood()) {
                    bindView();
                    Alert alert = null;
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Готово");
                    alert.setTitle("ok");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                } else {
                    Alert alert = null;
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("При формировании СВОД возникли ошибки, проверьте файл log.txt");
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            } catch (FormParseException ex) {
                LoggingService.writeLog(ex);
                enteryPoint.openMessagesWindow(enteryPoint.getSvod().getMessages());
            } catch (Exception ex) {
                LoggingService.writeLog(ex);
            }
        });

        versionMenuItem.setOnAction(x -> {
            enteryPoint.openVersionWindow();
        });

        inFilesDirectoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            File tryFile = new File(inFilesDirectoryName.getText().trim());
            if (tryFile.isDirectory() == false) {
                inFilesDirectoryName.getStyleClass().add("invalid");
            } else {
                inFilesDirectoryName.getStyleClass().removeAll("invalid");
            }
        });

        outFilesDirectoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            File tryFile = new File(outFilesDirectoryName.getText().trim());
            if (tryFile.isDirectory() == false) {
                outFilesDirectoryName.getStyleClass().add("invalid");
            } else {
                outFilesDirectoryName.getStyleClass().removeAll("invalid");
            }
        });

        inFilesDirectoryName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue() == false) { //выход из поля 
                    if (inFilesDirectoryName.getText().trim().equals(SettingsReader.getInstance().getValue("PFRFILES")) == false) {
                        input_directory = new File(inFilesDirectoryName.getText().trim());
                        if (input_directory.isDirectory()) {
                            SettingsReader.getInstance().setValue("PFRFILES", input_directory.getAbsolutePath());
                            SettingsReader.getInstance().saveSettings(false);
                        }
                    }
                }
            }
        });

        outFilesDirectoryName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue() == false) { //выход из поля 
                    if (outFilesDirectoryName.getText().trim().equals(SettingsReader.getInstance().getValue("EXCELFILES")) == false) {
                        output_directory = new File(outFilesDirectoryName.getText().trim());
                        if (output_directory.isDirectory()) {
                            SettingsReader.getInstance().setValue("EXCELFILES", output_directory.getAbsolutePath());
                            SettingsReader.getInstance().saveSettings(false);
                        }
                    }
                }
            }
        });
        makeFoldersCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                boolean selected = makeFoldersCheckBox.isSelected();
                SettingsReader.getInstance().setMakeFoldersForPril(selected);
            }
        });
    }

    private File callFileChooser() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(enteryPoint.getSvod().getBootPath());
        dirChooser.setTitle("выбрать файл");
        return dirChooser.showDialog(scene.getWindow());
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setEnteryPoint(EnteryPoint epoint) {
        enteryPoint = epoint;
        svod = enteryPoint.getSvod();
        Stage st = enteryPoint.getFrontStage();
        Scene sc = st.getScene();
        ObservableList<String> stylesName = sc.getStylesheets();
        stylesName.add("/resources/style.css");
    }
}

package pkg1crebuild;

import controller.MessagesController;
import controller.MainController;
import controller.SettingsController;
import controller.VersionController;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import svod.Svod;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.LoggingService;
import settings.FoldersInformation;
import view.MainViewStringFXML;
import view.MessagesViewStringFXML;
import view.SettingsViewStringFXML;
import view.VersionViewStringFXML;

/**
 * Основной FX класс для запуска программы
 *
 * @author kneretin
 */
public class EnteryPoint extends Application {

    private Svod svod;
    private Stage frontStage;
    //Модлальное окно - настройки, класссификаторы
    private Stage modalStage;

    @Override
    public void start(Stage primaryStage) {
        svod = Svod.getInstance();
        mainForm();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void mainForm() {
        try {
            frontStage = new Stage();
            FXMLLoader loader = new FXMLLoader(EnteryPoint.class.getResource("/view/MainView.fxml"));
            VBox pane = (VBox) loader.load();
            MainController controller = loader.getController();
            Scene scene = new Scene(pane);
            scene.getStylesheets().clear();
            scene.getStylesheets().add("/resources/style.css");
            frontStage.setScene(scene);
            frontStage.setResizable(false);
            frontStage.setTitle("СВОД 1С");
            frontStage.show();
            controller.setEnteryPoint(this);
            controller.setScene(scene);
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }

    public Svod getSvod() {
        return svod;
    }

    public void closeAditionalWindow() {
        modalStage.close();
        modalStage = null;
        frontStage.show();
    }

    public Stage getFrontStage() {
        return frontStage;
    }

    public void openSettingsWindow() {
        try {
            modalStage = new Stage();
            FXMLLoader loader = new FXMLLoader(EnteryPoint.class.getResource("/view/settView.fxml"));
            VBox pane = (VBox) loader.load();
            SettingsController controller = loader.getController();
            
            controller.setEnteryPoint(this);
            Scene scene = new Scene(pane);
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            modalStage.setTitle("Параметры");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(frontStage);
            modalStage.show();
            controller.setEnteryPoint(this);
            controller.setScene(scene);
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }

    public static void redirectLogFile() {
        File log = new File(FoldersInformation.LOG_FILE);
        try {
            PrintStream pslog = new PrintStream(log);
            if (!log.exists()) {
                log.delete();
                log.createNewFile();
            }
            System.setOut(pslog);
            System.setErr(pslog);
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }

    public void openVersionWindow() {
        try {
            modalStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            AnchorPane pane = (AnchorPane) loader.load(new ByteArrayInputStream(VersionViewStringFXML.FXML.getBytes("UTF8")));

            VersionController controller = loader.getController();
            controller.setEnteryPoint(this);
            Scene scene = new Scene(pane);
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            modalStage.setTitle("Параметры");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(frontStage);
            modalStage.show();
            controller.setEnteryPoint(this);
            controller.setScene(scene);
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }

    public void openMessagesWindow(ArrayList<String> messages) {
        try {
            modalStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            AnchorPane pane = (AnchorPane) loader.load(new ByteArrayInputStream(MessagesViewStringFXML.FXML.getBytes("UTF8")));
            MessagesController controller = loader.getController();
            Scene scene = new Scene(pane);
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            modalStage.setTitle("Сообщения");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(frontStage);
            modalStage.show();
            controller.setEnteryPoint(this);
            controller.setMessages(messages);
            controller.setScene(scene);
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }
}

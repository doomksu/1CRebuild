package controller;

import javafx.scene.Scene;
import pkg1crebuild.EnteryPoint;
import svod.Svod;

/**
 *
 * @author kneretin
 */
class AbstractController {

    protected EnteryPoint enteryPoint;
    protected Svod svod;
    protected Scene scene;

    public void setEnteryPoint(EnteryPoint epoint) {
        enteryPoint = epoint;
        svod = enteryPoint.getSvod();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
}

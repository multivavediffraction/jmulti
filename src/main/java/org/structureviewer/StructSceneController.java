package org.structureviewer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import org.jmulti.UnitCell;
import org.jmulti.calc.AtomDescr;
import org.jmulti.calc.P3;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class StructSceneController implements Initializable {
    @FXML private SubScene scene;
    @FXML private Group world;

    private Map<String, Material> materials = new HashMap<>();

    private double[] atomsTransMat = {
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
    };
    private double oldMouseX = 0;
    private double oldMouseY = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Camera cam = new PerspectiveCamera(true);
        cam.setTranslateZ(-50);
        cam.setTranslateX(0);
        cam.setTranslateY(0);
        scene.setCamera(cam);
    }

    private Material getMaterial(String name){
        if (materials.containsKey(name)) {
            return materials.get(name);
        } else {
            PhongMaterial mat = new PhongMaterial();
            float[] color = GlUtils.colorFromElementName(name);
            mat.setDiffuseColor(Color.color(color[0], color[1], color[2]));
            mat.setSpecularColor(Color.WHITE);
            materials.put(name, mat);
            return mat;
        }
    }

    public void setAtoms(UnitCell uc, AtomDescr[] atoms) {
        P3[] base = uc.getUnitCellVectors();

        P3 center = new P3(0,0,0);
        double xmin = 0, xmax = 0;
        double ymin = 0, ymax = 0;
        double zmin = 0, zmax = 0;

        if (atoms.length > 0) {
            xmin = xmax = atoms[0].p().x();
            ymin = ymax = atoms[0].p().y();
            zmin = zmax = atoms[0].p().z();
        }

        for (int i = 0; i < atoms.length; ++i){
            atoms[i] = atoms[i].updateP(base[0].mul(atoms[i].p().x()).plus(base[1].mul(atoms[i].p().y())).plus(base[2].mul(atoms[i].p().z())));
            center = center.plus(atoms[i].p());
            xmin = Math.min(atoms[i].p().x(), xmin);
            xmax = Math.max(atoms[i].p().x(), xmax);
            ymin = Math.min(atoms[i].p().y(), ymin);
            ymax = Math.max(atoms[i].p().y(), ymax);
            zmin = Math.min(atoms[i].p().z(), zmin);
            zmax = Math.max(atoms[i].p().z(), zmax);
        }

        P3 pmin = new P3(xmin, ymin, zmin);
        P3 pmax = new P3(xmax, ymax, zmax);
        double radius = Math.sqrt(pmax.plus(pmin.mul(-1)).normSquare()) + 2.5; //2.5 is a safety margin to account for atom radius

        center = center.mul(-1.0/atoms.length);

        world.getChildren().clear();
        for(AtomDescr a: atoms){
            Sphere s = new Sphere(GlUtils.radiusFromElementName(a.name()));
            s.setMaterial(getMaterial(a.name()));
            P3 p = a.p().plus(center);
            s.setTranslateX(p.x());
            s.setTranslateY(p.y());
            s.setTranslateZ(p.z());
            world.getChildren().add(s);
        }

        Camera cam = scene.getCamera();
        if (cam instanceof PerspectiveCamera) {
            PerspectiveCamera pcam = (PerspectiveCamera) cam;
            double fov = pcam.getFieldOfView();
            double z = radius / Math.tan(fov * Math.PI / 180) + uc.c();
            pcam.setTranslateZ(-z);

            if (scene.getWidth() < scene.getHeight()) {
                pcam.setVerticalFieldOfView(false);
            } else {
                pcam.setVerticalFieldOfView(true);
            }
        }
    }

    @FXML
    public void mouseDragged(MouseEvent e) {
        updateTransMatrix(e);

        world.getTransforms().setAll(
                new Affine(
                        atomsTransMat[0], atomsTransMat[1], atomsTransMat[2], 0,
                        atomsTransMat[3], atomsTransMat[4], atomsTransMat[5], 0,
                        atomsTransMat[6], atomsTransMat[7], atomsTransMat[8], 0)
        );
    }

    @FXML
    public void mouseMoved(MouseEvent e) {
        updateMousePos(e);
    }

    private void updateMousePos(MouseEvent e) {
        oldMouseX = e.getX();
        oldMouseY = e.getY();
    }

    private void updateTransMatrix(MouseEvent e) {
        double w = 0.01;
        double cosX = Math.cos(w*(e.getX() - oldMouseX));
        double sinX = Math.sin(w*(e.getX() - oldMouseX));

        double cosY = Math.cos(w*(e.getY() - oldMouseY));
        double sinY = Math.sin(w*(e.getY() - oldMouseY));

        updateMousePos(e);

        double[] My = {
                cosX, 0, -sinX,
                0, 1,    0,
                sinX, 0, cosX
        };

        double[] Mx = {
                1,    0,     0,
                0, cosY, -sinY,
                0, sinY,  cosY
        };

        double[] diffMatrix = Utils.matMul( My, Mx, 3);

        atomsTransMat = Utils.matMul(atomsTransMat, diffMatrix, 3);
    }

}

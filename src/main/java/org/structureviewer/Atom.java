package org.structureviewer;

import javafx.scene.paint.Color;
import org.jmulti.calc.AtomDescr;
import org.jmulti.calc.P3;

public class Atom {
    private AtomDescr atom;

    public Atom(AtomDescr atom) {
        this.atom = atom;
    }

    public Atom(String name, int charge, double x, double y, double z) {
        this.atom = new AtomDescr(name, charge, new P3(x, y, z));
    }

    public Atom() {
        this("H", -1, 0.0, 0.0, 0.0);
    }

    public AtomDescr getDescr(){
        return this.atom;
    }

    public Color getColor() {
        var c = GlUtils.colorFromElementName(atom.name());
        return Color.color(c[0], c[1], c[2]);
    }

    public String getName(){
        return atom.name();
    }

    public void setName(String name){
        this.atom = this.atom.updateName(name);
    }

    public int getCharge() {
        return atom.charge();
    }

    public void setCharge(int charge){
        this.atom = this.atom.updateCharge(charge);
    }

    public double getX() {
        return this.atom.p().x();
    }

    public void setX(double x) {
        this.atom = this.atom.updateX(x);
    }

    public double getY() {
        return this.atom.p().y();
    }

    public void setY(double y) {
        this.atom = this.atom.updateY(y);
    }

    public double getZ() {
        return this.atom.p().z();
    }

    public void setZ(double z) {
        this.atom = this.atom.updateZ(z);
    }

}

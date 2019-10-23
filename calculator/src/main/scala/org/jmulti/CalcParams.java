package org.jmulti;

public class CalcParams {
    public final double psiStart;
    public final double psiEnd;
    public final int psiSteps;

    public final int h;
    public final int k;
    public final int l;

    public final double energy;
    public final String title;

    public final int namax=61;  // should be odd 59
    public final int nbmax=101; // should be odd 101
    public final int ncmax=151; // should be odd 151


    public CalcParams(double psiStart, double psiEnd, int psiSteps, int h, int k, int l, double energy, String title){
        this.psiStart = psiStart;
        this.psiEnd = psiEnd;
        this.psiSteps = psiSteps;
        this.h = h;
        this.k = k;
        this.l = l;
        this.energy = energy;
        this.title = title;
    }
}

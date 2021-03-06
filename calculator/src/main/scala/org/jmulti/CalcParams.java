package org.jmulti;

public class CalcParams {
    public final double psiStart;
    public final double psiEnd;
    public final int psiSteps;

    public final int h;
    public final int k;
    public final int l;

    public final double energyStart;
    public final double energyEnd;
    public final int energySteps;
    public final String title;

    public final ParametersSweep sweep;
    public final boolean parallelCalc;

    public final int hrange; // should be odd 59
    public final int krange; // should be odd 101
    public final int lrange; // should be odd 151

    public CalcParams(double psiStart, double psiEnd, int psiSteps,
                      int h, int k, int l,
                      double energyStart, double energyEnd, int energySteps,
                      String title, ParametersSweep sweep, boolean parallelCalc,
                      int hrange, int krange, int lrange)
    {
        this.psiStart = psiStart;
        this.psiEnd = psiEnd;
        this.psiSteps = psiSteps;
        this.h = h;
        this.k = k;
        this.l = l;
        this.energyStart = energyStart;
        this.energyEnd = energyEnd;
        this.energySteps = energySteps;
        this.title = title;
        this.sweep = sweep;
        this.parallelCalc = parallelCalc;
        this.hrange = hrange;
        this.krange = krange;
        this.lrange = lrange;
    }
}

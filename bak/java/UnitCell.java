package org.jmulti;

import org.jetbrains.annotations.NotNull;
import org.jmulti.calc.P3;

public class UnitCell {
    public final double a;
    public final double b;
    public final double c;
    public final double alpha;
    public final double beta;
    public final double gamma;

    private static final double rad = Math.PI / 180.0;

    public UnitCell(double a, double b, double c, double alpha, double beta, double gamma){
        this.a = a;
        this.b = b;
        this.c = c;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    @NotNull
    public static UnitCell cubic(double a){
        return new UnitCell(a, a, a, 90.0, 90.0, 90.0);
    }

    @NotNull
    static UnitCell triclinic(double a, double b, double c, double alpha, double beta, double gamma){
        return new UnitCell(a, b, c, alpha, beta, gamma);
    }

    @NotNull
    static UnitCell monoclinic(double a, double b, double c, double beta){
        return new UnitCell(a, b, c, 90.0, beta, 90.0);
    }

    @NotNull
    public static UnitCell orthorhombic(double a, double b, double c){
        return new UnitCell(a, b, c, 90.0, 90.0, 90.0);
    }

    @NotNull
    public static UnitCell tetragonal(double a, double c){
        return new UnitCell(a, a, c, 90.0, 90.0, 90.0);
    }

//    @NotNull
    public static UnitCell rhombohedral(double a, double alpha){
        return new UnitCell(a, a, a, alpha, alpha, alpha);
    }

//    @NotNull
    public static UnitCell hexagonal(double a, double c){
        return new UnitCell(a, a, c, 90.0, 90.0, 120.0);
    }

    public double d_hkl(double h,  double k, double l) {
        //1/d^2 ==
        double sin_a = Math.sin(alpha * rad);
        double cos_a = Math.cos(alpha * rad);
        double sin_b = Math.sin(beta * rad);
        double cos_b = Math.cos(beta * rad);
        double sin_c = Math.sin(gamma * rad);
        double cos_c = Math.cos(gamma * rad);
        return ((h*h*sin_a*sin_a/(a*a)) + (k*k*sin_b*sin_b/(b*b)) + (l*l*sin_c*sin_c/(c*c)) +
                ((2*k*l*cos_a)/(b*c)) + ((2*h*l*cos_b)/(a*c)) + ((2*h*k*cos_c)/(a*b))) /
                (1 - cos_a*cos_a - cos_b*cos_b - cos_c*cos_c + 2*cos_a*cos_b*cos_c);
    }

    public P3[] getUnitCellVectors() {
        double rad = Math.PI / 180;
        org.jmulti.calc.P3 a = new org.jmulti.calc.P3(this.a, 0, 0);

        org.jmulti.calc.P3 b = new org.jmulti.calc.P3(this.b * Math.cos(gamma*rad), this.b * Math.sin(gamma*rad), 0);

        double cz = (this.c/Math.sin(gamma*rad)) * Math.sqrt(
                1 - Math.pow(Math.cos(alpha*rad),2) - Math.pow(Math.cos(beta*rad),2) - Math.pow(Math.cos(gamma*rad),2)
                + 2*Math.cos(alpha*rad)*Math.cos(beta*rad)*Math.cos(gamma*rad)
        );
        org.jmulti.calc.P3 c = new org.jmulti.calc.P3(this.c * Math.cos(beta*rad),
                this.c * (Math.cos(alpha * rad) - Math.cos(beta*rad)*Math.cos(gamma*rad))/Math.sin(gamma*rad),
                cz);

        return new P3[] {a, b, c};
    }
}

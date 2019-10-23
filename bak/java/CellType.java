package org.jmulti;

public enum CellType {
    CUBIC {
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            return (h*h + k*k + l*l)/(uc.a*uc.a);
        }
    },

    TETRAGONAL{
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            return (h*h + k*k)/(uc.a*uc.a) + (l*l)/(uc.c*uc.c);
        }
    },

    HEXAGONAL {
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            return (4.0/3.0)*(h*h + h*k + k*k)/(uc.a*uc.a) + (l*l)/(uc.c*uc.c);
        }
    },

    RHOMBOHEDRAL {
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            double sin_a = Math.sin(uc.alpha);
            double cos_a = Math.cos(uc.alpha);
            return ((h*h + k*k + l*l) * sin_a*sin_a + 2*(h*k + k*l + h*l)*(cos_a*cos_a - cos_a))/
                    ((uc.a*uc.a)*(1 - 3*cos_a*cos_a + 2*cos_a*cos_a*cos_a));
        }
    },
    ORTHORHOMBIC {
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            return (h*h)/(uc.a*uc.a) + (k*k)/(uc.b*uc.b) + (l*l)/(uc.c*uc.c);
        }
    },

    MONOCLINIC {
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            double sin_b = Math.sin(uc.beta);
            double cos_b = Math.cos(uc.beta);
            return h*h/(uc.a*uc.a*sin_b*sin_b) + k*k/(uc.b*uc.b) + l*l /(uc.c*uc.c*sin_b*sin_b) -
                    2*h*l*cos_b/(uc.a * uc.c*sin_b*sin_b);
        }
    },

    TRICLINIC {
        @Override
        public double d_hkl(int h, int k, int l, UnitCell uc) {
            //1/d^2 ==
            double sin_a = Math.sin(uc.alpha);
            double cos_a = Math.cos(uc.alpha);
            double sin_b = Math.sin(uc.beta);
            double cos_b = Math.cos(uc.beta);
            double sin_c = Math.sin(uc.gamma);
            double cos_c = Math.cos(uc.gamma);
            return ((h*h*sin_a*sin_a/(uc.a*uc.a)) + (k*k*sin_b*sin_b/(uc.b*uc.b)) + (l*l*sin_c*sin_c/(uc.c*uc.c)) +
                    ((2*k*l*cos_a)/(uc.b*uc.c)) + ((2*h*l*cos_b)/(uc.a*uc.c)) + ((2*h*k*cos_c)/(uc.a*uc.b))) /
                    (1 - cos_a*cos_a - cos_b*cos_b - cos_c*cos_c + 2*cos_a*cos_b*cos_c);
        }
    }
    ;

    abstract public double d_hkl(int h, int k, int l, UnitCell uc);
}

module org.jmulti {
    requires scala.library;

    requires org.jmulti.plugins;
    provides org.jmulti.plugins.Calculator with org.jmulti.calc.Calc;

    exports org.jmulti.calc;
    exports org.jmulti;
}

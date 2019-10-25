package org.jmulti.calc

import org.jmulti.{CalcParams, ParametersSweep, UnitCell}

object SampleData {
  val unitCell: UnitCell = new UnitCell(4.91410000, 8.51147087, 5.40600000, 90.0, 90.0, 90.0)

  val params: CalcParams = new CalcParams(0,40,20, 0,0,2, 4.51100000,
    4.51100000,1,"SiO2-ortho", ParametersSweep.PSI, false)

  val atoms: Array[AtomDescr] = Array(
    AtomDescr("Si", 4, P3(0.23500000, -0.23500000, 0.33333333)),
    AtomDescr("Si", 4, P3(0.23500000, 0.23500000, 0.66666667)),
    AtomDescr("Si", 4, P3(-0.47000000, 0.00000000, 0.00000000)),
    AtomDescr("Si", 4, P3(0.73500000, 0.26500000, 0.33333333)),
    AtomDescr("Si", 4, P3(0.73500000, 0.73500000, 0.66666667)),
    AtomDescr("Si", 4, P3(0.03000000, 0.50000000, 0.00000000)),

    AtomDescr("O", -2, P3(0.34040000, -0.07270000, 0.21440000)),
    AtomDescr("O", -2, P3(-0.06115000, 0.20655000, 0.54773333)),
    AtomDescr("O", -2, P3(-0.27925000, -0.13385000, 0.88106667)),
    AtomDescr("O", -2, P3(0.34040000, 0.07270000, -0.21440000)),
    AtomDescr("O", -2, P3(-0.06115000, -0.20655000, -0.54773333)),
    AtomDescr("O", -2, P3(-0.27925000, 0.13385000, -0.88106667)),
    AtomDescr("O", -2, P3(0.84040000, 0.42730000, 0.21440000)),
    AtomDescr("O", -2, P3(0.43885000, 0.70655000, 0.54773333)),
    AtomDescr("O", -2, P3(0.22075000, 0.36615000, 0.88106667)),
    AtomDescr("O", -2, P3(0.84040000, 0.57270000, -0.21440000)),
    AtomDescr("O", -2, P3(0.43885000, 0.29345000, -0.54773333)),
    AtomDescr("O", -2, P3(0.22075000, 0.63385000, -0.88106667))
  )
}

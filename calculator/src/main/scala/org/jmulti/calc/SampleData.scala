package org.jmulti.calc

import org.jmulti.{CalcParams, ParametersSweep, UnitCell}

trait SampleData {
  val unitCell: UnitCell
  val params: CalcParams
  val atoms: Array[AtomDescr]
}

object Samples {
  val sampleSiO2: SampleData = new SampleData {
    val unitCell: UnitCell = new UnitCell(4.91410000, 8.51147087, 5.40600000, 90.0, 90.0, 90.0)

    val params: CalcParams = new CalcParams(0, 40, 20, 0, 0, 2, 4.51100000,
      4.51100000, 1, "SiO2-ortho", ParametersSweep.PSI, true)

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

  val sampleCuB2O4: SampleData = new SampleData {
    override val unitCell: UnitCell = new UnitCell(12.00000000, 12.00000000, 5.62000000, 90.0, 90.0, 90.0)
    override val params: CalcParams = new CalcParams(0, 360, 1200, 0, 0, 2, 8.04826100,
      8.04826100, 1, "CuB2O4-sample", ParametersSweep.PSI, true)
    override val atoms: Array[AtomDescr] = Array(
     AtomDescr("Cu", 9, P3(0.0000000000000000,0.0000000000000000,0.50000000000000000)),
   AtomDescr("Cu", 9, P3(0.50000000000000000,0.0000000000000000,0.25000000000000000)),
   AtomDescr("Cu", 9, P3(0.50000000000000000,0.50000000000000000,0.0000000000000000)),
   AtomDescr("Cu", 9, P3(0.0000000000000000,0.50000000000000000,0.75000000000000000)),
   AtomDescr("Cu", 9, P3(8.1500000000000003E-002,0.25000000000000000,0.12500000000000000)),
   AtomDescr("Cu", 9, P3(-8.1500000000000003E-002,-0.25000000000000000,0.12500000000000000)),
   AtomDescr("Cu", 9, P3(0.25000000000000000,-8.1500000000000003E-002,-0.12500000000000000)),
   AtomDescr("Cu", 9, P3(-0.25000000000000000,8.1500000000000003E-002,-0.12500000000000000)),
   AtomDescr("Cu", 9, P3(0.58150000000000002,0.75000000000000000,0.62500000000000000)),
   AtomDescr("Cu", 9, P3(0.41849999999999998,0.25000000000000000,0.62500000000000000)),
   AtomDescr("Cu", 9, P3(0.75000000000000000,0.41849999999999998,0.37500000000000000)),
   AtomDescr("Cu", 9, P3(0.25000000000000000,0.58150000000000002,0.37500000000000000)),

   AtomDescr("O", -1, P3(0.15900000000000000,7.0800000000000002E-002,0.49809999999999999)),
   AtomDescr("O", -1, P3(-0.15900000000000000,-7.0800000000000002E-002,0.49809999999999999)),
   AtomDescr("O", -1, P3(7.0800000000000002E-002,-0.15900000000000000,-0.49809999999999999)),
   AtomDescr("O", -1, P3(-7.0800000000000002E-002,0.15900000000000000,-0.49809999999999999)),
   AtomDescr("O", -1, P3(0.34099999999999997,7.0800000000000002E-002,0.25190000000000001)),
   AtomDescr("O", -1, P3(-0.34099999999999997,-7.0800000000000002E-002,0.25190000000000001)),
   AtomDescr("O", -1, P3(7.0800000000000002E-002,-0.34099999999999997,-0.25190000000000001)),
   AtomDescr("O", -1, P3(-7.0800000000000002E-002,0.34099999999999997,-0.25190000000000001)),
   AtomDescr("O", -1, P3(0.65900000000000003,0.57079999999999997,0.99809999999999999)),
   AtomDescr("O", -1, P3(0.34099999999999997,0.42920000000000003,0.99809999999999999)),
   AtomDescr("O", -1, P3(0.57079999999999997,0.34099999999999997,1.9000000000000128E-003)),
   AtomDescr("O", -1, P3(0.42920000000000003,0.65900000000000003,1.9000000000000128E-003)),
   AtomDescr("O", -1, P3(0.84099999999999997,0.57079999999999997,0.75190000000000001)),
   AtomDescr("O", -1, P3(0.15900000000000003,0.42920000000000003,0.75190000000000001)),
   AtomDescr("O", -1, P3(0.57079999999999997,0.15900000000000003,0.24809999999999999)),
   AtomDescr("O", -1, P3(0.42920000000000003,0.84099999999999997,0.24809999999999999)),
   AtomDescr("O", -1, P3(0.24720000000000000,0.25000000000000000,0.12500000000000000)),
   AtomDescr("O", -1, P3(-0.24720000000000000,-0.25000000000000000,0.12500000000000000)),
   AtomDescr("O", -1, P3(0.25000000000000000,-0.24720000000000000,-0.12500000000000000)),
   AtomDescr("O", -1, P3(-0.25000000000000000,0.24720000000000000,-0.12500000000000000)),
   AtomDescr("O", -1, P3(0.74719999999999998,0.75000000000000000,0.62500000000000000)),
   AtomDescr("O", -1, P3(0.25280000000000002,0.25000000000000000,0.62500000000000000)),
   AtomDescr("O", -1, P3(0.75000000000000000,0.25280000000000002,0.37500000000000000)),
   AtomDescr("O", -1, P3(0.25000000000000000,0.74719999999999998,0.37500000000000000)),
   AtomDescr("O", -1, P3(-8.2699999999999996E-002,0.25000000000000000,0.12500000000000000)),
   AtomDescr("O", -1, P3(8.2699999999999996E-002,-0.25000000000000000,0.12500000000000000)),
   AtomDescr("O", -1, P3(0.25000000000000000,8.2699999999999996E-002,-0.12500000000000000)),
   AtomDescr("O", -1, P3(-0.25000000000000000,-8.2699999999999996E-002,-0.12500000000000000)),
   AtomDescr("O", -1, P3(0.41730000000000000,0.75000000000000000,0.62500000000000000)),
   AtomDescr("O", -1, P3(0.58270000000000000,0.25000000000000000,0.62500000000000000)),
   AtomDescr("O", -1, P3(0.75000000000000000,0.58270000000000000,0.37500000000000000)),
   AtomDescr("O", -1, P3(0.25000000000000000,0.41730000000000000,0.37500000000000000)),
   AtomDescr("O", -1, P3(7.4499999999999997E-002,0.18900000000000000,0.79559999999999997)),
   AtomDescr("O", -1, P3(-7.4499999999999997E-002,-0.18900000000000000,0.79559999999999997)),
   AtomDescr("O", -1, P3(0.18900000000000000,-7.4499999999999997E-002,-0.79559999999999997)),
   AtomDescr("O", -1, P3(-0.18900000000000000,7.4499999999999997E-002,-0.79559999999999997)),
   AtomDescr("O", -1, P3(0.42549999999999999,0.18900000000000000,-4.5599999999999974E-002)),
   AtomDescr("O", -1, P3(-0.42549999999999999,-0.18900000000000000,-4.5599999999999974E-002)),
   AtomDescr("O", -1, P3(0.18900000000000000,-0.42549999999999999,4.5599999999999974E-002)),
   AtomDescr("O", -1, P3(-0.18900000000000000,0.42549999999999999,4.5599999999999974E-002)),
   AtomDescr("O", -1, P3(0.57450000000000001,0.68900000000000006,1.2955999999999999)),
   AtomDescr("O", -1, P3(0.42549999999999999,0.31100000000000000,1.2955999999999999)),
   AtomDescr("O", -1, P3(0.68900000000000006,0.42549999999999999,-0.29559999999999997)),
   AtomDescr("O", -1, P3(0.31100000000000000,0.57450000000000001,-0.29559999999999997)),
   AtomDescr("O", -1, P3(0.92549999999999999,0.68900000000000006,0.45440000000000003)),
   AtomDescr("O", -1, P3(7.4500000000000011E-002,0.31100000000000000,0.45440000000000003)),
   AtomDescr("O", -1, P3(0.68900000000000006,7.4500000000000011E-002,0.54559999999999997)),
   AtomDescr("O", -1, P3(0.31100000000000000,0.92549999999999999,0.54559999999999997)),

   AtomDescr("B", 9, P3(0.18379999999999999,0.14840000000000000,0.69920000000000004)),
   AtomDescr("B", 9, P3(-0.18379999999999999,-0.14840000000000000,0.69920000000000004)),
   AtomDescr("B", 9, P3(0.14840000000000000,-0.18379999999999999,-0.69920000000000004)),
   AtomDescr("B", 9, P3(-0.14840000000000000,0.18379999999999999,-0.69920000000000004)),
   AtomDescr("B", 9, P3(0.31620000000000004,0.14840000000000000,5.0799999999999956E-002)),
   AtomDescr("B", 9, P3(-0.31620000000000004,-0.14840000000000000,5.0799999999999956E-002)),
   AtomDescr("B", 9, P3(0.14840000000000000,-0.31620000000000004,-5.0799999999999956E-002)),
   AtomDescr("B", 9, P3(-0.14840000000000000,0.31620000000000004,-5.0799999999999956E-002)),
   AtomDescr("B", 9, P3(0.68379999999999996,0.64839999999999998,1.1992000000000000)),
   AtomDescr("B", 9, P3(0.31620000000000004,0.35160000000000002,1.1992000000000000)),
   AtomDescr("B", 9, P3(0.64839999999999998,0.31620000000000004,-0.19920000000000004)),
   AtomDescr("B", 9, P3(0.35160000000000002,0.68379999999999996,-0.19920000000000004)),
   AtomDescr("B", 9, P3(0.81620000000000004,0.64839999999999998,0.55079999999999996)),
   AtomDescr("B", 9, P3(0.18379999999999996,0.35160000000000002,0.55079999999999996)),
   AtomDescr("B", 9, P3(0.64839999999999998,0.18379999999999996,0.44920000000000004)),
   AtomDescr("B", 9, P3(0.35160000000000002,0.81620000000000004,0.44920000000000004)),
   AtomDescr("B", 9, P3(0.50109999999999999,0.25000000000000000,0.12500000000000000)),
   AtomDescr("B", 9, P3(-0.50109999999999999,-0.25000000000000000,0.12500000000000000)),
   AtomDescr("B", 9, P3(0.25000000000000000,-0.50109999999999999,-0.12500000000000000)),
   AtomDescr("B", 9, P3(-0.25000000000000000,0.50109999999999999,-0.12500000000000000)),
   AtomDescr("B", 9, P3(1.0011000000000001,0.75000000000000000,0.62500000000000000)),
   AtomDescr("B", 9, P3(-1.0999999999999899E-003,0.25000000000000000,0.62500000000000000)),
   AtomDescr("B", 9, P3(0.75000000000000000,-1.0999999999999899E-003,0.37500000000000000)),
   AtomDescr("B", 9, P3(0.25000000000000000,1.0011000000000001,0.37500000000000000)),
    )
  }
}

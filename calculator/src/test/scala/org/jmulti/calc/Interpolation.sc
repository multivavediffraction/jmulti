import org.jmulti.calc.Complex

val values = Array((1.0,3.0), (2.0,4.0), (3.0, 4.5))
val wavelength = 1.5

val pairs = values.zip(values.drop(1))
val inner = pairs filter {
  case ((a,_),(b,_)) => a <= wavelength && wavelength < b
}
val first = pairs(0)
val result = if (inner.isEmpty)
  if (wavelength < first._1._1) first else pairs.last
else inner(0)

lazy val corrections: Map[String, Array[(Double, Complex)]] =
  Map("Si" -> Array((2.74851,Complex(0.3921,0.9619)),(2.28962, Complex(0.3647,0.6921)), (1.93597,Complex(0.3209,0.5081))),
    "O" -> Array((2.74851,Complex(0.1213,0.1057)),(2.28962, Complex(0.0928,0.0731)))
  )

val wavelength = 2.5


def selectValues(sym:String, wavelength: Double):((Double,Complex),(Double,Complex)) = {
  val tableRow = corrections(sym)
  val pairs = tableRow.zip(tableRow.drop(1))
  val inner = pairs filter {
    case ((a,_),(b,_)) => b <= wavelength && wavelength < a
  }
  val first = pairs(0)

  if (inner.isEmpty)
    if (first._1._1 < wavelength) first else pairs.last
  else inner(0)
}

selectValues("Si", 2.6)
selectValues("Si", 2.8)
selectValues("Si", 2.0)
selectValues("Si", 1.0)
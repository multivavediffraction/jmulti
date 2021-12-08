package org.jmulti.tables

import java.io.FileInputStream

import org.jmulti.Logger
import org.jmulti.calc.Complex

import scala.annotation.strictfp
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
 * International tables for crystallography vol. C
 * Table 4.2.6.8. Dispersion corrections for forward scattering
 */
object DispersionCorrectionFS {

  @strictfp def loadTable(file:String): Map[String, Array[(Double, Complex)]] = {
    import Control._

    Logger.log(s"Loading table: $file")
    def double(line:Int)(str:String):Double = {
      try {
        str.toDouble
      } catch {
        case ex:Throwable =>
          Logger.log(s"Failed to parse number: $str, error was: ${ex.getMessage}")
          0.0
      }
    }

    val stream = new FileInputStream(file)
    if (null != stream) {
      using(Source.fromInputStream(stream)) { source => {
        val lines = source.getLines().zipWithIndex.toList
        val energies = lines.head._1.split(";").drop(2).map(double(1))
        val even = lines.tail.filter { case (_, i) => i % 2 == 0 }
        val odd = lines.tail.filter { case (_, i) => i % 2 == 1 }
        odd.zip(even) map {
          case ((s1, line1), (s2, line2)) =>
            val t1 = s1.split(";")
            val t2 = s2.split(";")
            val values = t1.drop(2).map(double(line1)).zip(t2.drop(2).map(double(line2))) map {
              case (a, b) => Complex(a, b)
            }
            t1(0) -> energies.zip(values)
        }
      }
      }.toMap
    } else {
      Logger.log(s"Failed to read $file")
      Logger.log(s"Current dir: ${new java.io.File(".").getAbsolutePath}")
      Map()
    }
  }

  lazy val corrections: Map[String, Array[(Double, Complex)]] = loadTable("tables/ict.c.4268.csv")

//  lazy val corrections: Map[String, Array[(Double, Complex)]] =
//    Map("Si" -> Array((2.74851,Complex(0.3921,0.9619)),(2.28962, Complex(0.3647,0.6921)), (1.93597,Complex(0.3209,0.5081))),
//      "O" -> Array((2.74851,Complex(0.1213,0.1057)),(2.28962, Complex(0.0928,0.0731)))
//    )

  @strictfp def interpolate(a:Double, b:Double, v:Complex, u:Complex, x:Double): Complex = {
    val t = (x-a)/(b-a)
    t*u + (1-t)*v
  }

  def selectValues(tableRow:Array[(Double, Complex)], wavelength: Double):((Double,Complex),(Double,Complex)) = {
    val pairs = tableRow.zip(tableRow.drop(1))
    val inner = pairs filter {
      case ((a,_),(b,_)) => b <= wavelength && wavelength < a
    }
    val first = pairs(0)

    if (inner.isEmpty)
      if (first._1._1 < wavelength) first else pairs.last
    else inner(0)
  }

  def apply(sym:String, wavelength:Double):Try[Complex] =
  {
    corrections.get(sym) match {
      case None => Failure(new NotImplementedError(s"Dispersion correction fo forward scattering is not defined for $sym"))
      case Some(row) =>
        val ((a,b),(c,d)) = selectValues(row, wavelength)
        Success(interpolate(a, c, b, d, wavelength))
    }
  }
}

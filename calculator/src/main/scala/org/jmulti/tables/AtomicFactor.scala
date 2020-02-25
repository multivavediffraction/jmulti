package org.jmulti.tables

import java.io.FileInputStream

import org.jmulti.Logger

import scala.annotation.strictfp
import scala.util.{Failure, Success, Try}
import scala.io.Source

/**
 * International tables for crystallography vol. C
 * Tables 6.1.1.4 and  6.1.1.5
 */
object AtomicFactor {

  def loadTable[T](file:String)(f: ((String, Int))=>T): Seq[T] = {
    import Control._
    Logger.log(s"Loading table: $file")

    val stream = new FileInputStream(file)
    if (null != stream) {
      using(Source.fromInputStream(stream)) { source => {
        source.getLines.zipWithIndex.drop(1).toList map f
      }}
    } else {
      Logger.log(s"Failed to read $file")
      Logger.log(s"Current dir: ${new java.io.File(".").getAbsolutePath}")
      List()
    }
  }
  /**
   * Table 6.1.1.4
   */
  private val nameAndCharge = """([^\d-+]+)(\d{0,2})([-+]?)""".r
  private val onlyName = """([^\d-+]+)""".r

  def parseCharge(str: String, line: Int):(String,Int) = str match {
    case onlyName(element) =>
      (element, 0)
    case nameAndCharge(element, charge, sign) =>
      (element, (sign+charge).toInt)
    case _ =>
      Logger.log(s"Failed to parse ion description in line $line: $str")
      ("Null", 0)
  }
  
  def double(str:String, line: Int):Double = {
    try {
      str.toDouble
    } catch {
      case ex:Throwable =>
        Logger.log(s"Failed to parse number: $str, error was: ${ex.getMessage}")
        0.0
    }
  }
  
  lazy val smallSinOverLambda:Map[(String,Int),(Double,Array[(Double,Double)])] =
    loadTable("tables/ict.c.6114.csv") {case (str, line) =>
        val values = str.split(";")
        parseCharge(values(0), line) -> (double(values(10), line), Array((double(values(2), line), double(values(3), line)),
          (double(values(4), line), double(values(5), line)), (double(values(6), line), double(values(7), line)),
          (double(values(8), line), double(values(9), line))))
    }.toMap
//  val smallSinOverLambda:Map[(String,Int),(Double,Array[(Double,Double)])] = Map(
//    ("O",-2) -> (21.9412, Array((4.1916,12.8573), (1.63969,4.17236), (1.52673,47.0179), (-20.307,-0.01404))),
//    ("Si",4) -> (0.746297, Array((4.43918,1.64167), (3.20345,3.43757), (1.19453,0.214900), (0.416530,6.65365))),
//    ("Te",4) -> (19.9644, Array((4.81742, 19.0138), (0.420885, 6.14487), (28.5284, 2.5239), (70.8403, 4.352)))
//  )

  /**
   * Table 6.1.1.5
   */
  lazy val largeSinOverLambda:Map[String,Array[Double]] =
    loadTable("tables/ict.c.6115.csv") { case (str, line) =>
    val values = str.split(";")
    values(1) -> Array(double(values(2), line), double(values(3), line), double(values(4), line)/10.0, double(values(5), line)/100.0)
  }.toMap
//  val largeSinOverLambda:Map[String,Array[Double]] = Map(
//    "O" -> Array(1.3053, -0.83742, -0.016738, 0.00475),
//    "Si" -> Array(0.70683, -0.09888, -0.098356, 0.0055631),
//    "Te" -> Array(3.88240, 1.30980, 2.41170, -1.86420)
//  )

  @strictfp def apply(atom:String, charge: Int, sl:Double):Try[Double] = {
    if (sl <= 2.0) {
      if (smallSinOverLambda.contains((atom, charge))) {
        val c = smallSinOverLambda((atom, charge))
        val s = sl*sl
        Success(c._1 + c._2.map({ case (a, b) => a * Math.exp(-b * s) }).sum)
      } else {
        Failure(new NotImplementedError(s"Atomic factor table data is not defined for small sin(theta)/lambda for $atom$charge."))
      }
    } else {
      if (largeSinOverLambda.contains(atom)) {
        val c = largeSinOverLambda(atom).zip(List(1.0, sl, sl*sl, sl*sl*sl))
        Success(Math.exp(c.map({ case (a, b) => a * b }).sum))
      } else {
        Failure(new NotImplementedError(s"Atomic factor table data is not defined for large sin(theta)/lambda for $atom$charge"))
      }
    }
  }
}

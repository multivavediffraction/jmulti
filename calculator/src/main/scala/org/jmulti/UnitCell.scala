package org.jmulti

import org.jmulti.calc.P3

import scala.annotation.strictfp

object UnitCell {
  @strictfp private val rad = Math.PI / 180.0

  def apply(a: Double, b: Double, c: Double, alpha: Double, beta: Double, gamma: Double): UnitCell = new UnitCell(a, b, c, alpha, beta, gamma)

  def cubic(a: Double) = new UnitCell(a, a, a, 90.0, 90.0, 90.0)

  def triclinic(a: Double, b: Double, c: Double, alpha: Double, beta: Double, gamma: Double) = new UnitCell(a, b, c, alpha, beta, gamma)

  def monoclinic(a: Double, b: Double, c: Double, beta: Double) = new UnitCell(a, b, c, 90.0, beta, 90.0)

  def orthorhombic(a: Double, b: Double, c: Double) = new UnitCell(a, b, c, 90.0, 90.0, 90.0)

  def tetragonal(a: Double, c: Double) = new UnitCell(a, a, c, 90.0, 90.0, 90.0)

  def rhombohedral(a: Double, alpha: Double) = new UnitCell(a, a, a, alpha, alpha, alpha)

  def hexagonal(a: Double, c: Double) = new UnitCell(a, a, c, 90.0, 90.0, 120.0)
}

class UnitCell(val a: Double, val b: Double, val c: Double, val alpha: Double, val beta: Double, val gamma: Double) {
  @strictfp def d_hkl(h: Double, k: Double, l: Double): Double = { //1/d^2 ==
    val sin_a = Math.sin(alpha * UnitCell.rad)
    val cos_a = Math.cos(alpha * UnitCell.rad)
    val sin_b = Math.sin(beta * UnitCell.rad)
    val cos_b = Math.cos(beta * UnitCell.rad)
    val sin_c = Math.sin(gamma * UnitCell.rad)
    val cos_c = Math.cos(gamma * UnitCell.rad)
    ((h * h * sin_a * sin_a / (a * a)) + (k * k * sin_b * sin_b / (b * b)) + (l * l * sin_c * sin_c / (c * c)) + ((2 * k * l * cos_a) / (b * c)) + ((2 * h * l * cos_b) / (a * c)) + ((2 * h * k * cos_c) / (a * b))) / (1 - cos_a * cos_a - cos_b * cos_b - cos_c * cos_c + 2 * cos_a * cos_b * cos_c)
  }

  @strictfp def getUnitCellVectors: Array[P3] = {
    val rad = Math.PI / 180
    val a = new P3(this.a, 0, 0)
    val b = new P3(this.b * Math.cos(gamma * rad), this.b * Math.sin(gamma * rad), 0)
    val cz = (this.c / Math.sin(gamma * rad)) * Math.sqrt(1 - Math.pow(Math.cos(alpha * rad), 2) - Math.pow(Math.cos(beta * rad), 2) - Math.pow(Math.cos(gamma * rad), 2) + 2 * Math.cos(alpha * rad) * Math.cos(beta * rad) * Math.cos(gamma * rad))
    val c = new P3(this.c * Math.cos(beta * rad), this.c * (Math.cos(alpha * rad) - Math.cos(beta * rad) * Math.cos(gamma * rad)) / Math.sin(gamma * rad), cz)
    Array[P3](a, b, c)
  }
}

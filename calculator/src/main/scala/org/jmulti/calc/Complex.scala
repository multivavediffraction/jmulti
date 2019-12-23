package org.jmulti.calc

import scala.annotation.strictfp

case class Complex (re: Double, im: Double) {
  private val modulus = Math.sqrt(re**2 + im**2)

  @strictfp def +(b:Complex) = Complex(re+b.re, im+b.im)
  @strictfp def +(b:Double) = Complex(re+b, im)

  @strictfp def -(b:Complex) = Complex(re-b.re, im-b.im)
  @strictfp def -(b:Double) = Complex(re-b, im)

  @strictfp def unary_- = Complex(-re, -im)
  def unary_! :Double = modulus
  @strictfp def normSquare:Double = re**2 + im**2

  @strictfp def *(c: Complex) = Complex(re * c.re - im * c.im, im * c.re + re * c.im)
  @strictfp def *(v:Double) = Complex(re * v, im * v)

  @strictfp def /(c: Complex):Complex = {
    require(c.re != 0 || c.im != 0)
    val d = Math.pow(c.re, 2) + Math.pow(c.im, 2)
    Complex((re * c.re + im * c.im) / d, (im * c.re - re * c.im) / d)
  }
  @strictfp def /(v:Double):Complex = this./(Complex(v,0))

  override def toString: String = s"($re, $im)"
}

package org.jmulti.calc

class P3(val x: Double, val y:Double, val z:Double) {
  def update(x:Double = x, y:Double = y, z:Double = z) = P3(x, y, z)
  def plus(v: P3): P3 = {
    P3(x + v.x, y + v.y, z + v.z)
  }
  def + : P3 => P3 = plus

  def mul(a: Double): P3 = {
    P3(a * x, a * y, a * z)
  }
  def mul(a:Int): P3 = this * a.toDouble
  def dot(p: P3): Double = x*p.x + y*p.y + z*p.z

  def *(v: Double): P3 = mul(v)
  def *(v: Int): P3 = mul(v)
  def *(p: P3): Double = dot(p)

  def cross(p:P3): P3 = {
    P3(y*p.z - z*p.y, z*p.x - x*p.z, x*p.y - y*p.x)
  }

  def /(v:Double) = P3(x/v, y/v, z/v)

  def normSquare: Double = x*x + y*y + z*z
  lazy val norm: P3 = this / Math.sqrt(normSquare)
}

object P3 {
  def apply(x: Double, y: Double, z: Double): P3 = new P3(x, y, z)
  def tripleProduct(p1: P3, p2: P3, p3: P3): Double = {
    p1.x*p2.y*p3.z + p1.y*p2.z*p3.x + p1.z*p2.x*p3.y - p1.x*p2.z*p3.y - p1.y*p2.x*p3.z - p1.z*p2.y*p3.x
  }
}
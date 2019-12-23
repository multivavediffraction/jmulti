package org.jmulti

import scala.annotation.strictfp

package object calc {
  implicit class RealOps(val v:Double) extends AnyVal {
    def **(p:Double):Double = Math.pow(v,p)
    @strictfp def **(c:Complex):Complex = {
      val mod = Math.pow(v, c.re)
      Complex(mod * Math.cos(c.im), mod * Math.sin(c.im))
    }
    @strictfp def *(c:Complex):Complex = Complex(v*c.re, v*c.im)
    @strictfp def +(c:Complex):Complex = Complex(v+c.re, c.im)
    @strictfp def -(c:Complex):Complex = Complex(v-c.re, -c.im)
    @strictfp def /(c:Complex):Complex = Complex(v,0)/c

    @strictfp def *(p:P3):P3 = P3(p.x*v, p.y*v, p.z*v)
  }

  implicit class IntOps(val v: Int) extends AnyVal {
    @strictfp def *(p: P3): P3 = p.mul(v)
  }

  @strictfp def cexp(c: Complex):Complex = Math.E ** c
}

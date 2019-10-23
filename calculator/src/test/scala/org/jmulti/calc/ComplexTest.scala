package org.jmulti.calc

import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

import org.scalatest._

@RunWith(classOf[JUnitRunner])
class ComplexTest extends FlatSpec with Matchers {
  "A complex exponent" should "produce same results as euler formula" in {
    val e1 = cexp(Complex(0, Math.PI))

    e1.re should be (-1.0)
  }
}

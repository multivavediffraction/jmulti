package org.jmulti.calc

class AtomDescr(val name:String, val charge:Int, val p:P3) {
  def this() = this("none", 0, P3(0,0,0))

  def updateName(n: String): AtomDescr = AtomDescr(n, charge, p)
  def updateCharge(c: Int): AtomDescr = AtomDescr(name, c, p)
  def updateX(x: Double): AtomDescr = AtomDescr(name, charge, p.update(x=x))
  def updateY(y: Double): AtomDescr = AtomDescr(name, charge, p.update(y=y))
  def updateZ(z: Double): AtomDescr = AtomDescr(name, charge, p.update(z=z))
  def updateP(newP: P3): AtomDescr = AtomDescr(name, charge, newP)
}

object AtomDescr {
  def apply(name:String, charge:Int, p:P3) = new AtomDescr(name, charge, p)
}

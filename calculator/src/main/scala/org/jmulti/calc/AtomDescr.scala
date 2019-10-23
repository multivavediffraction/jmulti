package org.jmulti.calc

class AtomDescr(val name:String, val charge:Int, val p:P3) {
  def this() = this("none", 0, P3(0,0,0))
}

object AtomDescr {
  def apply(name:String, charge:Int, p:P3) = new AtomDescr(name, charge, p)
}

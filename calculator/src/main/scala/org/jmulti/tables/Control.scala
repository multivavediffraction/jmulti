package org.jmulti.tables

import scala.io.BufferedSource

object Control {
  trait HasClose[A] {
    def close(a:A):Unit
  }

  def using[A, B](resource:A)(f: A=>B)(implicit hc: HasClose[A]): B =
    try {
      f(resource)
    } finally {
      hc.close(resource)
    }

  implicit val wrapBufferedSource: HasClose[BufferedSource] = (s: BufferedSource) => s.close()
}

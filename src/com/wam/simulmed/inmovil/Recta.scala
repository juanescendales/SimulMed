package com.wam.simulmed.inmovil

trait Recta {
  type T<: Punto
  val origen:T
  val fin:T
  val angulo =  math.atan2(fin.y - origen.y, fin.x - origen.x).toDegrees
}

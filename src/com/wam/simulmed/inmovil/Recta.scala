package com.wam.simulmed.inmovil

trait Recta {
  type T<: Punto
  val origen:T
  val fin:T
  val angulo =  math.atan(math.abs(fin.y - origen.y) / math.abs(fin.x - origen.x)).toDegrees 
}

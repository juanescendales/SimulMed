package com.wam.simulmed.ciudad

import com.wam.simulmed.inmovil._

class Via (val origen:Interseccion , val fin:Interseccion, val velMaxima:Double , val tipoVia:TipoVia , val sentido:Sentido,val numero:String , val nombre:String) extends Recta{
  type T = Interseccion
  val distancia:Double = Math.sqrt(Math.pow(fin.x - origen.x,2) + Math.pow(fin.y - origen.y,2))
  override def toString() = s"$nombre"
}

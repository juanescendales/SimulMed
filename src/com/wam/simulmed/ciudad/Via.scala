package com.wam.simulmed.ciudad

import com.wam.simulmed.inmovil._

case class Via (val origen:Interseccion , val fin:Interseccion, val velMaxima:Double , val tipoVia:TipoVia , val sentido:Sentido,val numero:String , val nombre:Option[String], val semaforoInicio:Option[Semaforo]=None, val semaforoFin:Option[Semaforo]=None) extends Recta{
  type T = Interseccion
  val distancia:Double = Math.sqrt(Math.pow(fin.x - origen.x,2) + Math.pow(fin.y - origen.y,2))
}



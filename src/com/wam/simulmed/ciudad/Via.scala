package com.wam.simulmed.ciudad

import com.wam.simulmed.inmovil._

case class Via(val id:Int, val origen: Interseccion, val fin: Interseccion, val velMaxima: Double, val tipoVia: TipoVia, val sentido: Sentido,
               val numero: String, val nombre: Option[String], var semaforoInicio: Option[Semaforo] = None, var semaforoFin: Option[Semaforo] = None,
               var camaraDeFotoDeteccion: Option[CamaraFotoDeteccion] = None) extends Recta {

  type T = Interseccion
  val distancia: Double = Math.sqrt(Math.pow(fin.x - origen.x, 2) + Math.pow(fin.y - origen.y, 2))
  Via.listaVias += ((this.id-> this))
  Simulacion.listaVias += this
}

object Via{
  val listaVias = collection.mutable.Map[Int, Via]()
  
}

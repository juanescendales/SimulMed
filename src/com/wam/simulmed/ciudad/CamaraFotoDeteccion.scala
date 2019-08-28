package com.wam.simulmed.ciudad
import com.wam.simulmed.movil._
import com.wam.simulmed.ciudad._
import com.wam.simulmed.inmovil._
class CamaraFotoDeteccion(val via: Via, id: Int) {
  
  val posicion: Punto = new Punto(Recta.parametrizacionDeDosPuntos(via.origen, via.fin, 0.5)._1, Recta.parametrizacionDeDosPuntos(via.origen, via.fin, 0.5)._2)
  val distanciaAVia = Punto.distanciaEntre2Puntos(via.origen, posicion)

}

object CamaraFotoDeteccion {
  def verificarVelocidad(vehiculo: Vehiculo, via: Via): Unit = {
    if (vehiculo.velocidad.magnitud > via.velMaxima) {
      new Comparendo(vehiculo, via)(via.velMaxima)
    }
  }
}
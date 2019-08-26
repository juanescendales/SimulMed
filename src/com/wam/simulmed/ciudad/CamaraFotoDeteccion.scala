package com.wam.simulmed.ciudad
import com.wam.simulmed.movil._
import com.wam.simulmed.ciudad._
import com.wam.simulmed.inmovil._
class CamaraFotoDeteccion(val via: Via) {
  val posicion: Punto = new Punto(via.fin.x * (0.5) + via.origen.x * (0.5), via.fin.y * (0.5) + via.origen.y * (0.5))
  val distanciaAVia = Punto.distanciaEntre2Puntos(via.origen, posicion)

}

object CamaraFotoDeteccion {
  def verificarVelocidad(vehiculo: Vehiculo, via: Via): Unit = {
    if (vehiculo.velocidad.magnitud > via.velMaxima) {
      new Comparendo(vehiculo, via)(via.velMaxima)
    }
  }
}
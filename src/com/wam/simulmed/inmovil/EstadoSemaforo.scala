package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._

trait EstadoSemaforo{
  def avanzarEstado(estadoActual:EstadoSemaforo)= estadoActual match{
      case EstadoAmarillo => EstadoRojo
      case EstadoRojo => EstadoVerde
      case EstadoVerde => EstadoAmarillo
    }
  def getTiempo:Double
}

object EstadoAmarillo extends EstadoSemaforo{
  private val tiempoAmarillo:Double=5
  def getTiempo:Double=tiempoAmarillo
} 

object EstadoVerde extends EstadoSemaforo{
  val tiempoVerde:Double=50
  def getTiempo:Double=tiempoVerde
}

object EstadoRojo extends EstadoSemaforo{
  val tiempoRojo:Double=50
  def getTiempo:Double=tiempoRojo
}
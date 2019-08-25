package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._

trait EstadoSemaforo{
  def avanzarEstado(estadoActual:EstadoSemaforo)= estadoActual match{
      case EstadoAmarillo => EstadoRojo
      case EstadoRojo => EstadoVerde
      case EstadoVerde => EstadoAmarillo
    }
  def getTiempo:Int
}

object EstadoAmarillo extends EstadoSemaforo{
  private val tiempoAmarillo:Int=0
  def getTiempo:Int=tiempoAmarillo
} 

object EstadoVerde extends EstadoSemaforo{
  val tiempoVerde:Int=0
  def getTiempo:Int=tiempoVerde
}

object EstadoRojo extends EstadoSemaforo{
  val tiempoRojo:Int=0
  def getTiempo:Int=tiempoRojo
}
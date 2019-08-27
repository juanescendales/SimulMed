package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._

trait EstadoSemaforo{
  def avanzarEstado(estadoActual:EstadoSemaforo)= estadoActual match{
      case EstadoAmarillo => EstadoRojo
      case EstadoRojo => EstadoVerde
      case EstadoVerde => EstadoAmarillo
    }
  def getTiempo:Double
  val distanciaFrenado:Double
}

object EstadoAmarillo extends EstadoSemaforo{
  private val tiempoAmarillo:Double=5
  val distanciaFrenado:Double=100
  def getTiempo:Double=tiempoAmarillo
} 

object EstadoVerde extends EstadoSemaforo{
  val tiempoVerde:Double=40
  val distanciaFrenado:Double=EstadoRojo.distanciaFrenado*2
  def getTiempo:Double=tiempoVerde
}

object EstadoRojo extends EstadoSemaforo{
  val tiempoRojo:Double=40
  val distanciaFrenado:Double=200
  def getTiempo:Double=tiempoRojo
}
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
  val tiempoAmarillo:Double=Simulacion.tiempoAmarillo
  val distanciaFrenado:Double=Simulacion.XSemaforoFrenar
  def getTiempo:Double=tiempoAmarillo
  override def toString = "EstadoAmarillo"
} 

object EstadoVerde extends EstadoSemaforo{
  val minTiempoVerde=Simulacion.minTiempoVerde
  val maxTiempoVerde=Simulacion.maxTiempoVerde
  val tiempoVerde:Double=0 //Depende de cada semaforo
  val distanciaFrenado:Double=EstadoRojo.distanciaFrenado*2
  def getTiempo:Double=tiempoVerde
  override def toString = "EstadoVerde"
}

object EstadoRojo extends EstadoSemaforo{
  val tiempoRojo:Double=120
  val distanciaFrenado:Double=200
  def getTiempo:Double=tiempoRojo
  override def toString = "EstadoRojo"
}